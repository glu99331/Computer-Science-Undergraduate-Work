#ifndef _BLOCKSTRUCT_H_
#define _BLOCKSTRUCT_H_
#include <stdbool.h>


typedef struct heapBlock{
    int data_size;  //size of the data
    bool isUsed; //if false -> free, true -> used
    struct heapBlock* previousBlock;
    struct heapBlock* nextBlock;
} heapBlock;

#endif