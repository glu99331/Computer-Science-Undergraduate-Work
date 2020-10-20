#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <stdbool.h>

#define MAX_STRING_LENGTH 4

int valid_ascii_char(char c)
{
    return 32 <= c && c <= 126 ? true : false;
}

int main(int argc, char* argv[])
{
     char buffer[MAX_STRING_LENGTH+1]; //create a buffer
    FILE* input_file = fopen(argv[1], "rb"); //open the file for reading a binary file
    if(!argv[1])    //if file is not specified
    {
        printf("Please provide a valid filename...");
        return 1;
    }
    if(input_file == NULL)  //if the file does not exist
    {
        printf("The specified file does not exist...");
        return 1;
    }

    int valid_string = false; //bool value to determine whether and ASCII string is valid
    int counter = 0;

    while(!feof(input_file))    //while we have not reached the end of the file
    {
		char c;
		if(fread(&c, sizeof(char), 1, input_file) > 0)  //fread for a char
        {
			if(valid_ascii_char(c) && valid_string) //if the char is valid and the string is valid...
            {
				printf("%c", c);    //just print out the char
            }
            else if(valid_ascii_char(c) && !valid_string)   //but if the char is valid but the string is not...
            {
				buffer[counter++] = c;  
			    if(counter >= MAX_STRING_LENGTH) 
                {
					buffer[MAX_STRING_LENGTH] = '\0';   //we gotta make it 0-terminated, ya know
					printf("%s", buffer);       
					valid_string = true;
			    }
				
			} 
            else    //else its just not a valid string/char..
            {
				if(valid_string) 
                {
					printf("\n");
					valid_string = false;
				}
				counter = 0;
			}
		}
	}
	fclose(input_file); //close the file 
	return 0;
}