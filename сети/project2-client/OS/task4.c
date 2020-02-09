#include "unistd.h"
#include "stdlib.h"
#include "stdio.h"
#include "pthread.h"

#define amount_of_string 4

void* thread(void* string_to_print);

int main() {
    pthread_t id_child;
    pthread_attr_t attribute;
    int error_initial = pthread_attr_init(&attribute);
    if (error_initial != 0) {
        printf("error of pthread_attr_init()\n");
        return EXIT_FAILURE;
    }

    char* string_for_child = {"I'm a child and I'm alive\n"};
    int error_create = pthread_create(&id_child, &attribute, thread, string_for_child);

    if(error_create != 0) {
        printf("error of pthread_create()\n");
        int error_destroy = pthread_attr_destroy(&attribute);
        if (error_destroy != 0) {
            printf("error of pthread_attr_destroy()\n");
            return EXIT_FAILURE;
        }
        return EXIT_FAILURE;
    }

    printf("I'm a parent and I'm going to sleep\n");
    sleep(2);
    printf("I'm a parent and I'm going to cancel the child\n");
    int error_cancel = pthread_cancel(id_child);
    if(error_cancel != 0) {
        printf("error of pthread_cancel()\n");
        int error_destroy = pthread_attr_destroy(&attribute);
        if (error_destroy != 0) {
            printf("error of pthread_attr_destroy()\n");
            return EXIT_FAILURE;
        }
        return EXIT_FAILURE;
    }

    return EXIT_SUCCESS;
}

void* thread(void*  string_to_print) {
    while(1) {
        printf("%s", string_to_print);
        sleep(1);
    }
}
