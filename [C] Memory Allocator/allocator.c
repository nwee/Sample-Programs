#include<stdio.h>
#include<stdlib.h>
#include "allocator.h"
#define header_size sizeof(header)

typedef struct _header *link;

typedef struct _header {
   int magic;
   int size;
   link next;
   link prev;
} header;

int merge(header * node);

typedef unsigned char byte;
static byte *memory = NULL;
link free_list_ptr;
link previous = NULL;

void allocator_init (u_int32_t size) {
   u_int32_t power2 = 2;
   while (power2 < size) {
      power2 = power2 * 2; //Ensures that input is a power of 2
   }
   size = power2;

   memory = malloc(size);
   free_list_ptr = (header*)memory;
   free_list_ptr->magic = 0xDEADBEEF;  //Signature
   free_list_ptr->size = size;   //requested size
   free_list_ptr->next = free_list_ptr;   //Points to itself 
   free_list_ptr->prev = free_list_ptr;
}

void allocator_end() {
   free(memory);
   memory=NULL;
}

void *allocator_malloc(u_int32_t n){
   header * ptr = free_list_ptr;
   header * current = free_list_ptr; 
   if(current->next == free_list_ptr){ //If points to itself
      previous = free_list_ptr;  //Initial case
   }
   while((current->size)/2 >= n+header_size){ //If the input needs 1 or more splits
      current = (header*)((byte*)free_list_ptr + (current->size)/2);
      current->size = (free_list_ptr->size)/2;
      free_list_ptr->size = current->size;
      current->magic = 0xDEADBEEF;

      previous->prev = current;
      current->prev = free_list_ptr;
      current->next = previous;
      free_list_ptr->next = current;
      previous = current;       	
   }

   do {//traverse the list	
      if( ptr->size >= n+header_size){
         if((ptr->size)/2 > n+header_size) {// If the input needs just one more split
	         header * ptr2 = ptr;
	         ptr2 = (header*)((byte*)ptr + (ptr->size)/2);
            ptr2->size = ptr->size/2;
            ptr->size = ptr->size/2;
	         ptr2->magic = 0xDEADBEEF;

	         ptr2->next = ptr->prev;
	         ptr2->prev = ptr;
	         ptr->prev->prev = ptr2;
	         ptr->next = ptr2;
         }
         if(current->magic != 0xDEADBEEF){ //Checks the Magic magic
	         fprintf(stderr,"ERROR: Memory Allocation Failed\n");
	         abort();
         }
         else{ //The region is of sufficient size
	         if(free_list_ptr->prev == free_list_ptr) {   //Lonely region
	           return NULL;
	         }
            else{
	            ptr->magic = 0xBABEFACE;   //Signify occupied space  
	            if(ptr == free_list_ptr){
	               while(free_list_ptr -> magic == 0xBABEFACE){
	                  free_list_ptr = free_list_ptr->next;
	                  previous = previous->next;
	               }
	            }	 
	            ptr->next->prev = ptr->prev;  //Fix links 
	            ptr->prev->next = ptr->next;

               return (void*)((byte*)ptr+header_size);
            }
         }
      }
      ptr = ptr->next;
   } while(ptr!=free_list_ptr);  // circles the list
	return NULL;
}

void allocator_free(void *object){
   header * temp;
	temp = (header*)((byte*)object - header_size);
	header * current=free_list_ptr;
   int freed = 0;

   //Checks the order of the pointers
	int point1 = (byte*)(current) - memory;
   int point2 = (byte*)(current->prev) - memory;
   int point3 = (byte*)(temp) - memory;
   

   if(temp->magic == 0xBABEFACE) {   //If the memory is used
      while ((current->prev != free_list_ptr)&&(freed == 0)) {
	      if((point1 < point3)&&( point2 > point3 )){ //Memory between 2 spaces
		      temp->prev = current;
		      temp->next = current->next;
		      current->next->prev = temp;
		      current->next = temp;
	         temp->magic = 0xDEADBEEF;
		      freed = 1;
	      }
         current = current->next;
      }

      if(freed == 0) { 
         if(point3 > point1){ //Occupied pointer, temp, has the largest size
		      temp->prev = free_list_ptr->prev;
		      temp->next = free_list_ptr;
		      free_list_ptr->prev->next = temp;
		      free_list_ptr->prev = temp;
		      temp->magic = 0xDEADBEEF;
		   }
         else {   //Occupied pointer, temp, has the smallest size
            temp->prev = free_list_ptr->prev;
            temp->next = free_list_ptr;
            free_list_ptr->prev->next = temp;
            free_list_ptr->prev = temp;
            free_list_ptr = temp;
	         temp->magic = 0xDEADBEEF;
	      }
      }
   }
	  
   current = free_list_ptr;
   temp = free_list_ptr;
   while(current->next!= free_list_ptr) {
	   int succ = merge(current);
	   while(succ == 1) {   //if current correctly merges, it checks if it can merge again
		   succ = merge(current);
	   }
		current=current->next;
   } 
   while(temp != free_list_ptr) {
      temp = temp->next;
   }
}

int merge(header * node){
   int ret = 0;
   if(node->next->size == node->size){
      if( node->next->magic == 0xDEADBEEF){
         int s = node->size;
		   if((byte*)(node->next) - (byte*)node == s){
			   if(((byte*)(node) - memory)%2 == 0){      
				   node -> next=  node->next->next;
				   node -> next->prev = node;
				   node->size = 2* node->size;
				   ret = 1;
			   }
		   }
	   }
   }
   else if(node->prev->size == node->size){
      if( node->prev->magic == 0xDEADBEEF){
	   int s = node->size;
		   if((byte*)node - (byte*)node->prev == s){
			   if(((byte*)(node) - memory)%2 != 0){
				   node = node->prev;
				   node->next =  node->next->next;
				   node->next->prev = node;
				   node->size = 2*node->size;
				   ret = 1;
			   }
		   }
	   }
   }
	return ret;
}