#include "stdlib.h" 
#include "stdio.h" 
#include "pthread.h" 
#define number_of_lines_printed 10

void* thread(void* tmp); 

int main(){ 

 pthread_t id_thread; 			
 pthread_attr_t attribute; 	

 int error_init = pthread_attr_init(&attribute);
 int error_destroy;
 if(error_init != 0)
 {
   printf("error of init");
   return EXIT_FAILURE;
 }

 int error_create = pthread_create(&id_thread, &attribute, thread, NULL);

 int error_detach = pthread_detach(id_thread);
 if(error_detach != 0)
 {
    printf("error detach\n");
    return EXIT_FAILURE;
 }

 if(0 == error_create)
 {
   for(int i = 0; i < number_of_lines_printed; i++)
   {
  	printf("text of parent %d\n", i);
   }
   printf("parent is close\n");
   error_destroy = pthread_attr_destroy(&attribute);
   if(error_destroy != 0)
   {
      printf("error of destroy");
      return EXIT_FAILURE;
   }
   pthread_exit(0);
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

   printf("error of pthread_create()"); 
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
