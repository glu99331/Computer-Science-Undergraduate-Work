/* 
Gordon Lu (GOL6)
CS 0449 - Introduction to Systems Software
Spring 2019 - Project 4
 */

#define _GNU_SOURCE
#include <errno.h>
#include <stdio.h>
#include <signal.h>
#include <string.h>
#include <stdlib.h>
#include <ctype.h>
#include <stdbool.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/wait.h>

#define yeet exit
#define DELIMITERS " \t\n"  //preprocessor for delimiters for strtok
#define BUFFER_SIZE 500 //preprocessor constant for arbitrary buffer size 
#define BOILER_PLATE "Developed by Gordon Lu\nFor CS 0449 -- Jarrett Billingsley, Spring 2019.\n\n"

/* int streq(const char* a, const char* b) //if strings are equal -> return 1!!
{
    return strcmp(a, b) == 0;
} */
void signal_handler(int sig)
{
    printf("Program crashed due to a Segmentation Fault --- Fix your code, Recompile, and Run!!\n");
    yeet(1);
}
void get_line(char* input, int size)  //efficient getline -> account for the null terminator
{
	fgets(input, size, stdin);
	int len = strlen(input);
	input[len - 1] = '\0';
}
void read_command(char* input_buffer, int buffer_length) //read command from user input
{
    printf("[myshell]: ");
    get_line(input_buffer, buffer_length);
}
int run_exit(char** tokens) //exit either with input of tokens[1] or simply 0
{
    if(tokens[1] != NULL)
    {
        printf("\nNow exiting myshell with code %d...\n", atoi(tokens[1]));
        return atoi(tokens[1]);
    }
    printf("\nNow exiting myshell...\n");
    return 0;
}

void tokenize_command(char** tokens, char* input_buffer, char* token_command)   //tokenize the command and put it into tokens array
{
    int i;
    for(i = 0; token_command != NULL; i++)
    {
        tokens[i] = token_command;
        token_command = strtok(NULL, DELIMITERS);           
    }
    tokens[i] = NULL;
}

void handle_regular_programs()
{
    int status;
    int childpid = waitpid(-1, &status, 0);

    if(childpid == -1) //if waitpid returns error value
    {
        perror("Cannot access :\n");
        yeet(1);
    }
    else if(WIFEXITED(status))	//if child exited
    {
        if(WEXITSTATUS(status) == 0) //if child exited successfully, print a message saying so
        {
            printf("\nChild exited successfully!\n");
        }
        else	//if child exited, but with a non-zero exit status, print a message saying so
        {
            printf("\nChild exited with error code %d\n", WEXITSTATUS(status));
        }
    }
    else if(WIFSIGNALED(status)) //if child exited due to some signal, print a message saying so!
    {
        printf(": Child terminated due to signal: %s\n", strsignal(WTERMSIG(status)));
    }
    else //if the child exits for some other reason...
    {
        printf("\nChild terminated some other way!\n");
    }
}
void check_for_redirections(char** tokens) 
{
    int i;
    int input_redirection_counter = 0,  output_redirection_counter = 0;
    char *input_file, *output_file;
    for(i = 0; tokens[i] != NULL; i++)
    {
        if(streq(tokens[i], "<")) 
        {
            if(tokens[i+1] != NULL)
            {
                input_file = tokens[i+1];
                tokens[i] = NULL;

            }
            input_redirection_counter++;
        }
        else if(streq(tokens[i], ">"))
        {
            if(tokens[i+1] != NULL)
            {
                output_file = tokens[i+1];
                tokens[i] = NULL;
            }
            output_redirection_counter++;
        }
    }

    if(input_redirection_counter > 0)
    {
        if(input_redirection_counter > 1) {
            printf("Error, invalid argument, %d, for <", input_redirection_counter);
            yeet(1);
        }
        FILE* fp_input = freopen(input_file, "r", stdin);

        if(fp_input == NULL)
        {
            fprintf(stderr, "Error. File to read could not be opened\n");
        }
    }
    
    if(output_redirection_counter > 0)
    {
        if(output_redirection_counter > 1) {
            printf("Error, invalid argument, %d, for >", output_redirection_counter);
            yeet(1);
        }
        FILE* fp_output = freopen(output_file, "w", stdout);

        if(fp_output == NULL)
        {
            fprintf(stderr, "Error. File to write could not be opened\n");
        }
    } 
}

void run_shell(char** tokens, char* token_command)
{
   if(streq(tokens[0], "cd"))
   {
       if(tokens[1] != NULL)
       {
           chdir(tokens[1]);
       }
   }
   /* else if(streq("pwd", tokens[0]))
   {
       char cwd[1024];
       printf("%s\n", getcwd(cwd, sizeof(cwd)));
   } */
   else if(fork() == 0) 
   {
       signal(SIGINT, SIG_DFL);
       check_for_redirections(tokens);
       execvp(tokens[0], &tokens[0]);
       perror("Error running program:");
       yeet(1);
   }
   else
   {
       handle_regular_programs();
   }    

}
int main(int argc, char** argv) 
{
    signal(SIGINT, SIG_IGN);    //at the beginning of main, ignore SIGINT
    signal(SIGSEGV, &signal_handler);   //handle segfaults
    char input_buffer[BUFFER_SIZE]; //create an input buffer 
    int num_tokens =(BUFFER_SIZE/2) + 1; //calculate the worst case for the number of tokens 
    char* tokens[num_tokens]; //create a string array to store the tokenized command
    //while we're still in the shell....
    printf("Welcome to myshell: \nCurrently, only cd, exit, and regular commands are supported....\n\n");
    printf(BOILER_PLATE);
    while(1)
    {
        read_command(input_buffer, BUFFER_SIZE);    //read a command from the user, and put it into input_buffer
        char* token_command = strtok(input_buffer, DELIMITERS); //before tokenizing, split the input using delimiters
        tokenize_command(tokens,input_buffer, token_command); //then tokenize the split up string, and store the tokens into token_command
        if(tokens[0] != NULL)
        {   
            if(streq(tokens[0], "exit"))
                return run_exit(tokens); 
            else
                run_shell(tokens, token_command);
        }
    }
}
