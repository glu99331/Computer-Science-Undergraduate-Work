#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <time.h>
#include <stdbool.h>
//colors
void red () {
  printf("\033[0;31m");
}
void green () {
  printf("\033[0;32m");
}
void bold_green()
{
   printf("\033[1;32m");	
}
void cyan()
{
   printf("\033[0;36m");
}
void reset () {
  printf("\033[0m");
}
void get_line_file(char* input, int size, FILE* file) //get lines from file
{
	fgets(input, size, file);
	int len = strlen(input);
    
    if(len != 0 && input[len - 1] == '\n')
	input[len - 1] = '\0';
}

void get_line(char* input, int size) //get lines from input
{
	fgets(input, size, stdin);
	int len = strlen(input);
    
    if(len != 0 && input[len - 1] == '\n')
	input[len - 1] = '\0';
}

void print_unguessed_letters(char* position, int random_word_length) //print underscores if the letter hasn't been guessed yet
{
    for(int i = 0; i < random_word_length; i++)
    {
            red();
            position[i] = '_';
            reset();
    }   
}

int streq_nocase(const char* a, const char* b) {
	// hohoho aren't I clever
	for(; *a && *b; a++, b++) if(tolower(*a) != tolower(*b)) return 0;
	return *a == 0 && *b == 0;
}

int random_range(int high_value, int low_value)
{
    return rand() % (high_value - low_value) + low_value;
}

bool input_has_number(char* input)
{
    return strpbrk(input, "0123456789") != NULL;
}

bool char_in_word(char* user_guess, char* position, char* random_word, int random_word_length)
{
    bool found_letter = false;

    for(int j = 0; j < random_word_length; j++)
    {
        if(user_guess[0] == random_word[j])
        {
            green();
            position[j] = user_guess[0];
            found_letter = true;
            reset();
        }
    }

    return found_letter;
}

bool player_won(char* position, int random_word_length)
{
    bool player_won = false;
    int underscore_count = 0;
    for(int i = 0; i < random_word_length; i++)
    {
        if(position[i] == '_')
        {
            underscore_count++;
        }
    }
    if(underscore_count == 0)
    {player_won = true;}
    return player_won;
}

bool validDictionary(FILE* dictionary)
{
    if(dictionary == NULL)  //if the file aint there, exit the program :(
    {
        red();
        printf("That file does not exist.\n");
        reset();
        return false;
    }
    return true;
}
void print_game_msg(size_t random_word_length)
{
        cyan();
        printf("Welcome to hangman! Your word has ");
        green();
        printf("%zu ",random_word_length);
        cyan();
        printf("letters: \n"); //welcome to hangman, your word has %d letters
        reset();
}
void print_current_progress(size_t random_word_length, char* position)
{
      for(int i = 0; i < random_word_length; i++) 
      {
          green();
          printf("%c ", position[i]);
          reset();
      }
}
void prompt_n_get_user_guess(char* user_guess)
{
    cyan();
    printf("Guess a letter or type the whole word: ");
    green();
    get_line(user_guess, sizeof(user_guess));
    reset();
}
void invalid_input()
{
     red();
     printf("Invalid input, please enter a letter.\n"); 
     reset();
}
void victory_msg(char* random_word)
{
     green();
     printf("You got it! The word was '%s'.\n", random_word);
     reset();
}
void defeat_msg(char* random_word)
{
     red();
     printf("Sorry, you lost! The word was ");
     green();
     printf("'%s'.\n", random_word);
     reset();
}
int main(int argc, char** argv)
{
    const int max_string_length = 20; //constant value for string length of dictionary words
    srand((unsigned int)time(NULL)); //declaring random object
    FILE* dictionary = fopen("dictionary.txt", "r"); //opening the dictionary file
    char line[100]; //declare string

    int dictionaryIndex = 0; //variable to serve as an increment and iterate through the whole dictionary file
    char user_guess[100];
    char position[100]; //initializing an array of strings to hold whatever is at the underscores
    //so that later when charAt(user_guess[i]) == charAt(words[random_number][i]), position[i] = user_guess[i] :)
    int wrongGuess = 0; //counter to keep track of incorrect guesses

    if(validDictionary(dictionary) == false){return 0;}
   
    int len = atoi(fgets(line, 100, dictionary));   //parse string as an int -> i.e. parseInt
    char words[len][max_string_length];    //create an array of strings to store values of dictionary.txt
    
    while(!feof(dictionary))    //while we haven't reached the end of the file..
    {
        get_line_file(words[dictionaryIndex], sizeof(words[dictionaryIndex]), dictionary); //copy the line at index i into word array
        dictionaryIndex++; //increment the index
    }
    fclose(dictionary); //close the dictionary file
     
    int random_number = random_range(len, 1); //generate a random number between 1 and 10
    char *random_word = words[random_number]; //using random number to serve as an index, so we get a random word!! 
    size_t random_word_length = strlen(random_word); 

    if(argc >1) //if there's actually something besides ./hangman do something!!
    {
        random_word = argv[1];
        random_word_length = strlen(random_word); // set random word length to length of the random word!!
    }
    print_game_msg(random_word_length);
    print_unguessed_letters(position, random_word_length); //
    while(wrongGuess < 5) //while we haven't guessed wrong 5 times...
    {
        print_current_progress(random_word_length, position);
        prompt_n_get_user_guess(user_guess);
        if(input_has_number(user_guess))
        { 
            invalid_input();  
        }
        else if(strlen(user_guess) > 1)
        {
            if(!streq_nocase(user_guess, random_word))
            {
                wrongGuess++;
                red();
                printf("Strike %d!\n", wrongGuess);
                reset();
            }
            else
            {
                victory_msg(random_word);
                return 0;
            }
        }
        else if(strlen(user_guess) == 1)
        {
            if(char_in_word(user_guess, position, random_word, random_word_length))
            {
                if(player_won(position, random_word_length) == true) // returns true if there are any _ in position; false otherwise.
                {
                    victory_msg(random_word);
                    return 0;
                }
            }
            else
            {
                wrongGuess++;
                red();
                printf("Strike %d!\n", wrongGuess);
                reset();            } 
        }
    }
    if(wrongGuess == 5)
    {
        defeat_msg(random_word);
        return 0;
    }    
    return 0;
}