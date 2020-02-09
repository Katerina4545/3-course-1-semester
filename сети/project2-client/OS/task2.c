#include "stdlib.h" 
#include "stdio.h" 
#include "pthread.h" 
#define number_of_lines_printed 10

void* thread(void* tmp); 

int main(){ 

 pthread_t id_thread; 		
 pthread_attr_t attribute;;

 int error_init = pthread_attr_init(&attribute);
 int error_destroy;
 if(error_init != 0)
 {
   printf("error of init");
   return EXIT_FAILURE;
 }

 int error_create = pthread_create(&id_thread, &attribute, thread, NULL);
 
 if(0 == error_create)
 {
   int error_join = pthread_join(id_thread, NULL);
   if(error_join != 0)
   {
    error_destroy = pthread_attr_destroy(&attribute);
    if(error_destroy != 0)
    {
       printf("error of destroy");
       printf("error of join");
       return EXIT_FAILURE;
    }

     printf("error of join");
     return EXIT_FAILURE;
   }
     
   for(int i = 0; i < number_of_lines_printed; i++)
   {
  	printf("text of parent %d\n", i);
   }

   printf("parent is closed\n");

   error_destroy = pthread_attr_destroy(&attribute);
   if(error_destroy != 0)
   {
      printf("error of destroy");
      return EXIT_FAILURE;
   }
   return EXIT_SUCCESS;
 }
 else
 {
   error_destroy = pthread_attr_destroy(&attribute);
   if(error_destroy != 0)
   {
      printf("error of destroy");
      printf("error of pthread_create()");
      return EXIT_FAILURE;
   }

   printf("error pthread_create()");
   return EXIT_FAILURE;
 }

}


void* thread(void* tmp){

 for(int i = 0; i < number_of_lines_printed; i++)
 {
	printf("text of child %d\n", i);
 }

 printf("child is closed\n");
 return 0; 
}



