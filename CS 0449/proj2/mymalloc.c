#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <stdbool.h>
#include "mymalloc.h"
#include "blockstruct.h"

// USE THIS GODDAMN MACRO OKAY
#define PTR_ADD_BYTES(ptr, byte_offs) ((void*)(((char*)(ptr)) + (byte_offs)))
// Don't change or remove these constants.
#define MINIMUM_ALLOCATION  16
#define SIZE_MULTIPLE       8

heapBlock* heap_headBlock;
heapBlock* heap_tailBlock;

unsigned int round_up_size(unsigned int data_size) {
    if(data_size == 0)
        return 0;
    else if(data_size < MINIMUM_ALLOCATION)
        return MINIMUM_ALLOCATION;
    else
        return (data_size + (SIZE_MULTIPLE - 1)) & ~(SIZE_MULTIPLE - 1);
}
void free_block(heapBlock* chunk)
{
    chunk->isUsed = false;
}
void contractHeap(heapBlock* block)
{
    brk(block);
}
void checkContract(heapBlock* block)
{
    if(block->previousBlock == NULL && block->nextBlock == NULL)
    {
        contractHeap(block);
        heap_headBlock = NULL;
        heap_tailBlock = NULL;
        return;
    }
    if(block->nextBlock == NULL && block->isUsed == false)
    {
        heap_tailBlock = block->previousBlock;
        heap_tailBlock->nextBlock = NULL;
        contractHeap(block);
        return;
    }
}
bool doesItFit(int size, heapBlock* curr)
{
    return curr->data_size >= size; 
}

heapBlock* splitHeapBlock(heapBlock* block, unsigned int size)
{
    if(block->data_size >= (size + (sizeof(heapBlock)) + MINIMUM_ALLOCATION))
    {
        heapBlock* newHeapHeader = PTR_ADD_BYTES(block, (size+sizeof(heapBlock)));
        unsigned int newHeapHeaderSize = block -> data_size - (size + sizeof(heapBlock));
        newHeapHeader -> data_size = newHeapHeaderSize;
        newHeapHeader -> nextBlock = block -> nextBlock;
        block -> nextBlock -> previousBlock = newHeapHeader;
        block -> nextBlock = newHeapHeader;
        newHeapHeader -> previousBlock = block;
        block -> data_size = size;
        block -> isUsed = true;
        newHeapHeader -> isUsed = false;
            return block;

    }
    else{
        block->isUsed = true;
        return block;
    }
}
heapBlock* doFirstFit(unsigned int size)
{
    for(heapBlock* curr = heap_headBlock; curr != NULL; curr = curr->nextBlock)
    {
        
        if(curr->isUsed == false && doesItFit(size, curr)) 
        {
			curr = splitHeapBlock(curr, size);
            return curr;
        }
	}
    return NULL;
}

bool isPrevCoalescable(heapBlock* block)
{
    if(block->previousBlock != NULL && block->previousBlock->isUsed == false)
        return true;
    else
        return false; 
}
bool isNextCoalescable(heapBlock* block)
{   
    if(block->nextBlock != NULL && block->nextBlock->isUsed == false)
        return true;
    else
        return false;
    //return (block->nextBlock != NULL && block->nextBlock->isUsed == false);
}
heapBlock* coalesce_prev(heapBlock* block)
{
    if(isPrevCoalescable(block))
    {
        unsigned int new_size = block->data_size + block->previousBlock->data_size + sizeof(heapBlock);
        if(block->nextBlock == NULL)
        {
            block->previousBlock->nextBlock = NULL;
            block->previousBlock->data_size = new_size;
            heap_tailBlock = block->previousBlock;
            return block->previousBlock; 
        }
        else
        {
            block->previousBlock->nextBlock = block->nextBlock;
            block->nextBlock->previousBlock = block->previousBlock;
            block->previousBlock->data_size = new_size;         
            return block->previousBlock;
        }
        
    }
    return block; 

}
heapBlock* coalesce_next(heapBlock* block)
{
    if(isNextCoalescable(block)){
        unsigned int new_size = block->data_size + block->nextBlock->data_size + sizeof(heapBlock);
        block->nextBlock = block->nextBlock->nextBlock;
        block->nextBlock->previousBlock = block;
        block->data_size = new_size;
        return block;
    }
    else
    {
        return block;
    }
}

heapBlock* coalesce_both(heapBlock* block)
{
	if(isNextCoalescable(block) && isPrevCoalescable(block))
    {
        unsigned int new_size = block->data_size + 2*sizeof(heapBlock) + block->previousBlock->data_size + block->nextBlock->data_size;
        if (block->nextBlock == NULL)
	    {
		    heap_tailBlock = block->previousBlock;
            block->previousBlock->nextBlock = NULL;
            block->data_size = new_size;
            return block->previousBlock;
	    }
        else {
            block->previousBlock->nextBlock = block->nextBlock->nextBlock;
            block->nextBlock->nextBlock->previousBlock = block->previousBlock;
            block->previousBlock->data_size = new_size;
            return block->previousBlock;
            /* block->previousBlock->data_size = new_size;
            block->previousBlock->nextBlock = block->nextBlock->nextBlock; */
        }
    }
	return block;
}

heapBlock* coalesce(heapBlock* block) {
    block = coalesce_both(block);
    block = coalesce_prev(block);
    block = coalesce_next(block);
    return block;
}

heapBlock* createHeapBlock(unsigned int size)
{
    heapBlock* chunk = sbrk(sizeof(heapBlock) + size); 
    chunk->data_size = size;
    chunk->isUsed = true;
    chunk->previousBlock = NULL;
    chunk->nextBlock = NULL;
    return chunk; 
}

heapBlock* createFirstBlock(unsigned int size) {
    heapBlock* block = createHeapBlock(size);
    heap_headBlock = block;
    heap_tailBlock = block;
    heap_headBlock->previousBlock = NULL;
    heap_tailBlock->nextBlock = NULL;
    return block;
}

heapBlock* appendHeapBlock(heapBlock* block, unsigned int size)
{
    if(block == NULL) {
        block = heap_tailBlock;
        block->nextBlock = createHeapBlock(size);
        block->nextBlock->previousBlock = block;
        heap_tailBlock = block->nextBlock;
        return block->nextBlock;
    }
    return block;
}


void* my_malloc(unsigned int size) {
    if(size == 0)
        return NULL;

    size = round_up_size(size);

    heapBlock* curr;
    if(heap_headBlock == NULL)
    {
        curr = createFirstBlock(size);
        unsigned int* data = PTR_ADD_BYTES(curr, sizeof(heapBlock));
        return data;
    }
    curr = doFirstFit(size);
    curr = appendHeapBlock(curr, size);
    unsigned int* currData = PTR_ADD_BYTES(curr, sizeof(heapBlock));
    return currData;
}
void my_free(void* ptr) {
    if(ptr == NULL)
        return;
    heapBlock* header = PTR_ADD_BYTES(ptr, -sizeof(heapBlock));
    free_block(header);
    header = coalesce(header);
    checkContract(header);
}