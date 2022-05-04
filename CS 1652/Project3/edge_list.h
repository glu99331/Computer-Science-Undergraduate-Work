/*
 * CS 1652 Project 3 
 * (c) Amy Babay, 2022
 * (c) <Student names here>
 * 
 * Computer Science Department
 * University of Pittsburgh
 */

#ifndef __EDGE_LIST_H__
#define __EDGE_LIST_H__

#include "node_list.h"

#define MAX_EDGES 500


/* The type field in the struct edge should be one of these values to
 * identify whether it is dead or not */

#define LINK_DEAD 1
#define LINK_UP 0

struct edge {
    uint32_t      src_id;
    uint32_t      dst_id;
    uint32_t      cost;
    uint32_t      link_state;
    uint32_t      forward_timer;
    struct node * src_node;
    struct node * dst_node;
};

struct edge_list {
    struct edge *edges[MAX_EDGES];
    int num_edges;
};

/* Add edge to edge list.
 *     returns pointer to edge if added successfully, NULL if list is full.
 *   Note that this functions creates a new edge struct and copies the edge
 *   param passed in into it */
struct edge * add_edge_to_list(struct edge_list *list, struct edge e);

/* Find and update edge with given src and dest.
 *     returns pointer to edge if found successfully, NULL if not found */
struct edge *update_edge_from_id(struct edge_list *list, uint32_t src, uint32_t dst, struct edge e);

/* Find edge with given src and dest.
 *     returns pointer to edge if found successfully, NULL if not found */
struct edge *find_edge_from_id(struct edge_list *list, uint32_t src, uint32_t dst);

/* Add edge to edge list.
 *     returns pointer to edge if added successfully, NULL if list is full.
 *   Note that this functions creates a new edge struct and copies the edge
 *   param passed in into it */
struct edge * add_edge_to_front(struct edge_list *list, struct edge e);

#endif
