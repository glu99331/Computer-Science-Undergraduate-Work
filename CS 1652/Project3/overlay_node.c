/*
 * CS 1652 Project 3 
 * (c) Amy Babay, 2022
 * (c) <Student names here>
 * 
 * Computer Science Department
 * University of Pittsburgh
 */


#include <stdlib.h>
#include <stdint.h>
#include <stdbool.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <netdb.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <sys/errno.h>
#include <time.h>
#include <math.h>

#include <spu_alarm.h>
#include <spu_events.h>

#include "packets.h"
#include "client_list.h"
#include "node_list.h"
#include "edge_list.h"

#define PRINT_DEBUG 1

#define MAX_CONF_LINE 1024

enum mode {
    MODE_NONE,
    MODE_LINK_STATE,
    MODE_DISTANCE_VECTOR,
};

static uint32_t           My_IP      = 0;
static uint32_t           My_ID      = 0;
static uint16_t           My_Port    = 0;
static enum mode          Route_Mode = MODE_NONE;
static int                dist[MAX_NODES][MAX_NODES]; //link state for dijkstra
static struct edge_list   edge[MAX_NODES][MAX_NODES]; // sp for each node, may need to modify this
static struct node_list   node[MAX_NODES][MAX_NODES]; //next hops
static size_t             clocks[MAX_NODES];
static struct client_list Client_List;
static struct node_list   Node_List;
static struct edge_list   Edge_List;
static int Overlay_Ctrl_Sock;
static int Overlay_Data_Sock;
static const sp_time Data_Timeout = {10, 0};
static const sp_time Heartbeat_Timer = {1,0};

void handle_heartbeat_echo(struct heartbeat_echo_pkt *pkt);
void init_link_state();
void init_distance_vector();
/* Forward the packet to the next-hop node based on forwarding table */
void forward_data(struct data_pkt *pkt)
{
    Alarm(DEBUG, "overlay_node: forwarding data to overlay node %u, client port "
                 "%u\n", pkt->hdr.dst_id, pkt->hdr.dst_port);
    /*
     * Students fill in! Do forwarding table lookup, update path information in
     * header (see deliver_locally for an example), and send packet to next hop
     * */
    
    //Loop through the node list to get dst_id:
    struct node* src, *dst; 
    int ret;
    src = get_node_from_id(&Node_List, pkt->hdr.src_id);
    dst = get_node_from_id(&Node_List, pkt->hdr.dst_id);

    //we should periodically send a heart beat...
    // From Dr. Babay:
    // we want to send a link state update for dijkstra:
    // and then, of course, you also need to actually send data to that socket for the function to get triggered
  
    // For now the simplified approach should assume:
    /*
        1) No heartbeats/heartbeat echoes
        2) Assume no distance vector
    */

    
    //"Also, what does it mean to send an update" - "send an update" means send 
    //a link state update or new version of your distance vector (depending on the protocol)
    int path_len = 0;
    int bytes = 0;
    // int ret = -1;
    //For testing, node should also stamp its ID in the "path" field of pkt header and increment the path_len before forwarding:
    /* stamp packet so we can see the path taken */
    //this is updating path information
    path_len = pkt->hdr.path_len;
    if (path_len < MAX_PATH) {
        pkt->hdr.path[path_len] = My_ID;
        pkt->hdr.path_len++;
    }
    // the function should get called every time there is a packet to receive on that socket
    //send packet:
    Alarm(DEBUG, "Sending packet to destination: %d\n", dst->id);
    //We need to go through the links though!
    //For example, to get to 1 to 4, we need to go through 2 or 3:
    Alarm(DEBUG, "Node List for node %d to node %d, which has %d nodes:\n", (pkt->hdr.src_id-1+1), (pkt->hdr.dst_id-1+1), node[pkt->hdr.src_id-1][pkt->hdr.dst_id-1].num_nodes);
    for(int k = 0; k < node[pkt->hdr.src_id-1][pkt->hdr.dst_id-1].num_nodes; k++){
        struct node* src = node[pkt->hdr.src_id-1][pkt->hdr.dst_id-1].nodes[k];
        // Alarm(DEBUG, "(%d) ", src->id);
        if(src->next_hop != NULL){
            Alarm(DEBUG, "Node and its next hop is: %d, %d\n", src->id, src->next_hop->id);
        }
    }
    Alarm(DEBUG, "\n");
        
    for(struct node* n = get_node_from_id(&node[pkt->hdr.src_id-1][pkt->hdr.dst_id-1], pkt->hdr.src_id); n->next_hop != NULL; n = n->next_hop){
        Alarm(DEBUG, "Currently sending from node %d to node %d\n", n->id, n->next_hop->id);
        dst = n->next_hop;
        bytes = sizeof(struct data_pkt) - MAX_PAYLOAD_SIZE + pkt->hdr.data_len;
        struct sockaddr_in ctrl_addr = dst->addr; //send to destination address
        // ctrl_addr.sin_port = htons(ntohs(dst->addr.sin_port)+1); //this is for Data Socket
        // ret = sendto(Overlay_Data_Sock, pkt, bytes, 0,
        //             (struct sockaddr *)&ctrl_addr,
        //             sizeof(ctrl_addr));
        ret = sendto(Overlay_Data_Sock, pkt, bytes, 0, (struct sockaddr *)&ctrl_addr, sizeof(ctrl_addr));
    }

    // E_attach_fd(Overlay_Ctrl_Sock, READ_FD, handle_overlay_ctrl, 0, NULL, MEDIUM_PRIORITY);
    // bytes = sizeof(struct data_pkt) - MAX_PAYLOAD_SIZE + pkt->hdr.data_len;
    // //Fix ctrl addr from Slack:
    // struct sockaddr_in ctrl_addr = dst->addr; //send to destination address
    // // ctrl_addr.sin_port = htons(ntohs(dst->addr.sin_port)+1); //this is for Data Socket
    // ret = sendto(Overlay_Data_Sock, pkt, bytes, 0,
    //              (struct sockaddr *)&ctrl_addr,
    //              sizeof(ctrl_addr));
    // ret = sendto(Overlay_Ctrl_Sock, pkt, bytes, 0,
    //              (struct sockaddr *)&c->data_remote_addr,
    //              sizeof(c->data_remote_addr));

}

