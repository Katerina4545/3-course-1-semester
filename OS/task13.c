#include "stdlib.h" 
#include "stdio.h" 
#include "pthread.h" 
#include "semaphore.h"
#define number_of_lines_printed 10

void* thread(void* tmp); 

struct semafore_structure {
 sem_t first_semafore; 
 sem_t second_semafore;
};

int main(){ 

 pthread_t id_thread; 			
 pthread_attr_t attribute; 	

 int error_init = pthread_attr_init(&attribute);
 if(error_init != 0)
 {
   printf("error of init");
   return EXIT_FAILURE;
 }


 //add code for 13 task
 struct semafore_structure semafores;
 int pshared = 0;
 int value_semafore = 1;
 int error_sem_init = sem_init(&semafores.first_semafore, pshared , value_semafore);

 if(0 != error_sem_init) {
	printf("error sem_init\n");
	
	int error_destroy_attribute = pthread_attr_destroy(&attribute);
	if(error_destroy_attribute != 0)
	{
       		printf("error of destroy_attribute");
	}

	return EXIT_FAILURE;

 }


 value_semafore = 0;
 error_sem_init = sem_init(&semafores.second_semafore, pshared , value_semafore);

 if(0 != error_sem_init) {
	printf("error sem_init 2\n");
	
	int error_destroy_attribute = pthread_attr_destroy(&attribute);
	if(error_destroy_attribute != 0)
	{
       		printf("error of destroy_attribute");
	}

	int error_sem_destroy = sem_destroy(&semafores.first_semafore);
        if(error_sem_destroy != 0) {
		printf("error sem_destroy\n");
        }

	return EXIT_FAILURE;
 }


 int error_create = pthread_create(&id_thread, &attribute, thread, (void*)&semafores);

 int error_detach = pthread_detach(id_thread);
 if(error_detach != 0)
 {
    printf("error detach\n");
    int error_sem_destroy = sem_destroy(&semafores.first_semafore);
    error_sem_destroy = sem_destroy(&semafores.second_semafore);
    if(error_sem_destroy != 0) {
	printf("error sem_destroy\n");
    }
    int error_destroy_attribute = pthread_attr_destroy(&attribute);
    if(error_destroy_attribute != 0)
    {
       printf("error of destroy_attribute");
    }
    return EXIT_FAILURE;
 }

 int error_sem_wait, error_sem_post;
 if(0 == error_create)
 { 
   for(int i = 0; i < number_of_lines_printed; i++)
   {
	error_sem_wait = sem_wait(&semafores.first_semafore);
	if(error_sem_wait != 0)
	{
	    printf("error sem_wait");
	        int error_sem_destroy = sem_destroy(&semafores.first_semafore);
                error_sem_destroy = sem_destroy(&semafores.second_semafore);

    	    if(error_sem_destroy != 0) {
	        printf("error sem_destroy\n");
	    }
	    int error_destroy_attribute = pthread_attr_destroy(&attribute);
    	    if(error_destroy_attribute != 0) {
       	         printf("error of destroy_attribute");
    	    }
    	    return EXIT_FAILURE;
	}


  	printf("text of parent %d\n", i);

	error_sem_post = sem_post(&semafores.second_semafore);
	if(error_sem_post != 0)
	{
	    printf("error sem_post");
	    int error_sem_destroy = sem_destroy(&semafores.first_semafore);
	    error_sem_destroy = sem_destroy(&semafores.second_semafore);
	    if(error_sem_destroy != 0) {
	        printf("error sem_destroy\n");
	    }
	    int error_destroy_attribute = pthread_attr_destroy(&attribute);
    	    if(error_destroy_attribute != 0) {
       	         printf("error of destroy_attribute");
    	    }
    	    return EXIT_FAILURE;
	}

   }
   printf("parent is close\n");

    int error_sem_destroy = sem_destroy(&semafores.first_semafore);
    error_sem_destroy = sem_destroy(&semafores.second_semafore);

   if(error_sem_destroy != 0) {
	 printf("error sem_destroy\n");
	 int error_destroy_attribute = pthread_attr_destroy(&attribute);
         if(error_destroy_attribute != 0)
         {
             printf("error of destroy_attribute");
         }
	 return EXIT_FAILURE;
   }


   int error_destroy_attribute = pthread_attr_destroy(&attribute);
   if(error_destroy_attribute != 0)
   {
      printf("error of destroy_attribute");
      return EXIT_FAILURE;
   }
   pthread_exit(0);
 }
 else
 {
   int error_destroy_attribute = pthread_attr_destroy(&attribute);
   if(error_destroy_attribute != 0)
   {
      printf("error of destroy_attribute");
      printf("error of pthread_create()");  
      int error_sem_destroy = sem_destroy(&semafores.first_semafore);
      error_sem_destroy = sem_destroy(&semafores.second_semafore);

      if(error_sem_destroy != 0) {
           printf("error of sem_destroy");
      }
      return EXIT_FAILURE;
   }


  
    int error_sem_destroy = sem_destroy(&semafores.first_semafore);
    error_sem_destroy = sem_destroy(&semafores.second_semafore);
    if(error_sem_destroy != 0) {
       printf("error of pthread_create()"); 
       printf("error of sem_destroy");
       return EXIT_FAILURE;
    }
    printf("error of pthread_create()"); 
    return EXIT_FAILURE;
 }

}



void* thread(void* pointer_semafore_structure){
 struct semafore_structure* semafores = (struct semafore_structure*)pointer_semafore_structure;

 for(int i = 0; i < number_of_lines_printed; i++)
 {
	sem_wait(&semafores->second_semafore);
	printf("text of child %d\n", i);
	sem_post(&semafores->first_semafore);
 }

 printf("child is closed\n");
 return 0;
}
