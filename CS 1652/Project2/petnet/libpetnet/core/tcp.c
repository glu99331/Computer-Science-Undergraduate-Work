/* 
 * Copyright (c) 2020, Jack Lange <jacklange@cs.pitt.edu>
 * All rights reserved.
 *
 * This is free software.  You are permitted to use,
 * redistribute, and modify it as specified in the file "PETLAB_LICENSE".
*/

#include <string.h>
#include <errno.h>
#include <petnet.h>
#include <unistd.h>
#include <stdbool.h>

#include <petlib/pet_util.h>
#include <petlib/pet_log.h>
#include <petlib/pet_hashtable.h>
#include <petlib/pet_json.h>

#include <util/ip_address.h>
#include <util/inet.h>
#include <util/checksum.h>

#include "ethernet.h"
#include "ipv4.h"
#include "tcp.h"
#include "tcp_connection.h"
#include "packet.h"
#include "socket.h"

//Preprocessor Macro to calculate MIN and MAX
#define MIN(x, y) (((x) < (y)) ? (x) : (y))
#define MAX(x, y) (((x) > (y)) ? (x) : (y))


extern int petnet_errno;

struct tcp_state {
    struct tcp_con_map * con_map;
};



static inline struct tcp_raw_hdr *
__get_tcp_hdr(struct packet * pkt)
{
    struct tcp_raw_hdr * tcp_hdr = pkt->layer_2_hdr + pkt->layer_2_hdr_len + pkt->layer_3_hdr_len;

    pkt->layer_4_type    = TCP_PKT;
    pkt->layer_4_hdr     = tcp_hdr;
    pkt->layer_4_hdr_len = tcp_hdr->header_len * 4;

    return tcp_hdr;
}


static inline struct tcp_raw_hdr *
__make_tcp_hdr(struct packet * pkt, 
               uint32_t        option_len)
{
    pkt->layer_4_type    = TCP_PKT; 
    pkt->layer_4_hdr     = pet_malloc(sizeof(struct tcp_raw_hdr) + option_len);
    pkt->layer_4_hdr_len = sizeof(struct tcp_raw_hdr) + option_len;

    return (struct tcp_raw_hdr *)(pkt->layer_4_hdr);
}

static inline void * //modified from get_payload to get length as well
__pkt(struct packet * pkt){
    if (pkt->layer_3_type == IPV4_PKT) {
        struct ipv4_raw_hdr * ipv4_hdr = pkt->layer_3_hdr;

        pkt->payload     = pkt->layer_4_hdr + pkt->layer_4_hdr_len;
        pkt->payload_len = ntohs(ipv4_hdr->total_len) - (pkt->layer_3_hdr_len + pkt->layer_4_hdr_len);

        return pkt;
    } else {
        log_error("Unhandled layer 3 packet format\n");
        return NULL;
    }
}

static 

pet_json_obj_t
tcp_hdr_to_json(struct tcp_raw_hdr * hdr)
{
    pet_json_obj_t hdr_json = PET_JSON_INVALID_OBJ;

    hdr_json = pet_json_new_obj("TCP Header");

    if (hdr_json == PET_JSON_INVALID_OBJ) {
        log_error("Could not create TCP Header JSON\n");
        goto err;
    }

    pet_json_add_u16 (hdr_json, "src port",    ntohs(hdr->src_port));
    pet_json_add_u16 (hdr_json, "dst port",    ntohs(hdr->dst_port));
    pet_json_add_u32 (hdr_json, "seq num",     ntohl(hdr->seq_num));
    pet_json_add_u32 (hdr_json, "ack num",     ntohl(hdr->ack_num));
    pet_json_add_u8  (hdr_json, "header len",  hdr->header_len * 4);
    pet_json_add_bool(hdr_json, "URG flag",    hdr->flags.URG);
    pet_json_add_bool(hdr_json, "ACK flag",    hdr->flags.ACK);
    pet_json_add_bool(hdr_json, "URG flag",    hdr->flags.URG);
    pet_json_add_bool(hdr_json, "RST flag",    hdr->flags.RST);
    pet_json_add_bool(hdr_json, "SYN flag",    hdr->flags.SYN);
    pet_json_add_bool(hdr_json, "FIN flag",    hdr->flags.FIN);
    pet_json_add_u16 (hdr_json, "recv win",    ntohs(hdr->recv_win));
    pet_json_add_u16 (hdr_json, "checksum",    ntohs(hdr->checksum));
    pet_json_add_u16 (hdr_json, "urgent ptr",  ntohs(hdr->urgent_ptr));


    return hdr_json;

err:
    if (hdr_json != PET_JSON_INVALID_OBJ) pet_json_free(hdr_json);

    return PET_JSON_INVALID_OBJ;
}