/* Deliver packet to one of my local clients */
void deliver_locally(struct data_pkt *pkt)
{
    int path_len = 0;
    int bytes = 0;
    int ret = -1;
    struct client_conn *c = get_client_from_port(&Client_List, pkt->hdr.dst_port);

    /* Check whether we have a local client with this port to deliver to. If
     * not, nothing to do */
    if (c == NULL) {
        Alarm(PRINT, "overlay_node: received data for client that does not "
                     "exist! overlay node %d : client port %u\n",
                     pkt->hdr.dst_id, pkt->hdr.dst_port);
        return;
    }

    Alarm(DEBUG, "overlay_node: Delivering data locally to client with local "
                 "port %d\n", c->data_local_port);

    /* stamp packet so we can see the path taken */
    path_len = pkt->hdr.path_len;
    if (path_len < MAX_PATH) {
        pkt->hdr.path[path_len] = My_ID;
        pkt->hdr.path_len++;
    }

    /* Send data to client */
    bytes = sizeof(struct data_pkt) - MAX_PAYLOAD_SIZE + pkt->hdr.data_len;
    ret = sendto(c->data_sock, pkt, bytes, 0,
                 (struct sockaddr *)&c->data_remote_addr,
                 sizeof(c->data_remote_addr));
    if (ret < 0) {
        Alarm(PRINT, "Error sending to client with sock %d %d:%d\n",
              c->data_sock, c->data_local_port, c->data_remote_port);
        goto err;
    }

    return;

err:
    remove_client_with_sock(&Client_List, c->control_sock);
}

/* Handle incoming data message from another overlay node. Check whether we
 * need to deliver locally to a connected client, or forward to the next hop
 * overlay node */
void handle_overlay_data(int sock, int code, void *data)
{
    int bytes;
    struct data_pkt pkt;
    struct sockaddr_in recv_addr;
    socklen_t fromlen;

    Alarm(DEBUG, "overlay_node: received overlay data msg!\n");

    fromlen = sizeof(recv_addr);
    bytes = recvfrom(sock, &pkt, sizeof(pkt), 0, (struct sockaddr *)&recv_addr,
                     &fromlen);
    if (bytes < 0) {
        Alarm(EXIT, "overlay node: Error receiving overlay data: %s\n",
              strerror(errno));
    }

    /* If there is data to forward, find next hop and forward it */
    if (pkt.hdr.data_len > 0) {
        char tmp_payload[MAX_PAYLOAD_SIZE+1];
        memcpy(tmp_payload, pkt.payload, pkt.hdr.data_len);
        tmp_payload[pkt.hdr.data_len] = '\0';
        Alarm(DEBUG, "Got forwarded data packet of %d bytes: %s\n",
              pkt.hdr.data_len, tmp_payload);

        if (pkt.hdr.dst_id == My_ID) {
            deliver_locally(&pkt);
        } else {
            forward_data(&pkt);
        }
    }
}
void send_heartbeat(int src_id){
    Alarm(DEBUG, "Sending heartbeat with id: %d\n", src_id);
    struct heartbeat_pkt pkt; 
    int bytes = 0;
    pkt.hdr.type = CTRL_HEARTBEAT;
    pkt.hdr.src_id = src_id;

    //send to ctrl socket
    bytes = sizeof(struct heartbeat_pkt);
    //Fix ctrl addr from Slack:
    struct node* src, *dst; 
    
    int ret;
    src = get_node_from_id(&Node_List, pkt.hdr.src_id);
    //find the neighbors of src:
    //look through edge list:

    int i;
    struct edge* e;
    for (i = 0; i < Edge_List.num_edges; i++) {
        e = Edge_List.edges[i];
        if(e->src_id == src_id){
            //send with heartbeat_pkt
            pkt.hdr.dst_id = e->dst_id;
            dst = get_node_from_id(&Node_List, e->dst_id);
            struct sockaddr_in ctrl_addr = dst->addr; //send to destination address
            ctrl_addr.sin_port = htons(ntohs(dst->addr.sin_port)+1); //this is for Data Socket
            ret = sendto(Overlay_Ctrl_Sock, &pkt, bytes, 0, // wouldn't this be different in a control socket
                        (struct sockaddr *)&ctrl_addr,
                        sizeof(ctrl_addr));
        }
    }
    
    //send a heartbeat to each node:
    // then send packet to control socket
    // set timer for 1 second, check every second: 
    //E_queue() //enqueue for one second
    E_queue(send_heartbeat, My_ID, &pkt, Heartbeat_Timer);
}
/* Respond to heartbeat message by sending heartbeat echo */
void handle_heartbeat(struct heartbeat_pkt *pkt)
{
    if (pkt->hdr.type != CTRL_HEARTBEAT) {
        Alarm(PRINT, "Error: non-heartbeat msg in handle_heartbeat\n");
        return;
    }

    Alarm(DEBUG, "Got heartbeat from %d\n", pkt->hdr.src_id);

     /* Students fill in! */
     //Upon heartbeat: send heartbeat_echo to sender
     //handle timeout
    //send a heartbeat echo: doesn't need to have anything about neighbor
     //set timer in heartbeat_echo, and send echo periodically
    //should be a way which heartbeat it is echoing from
    //TODO: Handle timeout?
    //timer goes here 

    //before sending, check the time:
    //clock_t
    struct heartbeat_echo_pkt echo_pkt; 
    int bytes = 0;
    echo_pkt.hdr.type = CTRL_HEARTBEAT_ECHO;
    echo_pkt.hdr.src_id = pkt->hdr.src_id;

    //send to ctrl socket
    bytes = sizeof(struct heartbeat_echo_pkt);
    //Fix ctrl addr from Slack:
    struct node* src, *dst; 
    
    int ret;
    src = get_node_from_id(&Node_List, pkt->hdr.src_id);

    //for each node:
    int i;
    struct edge* e;
    // try to send heartbeat echo to neighbor:

    for (i = 0; i < Edge_List.num_edges; i++) {
        e = Edge_List.edges[i];
        if(e->src_id == pkt->hdr.src_id){
            //send with heartbeat_pkt
            echo_pkt.hdr.dst_id = e->dst_id;
            dst = get_node_from_id(&Node_List, e->dst_id);
            clock_t end = clock();
            double cpu_used = ((double) (end-clocks[e->dst_id]))/CLOCKS_PER_SEC;
            if(cpu_used >= 10.0){
                //update edge from A to B:
                e->link_state = LINK_DEAD;
                update_edge_from_id(&Edge_List, e->src_id, e->dst_id, *e);
                if(Route_Mode == MODE_LINK_STATE){
                     //send LSA packet
                    struct lsa_pkt lsa_pkt; 
                    bytes = 0;
                    lsa_pkt.hdr.type = CTRL_LSA;
                    lsa_pkt.hdr.src_id = pkt->hdr.src_id;

                    //send to ctrl socket
                    bytes = sizeof(struct heartbeat_echo_pkt);
                    //Fix ctrl addr from Slack:
                    src = get_node_from_id(&Node_List, pkt->hdr.src_id);
                    lsa_pkt.hdr.dst_id = e->dst_id;
                    //for each node:
                    struct sockaddr_in ctrl_addr = dst->addr; //send to destination address
                    ctrl_addr.sin_port = htons(ntohs(dst->addr.sin_port)+1); //this is for Data Socket
                    ret = sendto(Overlay_Ctrl_Sock, &lsa_pkt, bytes, 0, // wouldn't this be different in a control socket
                            (struct sockaddr *)&ctrl_addr,
                            sizeof(ctrl_addr));
                }else{
                    //send DV packet
                    struct dv_pkt dv_pkt; 
                    bytes = 0;
                    dv_pkt.hdr.type = CTRL_DV;
                    dv_pkt.hdr.src_id = pkt->hdr.src_id;

                    //send to ctrl socket
                    bytes = sizeof(struct heartbeat_echo_pkt);
                    //Fix ctrl addr from Slack:
                    src = get_node_from_id(&Node_List, pkt->hdr.src_id);
                    dv_pkt.hdr.dst_id = e->dst_id;
                    //for each node:
                    struct sockaddr_in ctrl_addr = dst->addr; //send to destination address
                    ctrl_addr.sin_port = htons(ntohs(dst->addr.sin_port)+1); //this is for Data Socket
                    ret = sendto(Overlay_Ctrl_Sock, &dv_pkt, bytes, 0, // wouldn't this be different in a control socket
                            (struct sockaddr *)&ctrl_addr,
                            sizeof(ctrl_addr));
                }
               
            }else{
                struct sockaddr_in ctrl_addr = dst->addr; //send to destination address
                ctrl_addr.sin_port = htons(ntohs(dst->addr.sin_port)+1); //this is for Data Socket
                ret = sendto(Overlay_Ctrl_Sock, &echo_pkt, bytes, 0, // wouldn't this be different in a control socket
                        (struct sockaddr *)&ctrl_addr,
                        sizeof(ctrl_addr));
            }
            
        }
    }
    // E_queue(handle_heartbeat_echo, 0, (struct heartbeat_echo_pkt *)pkt, Data_Timeout);
    //end timer goes there
    //measure difference, if >= 10s, then just KILL NODE, since node was killed, we now send LSA or DV packet
    handle_heartbeat_echo((struct heartbeat_echo_pkt *)pkt); //(don't call each other)
}

