#define _SEM_H_
struct cs1550_sem
{
    int value;
    struct cs1550_queue* process_priority_queue;
};

struct cs1550_queue
{
  struct cs1550_node* head;
  struct cs1550_node* tail;
};

struct cs1550_node
{
  struct cs1550_node* next_task;
  struct task_struct* current_task;
};