void
print_tcp_header(struct tcp_raw_hdr * tcp_hdr)
{
    pet_json_obj_t hdr_json = PET_JSON_INVALID_OBJ;

    char * json_str = NULL;

    hdr_json = tcp_hdr_to_json(tcp_hdr);

    if (hdr_json == PET_JSON_INVALID_OBJ) {
        log_error("Could not serialize TCP Header to JSON\n");
        return;
    }

    json_str = pet_json_serialize(hdr_json);

    pet_printf("\"TCP Header\": %s\n", json_str);

    pet_free(json_str);
    pet_json_free(hdr_json);

    return;

}

uint16_t
calculate_checksum_continue_flip(uint16_t   checksum,
                            void     * data,
                            uint32_t   length_in_words)
{
    uint32_t   scratch   = checksum;
    uint16_t * cast_data = data;

    uint32_t i = 0;

    for (i = 0; i < length_in_words; i++) {
        scratch += htons(cast_data[i]);

        // Wrap overflow
        if (scratch & 0xffff0000) {
            scratch &= 0x0000ffff;
            scratch += 1;
        }
    }

    return (uint16_t)scratch;
}

//Adapted from udp.c's checksum function
static uint16_t 
_calculate_checksum(struct ipv4_addr    * local_addr,
                   struct ipv4_addr    * remote_addr,
                   struct packet       * pkt)
{
    struct ipv4_pseudo_hdr hdr;
    uint16_t checksum = 0;
    memset(&hdr, 0, sizeof(struct ipv4_pseudo_hdr));

    ipv4_addr_to_octets(local_addr,  hdr.src_ip);
    ipv4_addr_to_octets(remote_addr, hdr.dst_ip);

    hdr.proto  = IPV4_PROTO_TCP;
    hdr.length = htons(pkt->layer_4_hdr_len + pkt->payload_len);

    checksum = calculate_checksum_begin(&hdr, sizeof(struct ipv4_pseudo_hdr) / 2); 
    checksum = calculate_checksum_continue(checksum, pkt->layer_4_hdr, pkt->layer_4_hdr_len / 2);  
    checksum = calculate_checksum_continue_flip(checksum, pkt->payload, pkt->payload_len / 2);

    /* 
     * If there is an odd number of data bytes we have to include a 0-byte after the the last byte 
    */
    if ((pkt->payload_len % 2) != 0) {
        uint16_t tmp = *(uint8_t *)(pkt->payload + pkt->payload_len - 1);

        checksum = calculate_checksum_finalize(checksum, &tmp, 1);
    } else {
        checksum = calculate_checksum_finalize(checksum, NULL, 0);
    }

    return checksum;
}