/* Handle heartbeat echo. This indicates that the link is alive, so update our
 * link weights and send update if we previously thought this link was down.
 * Push forward timer for considering the link dead */
void handle_heartbeat_echo(struct heartbeat_echo_pkt *pkt)
{
    if (pkt->hdr.type != CTRL_HEARTBEAT_ECHO) {
        Alarm(PRINT, "Error: non-heartbeat_echo msg in "
                     "handle_heartbeat_echo\n");
        return;
    }

    Alarm(DEBUG, "Got heartbeat_echo from %d\n", pkt->hdr.src_id);
    /* Students fill in! */
    //Upon heartbeat_echo: if link was previously dead, send update indicating that it is up; push forward timer for declaring link dead:
    //First get the edge:
    uint32_t src_id, dst_id;
    struct edge *e;
    src_id = pkt -> hdr.src_id;
    dst_id = pkt -> hdr.dst_id;
    //iterate through edge list:
    int i;
    for (i = 0; i < Edge_List.num_edges; i++) {
        e = Edge_List.edges[i];
        if(e->src_id == src_id && e->dst_id == dst_id){
            break;
        }
    }
    if(e->src_id != src_id || e->dst_id != dst_id){
        Alarm(PRINT, "Error: could not find edge connecting %d and %d!\n", src_id, dst_id);
        return;
    }
    clocks[pkt->hdr.dst_id] = clock();
    //TODO: What does it mean by pushing forward timer?
    if(e->link_state == LINK_DEAD){
        e->link_state = LINK_UP; //send update indicating it is up
        Alarm(PRINT, "Link is back up!\n");
        e->forward_timer++; //push forward timer for declaring link dead
    }else{
        Alarm(PRINT, "Link is up and reachable!\n");
    }
    //E_queue(handle_heartbeat_echo, 0, (struct heartbeat_echo_pkt *)pkt, Data_Timeout);
    //record time here
}

/* Process received link state advertisement */
void handle_lsa(struct lsa_pkt *pkt)
{
    if (pkt->hdr.type != CTRL_LSA) {
        Alarm(PRINT, "Error: non-lsa msg in handle_lsa\n");
        return;
    }

    if (Route_Mode != MODE_LINK_STATE) {
        Alarm(PRINT, "Error: LSA msg but not in link state routing mode\n");
    }

    Alarm(DEBUG, "Got lsa from %d\n", pkt->hdr.src_id);
    init_link_state(); //update
    //Update based on received packet...
    //TODO: One thing I don't understand is how the packet lends itself here, shouldn't it be the links that matter more?
     /* Students fill in! */
     //Update link state advertisement: if there is a change in the link weight, re-run route computation and update forwarding table; flood to neighbors
     
     //need to update node_list based on dijkstra's
}

