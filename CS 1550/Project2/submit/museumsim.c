/*******************************************************************************
* =============== CS 1550: Project II, Process Synchronization. ============== *
* ---------------------------------------------------------------------------- *
* Author: Gordon Lu																				                     *
* Term: Spring 2020, Dr.Mosse's Operating Systems class		                     *
* ---------------------------------------------------------------------------- *
*******************************************************************************/


#include <linux/unistd.h>
#include "sem.h"
#include <string.h>
#include <stdio.h>
#include <sys/mman.h>
#include <sys/resource.h>
#include <stdlib.h>
#include <sys/time.h>
#include <stdbool.h>

#define MAX_VISITORS 10 //Maximum number of visitors per visitor is 10!

//Define the down and up syscalls!
void down(struct cs1550_sem *sem) {
syscall(__NR_cs1550_down, sem);
}

void up(struct cs1550_sem *sem) {
 syscall(__NR_cs1550_up, sem);
}

/*******************Structs**********************/
typedef struct museumsim_args
{
  int num_visitors; //the number of tenants
  int num_tour_guides; //the number of tour guides
  int probability_of_visitor_following_another_visitor; //probability that another visitor follows another visitor
  int delay_in_seconds_vistor_does_not_follow_another_visitor; //the delay in seconds that a visitor does not follow another one
  int random_seed_for_vistor_arrival_process; //random seed for visitor arrival!
  int probability_of_tour_guide_following_another_tour_guide ; //probability that another tour guide follows another tour guide
  int delay_in_seconds_tour_guide_does_not_follow_another_tour_guide ; //delay in seconds that a tour gude does not follow another one
  int random_seed_for_tour_guide_arrival_process; //random seed for tour guide arrival!
} museumsim_args;

typedef struct shared_memory
{
  int visitors_in;  //Number of visitors inside!
  int guides_in;   //Number of guides inside!
  int visitors_waiting; //Number of visitors waiting!
} shared_memory;

/*************************GLOBAL VARIABLES***************************/
museumsim_args* parsed_args;
struct timeval t_initial;
shared_memory* shared;
/********Additional Semaphores**********/
struct cs1550_sem* visitor_arrives; //Has the visitor arrived yet?
struct cs1550_sem* allow_guide_in; //Can we let a guide in yet??
struct cs1550_sem* museum_open; //Is the museum open yet?
struct cs1550_sem* tg_can_leave; //Can the tour guide leave?
struct cs1550_sem* shared_memory_lock; //Protect the shared variable!
/*****************FUNCTION PROTOTYPES ********************/
void visitorArrives(int i);
void tourguideArrives(int j);
void openMuseum(int j);
void viewMuseum(int i);
void visitorLeaves(int i);
void tourguideLeaves(int j);

/****************HELPER FUNCTIONS****************/
void allow_max_10_visitors();
void allow_more_visitors_in();
double calculate_time();
/********************************************************/
//Function to allow in 10 visitors per guide!
void allow_max_10_visitors()
{
  int i = 0;
  while(i < MAX_VISITORS)
  {
    //allow each visitor to go into the museum!
    up(museum_open);
    i++;
  }
}
void allow_more_visitors_in()
{
  //We don't want to directly mutate the visitors_in shared variable, so we use a temp!!
  int j = shared -> visitors_in;
  while(j > 0)
  {
    //allow each visitor to now leave the museum!
    down(museum_open);
    j--;
  }
}
//Simple function to calculate times!
double calculate_time()
{
  struct timeval t_end;
  gettimeofday(&t_end, NULL);
  double elapsed = (t_end.tv_sec - t_initial.tv_sec) + ((t_end.tv_usec - t_initial.tv_usec)/1000000.0);
  return elapsed;
}

void visitorArrives(int i)
{
  //Signal the guide that the visitor has arrived!
  up(visitor_arrives);

  double elapsed = calculate_time();  
  printf("Visitor %d arrives at time %d.\n", i, (int)elapsed);
  
  //Each visitor will wait for the museum to be open
  down(museum_open);
  
  //Increase the number of visitors that are waiting outside!
  down(shared_memory_lock);
  shared -> visitors_waiting++;
  up(shared_memory_lock);

}

void tourguideArrives(int j)
{
  double elapsed = calculate_time();    
  printf("Tour guide %d arrives at time %d.\n", j, (int)elapsed);
  //Wait for the next guide to arrive!
  down(allow_guide_in);
  //Wait for the visitor to arrive
  down(visitor_arrives);
}

