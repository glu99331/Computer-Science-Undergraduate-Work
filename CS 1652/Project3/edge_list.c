/*
 * CS 1652 Project 3 
 * (c) Amy Babay, 2022
 * (c) <Student names here>
 * 
 * Computer Science Department
 * University of Pittsburgh
 */

#include <stdlib.h>
#include "spu_alarm.h"
#include "edge_list.h"

/* Add edge to edge list.
 *     returns pointer to edge if added successfully, NULL if list is full.
 *   Note that this functions creates a new edge struct and copies the edge
 *   param passed in into it */
struct edge * add_edge_to_list(struct edge_list *list, struct edge e)
{
    if (list->num_edges == MAX_EDGES) {
        Alarm(DEBUG, "Edge list full. Not adding new edge\n");
        return NULL;
    }

    list->edges[list->num_edges] = calloc(1, sizeof(struct edge));
    *(list->edges[list->num_edges]) = e;
    list->num_edges++;

    return list->edges[list->num_edges - 1];
}

struct edge *update_edge_from_id(struct edge_list *list, uint32_t src, uint32_t dst, struct edge e){
    int i;

    for (i = 0; i < list->num_edges; i++)
    {
        if (list->edges[i]->src_id == src && list->edges[i]->dst_id == dst) {
            *(list->edges[i]) = e;
            return list->edges[i];
        }
    }
    return NULL;
}

struct edge *find_edge_from_id(struct edge_list *list, uint32_t src, uint32_t dst){
    int i;

    for (i = 0; i < list->num_edges; i++)
    {
        if (list->edges[i]->src_id == src && list->edges[i]->dst_id == dst) {
            return list->edges[i];
        }
    }
    return NULL;
}

struct edge * add_edge_to_front(struct edge_list *list, struct edge e){
    if (list->num_edges == MAX_EDGES) {
        Alarm(DEBUG, "Edge list full. Not adding new edge\n");
        return NULL;
    }

    int i;
    for(i = list->num_edges; i >= 0; i--){
        list->edges[i+1] = list->edges[i];
    }
    list->edges[0] = calloc(1, sizeof(struct edge));
    *(list->edges[0]) = e;
    list->num_edges++;

    return list->edges[0];
}