/* Process received distance vector update */
void handle_dv(struct dv_pkt *pkt)
{
    if (pkt->hdr.type != CTRL_DV) {
        Alarm(PRINT, "Error: non-dv msg in handle_dv\n");
        return;
    }

    if (Route_Mode != MODE_DISTANCE_VECTOR) {
        Alarm(PRINT, "Error: Distance Vector Update msg but not in distance "
                     "vector routing mode\n");
    }

    Alarm(DEBUG, "Got dv from %d\n", pkt->hdr.src_id);

     /* Students fill in! */
     init_distance_vector(); //update
     //TODO: One thing I don't understand is how the packet lends itself here, shouldn't it be the links that matter more?
     /* Students fill in! */
     //Update link state advertisement: if there is a change in the link weight, re-run route computation and update forwarding table; flood to neighbors
     
     //Update distance vector update: if there is a change, re-run route computation and update forwarding table; if my distance vector changed, send to 
     //neighbors

}

/* Process received overlay control message. Identify message type and call the
 * relevant "handle" function */
void handle_overlay_ctrl(int sock, int code, void *data)
{
    char buf[MAX_CTRL_SIZE];
    struct sockaddr_in recv_addr;
    socklen_t fromlen;
    struct ctrl_hdr * hdr = NULL;
    int bytes = 0;

    Alarm(DEBUG, "overlay_node: received overlay control msg!\n");

    fromlen = sizeof(recv_addr);
    bytes = recvfrom(sock, buf, sizeof(buf), 0, (struct sockaddr *)&recv_addr,
                     &fromlen);
    if (bytes < 0) {
        Alarm(EXIT, "overlay node: Error receiving ctrl message: %s\n",
              strerror(errno));
    }
    hdr = (struct ctrl_hdr *)buf;

    /* sanity check */
    if (hdr->dst_id != My_ID) {
        Alarm(PRINT, "overlay_node: Error: got ctrl msg with invalid dst_id: "
              "%d\n", hdr->dst_id);
    }

    if (hdr->type == CTRL_HEARTBEAT) {
        /* handle heartbeat */
        handle_heartbeat((struct heartbeat_pkt *)buf);
    } else if (hdr->type == CTRL_HEARTBEAT_ECHO) {
        /* handle heartbeat echo */
        handle_heartbeat_echo((struct heartbeat_echo_pkt *)buf);
    } else if (hdr->type == CTRL_LSA) {
        /* handle link state update */
        handle_lsa((struct lsa_pkt *)buf);
    } else if (hdr->type == CTRL_DV) {
        /* handle distance vector update */
        handle_dv((struct dv_pkt *)buf);
    }
}

void handle_client_data(int sock, int unused, void *data)
{
    int ret, bytes;
    struct data_pkt pkt;
    struct sockaddr_in recv_addr;
    socklen_t fromlen;
    struct client_conn *c;

    Alarm(DEBUG, "Handle client data\n");
    
    c = (struct client_conn *) data;
    if (sock != c->data_sock) {
        Alarm(EXIT, "Bad state! sock %d != data sock\n", sock, c->data_sock);
    }

    fromlen = sizeof(recv_addr);
    bytes = recvfrom(sock, &pkt, sizeof(pkt), 0, (struct sockaddr *)&recv_addr,
                     &fromlen);
    if (bytes < 0) {
        Alarm(PRINT, "overlay node: Error receiving from client: %s\n",
              strerror(errno));
        goto err;
    }

    /* Special case: initial data packet from this client. Use it to set the
     * source port, then ack it */
    if (c->data_remote_port == 0) {
        c->data_remote_addr = recv_addr;
        c->data_remote_port = ntohs(recv_addr.sin_port);
        Alarm(DEBUG, "Got initial data msg from client with sock %d local port "
                     "%u remote port %u\n", sock, c->data_local_port,
                     c->data_remote_port);

        /* echo pkt back to acknowledge */
        ret = sendto(c->data_sock, &pkt, bytes, 0,
                     (struct sockaddr *)&c->data_remote_addr,
                     sizeof(c->data_remote_addr));
        if (ret < 0) {
            Alarm(PRINT, "Error sending to client with sock %d %d:%d\n", sock,
                  c->data_local_port, c->data_remote_port);
            goto err;
        }
    }

    /* If there is data to forward, find next hop and forward it */
    if (pkt.hdr.data_len > 0) {
        char tmp_payload[MAX_PAYLOAD_SIZE+1];
        memcpy(tmp_payload, pkt.payload, pkt.hdr.data_len);
        tmp_payload[pkt.hdr.data_len] = '\0';
        Alarm(DEBUG, "Got data packet of %d bytes: %s\n", pkt.hdr.data_len, tmp_payload);

        /* Set up header with my info */
        pkt.hdr.src_id = My_ID;
        pkt.hdr.src_port = c->data_local_port;

        /* Deliver / Forward */
        if (pkt.hdr.dst_id == My_ID) {
            deliver_locally(&pkt);
        } else {
            forward_data(&pkt);
        }
    }

    return;

err:
    remove_client_with_sock(&Client_List, c->control_sock);
    
}

