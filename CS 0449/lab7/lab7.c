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

int main(int argc, char** argv) {
	if(argc < 2) {
		printf("uh, you gotta gimme an executable to run...\n");
		return 0;
	}

	if(fork() == 0) {
		execvp(argv[1], &argv[1]);
		perror("Error running program:");
		exit(1);
	} else {
	
		signal(SIGINT, SIG_IGN); //ignore SIGINT

		int status;
		int childpid = waitpid(-1, &status, 0);
		printf("----------\n");

		if(childpid == -1) //if waitpid returns error value
		{
			perror("Cannot access :\n");
			exit(1);
		}
		else if(WIFEXITED(status))	//if child exited
		{
			if(WEXITSTATUS(status) == 0) //if child exited successfully, print a message saying so
			{
				printf("exited successfully!\n");
			}
			else	//if child exited, but with a non-zero exit status, print a message saying so
			{
				printf("exited with error code %d\n", WEXITSTATUS(status));
			}
		}
		else if(WIFSIGNALED(status)) //if child exited due to some signal, print a message saying so!
		{
			printf("Program terminated due to signal %s\n", strsignal(WTERMSIG(status)));
		}
		else //if the child exits for some other reason...
			printf("terminated some other way!\n");
	}
	return 0;
}