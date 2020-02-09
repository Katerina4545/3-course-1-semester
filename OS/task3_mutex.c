#include "stdlib.h"
#include "stdio.h"
#include "pthread.h"
#define amount_of_string 3

void* thread(void* tmp);

char string_to_print[3][50];
pthread_mutex_t mutex_for_string;
int count_working_thread = 0;

int main() {


    pthread_t id_first_thread, id_second_thread, id_third_thread, id_fourth_thread;
    pthread_attr_t attribute;
    int error_initial = pthread_attr_init(&attribute);
    if (error_initial != 0) {
        printf("error of pthread_attr_init()\n");
        return EXIT_FAILURE;
    }
	
	
	pthread_mutex_init(&mutex_for_string, NULL);
	
    int error_create_1 = pthread_create(&id_first_thread, &attribute, thread, NULL);
	
    int error_create_2 = pthread_create(&id_second_thread, &attribute, thread, NULL);
	
    int error_create_3 = pthread_create(&id_third_thread, &attribute, thread, NULL);
	
    int error_create_4 = pthread_create(&id_fourth_thread, &attribute, thread, NULL);

    if( error_create_1 != 0 || error_create_2 != 0 || error_create_3 != 0 || error_create_4 != 0 ) {
        printf("error of pthread_create()\n");
        return EXIT_FAILURE;
    }

    int error_join_1 = pthread_join(id_first_thread, NULL);
    int error_join_2 = pthread_join(id_second_thread, NULL);
    int error_join_3 = pthread_join(id_third_thread, NULL);
    int error_join_4 = pthread_join(id_fourth_thread, NULL);

    if(error_join_1 != 0 || error_join_2 != 0 || error_join_3 != 0 || error_join_4 != 0 ) {
        printf("error of pthread_join()\n");

        int error_destroy = pthread_attr_destroy(&attribute);
        if(error_destroy != 0) {
            printf("error of pthread_attr_destroy()\n");
            return EXIT_FAILURE;
        }

        return EXIT_FAILURE;
    }

    int error_destroy = pthread_attr_destroy(&attribute);
    if(error_destroy != 0) {
        printf("error of pthread_attr_destroy()\n");
        return EXIT_FAILURE;
    }
	return EXIT_SUCCESS;
}

void* thread(void* tmp) {
	
	pthread_mutex_lock(&mutex_for_string);
	count_working_thread++;
	for(int i = 0; i < amount_of_string; i++) {
        
		printf("enter %i string for %i thread\n", i, count_working_thread);
       		scanf("%s", string_to_print[i]);
  
  	}
	printf("\n");

	for(int i = 0; i< amount_of_string; i++) {
		printf("%s\n", string_to_print[i]);
	}
	printf("\n");

	pthread_mutex_unlock(&mutex_for_string);
	
    return NULL;
}
