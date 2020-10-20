#define _GNU_SOURCE

#include <errno.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>

#include <unistd.h>
#include <pthread.h>

// =================================================================================================
//
//
// Scroll down to the bottom!!!
// Don't change anything here!!!
//
//
// =================================================================================================

#define streq(a, b) (strcmp((a), (b)) == 0)

void exit_main_thread();
void show_status();
void set_alarm(long duration);

void get_line(char* input, int size) {
	fgets(input, size, stdin);
	int len = strlen(input);
	if(len > 0)
		input[len - 1] = '\0';
}

int main(int argc, char** argv) {
	while(true) {
		printf("> ");
		char buf[100];
		get_line(buf, sizeof(buf));

		char* saveptr;
		char* command = strtok_r(buf, " ", &saveptr);

		if(command == NULL) {
			continue;
		} else if(streq(command, "exit")) {
			exit_main_thread();
		} else if(streq(command, "status")) {
			show_status();
		} else if(streq(command, "alarm")) {
			char* duration = strtok_r(NULL, " ", &saveptr);

			if(duration == NULL) {
				printf("please give a duration in seconds.\n");
			} else {
				set_alarm(atoi(duration));
			}
		}
	}
	return 0;
}

// =================================================================================================
// You will write your code below this line.
// =================================================================================================

// this is a SHARED GLOBAL and its associated mutex.
int num_threads = 0;
pthread_mutex_t num_threads_mutex = PTHREAD_MUTEX_INITIALIZER;

void change_thread_counter(int delta) {
    pthread_mutex_lock(&num_threads_mutex);
    num_threads += delta;
    pthread_mutex_unlock(&num_threads_mutex);
	// SAFELY add "delta" to the current value of "num_threads".
}

void exit_main_thread() {
    if(num_threads > 0){
        printf("\tstill %d alarm(s) pending...\n", num_threads);
    }
    pthread_exit(&num_threads);
	// if there are any threads running, say so.
	// no matter what, exit the thread with pthread_exit().
}

void show_status() {
    printf("\t%d alarm(s) pending\n", num_threads);	// show how many threads are running.
}

void* thread_main(void* ctx)
{
	long param = (long)ctx;
    sleep(param);
    printf("(RING-RING)\a");
    fflush(stdout);
    change_thread_counter(-1);
	return NULL;
}
void set_alarm(long duration) {

    if(duration <= 0)
    {
        printf("Error. Invalid duration...");
    }
    else
    {
        pthread_t tids[duration];
        
        pthread_create(tids, NULL, &thread_main, (void*)(duration));
        change_thread_counter(1);


    }
	// if duration <= 0, give an error message.
	// otherwise, start an alarm thread, and use change_thread_counter to increment the counter.
}