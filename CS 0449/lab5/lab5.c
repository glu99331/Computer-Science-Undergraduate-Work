#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "lab5.h"

int filter(void* output, const void* input, int length, int item_size, PREDICATE pred) {
    int filteredCount = 0;
    for(int i = 0; i < length; i++)
    {
        if(pred(input) == true)
        {
            filteredCount++;
            memcpy(output, input, item_size);
            output = MOVE_PTR(output, item_size);   //move the void pointer to the next item 
        }
        input = MOVE_CONST_PTR(input, item_size);   //move the const pointer to the next item
    }
	return filteredCount;
}

int less_than_50(const void* p) {
    float pointer_val = *(const float*)p;

    return (pointer_val < filterNum) ? true : false; //is pointer val < 50?
}
// ------------------------------------------------------
// you shouldn't have to change the stuff below here.
// you can for testing, but please put it back the way it was before you submit.

float float_values[NUM_VALUES] = {
	31.94, 61.50, 36.10,  1.00,  6.35,
	20.76, 69.30, 19.60, 79.74, 51.29,
};

int main() {
	float filtered[NUM_VALUES];
	int filtered_len = filter(filtered, float_values, NUM_VALUES, sizeof(float), &less_than_50);

	printf("there are %d numbers less than 50:\n", filtered_len);

	for(int i = 0; i < filtered_len; i++)
		printf("\t%.2f\n", filtered[i]);

	return 0;
}
