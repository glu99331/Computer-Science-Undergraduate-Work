/*
 * CS 1652 Project 1 
 * (c) Jack Lange, 2020
 * (c) Amy Babay, 2022
 * (c) Gordon Lu, 2022 
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
#include <ctype.h>

#include <netinet/in.h>
#include <arpa/inet.h>
#include <sys/socket.h>

#define BUFSIZE 1024
#define FILENAMESIZE 100

/* 
    4. Do the following repeatedly:
        a. Accept a new connection on the accept socket (When does accept return? Is your
        process consuming cycles while it is in accept?) Accept will return a new socket
        for the connection. We'll call this new socket the connection socket. (What is the
        5-tuple describing the connection?)

        b. Read the HTTP request from the connection socket and parse it. (How do you
        know how many bytes to read?)
    
        c. Check to see if the file requested exists.
    
        d. If the file exists, construct the appropriate HTTP response (What's the right
        number?), write it to the connection socket, and then open the file and write its
        contents to the connection socket.

        e. If the file doesn't exist, construct a HTTP error response and write it back to the
        connection socket
    
        f. Close the connection socket.
*/
static int handle_connection(int connectionSocket){
    /*header declaration*/
    char * ok_response_f  = "HTTP/1.0 200 OK\r\n"        \
        					"Content-type: text/plain\r\n"                  \
        					"Content-length: %d \r\n\r\n";
 
    char * notok_response = "HTTP/1.0 404 FILE NOT FOUND\r\n"   \
        					"Content-type: text/html\r\n\r\n"                       \
        					"<html><body bgColor=black text=white>\n"               \
        					"<h2>404 FILE NOT FOUND</h2>\n"
        					"</body></html>\n";
    
    /*variable declarations*/
    int len;
    char buf[BUFSIZE];
    bool ok_response;
    int res;
    char write_buf[BUFSIZE];

    // a. Accept a new connection on the accept socket (When does accept return? Is your
    //     process consuming cycles while it is in accept?) Accept will return a new socket
    //     for the connection. We'll call this new socket the connection socket. (What is the
    //     5-tuple describing the connection?)
    if( connectionSocket >= 0) //if accept returned a nonnegative value, we can read the http request
    {
        printf("Client successfully connected to server!\n" );
        // b. Read the HTTP request from the connection socket and parse it. (How do you
        // know how many bytes to read?)
        if ((len = read(connectionSocket, buf, BUFSIZE - 1)) <= 0) //read data from connectionSocket
        {
            perror("tcp_server: recv error");
            exit(-1);
        }

        buf[len] = '\0';
        printf("Received %d bytes: %s\n", len, buf);
        
        char * fName;
        fName = (char*)calloc(FILENAMESIZE, sizeof(char)); 
        // parse file name
        strtok(buf, " ");
        char* file = strtok(NULL, " ");
        strcpy(fName, (file+1)); //get filename
      
        printf("The client requested the following file: %s\n", fName);
        // c. Check to see if the file requested exists.
        FILE *fp = fopen(fName, "rb");

        if (fp == NULL){
            fprintf(stderr, "An error occurred when trying to open the requested file:%s\n", fName);
            ok_response = false; //return 404 FILE NOT FOUND
        }
        else{
            printf("Successfully opened file!\n");
            ok_response = true; //return 200 OK
            free(fName); //don't need file name anymore
        }
        /*if the file was found, send the appropriate HTTP header with the contents of the file*/
        if (ok_response){
            // d. If the file exists, construct the appropriate HTTP response (What's the right
            // number?), write it to the connection socket, and then open the file and write its
            // contents to the connection socket.

            // e. If the file doesn't exist, construct a HTTP error response and write it back to the
            // connection socket

            //okresponse with sprintf
<<<<<<< HEAD

            //get size of file:
=======
>>>>>>> 45ef234bdaffe0d5e8e3356874654ca76dc351b3
            fseek(fp, 0, SEEK_END);
            int fsize = ftell(fp);
            fseek(fp, 0, SEEK_SET);
            sprintf(write_buf, ok_response_f, fsize);
<<<<<<< HEAD
            printf("hi: %s\n", write_buf);
=======

>>>>>>> 45ef234bdaffe0d5e8e3356874654ca76dc351b3
            if ((res = write(connectionSocket, write_buf, strlen(write_buf)+1)) <= 0){ //why is there a @ sign at the beginning
                fprintf(stderr, "Error: Failed to send the HTTP response to socket!\n");
                //close the socket here??
                exit(-1);
            }

            //append to buf
            char *toConn = (char*)calloc(BUFSIZE, sizeof(char));
            memset(buf, 0, sizeof(buf)); //clear contents of buf
            fgets(buf, BUFSIZE, fp); //make sure we have something in there first so we don't segfault
            strcpy(toConn, buf);
            while (fgets(buf, BUFSIZE, fp) != NULL) {
                strcat(toConn, buf); 
            }
            //Then send the contents to the connection socket
            // /*sends the contents of the finle into the socket*/
            if ((res = write(connectionSocket, toConn, strlen(toConn))) <= 0){
                fprintf(stderr, "Error: Failed to write out the contents of the file to the socket!\n");
                //Close the socket and free toConn/
                exit(-1);
            }
        }
        //otherwise send HTTP header with 404 FILE NOT FOUND
        else {
            //why is there a random @ sign at the beginning
            if ((res = write(connectionSocket, notok_response, strlen(notok_response))) <= 0){
                fprintf(stderr, "Error: Failed to send the HTTP response to socket\n");
                //Close the socket 
                exit(-1);
            }
        }

        printf("Client request successfully processed, now sending response...\n");
        //now close the connection socket:
        close(connectionSocket);
        
    }
    /* close socket and free pointers */
    // close(connectionSocket); //is this necessary is connectionSocket < 0?
    return 0;
}
int main(int argc, char ** argv){
    int server_port = -1;
    /* parse command line args */
    if (argc != 2) {
        fprintf(stderr, "usage: http_server1 port\n");
        exit(-1);
    }
    server_port = atoi(argv[1]);
    // Check if the user enters a valid port:
    // Case I: User enters a port number larger than max unsigned int for 16 bits (2^16 - 1)
    if(server_port > 65535){
        fprintf(stderr, "Error: The provided port number (%d) cannot exceed max unsigned 16-bit integer!\n", server_port);
        exit(-1);
    }
    // Case II: User enters a port number less than 1500 (reserved)
    if (server_port < 1500) {
        fprintf(stderr, "Error: The provided port number (%d) cannot be < 1500 (reserved)! \n", server_port);
        exit(-1);
    }
    
    /* 
         1. Create a TCP socket to listen for new connections on (What packet family and type
            should you use?
            - Stream socket allows communication through TCP (SOCK_STREAM)
            - Establish protocol family as Internet domain (AF_INET) 
            - Use TCP protocol (IPPROTO_TCP)
    */

    /* 
        Negative socket() value implies one of the following;
        1. EACCES Permission to create a socket of the specified type and/or
              protocol is denied.
        2. EAFNOSUPPORT
                The implementation does not support the specified address
                family.
        3. EINVAL 
                Unknown protocol, or protocol family not available.
        4. EINVAL 
                Invalid flags in type.
        5. EMFILE 
                The per-process limit on the number of open file
                descriptors has been reached.
        6. ENFILE 
                The system-wide limit on the total number of open files
                has been reached.
        7. ENOBUFS or ENOMEM
                Insufficient memory is available.  The socket cannot be
                created until sufficient resources are freed.
        8. EPROTONOSUPPORT
                The protocol type or the specified protocol is not
                supported within this domain.
    */
    int s;
    s = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);

    if (s < 0){
        fprintf(stderr, "Error: Failed to create TCP socket!\n");
        exit(-1);
    }

    /* set server address*/
    struct sockaddr_in saddr;
    memset(&saddr, 0, sizeof(saddr));
    saddr.sin_family = AF_INET;
    saddr.sin_addr.s_addr = INADDR_ANY;
    saddr.sin_port = htons(server_port);

    /* 
       2. Bind that socket to the port provided on the command line. We'll call this socket the
          accept socket. 

       Description of bind(int sockfd, const struct sockaddr *addr,
                socklen_t addrlen) method:
            When a socket is created with socket(2), it exists in a name
            space (address family) but has no address assigned to it. 
            bind() assigns the address specified by addr to the socket 
            referred to by the file descriptor sockfd.  
            - addrlen specifies the size, in bytes, of the address structure 
            pointed to by addr.
            - Traditionally, this operation is called "assigning a name to a
            socket".
    */

    /* 
        Negative bind() value implies one of the following:
        1. EACCES 
                The address is protected, and the user is not the
                superuser.
        2. EADDRINUSE
                The given address is already in use.
        3. EADDRINUSE
                (Internet domain sockets) The port number was specified as
                zero in the socket address structure, but, upon attempting
                to bind to an ephemeral port, it was determined that all
                port numbers in the ephemeral port range are currently in
                use.  
        4. EBADF  
                sockfd is not a valid file descriptor.
        5. EINVAL 
                The socket is already bound to an address.
        6. EINVAL 
                addrlen is wrong, or addr is not a valid address for this
                socket's domain.
        7. ENOTSOCK
                The file descriptor sockfd does not refer to a socket.
    */
   
    if (bind(s, (struct sockaddr *) &saddr, sizeof(saddr)) < 0){
        fprintf(stderr, "Error: Failed to bind socket!\n");
        close(s); // Since we couldn't bind the socket, we need to close the socket
        exit(-1); // Now exit with return code of -1
    }
    //Once binded, our socket is now the acceptSocket!

    /* 
        3. Listen on the accept socket (What will happen if you use a small backlog versus a larger
           backlog? What if you set the backlog to zero?) 

        Description of listen(int sockfd, int backlog) method:
            listen() marks the socket referred to by sockfd as a passive
            socket, that is, as a socket that will be used to accept incoming
            connection requests using accept(2).

            The backlog argument defines the maximum length to which the
            queue of pending connections for sockfd may grow.
            - Arbitrarily select 32 as backlog 
            - If backlog is small then the connection will be slow
            - Otherwise the backlog, then connections will be relatively quick
    */
    /* 
        Negative listen value implies one of the following:
        1. EADDRINUSE
                Another socket is already listening on the same port.
        2. EADDRINUSE
                (Internet domain sockets) The socket referred to by sockfd
                had not previously been bound to an address and, upon
                attempting to bind it to an ephemeral port, it was
                determined that all port numbers in the ephemeral port
                range are currently in use.  See the discussion of
                /proc/sys/net/ipv4/ip_local_port_range in ip(7).
        3. EBADF 
                The argument sockfd is not a valid file descriptor.
        4. ENOTSOCK
                The file descriptor sockfd does not refer to a socket.
        5. EOPNOTSUPP
                The socket is not of a type that supports the listen()
                operation.
    */
    if (listen(s, 32) < 0){
        fprintf(stderr, "Error: Failed to listen on the accept socket!\n ");
        close(s); //Since we failed to listen on the socket, we should close the socket and exit with -1.
        exit(-1);
    }
    //At this point the socket is now ready to accept connections!

    /* 
        4. Do the following repeatedly:
            a. Accept a new connection on the accept socket (When does accept return? Is your
            process consuming cycles while it is in accept?) Accept will return a new socket
            for the connection. We'll call this new socket the connection socket. (What is the
            5-tuple describing the connection?)
    
            b. Read the HTTP request from the connection socket and parse it. (How do you
            know how many bytes to read?)
        
            c. Check to see if the file requested exists.
        
            d. If the file exists, construct the appropriate HTTP response (What's the right
            number?), write it to the connection socket, and then open the file and write its
            contents to the connection socket.
    
            e. If the file doesn't exist, construct a HTTP error response and write it back to the
            connection socket
        
            f. Close the connection socket.
    */
    printf("Waiting for client to connect: \n");
    int retVal = 0;
    while (1) //Only stop accepting connections until server owner decides to close server (CTRL + C) 
    { 
        
    
        /*
        int accept(int sockfd, struct sockaddr *restrict addr,
                   socklen_t *restrict addrlen);

        The accept() system call is used with connection-based socket
        types (SOCK_STREAM, SOCK_SEQPACKET).  It extracts the first
        connection request on the queue of pending connections for the
        listening socket, sockfd, creates a new connected socket, and
        returns a new file descriptor referring to that socket.  The
        newly created socket is not in the listening state.  The original
        socket sockfd is unaffected by this call.

        The argument sockfd is a socket that has been created with
        socket(2), bound to a local address with bind(2), and is
        listening for connections after a listen(2).

        The argument addr is a pointer to a sockaddr structure.  This
        structure is filled in with the address of the peer socket, as
        known to the communications layer.  The exact format of the
        address returned addr is determined by the socket's address
        family (see socket(2) and the respective protocol man pages).
        When addr is NULL, nothing is filled in; in this case, addrlen is
        not used, and should also be NULL.

        The addrlen argument is a value-result argument: the caller must
        initialize it to contain the size (in bytes) of the structure
        pointed to by addr; on return it will contain the actual size of
        the peer address.

        The returned address is truncated if the buffer provided is too
        small; in this case, addrlen will return a value greater than was
        supplied to the call.

        If no pending connections are present on the queue, and the
        socket is not marked as nonblocking, accept() blocks the caller
        until a connection is present.  If the socket is marked
        nonblocking and no pending connections are present on the queue,
        accept() fails with the error EAGAIN or EWOULDBLOCK.

        */
        int connectionSocket = accept(s, NULL, NULL);
        retVal = handle_connection(connectionSocket); // Leave handling connections to handle_connection method
        if (retVal  < 0){
            fprintf(stderr, "Error: User failed to connect to server!\n");
            // close(s); Since something went wrong in handling user connections to the server, should we should close the socket?   
        }
        close(s);
    }
}