void set_flags(struct tcp_raw_hdr* tcp_hdr, bool syn, bool ack, bool fin){
    //if state negative at 
    if(syn){
        if(ack){
            if(fin){
                tcp_hdr->flags.SYN = 1;
                tcp_hdr->flags.ACK = 1;
                tcp_hdr->flags.FIN = 1;
            }
            else{
                tcp_hdr->flags.SYN = 1;
                tcp_hdr->flags.ACK = 1;
            }
        }else{ //!ack and syn
            if(fin){ //syn and ack
                tcp_hdr->flags.SYN = 1;
                tcp_hdr->flags.FIN = 1;
            }else{ //syn
                tcp_hdr->flags.SYN = 1;
            }     
        }
    }else{
        if(ack){ //!syn and ack
           if(fin){
                tcp_hdr->flags.ACK = 1;
                tcp_hdr->flags.FIN = 1;
           }else{
                tcp_hdr->flags.ACK = 1;
           }
        }else{
            //!ack
            if(fin){ //!ack, !syn, fin
                tcp_hdr->flags.FIN = 1;
            }else{
                return;
            }
        }
    }
}

int process_flags(struct tcp_connection* con, struct tcp_raw_hdr* tcp_hdr){
    switch(con->con_state){ //based on state diagram
        case SYN_SENT:
            set_flags(tcp_hdr, true, false, false);
            goto good_return;
        case SYN_RCVD:
            set_flags(tcp_hdr, true, true, false);
            goto good_return;
        case ESTABLISHED:
            set_flags(tcp_hdr, false, true, false);
            goto good_return;
        case CLOSE_WAIT:
            set_flags(tcp_hdr, false, true, true);
            goto good_return;
        case FIN_WAIT1:
            set_flags(tcp_hdr, false, true, true);
            goto good_return;
        case FIN_WAIT2:
            set_flags(tcp_hdr, false, true, false);
            goto good_return;
        case CLOSING:
            set_flags(tcp_hdr, false, true, true);
            goto good_return;
        case TIME_WAIT:
            set_flags(tcp_hdr, false, false, false);
            goto bad_return;
        default:
            set_flags(tcp_hdr, false, true, false);
            goto good_return;
    }    
    good_return:
        return 0;
    bad_return:
        return -1;
}

void setup_pkt_vals(struct tcp_connection * con, struct tcp_raw_hdr* tcp_hdr, struct packet* pkt){
    tcp_hdr->src_port = htons(con->ipv4_tuple.local_port); //set up tcp hdr according to slides
    tcp_hdr->dst_port = htons(con->ipv4_tuple.remote_port);

    tcp_hdr->seq_num = htonl(con->local_seq_num);
    tcp_hdr->ack_num = htonl(con->local_ack_num);
    tcp_hdr->header_len = pkt->layer_4_hdr_len;
    tcp_hdr->recv_win = htons((unsigned short) 1024);
    
    if(process_flags(con, tcp_hdr) == -1){
        pet_printf("An error occurred when setting up the flags.\n");
        exit(0);
    }
    
    pkt->payload_len = MIN((unsigned short)pet_socket_send_capacity(con->sock),con->received_recv_win);
    pkt->payload     = pet_malloc(pkt->payload_len);

    pet_socket_sending_data(con->sock, pkt->payload, pkt->payload_len);
    tcp_hdr->checksum = _calculate_checksum(con->ipv4_tuple.local_ip, con->ipv4_tuple.remote_ip, pkt);  

    ipv4_pkt_tx(pkt, con->ipv4_tuple.remote_ip);
    con->local_seq_num = con->local_seq_num + pkt->payload_len;
    
}

int process_packet(struct tcp_connection * con){
    pet_printf("Currently processing packet:\n");
    struct packet* pkt       = NULL;
    struct tcp_raw_hdr* tcp_hdr   = NULL;
    pkt = create_empty_packet();
    tcp_hdr = __make_tcp_hdr(pkt,1); //Create single byte

    setup_pkt_vals(con, tcp_hdr, pkt);

    return 0;   
}



int 
tcp_listen(struct socket    * sock, 
           struct ipv4_addr * local_addr,
           uint16_t           local_port)
{
    struct tcp_connection* tcp_conn = create_ipv4_tcp_con(petnet_state->tcp_state->con_map, local_addr, local_addr, local_port, local_port);
    add_sock_to_tcp_con(petnet_state->tcp_state->con_map,tcp_conn,sock);
    tcp_conn->con_state = LISTEN;
    put_and_unlock_tcp_con(tcp_conn);
    return 0;
}