void handle_client_ctrl_msg(int sock, int unused, void *data)
{
    int bytes_read = 0;
    int bytes_sent = 0;
    int bytes_expected = sizeof(struct conn_req_pkt);
    struct conn_req_pkt rcv_req;
    struct conn_ack_pkt ack;
    int ret = -1;
    int ret_code = 0;
    char * err_str = "client closed connection";
    struct sockaddr_in saddr;
    struct client_conn *c;

    Alarm(DEBUG, "Client ctrl message, sock %d\n", sock);

    /* Get client info */
    c = (struct client_conn *) data;
    if (sock != c->control_sock) {
        Alarm(EXIT, "Bad state! sock %d != data sock\n", sock, c->control_sock);
    }

    if (c == NULL) {
        Alarm(PRINT, "Failed to find client with sock %d\n", sock);
        ret_code = -1;
        goto end;
    }

    /* Read message from client */
    while (bytes_read < bytes_expected &&
           (ret = recv(sock, ((char *)&rcv_req)+bytes_read,
                       sizeof(rcv_req)-bytes_read, 0)) > 0) {
        bytes_read += ret;
    }
    if (ret <= 0) {
        if (ret < 0) err_str = strerror(errno);
        Alarm(PRINT, "Recv returned %d; Removing client with control sock %d: "
                     "%s\n", ret, sock, err_str);
        ret_code = -1;
        goto end;
    }

    if (c->data_local_port != 0) {
        Alarm(PRINT, "Received req from already connected client with sock "
                     "%d\n", sock);
        ret_code = -1;
        goto end;
    }

    /* Set up UDP socket requested for this client */
    if ((c->data_sock = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP)) < 0) {
        Alarm(PRINT, "overlay_node: client UDP socket error: %s\n", strerror(errno));
        ret_code = -1;
        goto send_resp;
    }

    /* set server address */
    memset(&saddr, 0, sizeof(saddr));
    saddr.sin_family = AF_INET;
    saddr.sin_addr.s_addr = INADDR_ANY;
    saddr.sin_port = htons(rcv_req.port);

    /* bind UDP socket */
    if (bind(c->data_sock, (struct sockaddr *)&saddr, sizeof(saddr)) < 0) {
        Alarm(PRINT, "overlay_node: client UDP bind error: %s\n", strerror(errno));
        ret_code = -1;
        goto send_resp;
    }

    /* Register socket with event handling system */
    ret = E_attach_fd(c->data_sock, READ_FD, handle_client_data, 0, c, MEDIUM_PRIORITY);
    if (ret < 0) {
        Alarm(PRINT, "Failed to register client UDP sock in event handling system\n");
        ret_code = -1;
        goto send_resp;
    }

send_resp:
    /* Send response */
    if (ret_code == 0) { /* all worked correctly */
        c->data_local_port = rcv_req.port;
        ack.id = My_ID;
    } else {
        ack.id = 0;
    }
    bytes_expected = sizeof(ack);
    Alarm(DEBUG, "Sending response to client with control sock %d, UDP port "
                 "%d\n", sock, c->data_local_port);
    while (bytes_sent < bytes_expected) {
        ret = send(sock, ((char *)&ack)+bytes_sent, sizeof(ack)-bytes_sent, 0);
        if (ret < 0) {
            Alarm(PRINT, "Send error for client with sock %d (removing...): "
                         "%s\n", sock, strerror(ret));
            ret_code = -1;
            goto end;
        }
        bytes_sent += ret;
    }

end:
    if (ret_code != 0 && c != NULL) remove_client_with_sock(&Client_List, sock);
}

void handle_client_conn(int sock, int unused, void *data)
{
    int conn_sock;
    struct client_conn new_conn;
    struct client_conn *ret_conn;
    int ret;

    Alarm(DEBUG, "Handle client connection\n");

    /* Accept the connection */
    conn_sock = accept(sock, NULL, NULL);
    if (conn_sock < 0) {
        Alarm(PRINT, "accept error: %s\n", strerror(errno));
        goto err;
    }

    /* Set up the connection struct for this new client */
    new_conn.control_sock     = conn_sock;
    new_conn.data_sock        = -1;
    new_conn.data_local_port  = 0;
    new_conn.data_remote_port = 0;
    ret_conn = add_client_to_list(&Client_List, new_conn);
    if (ret_conn == NULL) {
        goto err;
    }

    /* Register the control socket for this client */
    ret = E_attach_fd(new_conn.control_sock, READ_FD, handle_client_ctrl_msg,
                      0, ret_conn, MEDIUM_PRIORITY);
    if (ret < 0) {
        goto err;
    }

    return;

err:
    if (conn_sock >= 0) close(conn_sock);
}

void init_overlay_data_sock(int port)
{
    Overlay_Data_Sock = -1;
    // int sock = -1;
    int ret = -1;
    struct sockaddr_in saddr;

    if ((Overlay_Data_Sock = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP)) < 0) {
        Alarm(EXIT, "overlay_node: data socket error: %s\n", strerror(errno));
    }

    /* set server address */
    memset(&saddr, 0, sizeof(saddr));
    saddr.sin_family = AF_INET;
    saddr.sin_addr.s_addr = INADDR_ANY;
    saddr.sin_port = htons(port);

    /* bind listening socket */
    if (bind(Overlay_Data_Sock, (struct sockaddr *)&saddr, sizeof(saddr)) < 0) {
        Alarm(EXIT, "overlay_node: data bind error: %s\n", strerror(errno));
    }

    /* Register socket with event handling system */
    Alarm(PRINT, "Here I am about to use E_attach on handle_overlay_data!\n");
    ret = E_attach_fd(Overlay_Data_Sock, READ_FD, handle_overlay_data, 0, NULL, MEDIUM_PRIORITY);
    if (ret < 0) {
        Alarm(EXIT, "Failed to register overlay data sock in event handling system\n");
    }

}

void init_overlay_ctrl_sock(int port)
{
    Overlay_Ctrl_Sock = -1;
    // int sock = -1;
    int ret = -1;
    struct sockaddr_in saddr;

    if ((Overlay_Ctrl_Sock = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP)) < 0) {
        Alarm(EXIT, "overlay_node: ctrl socket error: %s\n", strerror(errno));
    }

    /* set server address */
    memset(&saddr, 0, sizeof(saddr));
    saddr.sin_family = AF_INET;
    saddr.sin_addr.s_addr = INADDR_ANY;
    saddr.sin_port = htons(port);

    /* bind listening socket */
    if (bind(Overlay_Ctrl_Sock, (struct sockaddr *)&saddr, sizeof(saddr)) < 0) {
        Alarm(EXIT, "overlay_node: ctrl bind error: %s\n", strerror(errno));
    }

    /* Register socket with event handling system */
    Alarm(PRINT, "Here I am about to use E_attach on handle_overlay_ctrl!\n");
    ret = E_attach_fd(Overlay_Ctrl_Sock, READ_FD, handle_overlay_ctrl, 0, NULL, MEDIUM_PRIORITY);
    if (ret < 0) {
        Alarm(EXIT, "Failed to register overlay ctrl sock in event handling system\n");
    }
}

