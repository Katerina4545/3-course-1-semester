#include "stdlib.h"
#include "stdio.h"
#include "pthread.h"
#define number_of_lines_printed 10

void* thread(void* string_to_print);

char string_to_print[4][100];

int main() {

    for(int i = 0; i < 4; i++) {
        printf("enter %i string\n", i);
        scanf("%s", string_to_print[i]);
    }

    pthread_t id_first_thread, id_second_thread, id_third_thread, id_fourth_thread;
    pthread_attr_t attribute;
    int error_initial = pthread_attr_init(&attribute);
    if (error_initial != 0) {
        printf("error of pthread_attr_init()\n");
        return EXIT_FAILURE;
    }
    int error_dest;

    int error_create_1 = pthread_create(&id_first_thread, &attr, thread, string_to_print[0]);
    int error_create_2 = pthread_create(&id_second_thread, &attr, thread, string_to_print[1]);
    int error_create_3 = pthread_create(&id_third_thread, &attr, thread, string_to_print[2]);
    int error_create_4 = pthread_create(&id_fourth_thread, &attr, thread, string_to_print[3]);

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

        int error_destroy = pthread_attr_destroy(&attr);
        if(error_destroy != 0) {
            printf("error of pthread_attr_destroy()\n");
            return EXIT_FAILURE;
        }

        return EXIT_FAILURE;
    }

    int error_destroy = pthread_attr_destroy(&attr);
    if(error_destroy != 0) {
        printf("error of pthread_attr_destroy()\n");
        return EXIT_FAILURE;
    }


}

void thread(void* string_to_print) {
    printf("%s\n", string_to_print);
    return 0;
}