void tourMuseum(int i)
{
  double elapsed = calculate_time();
  printf("Visitor %d tours the museum at time %d\n", i, (int)elapsed);
  
  down(shared_memory_lock);
  shared -> visitors_in++;
  shared -> visitors_waiting--;
  up(shared_memory_lock);
  
  //Each visitor will then sleep for 2 seconds!
  sleep(2);
}

void openMuseum(int j)
{
  double elapsed = calculate_time();  
  printf("Tour guide %d opens the museum for tours at time %d\n", j, (int)elapsed);  
  //Let at most 10 visitors in!
  allow_max_10_visitors(); 
  
  down(shared_memory_lock);
  //Increment the number of guides inside the museum!
  shared -> guides_in++;
  up(shared_memory_lock);
}


void visitorLeaves(int i)
{
  double elapsed = calculate_time();  
  printf("Visitor %d leaves the museum at time %d.\n", i, (int)elapsed);
  
  down(shared_memory_lock);
  shared -> visitors_in--;
  //If there are no more visitors inside...
  if(shared -> visitors_in == 0)
  {
    up(tg_can_leave); //Signal the tour guide that they can leave!
  }
  up(shared_memory_lock);
}

void tourguideLeaves(int j)
{
  //Wait until all visitors inside the museum have left!
  down(tg_can_leave);
  
  //Reset the museum open semaphore value, and then let more visitors into the museum, 
  //given that another tour guide is waiting!
  down(shared_memory_lock);
  allow_more_visitors_in();
  up(shared_memory_lock);
  
  //Signal the next guide to bring more visitors into the museum!
  up(allow_guide_in);

  double elapsed = calculate_time();  
  printf("Tour guide %d leaves the museum at time %d.\n", j, (int)elapsed);
  down(shared_memory_lock);
  shared -> guides_in--;
  if(shared -> visitors_in == 0 && shared -> guides_in == 0 && shared -> visitors_waiting == 0)
  {
    printf("The museum is now empty.\n");
  }
  up(shared_memory_lock);
}
int main(int argc, char** argv)
{
  /*
  Have the following command-line arguments:
• -m: number of visitors
• -k: number of tour guides
• -pv: probability of a visitor immediately following another visitor
• -dv: delay in seconds when a visitor does not immediately follow another visitor
• -sv: random seed for the visitor arrival process
• -pg: probability of a tour guide immediately following another tour guide
• -dg: delay in seconds when a tour guide does not immediately follow another tour guide
• -sg: random seed for the tour guide arrival process
  */
  //Initialize command line arguments to be parsed!
  
  /*
  To make our shared data and our semaphores, what we need is for multiple processes to be able to
  share the same memory region. We can ask for N bytes of RAM from the OS directly by using mmap():
  */
  parsed_args = mmap(NULL, sizeof(museumsim_args), PROT_READ|PROT_WRITE, MAP_SHARED|MAP_ANONYMOUS, 0, 0);
  
  //Cache the parsed args using a struct pointer.
  parsed_args -> num_visitors = 0;
  parsed_args -> num_tour_guides = 0;
  parsed_args -> probability_of_visitor_following_another_visitor = 0;
  parsed_args -> delay_in_seconds_vistor_does_not_follow_another_visitor = 0;
  parsed_args -> random_seed_for_vistor_arrival_process = 0;
  parsed_args -> probability_of_tour_guide_following_another_tour_guide = 0;
  parsed_args -> delay_in_seconds_tour_guide_does_not_follow_another_tour_guide = 0;
  parsed_args -> random_seed_for_tour_guide_arrival_process = 0;
  
  visitor_arrives = mmap(NULL, sizeof(struct cs1550_sem), PROT_READ|PROT_WRITE, MAP_SHARED|MAP_ANONYMOUS, 0, 0);
  visitor_arrives -> value = 0;
  
  allow_guide_in = mmap(NULL, sizeof(struct cs1550_sem), PROT_READ|PROT_WRITE, MAP_SHARED|MAP_ANONYMOUS, 0, 0);
  allow_guide_in -> value = 1;
  
  museum_open = mmap(NULL, sizeof(struct cs1550_sem), PROT_READ|PROT_WRITE, MAP_SHARED|MAP_ANONYMOUS, 0, 0);
  museum_open -> value = 0;
  
  tg_can_leave = mmap(NULL, sizeof(struct cs1550_sem), PROT_READ|PROT_WRITE, MAP_SHARED|MAP_ANONYMOUS, 0, 0);
  tg_can_leave -> value = 0;
  
  shared_memory_lock = mmap(NULL, sizeof(struct cs1550_sem), PROT_READ|PROT_WRITE, MAP_SHARED|MAP_ANONYMOUS, 0, 0);
  shared_memory_lock -> value = 1;

  
  shared = mmap(NULL, sizeof(shared_memory), PROT_READ|PROT_WRITE, MAP_SHARED|MAP_ANONYMOUS, 0, 0);
  shared -> visitors_in = 0;
  shared -> guides_in = 0;
  shared -> visitors_waiting = 0;
  //Analogous to saying: strcmp(argv[1], "-m") == 0
  if(!strcmp(argv[1], "-m"))
  {
    parsed_args -> num_visitors = atoi(argv[2]);
  }
  if(!strcmp(argv[3], "-k"))
  {
    parsed_args -> num_tour_guides = atoi(argv[4]);
  }
  if(!strcmp(argv[5], "-pv"))
  {
    parsed_args -> probability_of_visitor_following_another_visitor = atoi(argv[6]);
  }
  if(!strcmp(argv[7], "-dv"))
  {
    parsed_args -> delay_in_seconds_vistor_does_not_follow_another_visitor = atoi(argv[8]);
  }
  if(!strcmp(argv[9], "-sv"))
  {
    parsed_args -> random_seed_for_vistor_arrival_process = atoi(argv[10]);
  }
  if(!strcmp(argv[11], "-pg"))
  {
    parsed_args -> probability_of_tour_guide_following_another_tour_guide = atoi(argv[12]);
  }
  if(!strcmp(argv[13], "-dg"))
  {
    parsed_args -> delay_in_seconds_tour_guide_does_not_follow_another_tour_guide = atoi(argv[14]);
  }
  if(!strcmp(argv[15], "-sg"))
  {
    parsed_args -> random_seed_for_tour_guide_arrival_process = atoi(argv[16]);
  }
     //Start timing
    gettimeofday(&t_initial, NULL);

    //Initially the museum is empty!
    printf("The museum is now empty.\n");
    //Create the processes:
    int pid = fork();
    //We're at a child process!!
    if(pid == 0)
    {
      srand(parsed_args -> random_seed_for_tour_guide_arrival_process); //1. setup random seed for random arrival of tour guides!

      int i;
      for(i = 0; i < parsed_args -> num_tour_guides; i++)
      {
        //Protect the variable!!
        int pid2 = fork();
        
        if(pid2 == 0)
        {
          tourguideArrives(i);
          //printf("total visitors: %d\n", total_visitors);
          openMuseum(i);
          tourguideLeaves(i);
          exit(0);

        }
        else //original process(Tour Guide Arrival Process) when pid2!=0
        {
          int value = rand() % 100 + 1; //random number between 1-100
          if (value > parsed_args -> probability_of_tour_guide_following_another_tour_guide)
          {
            sleep(parsed_args -> delay_in_seconds_tour_guide_does_not_follow_another_tour_guide);
          }

          //then exit()

        }
        //Don't let any other processes access these processes!
        
      }
      //Wait until all children finish execution
      int ii;
      for(ii = 0; ii < parsed_args -> num_tour_guides; ii++)
      {
        wait(NULL);
      }
    }
    else
    {
          srand(parsed_args -> random_seed_for_vistor_arrival_process); //1. setup random seed for random arrival of visitors!
          int j;
          for(j = 0; j < parsed_args -> num_visitors; j++)
          {
            //Protect the variable!!
            int pid2 = fork();
            
            if(pid2 == 0)
            { 
              visitorArrives(j);
              tourMuseum(j);
              visitorLeaves(j);
              //tour guide arrives
              //then exit()
              exit(0);
            }
            else //original process(Visitor Arrival Process) when pid2!=0
            {
              int value = rand() % 100 + 1; //random number between 1-100
              if (value > parsed_args -> probability_of_visitor_following_another_visitor)
              {
                sleep(parsed_args -> delay_in_seconds_vistor_does_not_follow_another_visitor);
              }
            }
          
        }
      //}
      //Wait until all children finish execution
      int jj;
      for(jj = 0; jj < parsed_args -> num_visitors; jj++)
      {
        wait(NULL);
      }
      wait(NULL);
    }

}