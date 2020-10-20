#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <stdint.h>


typedef struct Node {
	int value;
	struct Node* next;
} Node;

Node* create_node(int value)
{
    Node* node = malloc(sizeof(Node));
    node->value = value;
    node->next = NULL;
    return node;    
}

void list_print(Node* head)
{
    while(head != NULL)
    {
        if(head->next != NULL)
        {
            printf("[%d] -> ", head->value);
        }
        else
        {
            printf("[%d] ", head->value);
        }
        head = head->next;
    }
    printf("\n");
}
Node* list_append(Node* head, int value)
{
    while(head != NULL)
    {
        Node* prev = head;
        head = head->next;
        if(head == NULL)
        {
            prev->next = create_node(value);
            head = prev->next;
            break;
        }
        
    }
    return head;
}
Node* list_prepend(Node* head, int value)
{
    Node* newHead = create_node(value);
    newHead->next = head;
    head = newHead;

    return newHead;
}
void list_free(Node* head)
{
    if(head == NULL)
    {
        return;
    }
    while(head != NULL)
    {
        Node* current = head->next;
        free(head);
        head = current;

    }

   
}
Node* list_remove(Node* head, int value)
{
    Node* current = head;
    current->value = head->value;
    Node* previous = NULL;

    if(current == NULL)
    {
        return NULL; 
    }
    if(head != NULL && head->value == value) 
    {
        head = current->next;
        free(current);
        return head;
    }
        while(current != NULL && current->value != value)
        { 
            previous = current;
            current = current->next;
        }
    previous->next = current->next;
    free(current);
    return head;
} 

int main()
{
	Node* head = create_node(1);
	//list_print(head);                  // 1
	Node* end = list_append(head, 2);
	//list_print(head);                  // 1 -> 2
	end->next = create_node(3);
	list_print(head);                  // 1 -> 2 -> 3
	head = list_prepend(head, 0);
	list_print(head);                  // 0 -> 1 -> 2 -> 3
	list_append(head, 4);
	list_print(head);                  // 0 -> 1 -> 2 -> 3 -> 4
	list_append(head, 5);
	list_print(head);                  // 0 -> 1 -> 2 -> 3 -> 4 -> 5

	head = list_remove(head, 5);
	list_print(head);                  // 0 -> 1 -> 2 -> 3 -> 4
	head = list_remove(head, 3);
	list_print(head);                  // 0 -> 1 -> 2 -> 4
	head = list_remove(head, 0);
	list_print(head);                  // 1 -> 2 -> 4
    
	list_free(head);     
    return 0;
}