void init_client_sock(int client_port)
{
    int client_sock = -1;
    int ret = -1;
    struct sockaddr_in saddr;

    if ((client_sock = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP)) < 0) {
        Alarm(EXIT, "overlay_node: client socket error: %s\n", strerror(errno));
    }

    /* set server address */
    memset(&saddr, 0, sizeof(saddr));
    saddr.sin_family = AF_INET;
    saddr.sin_addr.s_addr = INADDR_ANY;
    saddr.sin_port = htons(client_port);

    /* bind listening socket */
    if (bind(client_sock, (struct sockaddr *)&saddr, sizeof(saddr)) < 0) {
        Alarm(EXIT, "overlay_node: client bind error: %s\n", strerror(errno));
    }

    /* start listening */
    if (listen(client_sock, 32) < 0) {
        Alarm(EXIT, "overlay_node: client bind error: %s\n", strerror(errno));
        exit(-1);
    }

    /* Register socket with event handling system */
    ret = E_attach_fd(client_sock, READ_FD, handle_client_conn, 0, NULL, MEDIUM_PRIORITY);
    if (ret < 0) {
        Alarm(EXIT, "Failed to register client sock in event handling system\n");
    }

}

//On statup, the overlay needs to send initial link state and distance vector information on startup and schedule periodic events
void init_link_state()
{
    Alarm(DEBUG, "init link state\n");
    //given some start vertex:
    struct edge *e;
    for (int i = 0; i < Edge_List.num_edges; i++) {
        e = Edge_List.edges[i];
        Alarm(DEBUG, "Edge (%u, %u) : %u\n",
                e->src_id, e->dst_id, e->cost);
    }
    for(int start = 0; start < Node_List.num_nodes; start++){
        Alarm(DEBUG, "Running dijkstra's from starting node %d:\n", start);
        // int start = 0;
        bool inSPT[MAX_NODES] = {false}; //has the shortest path from start node to node i been found?
        for(int i = 0; i < Node_List.num_nodes; i++){
            Alarm(DEBUG, "inSPT[i] is: %d\n", inSPT[i]);
        }
        for(int i = 0; i < Node_List.num_nodes; i++){
            dist[start][i] = ~(1<<31); //max 32-bit int
        }
        dist[start][start] = 0;
        edge[start][start].edges[0] = NULL;
        // edge[start][start] = NULL;
        // consider neighbors of start:
            // for(int j = 0; j < Edge_List.num_edges; j++){
            //     struct edge* e;
            //     e = Edge_List.edges[j];
            //     Alarm(DEBUG, "Start is %d, and edge is: (%d,%d)\n", (start+1), e->src_id, e->dst_id);
            //     if((start+1) == e->src_id){ //edge relaxation
            //         Alarm(DEBUG, "Edge is: (%d,%d)\n", e->src_id, e->dst_id);
            //         dist[start][e->dst_id-1] = e->cost;
            //     }
            //     // else if(start == e->dst_id){
            //     //     dist[start][e->src_id] = e->cost;
            //     // }
            // }
        
    
        //We need to also consider if the link is UP or DOWN, if it is down, we have to ignore it in Dijkstra's
        for(int i = 0; i < Node_List.num_nodes; i++){ 
            int min_index;
            int min = (~(1<<31));

            for(int v = 0; v < Node_List.num_nodes; v++){
                if(inSPT[v] == false && dist[start][v] < min){
                    min = dist[start][v];
                    min_index = v;
                }
            }

            inSPT[min_index] = true;
            //now update dist value of all adjacent vertices of the chosen vertex:
            for(int j = 0; j < Edge_List.num_edges; j++){
                struct edge* e;
                e = Edge_List.edges[j];
                if(min_index == (e->src_id-1)){ //edge relaxation
                    //get the dest, and get cost, if the link is up, consider it valid
                    if(e->link_state == LINK_UP && inSPT[e->dst_id-1] == 0 && dist[start][min_index] != (~(1<<31)) && dist[start][min_index] + e->cost < dist[start][e->dst_id-1]){
                        dist[start][e->dst_id-1] = dist[start][min_index] + e->cost;
                        add_edge_to_list(&edge[start][e->dst_id-1], *e);
                        // add_edge_to_front(&edge[start][e->dst_id-1], *e); //add edge (min_index, e->dst_id) to edge list
                    }
                }
                // else if(min_index == (e->dst_id-1)){
                //     Alarm(DEBUG, "checking src edge %d: (%d, %d)\n", (min_index+1), e->src_id, e->dst_id);
                //     //get the src, and get cost:
                //     if(inSPT[e->src_id-1] == false && dist[start][min_index] != (~(1<<31)) && dist[start][min_index] + e->cost < dist[start][e->src_id-1]){
                //         dist[start][e->src_id-1] = dist[start][min_index] + e->cost < dist[start][e->src_id-1];
                //         add_edge_to_front(&edge[start], *e); //add edge (min_index, e->src_id) to edge list
                //     }
                // }
            }
            
        }
    }
    //Check dist matrix:
    for(int i = 0; i < Node_List.num_nodes; i++){
        Alarm(DEBUG, "Now we consider the SP from %d:\n", (i+1));
        for(int j = 0; j < Node_List.num_nodes; j++){
            Alarm(DEBUG, "SP from %d to %d is: %d\n", (i+1), (j+1), dist[i][j]);
        }
    }
    //Check edge matrix:
    for (int i = 0; i < Node_List.num_nodes; i++) {
        for(int j = 0; j < Node_List.num_nodes; j++){
            // Alarm(DEBUG, "Edge List from node %d to node %d: \n", (i+1), (j+1));
            // struct edge *e_init;
            // e_init = edge[i][j].edges[0];
            // if(e_init != NULL){
            //     Alarm(DEBUG, "Initial edge (%u, %u) : %u\n",
            //             e_init->src_id, e_init->dst_id, e_init->cost);
            // }
            

            // struct edge* e;
            // e = Edge_List.edges[j];
            struct edge_list temp_list = {0};
            int cnt = 0;
            for(struct edge* e = edge[i][j].edges[0]; e != NULL;  e = edge[i][e->src_id-1].edges[0]){
                Alarm(DEBUG, "Adding edge (%u, %u) : %u\n",
                        e->src_id, e->dst_id, e->cost);
                add_edge_to_front(&temp_list, *e);
                cnt++;
            }
            Alarm(DEBUG, "SIZE SHOULD BE: %d\n", temp_list.num_edges);
            Alarm(DEBUG, "Edge List from node %d to node %d: \n", (i+1), (j+1));
            struct edge* e;
            for(int k = 0; k < temp_list.num_edges; k++){
                e = temp_list.edges[k];
                struct node* src = get_node_from_id(&Node_List, e->src_id);
                struct node* dst = get_node_from_id(&Node_List, e->dst_id);
                // memcpy(src, get_node_from_id(&Node_List, e->src_id), sizeof(struct node));
                // memcpy(dst, get_node_from_id(&Node_List, e->dst_id), sizeof(struct node));
                // src->next_hop = strdup(get_node_from_id(&Node_List, e->src_id)->next_hop);
                // dst->next_hop = strdup(get_node_from_id(&Node_List, e->dst_id)->next_hop);

                // struct node* src = strdup(get_node_from_id(&Node_List, e->src_id));
                // struct node* dst = strdup(get_node_from_id(&Node_List, e->dst_id));
                // src->next_hop = dst;
                Alarm(DEBUG, "Adding node %d with next hop %d\n", src->id, dst->id);
                if(k == 0){
                    Alarm(DEBUG, "First iteration, add src node %d\n", src->id);
                    add_node_to_list(&node[i][j], *src);
                    // add_node_to_list(&node[src->id-1][dst->id-1], *src);
                }
                Alarm(DEBUG, "Other iteration, add dst node %d\n", dst->id);
                add_node_to_list(&node[i][j], *dst);

                //add_node_to_list(&node[src->id-1][dst->id-1], *dst);
                // src->next_hop = dst;
                // update_node_from_id(&Node_List, e->src_id, *src); //set next hops
                Alarm(DEBUG, "Edge (%u, %u) : %u\n",
                        e->src_id, e->dst_id, e->cost);
            }


        }
        //Let's look at the list we just added to:

        // Alarm(DEBUG, "Let's show all the nodes, and their next hops!\n");
        // for(int i = 0; i < Node_List.num_nodes; i++){
        //     for(int j = 0; j < Node_List.num_nodes; j++){
        //         Alarm(DEBUG, "Node List for node %d to node %d, which has %d nodes:\n", (i+1), (j+1), node[i][j].num_nodes);
        //         for(int k = 0; k < node[i][j].num_nodes; k++){
        //             struct node* src = node[i][j].nodes[k];
        //             Alarm(DEBUG, "(%d) ", src->id);
        //             // if(src->next_hop != NULL){
        //             //     Alarm(DEBUG, "Node and its next hop is: %d, %d\n", src->id, src->next_hop->id);
        //             // }
        //         }
        //         Alarm(DEBUG, "\n");
        //     }
            
        // }
        Alarm(DEBUG, "\n");
       
    }
    Alarm(DEBUG, "Let's show all the nodes, and their next hops!\n");
    for(int i = 0; i < Node_List.num_nodes; i++){
        for(int j = 0; j < Node_List.num_nodes; j++){
            Alarm(DEBUG, "Node List for node %d to node %d, which has %d nodes:\n", (i+1), (j+1), node[i][j].num_nodes);
            for(int k = 0; k < node[i][j].num_nodes-1; k++){
                struct node* src = node[i][j].nodes[k];
                src->next_hop = node[i][j].nodes[k+1];
                // if(src->next_hop != NULL){
                    Alarm(DEBUG, "Node and its next hop is: %d, %d\n", src->id, src->next_hop->id);
                // }
            }
            Alarm(DEBUG, "\n");
        }
        
    }

}

