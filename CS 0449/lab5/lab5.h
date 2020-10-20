#ifndef _LAB5_MACROS_H_
#define _LAB5_MACROS_H_

typedef int (*PREDICATE)(const void*);
//Macro to move where the constant void pointer points to 
#define MOVE_CONST_PTR(ptr, item_size) ((const void*)(((char*)(ptr)) + (item_size)))
//Macro to move where the void pointer points to 
#define MOVE_PTR(ptr, item_size) ((void*)(((char*)(ptr)) + (item_size)))
//any numbers above this value will be ignore :P 
#define filterNum 50 
//technically these are in stdbool butttt ehh im tryna some stuff :)  
#define true 1
#define false 0
//amount of values we're testing  
#define NUM_VALUES 10


#endif