int 
tcp_connect_ipv4(struct socket    * sock, 
                 struct ipv4_addr * local_addr, 
                 uint16_t           local_port,
                 struct ipv4_addr * remote_addr,
                 uint16_t           remote_port)
{
    struct tcp_connection* tcp_conn = create_ipv4_tcp_con(petnet_state->tcp_state->con_map,local_addr,remote_addr,local_port,remote_port); //first establish tcp_conn
    add_sock_to_tcp_con(petnet_state->tcp_state->con_map,tcp_conn,sock); //then add socket to tcp connection
    tcp_conn->con_state = SYN_SENT; //then send the syn
    process_packet(tcp_conn); //send corresponding data packet
    put_and_unlock_tcp_con(tcp_conn);
    return 0;
}


int tcp_passive_listen_ipv4(struct socket * sock, 
                 struct ipv4_addr * local_addr, 
                 uint16_t           local_port,
                 struct ipv4_addr * remote_addr,
                 uint16_t           remote_port,
                 tcp_con_state_t con_state, 
                 struct tcp_connection* tcp_con,
                 struct tcp_connection* tcp_listen){
    switch(con_state){
        case LISTEN:
            put_and_unlock_tcp_con(tcp_listen);
            tcp_con = create_ipv4_tcp_con(petnet_state->tcp_state->con_map,local_addr,remote_addr,local_port,remote_port);
            add_sock_to_tcp_con(petnet_state->tcp_state->con_map,tcp_con,sock);
            tcp_con->con_state = SYN_RCVD;
            put_and_unlock_tcp_con(tcp_con);
            return 0;
            break;
        default:
            pet_printf("Error listening on port: %d\n", remote_port);
            return -1;       
    }
}
int 
tcp_passive_connect_ipv4(struct socket    * sock, 
                 struct ipv4_addr * local_addr, 
                 uint16_t           local_port,
                 struct ipv4_addr * remote_addr,
                 uint16_t           remote_port)
{
    struct tcp_state* tcp_state = petnet_state->tcp_state;
    struct tcp_connection* tcp_listen = get_and_lock_tcp_con_from_ipv4(tcp_state->con_map,local_addr,local_addr,local_port,local_port);
    struct tcp_connection* tcp_con = NULL;
    return tcp_passive_listen_ipv4(sock, local_addr, local_port, remote_addr, remote_port, tcp_listen->con_state, tcp_con, tcp_listen);
}


int
tcp_send(struct socket * sock){
    struct tcp_connection * tcp_conn = get_and_lock_tcp_con_from_sock(petnet_state->tcp_state->con_map,sock);

    if(tcp_conn->con_state != ESTABLISHED){
        pet_printf("Error establishing a connection to the socket!\n");
        goto err;
    }
     
    process_packet(tcp_conn); //if the socket connection was established, try to send the packet
    put_and_unlock_tcp_con(tcp_conn);
    
    return 0;
err:
    if(tcp_conn != NULL) put_and_unlock_tcp_con(tcp_conn);
    return -1;
}



/* Petnet assumes SO_LINGER semantics, so if we're here there is no pending write data */
int
tcp_close(struct socket * sock)
{
    sleep(1);
    struct tcp_connection * tcp_conn = get_and_lock_tcp_con_from_sock(petnet_state->tcp_state->con_map,sock);
    tcp_conn->con_state = FIN_WAIT1;
    process_packet(tcp_conn);
    put_and_unlock_tcp_con(tcp_conn);

    return 0;
}

int stop_wait_receive(struct tcp_connection * con, struct tcp_raw_hdr* tcp_hdr, struct packet* pkt){

    con->received_seq_num = ntohl(tcp_hdr->seq_num);
    con->received_ack_num = ntohl(tcp_hdr->ack_num);
    con->received_recv_win = ntohs(tcp_hdr->recv_win);
    con->local_seq_num = con->received_ack_num;
    con->local_ack_num = con->received_seq_num;
    
    return 0;   
}