void init_distance_vector()
{
    Alarm(DEBUG, "init distance vector\n");
    //Not enough time to do this, but should be something like:
    /*
    struct node
    {
        unsigned dist[20];
        unsigned from[20];
    }rt[10];
    int costmat[20][20];
    int nodes,i,j,k,count=0;

    // for(i=0;i<Node_List.num_nodes;i++)
    // {
    //     for(j=0;j<Node_List.num_nodes;j++)
    //     {
    //         scanf("%d",&costmat[i][j]);
    //         costmat[i][i]=0;
    //         rt[i].dist[j]=costmat[i][j];//initialise the distance equal to cost matrix
    //         rt[i].from[j]=j;
    //     }
    // }
        do
        {
            count=0;
            for(i=0;i<Node_List.num_nodes;i++)//We choose arbitary vertex k and we calculate the direct distance from the node i to k using the cost matrix
            //and add the distance from k to node j
            for(j=0;j<Node_List.num_nodes;j++)
            for(k=0;k<Node_List.num_nodes;k++)
                if(rt[i].dist[j]>costmat[i][k]+rt[k].dist[j])
                {//We calculate the minimum distance
                    rt[i].dist[j]=rt[i].dist[k]+rt[k].dist[j];
                    rt[i].from[j]=k;
                    count++;
                }
        }while(count!=0);
        for(i=0;i<nodes;i++)
        {
            printf("\n\n For router %d\n",i+1);
            for(j=0;j<nodes;j++)
            {
                printf("\t\nnode %d via %d Distance %d ",j+1,rt[i].from[j]+1,rt[i].dist[j]);
            }
        }

    */
}

