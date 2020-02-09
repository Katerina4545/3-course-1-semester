#include "stdlib.h"
#include "stdio.h"
#include "pthread.h"
#define amount_of_string 4

void* thread(void* tmp);

int main() {

    pthread_t id_first_thread, id_second_thread, id_third_thread, id_fourth_thread;
    pthread_attr_t attribute;
    int error_initial = pthread_attr_init(&attribute);
    if (error_initial != 0) {
        printf("error of pthread_attr_init()\n");
        return EXIT_FAILURE;
    }
    char* string_for_first_thread[] = {"1: hello, i'm first", "1: how are you?", "1: good job, man", "1: bye"};
    int error_create_1 = pthread_create(&id_first_thread, &attribute, thread, string_for_first_thread);

    if(error_create_1 != 0) {

        int error_destroy = pthread_attr_destroy(&attribute);
        if(error_destroy != 0) {
            printf("error of pthread_create()\n");
            printf("error of pthread_attr_destroy()\n");
            return EXIT_FAILURE;
        }

        printf("error of pthread_create()\n");
        return EXIT_FAILURE;
    }

    char* string_for_second_thread[] = {"2: hello, i'm second", "2: i'm fine, thanks", "2: thank you", "2: bye"};
    int error_create_2 = pthread_create(&id_second_thread, &attribute, thread, string_for_second_thread);

    if(error_create_2 != 0) {

        int error_join_1 = pthread_join(id_first_thread, NULL);
        if(error_join_1 != 0) {

            int error_destroy = pthread_attr_destroy(&attribute);
            if(error_destroy != 0) {
                printf("error of pthread_create()\n");
                printf("error of pthread_join()\n");
                printf("error of pthread_attr_destroy()\n");
                return EXIT_FAILURE;
            }
            printf("error of pthread_join()\n");
            return EXIT_FAILURE;
        }
        printf("error of pthread_create()\n");
        return EXIT_FAILURE;
    }

    char* string_for_third_thread[] = {"3: hello, i'm third", "3: heeeey", "3: o-la-la", "3: bye"};
    int error_create_3 = pthread_create(&id_third_thread, &attribute, thread, string_for_third_thread);

    if(error_create_3 != 0) {

        int error_join_1 = pthread_join(id_first_thread, NULL);
        if(error_join_1 != 0) {

            int error_destroy = pthread_attr_destroy(&attribute);
            if(error_destroy != 0) {
                printf("error of pthread_create()\n");
                printf("error of pthread_join()\n");
                printf("error of pthread_attr_destroy()\n");
                return EXIT_FAILURE;
            }
            printf("error of pthread_join()\n");
            return EXIT_FAILURE;
        }

        int error_join_2 = pthread_join(id_second_thread, NULL);
        if(error_join_2 != 0) {

            int error_destroy = pthread_attr_destroy(&attribute);
            if(error_destroy != 0) {
                printf("error of pthread_create()\n");
                printf("error of pthread_join()\n");
                printf("error of pthread_attr_destroy()\n");
                return EXIT_FAILURE;
            }
            printf("error of pthread_join()\n");
            return EXIT_FAILURE;
        }

        printf("error of pthread_create()\n");
        return EXIT_FAILURE;
    }

    char* string_for_fourth_thread[] = {"4: hello, i'm fourth", "4: I forgot everything!", "4: what should I do?", "4: bye"};
    int error_create_4 = pthread_create(&id_fourth_thread, &attribute, thread, string_for_fourth_thread);

    if(error_create_4 != 0) {

        int error_join_1 = pthread_join(id_first_thread, NULL);
        if(error_join_1 != 0) {

            int error_destroy = pthread_attr_destroy(&attribute);
            if(error_destroy != 0) {
                printf("error of pthread_create()\n");
                printf("error of pthread_join()\n");
                printf("error of pthread_attr_destroy()\n");
                return EXIT_FAILURE;
            }
            printf("error of pthread_join()\n");
            return EXIT_FAILURE;
        }

        int error_join_2 = pthread_join(id_second_thread, NULL);
        if(error_join_2 != 0) {

            int error_destroy = pthread_attr_destroy(&attribute);
            if(error_destroy != 0) {
                printf("error of pthread_create()\n");
                printf("error of pthread_join()\n");
                printf("error of pthread_attr_destroy()\n");
                return EXIT_FAILURE;
            }
            printf("error of pthread_join()\n");
            return EXIT_FAILURE;
        }

        int error_join_3 = pthread_join(id_third_thread, NULL);
        if(error_join_3 != 0) {

            int error_destroy = pthread_attr_destroy(&attribute);
            if(error_destroy != 0) {
                printf("error of pthread_create()\n");
                printf("error of pthread_join()\n");
                printf("error of pthread_attr_destroy()\n");
                return EXIT_FAILURE;
            }
            printf("error of pthread_join()\n");
            return EXIT_FAILURE;
        }
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

void* thread(void* string_to_print) {

	if(NULL == string_to_print) {
		printf("error : get NULL pointer\n");
		return NULL;
	}
 
	for(int i = 0; i < amount_of_string; i++) {
		printf("%s\n", ((char**)string_to_print)[i]);
	}
	printf("\n");
    return NULL;
}