static int
__tcp_pkt_rx_ipv4(struct packet *pkt){
    struct tcp_raw_hdr * tcp_hdr = NULL;
    void * payload = NULL;

    tcp_hdr = __get_tcp_hdr(pkt);
    //int some_value = *((int *) some_param); <- some casting
    payload = ((struct packet *)__pkt(pkt))->payload;

    pet_printf("Received segment!!!\n");
    print_tcp_header(tcp_hdr);
    (void)payload;
    
    return 0;
}

int init_pkt_rx(struct tcp_raw_hdr* tcp_hdr, void* payload, struct packet * pkt, struct ipv4_addr* src_ip, struct ipv4_addr* dst_ip, struct tcp_connection* passive_conn, struct tcp_connection* tcp_conn, struct ipv4_raw_hdr* ipv4_hdr){
    //testing if we can listen, if so, we proceed as normal, otherwise we exit
    __tcp_pkt_rx_ipv4(pkt);

    src_ip = ipv4_addr_from_octets(ipv4_hdr->src_ip), dst_ip = ipv4_addr_from_octets(ipv4_hdr->dst_ip);

    if(tcp_hdr->flags.ACK != 1 && tcp_hdr->flags.SYN == 1){ //waiting for ack
        passive_conn = get_and_lock_tcp_con_from_ipv4(petnet_state->tcp_state->con_map,dst_ip,dst_ip,ntohs(tcp_hdr->dst_port),ntohs(tcp_hdr->dst_port)); //gotta free it
        if(passive_conn == NULL || passive_conn->con_state != LISTEN){
            pet_printf("Attempted to contact a server that was not listening");
            return -1;
        }
        put_and_unlock_tcp_con(passive_conn);
        
        tcp_passive_connect_ipv4(passive_conn->sock, dst_ip, ntohs(tcp_hdr->dst_port), src_ip, ntohs(tcp_hdr->src_port));
    } 

    tcp_conn = get_and_lock_tcp_con_from_ipv4(petnet_state->tcp_state->con_map, dst_ip, src_ip, ntohs(tcp_hdr->dst_port), ntohs(tcp_hdr->src_port));
    if(tcp_conn == NULL){ //exit if not listening
        return 0;
    }
    
    stop_wait_receive(tcp_conn, tcp_hdr, pkt);
    if(tcp_hdr->flags.FIN == 1){
        tcp_conn->con_state = CLOSE_WAIT;
    }
    return 0x7FFFFFFF;
}

void handle_pkt_rx_logic(struct tcp_raw_hdr * tcp_hdr, struct tcp_connection* con, struct packet* pkt, struct ipv4_addr * src_ip, int state){
    //this is all based on the state diagram
    if(state == 0){
         if(tcp_hdr->flags.ACK == 1 && tcp_hdr->flags.SYN == 1){
            con->con_state = ESTABLISHED;	
            con->local_ack_num++; 
            process_packet(con); 
            pet_socket_connected(con->sock);
        }else{   
            process_packet(con);                
        }
    }else if(state == 1){
         if(tcp_hdr->flags.ACK == 1){
            pet_printf("Successfully established Handshake.\n");    
            con->con_state = ESTABLISHED;
            add_sock_to_tcp_con( petnet_state->tcp_state->con_map,con,pet_socket_accepted(con->sock, src_ip, ntohs(tcp_hdr->src_port)));  
        }else{         
            con->local_ack_num  += 1; //increment for the SYN flag that we know we received
            con->local_seq_num = con->received_ack_num;                
            process_packet(con);
            con->local_seq_num += 1; //increment our seq because we sent a syn too
        }
    }else if(state == 2){
         if(tcp_hdr->flags.FIN == 1){
            con->con_state = CLOSE_WAIT;
        }else if(tcp_hdr->flags.ACK == 1 && ((struct packet *)__pkt(pkt))->payload_len != 0){
            pet_printf("Received: %u bytes\n ", (((struct packet *)__pkt(pkt))->payload_len));
            con->local_ack_num += ((struct packet *)__pkt(pkt))->payload_len;
            process_packet(con);
        }else if(tcp_hdr->flags.ACK == 1){
            //i don't know what to do here
        }
    }else if(state == 3){
        if(tcp_hdr->flags.FIN == 1){
            process_packet(con);
            con->con_state = LAST_ACK;
        }
    }else if(state == 4){
        if(tcp_hdr->flags.ACK == 1){
            con->con_state = CLOSED;
            pet_socket_closed(con->sock);
            remove_tcp_con(petnet_state->tcp_state->con_map, con);
        }
    }else if(state == 5){
        if(tcp_hdr->flags.ACK == 1){
            con->con_state = FIN_WAIT2;
		    process_packet(con);
            pet_socket_closed(con->sock);
            remove_tcp_con(petnet_state->tcp_state->con_map, con);
        }
    }else if(state == 6){
        if(tcp_hdr->flags.FIN == 1){
            con->con_state = CLOSED;
            pet_socket_closed(con->sock);
            remove_tcp_con(petnet_state->tcp_state->con_map, con);
        }
    }else{
        return;
    }
}