uint32_t ip_from_str(char *ip)
{
    struct in_addr addr;

    inet_pton(AF_INET, ip, &addr);
    return ntohl(addr.s_addr);
}

void process_conf(char *fname, int my_id)
{
    char     buf[MAX_CONF_LINE];
    char     ip_str[MAX_CONF_LINE];
    FILE *   f        = NULL;
    uint32_t id       = 0;
    uint16_t port     = 0;
    uint32_t src      = 0;
    uint32_t dst      = 0;
    uint32_t cost     = 0;
    int node_sec_done = 0;
    int ret           = -1;
    struct node n;
    struct edge e;
    struct node *retn = NULL;
    struct edge *rete = NULL;

    Alarm(DEBUG, "Processing configuration file %s\n", fname);

    /* Open configuration file */
    f = fopen(fname, "r");
    if (f == NULL) {
        Alarm(EXIT, "overlay_node: error: failed to open conf file %s : %s\n",
              fname, strerror(errno));
    }

    /* Read list of nodes from conf file */
    while (fgets(buf, MAX_CONF_LINE, f)) {
        Alarm(DEBUG, "Read line: %s", buf);

        if (!node_sec_done) {
            // sscanf
            ret = sscanf(buf, "%u %s %hu", &id, ip_str, &port);
            Alarm(DEBUG, "    Node ID: %u, Node IP %s, Port: %u\n", id, ip_str, port);
            if (ret != 3) {
                Alarm(DEBUG, "done reading nodes\n");
                node_sec_done = 1;
                continue;
            }

            if (id == my_id) {
                Alarm(DEBUG, "Found my ID (%u). Setting IP and port\n", id);
                My_Port = port;
                My_IP = ip_from_str(ip_str);
            }

            n.id = id;
            memset(&n.addr, 0, sizeof(n.addr));
            n.addr.sin_family = AF_INET;
            n.addr.sin_addr.s_addr = htonl(ip_from_str(ip_str));
            n.addr.sin_port = htons(port);
            n.next_hop = NULL;
            retn = add_node_to_list(&Node_List, n);
            if (retn == NULL) {
                Alarm(EXIT, "Failed to add node to list\n");
            }

        } else { /* Edge section */
            ret = sscanf(buf, "%u %u %u", &src, &dst, &cost);
            Alarm(DEBUG, "    Src ID: %u, Dst ID %u, Cost: %u\n", src, dst, cost);
            if (ret != 3) {
                Alarm(DEBUG, "done reading nodes\n");
                node_sec_done = 1;
                continue;
            }

            e.src_id = src;
            e.dst_id = dst;
            e.cost = cost;
            e.link_state = LINK_UP;
            e.forward_timer = 0;
            e.src_node = get_node_from_id(&Node_List, e.src_id);
            e.dst_node = get_node_from_id(&Node_List, e.dst_id);
            if (e.src_node == NULL || e.dst_node == NULL) {
                Alarm(EXIT, "Failed to find node for edge (%u, %u)\n", src, dst);
            }
            rete = add_edge_to_list(&Edge_List, e);
            if (rete == NULL) {
                Alarm(EXIT, "Failed to add edge to list\n");
            }
        }
    }
}

int 
main(int argc, char ** argv) 
{

    char * conf_fname    = NULL;

    if (PRINT_DEBUG) {
        Alarm_set_types(DEBUG);
    }

    /* parse args */
    if (argc != 4) {
        Alarm(EXIT, "usage: overlay_node <id> <config_file> <mode: LS/DV>\n");
    }

    My_ID      = atoi(argv[1]);
    conf_fname = argv[2];

    if (!strncmp("LS", argv[3], 3)) {
        Route_Mode = MODE_LINK_STATE;
    } else if (!strncmp("DV", argv[3], 3)) {
        Route_Mode = MODE_DISTANCE_VECTOR;
    } else {
        Alarm(EXIT, "Invalid mode %s: should be LS or DV\n", argv[5]);
    }

    Alarm(DEBUG, "My ID             : %d\n", My_ID);
    Alarm(DEBUG, "Configuration file: %s\n", conf_fname);
    Alarm(DEBUG, "Mode              : %d\n\n", Route_Mode);

    process_conf(conf_fname, My_ID);
    Alarm(DEBUG, "My IP             : "IPF"\n", IP(My_IP));
    Alarm(DEBUG, "My Port           : %u\n", My_Port);

    { /* print node and edge lists from conf */
        int i;
        struct node *n;
        struct edge *e;
        for (i = 0; i < Node_List.num_nodes; i++) {
            n = Node_List.nodes[i];
            Alarm(DEBUG, "Node %u : "IPF":%u\n", n->id,
                  IP(ntohl(n->addr.sin_addr.s_addr)),
                  ntohs(n->addr.sin_port));
        }

        for (i = 0; i < Edge_List.num_edges; i++) {
            e = Edge_List.edges[i];
            Alarm(DEBUG, "Edge (%u, %u) : "IPF":%u -> "IPF":%u\n",
                  e->src_id, e->dst_id,
                  IP(ntohl(e->src_node->addr.sin_addr.s_addr)),
                  ntohs(e->src_node->addr.sin_port),
                  IP(ntohl(e->dst_node->addr.sin_addr.s_addr)),
                  ntohs(e->dst_node->addr.sin_port));
        }
    }
    
    /* Initialize event system */
    E_init();

    /* Set up TCP socket for client connection requests */
    init_client_sock(My_Port);

    /* Set up UDP sockets for sending and receiving messages from other
     * overlay nodes */
    init_overlay_data_sock(My_Port);
    init_overlay_ctrl_sock(My_Port+1);

    if (Route_Mode == MODE_LINK_STATE) {
        init_link_state();
    } else {
        init_distance_vector();
    }
    

    //How to use E_queue ()
    //Set up heartbeats
    send_heartbeat(My_ID);
    /* Enter event handling loop */
    Alarm(DEBUG, "Entering event loop!\n");
    
    E_handle_events();

    return 0;
}
