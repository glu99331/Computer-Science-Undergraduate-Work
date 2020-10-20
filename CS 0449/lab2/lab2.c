#include <stdio.h>
#include <string.h>
#include <ctype.h>

void get_line(char* buffer, int size) {
	fgets(buffer, size, stdin);
	int len = strlen(buffer);
	// this is a little more robust than what we saw in class.
	if(len != 0 && buffer[len - 1] == '\n')
		buffer[len - 1] = '\0';
}

// returns 1 if the two strings are equal, and 0 otherwise.
int streq(const char* a, const char* b) {
	return strcmp(a, b) == 0;
}

// returns 1 if the two strings are equal ignoring case, and 0 otherwise.
// so "earth" and "Earth" and "EARTH" will all be equal.
int streq_nocase(const char* a, const char* b) {
	// hohoho aren't I clever
	for(; *a && *b; a++, b++) if(tolower(*a) != tolower(*b)) return 0;
	return *a == 0 && *b == 0;
}

float weight_on_planet(const char* planet_name, int user_weight)
{  
    if(streq_nocase(planet_name, "mars")) { 
        return user_weight * 0.38; //int * float = float
    }
    else if(streq_nocase(planet_name, "mercury")){
        return user_weight * 0.38;
    }
    else if(streq_nocase(planet_name, "venus")){
        return user_weight * 0.91;
    }
    else if(streq_nocase(planet_name, "jupiter")){
        return user_weight * 2.54;
    }
    else if(streq_nocase(planet_name, "saturn")){
        return user_weight * 0.38;
    }
    else if(streq_nocase(planet_name, "uranus")){
        return user_weight * 0.91;
    }
    else if(streq_nocase(planet_name, "neptune")){
          return user_weight * 1.19;
    }
    else{
        return -1;
    }
}
int main() {
    printf("How much do you weigh? ");
    
    char input[100];
    get_line(input, sizeof(input)); // notice the sizeof!

    if(strpbrk(input, "abcdefghijklmnopqrstuvwxyz") != 0 || strpbrk(input, "ABCDEFGHIJKLMNOPQRSTUVWXYZ") != 0) //check if the user entered an alphabetic character/string, im just doing some extra stuff :)
    { 
        printf("Invalid input, please enter a numerical value.\n"); 
        return 0;
    }
    
    int weight;
    sscanf(input, "%d", &weight);

    char planet_input[100]; //create a new char array to hold the input of planet name

    while(!strcmp(planet_input, "exit") == 0){ //while the user has not chosen to exit the program ...
        printf("What planet do you wanna go to ('exit' to exit)? ");
        get_line(planet_input, sizeof(planet_input)); 

        if(strcmp(planet_input, "earth") == 0) //if the user enters "earth", print a silly message
        {
            printf("uh, you're already there, buddy\n");
        }
        else if(strcmp(planet_input, "exit") == 0) //if the user enters "exit", exit the loop
        {
            break;
        }
        else if(weight_on_planet(planet_input, weight) < 0) //if the user enters a planet not in our solar system or not a planet, say it's not a planet
        {
            printf("That's not a planet.\n");
        }
        else
        {
            printf("You'd weight %.2f there.\n", weight_on_planet(planet_input, weight)); //otherwise carry out calculations as normal
        }
        
    }
	return 0;
}