int 
tcp_pkt_rx(struct packet * pkt)
{
    if (pkt->layer_3_type == IPV4_PKT) {
        struct tcp_connection* tcp_conn = NULL,*passive_conn = NULL;
        struct tcp_raw_hdr* tcp_hdr = NULL;
        void* payload = NULL;

        struct ipv4_addr* src_ip = NULL;
        struct ipv4_addr* dst_ip = NULL;

        int ret = 0;

        int res = init_pkt_rx(tcp_hdr, payload, pkt, src_ip, dst_ip,passive_conn, tcp_conn, (struct ipv4_raw_hdr *)pkt->layer_3_hdr);
        if(res != 0x7FFFFFFF){ //if we don't want to go and consider our states, meaning there's an error in listening or something else:
            return res; //return 0 or -1 based on if we don't have any connections or if the server is not listening
        }
        //based on state diagram from powerpoint
        switch(tcp_conn->con_state){
	        case SYN_SENT:
                handle_pkt_rx_logic(tcp_hdr, tcp_conn, pkt, src_ip, 0);
                break;
            case SYN_RCVD:
                handle_pkt_rx_logic(tcp_hdr, tcp_conn, pkt, src_ip, 1);
                break;
            case ESTABLISHED:
                handle_pkt_rx_logic(tcp_hdr, tcp_conn, pkt, src_ip, 2);
                break;
            case CLOSE_WAIT:
                handle_pkt_rx_logic(tcp_hdr, tcp_conn, pkt, src_ip, 3);
                break;
            case LAST_ACK:
                handle_pkt_rx_logic(tcp_hdr, tcp_conn, pkt, src_ip, 4);
                return ret;
                break;
            case FIN_WAIT1:
                handle_pkt_rx_logic(tcp_hdr, tcp_conn, pkt, src_ip, 5);
                return ret;
                break;
            case FIN_WAIT2:
                handle_pkt_rx_logic(tcp_hdr, tcp_conn, pkt, src_ip, 6);
                return ret;
                break;
            default:
                handle_pkt_rx_logic(tcp_hdr, tcp_conn, pkt, src_ip, 7);
                break;
        }    
        put_and_unlock_tcp_con(tcp_conn);
        pet_socket_received_data(tcp_conn->sock,payload,pkt->payload_len);
        goto return_w_zero;
    }

    return_w_zero:
        return 0;
    //bad return    
    return -1;
}


int 
tcp_init(struct petnet * petnet_state)
{
    struct tcp_state * state = pet_malloc(sizeof(struct tcp_state));

    state->con_map  = create_tcp_con_map();

    petnet_state->tcp_state = state;
    
    return 0;
}