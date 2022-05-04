/*
 * CS 1652 Project 1 
 * (c) Jack Lange, 2020
 * (c) Amy Babay, 2022
 * (c) Gordon Lu
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

#define BUFSIZE 1024

int 
main(int argc, char ** argv) 
{

    char * server_name = NULL;
    int    server_port = -1;
    char * server_path = NULL;
    char * req_str     = NULL;

    int ret = 0;

    /*parse args */
    // Parameter checking
    if (argc != 4) {
        fprintf(stderr, "usage: http_client <hostname> <port> <path>\n");
        exit(1);
    }

    server_name = argv[1];
    server_port = atoi(argv[2]);
    server_path = argv[3];


    // returns number of bytes
    // remember to free req_str
    // request has 3 fields: method, URL, and HTTP version
    ret = asprintf(&req_str, "GET /%s HTTP/1.0\r\n\r\n", server_path);

    if (ret == -1) {
        fprintf(stderr, "Failed to allocate request string\n");
        exit(1);
    }


    /* make socket */
    int s;
    if ((s = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP)) < 0) {
        fprintf(stderr, "Socket creation failed.\n");
        exit(1);
    }

    /* get host IP address  */
    /* Hint: use gethostbyname() */
    struct hostent *hp;
    if ((hp = gethostbyname(server_name)) == NULL) {
        fprintf(stderr, "ERROR, no such host.\n");
        close(s);
        exit(1);
    }

    /* set address */
    struct sockaddr_in saddr;
    saddr.sin_family = AF_INET;
    memcpy(&saddr.sin_addr.s_addr, hp->h_addr, hp->h_length);
    saddr.sin_port = htons(server_port);


    /* connect to the server socket */
    if (connect(s, (struct sockaddr *) &saddr, sizeof(saddr)) < 0) {
        fprintf(stderr, "Error connecting!\n");
        close(s);
        exit(1);
    }

    /* send request message */
    int res;
    if ((res = write(s, req_str, strlen(req_str))) <= 0) {
        fprintf(stderr, "Error sending request!");
        close(s);
        exit(1);
    }

    /* wait till socket can be read. */
    /* Hint: use select(), and ignore timeout for now. */
    fd_set read_mask;
    FD_ZERO(&read_mask); 
    FD_SET(s, &read_mask); 
    select(s + 1, &read_mask, NULL, NULL, NULL);

    /* first read loop -- read headers */
    char buf[BUFSIZE];
    if ((res = read(s, &buf, BUFSIZE - 1)) <= 0) {
        fprintf(stderr, "Error reading from socket!\n");
        close(s);
        exit(1);
    }

    buf[res] = 0;
    
    char* EOH = strstr(buf, "\r\n\r\n");
    char EOH_copy[BUFSIZE];
    strcpy(EOH_copy, EOH);
    EOH[2] = 0;
    // char *data = strstr(buf, "\r\n\r\n");
    char* curr_line = buf;
    char* EOL = strstr(curr_line, "\r\n");
    int status = 0;

    char headers[BUFSIZE];
    int lineNum = 0;
    while(EOL != NULL){
        *EOL = 0;
        char ptr[BUFSIZE];
        if((strstr(EOL+2, "\r\n")) != NULL){ //does the next line contain "\r\n"
            strcpy(ptr, strcat(curr_line, "\n"));
        }else{
            strcpy(ptr, curr_line); //if not then we don't insert that extra new line
        }
        strcat(headers, ptr);

        if(lineNum == 0){
            strtok(ptr, " ");
            status = atoi(strtok(NULL, " "));
        }
        curr_line = EOL + 2; //to get to next line
        EOL = strstr(curr_line, "\r\n"); //progress EOL
        lineNum++; //line num simply used to track if at last line or not
        buf[res] = 0;
    }

    char *body = (char *) malloc(BUFSIZE);
    strcpy(body, EOH_copy);
    while(res > 0){
        select(s + 1, &read_mask, NULL, NULL, NULL);
        res = read(s, &buf, BUFSIZE - 1);
        buf[res] = 0;

        body = realloc(body, strlen(body)+strlen(buf)); //read the body
        strcat(body, buf);
    }
    //this feels really hacky
    if(status != 200){
        fprintf(stderr, "%s", headers);
        fprintf(stderr, "%s\n", body);
        close(s);
        free(body);
        return -1;
    } else{
        printf("%s", headers);
        printf("%s\n", body);
        close(s);
        free(body);
        return 0;
    }

    /* Want something like: 
    HTTP/1.1 200 OK
    Date: Mon, 07 Feb 2022 01:35:27 GMT
    Server: Apache/2.2.24 (Unix) mod_ssl/2.2.24 OpenSSL/1.0.1e-fips PHP/7.2.23 mod_pubcookie/3.3.4a mod_uwa/3.2.1
    Last-Modified: Mon, 07 Feb 2022 00:47:10 GMT
    ETag: "f0aaa-10381-5d762f0c89f80"
    Accept-Ranges: bytes
    Content-Length: 66433
    Vary: Accept-Encoding,User-Agent
    Connection: close
    Content-Type: text/html
    */
    //"\r\n\r\n"
    /* examine return code */   
    // Skip protocol version (e.g. "HTTP/1.0")
    // Normal reply has return code 200

    /* print first part of response: header, error code, etc. */

    /* second read loop -- print out the rest of the response: real web content */

    /* close socket */

}
