#include "stdlib.h"
#include "stdio.h"
#include "pthread.h"
#define amount_of_string 1
#define amount_of_thread 4

void* thread(void* tmp);

int main() {

    pthread_t id_thread[amount_of_thread];
    pthread_attr_t attribute;
    int error_initial = pthread_attr_init(&attribute);
    if (error_initial != 0) {
        printf("error of pthread_attr_init()\n");
        return EXIT_FAILURE;
    }
    char* string_for_print[] = {"1\n", "2\n", "3\n", "4\n"};
    for(int i = 0; i < amount_of_thread; i++) {
        int error_create = pthread_create(&id_thread[i], &attribute, thread, string_for_print[i]);
        if(error_create != 0) {
            //destroy attribute
            int error_destroy = pthread_attr_destroy(&attribute);
            if(error_destroy != 0) {
                printf("error of pthread_create()\n");
                printf("error of pthread_join()\n");
                printf("error of pthread_attr_destroy()\n");
                return EXIT_FAILURE;
            }
            //join for all created threads
            for(int j = 0; j!=i; j++) {
                int error_join = pthread_join(id_thread[j], NULL);
                if(error_join != 0) {
                    printf("error of pthread_join()\n");
                    return EXIT_FAILURE;
                }
            }
        }
    }

    int error_join_1 = pthread_join(id_thread[0], NULL);
    int error_join_2 = pthread_join(id_thread[1], NULL);
    int error_join_3 = pthread_join(id_thread[2], NULL);
    int error_join_4 = pthread_join(id_thread[3], NULL);

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

void* thread(void* string_to_print) {

	if(NULL == string_to_print) {
		printf("error : get NULL pointer\n");
		return NULL;
	}
 
	for(int i = 0; i < amount_of_string; i++) {
		printf("%s", string_to_print);
	}
	printf("\n");
    return NULL;
}
