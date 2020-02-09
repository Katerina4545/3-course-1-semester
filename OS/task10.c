#include "stdlib.h"
#include "stdio.h"
#include "pthread.h"
#define number_of_lines_printed 10

void* thread(void*);

struct StructMutex {
    pthread_mutex_t mutex_one;
    pthread_mutex_t mutex_two;
    pthread_mutex_t mutex_three;
};

int main(){

    struct StructMutex myStructMutex;

    pthread_t id_thread;
    pthread_attr_t attribute;

    pthread_mutexattr_t attribute_mutex;
    pthread_mutexattr_init(&attribute_mutex);
    pthread_mutexattr_settype(&attribute_mutex, PTHREAD_MUTEX_ERRORCHECK);

    int error_init_1 = pthread_mutex_init(&myStructMutex.mutex_one, &attribute_mutex);
    int error_init_2 = pthread_mutex_init(&myStructMutex.mutex_two, &attribute_mutex);
    int error_init_3 = pthread_mutex_init(&myStructMutex.mutex_three, &attribute_mutex);

    if (error_init_1 != 0 || error_init_2 != 0 || error_init_3 != 0) {

        //destroy attributes
        int error_destroy = pthread_attr_destroy(&attribute);
        if(error_destroy != 0) {
            printf("error from pthread_attr_destroy\n");
        }

        printf("error from pthread_mutex_init\n");
        return EXIT_FAILURE;
    }

    int error_init = pthread_attr_init(&attribute);
    int error_destroy;
    if(error_init != 0)
    {
        printf("error of init");
        return EXIT_FAILURE;
    }

    int error_create = pthread_create(&id_thread, &attribute, thread, &myStructMutex);

    int error_detach = pthread_detach(id_thread);
    if(error_detach != 0)
    {
        printf("error detach\n");
        return EXIT_FAILURE;
    }

    pthread_mutex_lock(&myStructMutex.mutex_three);

    if(0 == error_create)
    {
        for(int i = 0; i < number_of_lines_printed; i++)
        {
            pthread_mutex_unlock(&myStructMutex.mutex_three);

            pthread_mutex_lock(&myStructMutex.mutex_one);
            pthread_mutex_lock(&myStructMutex.mutex_two);
            printf("text of parent %d\n", i);
            pthread_mutex_unlock(&myStructMutex.mutex_one);
            pthread_mutex_unlock(&myStructMutex.mutex_two);

            pthread_mutex_lock(&myStructMutex.mutex_three);
        }
        printf("parent is close\n");

        error_destroy = pthread_attr_destroy(&attribute);
        if(error_destroy != 0)
        {
            printf("error of destroy");
            return EXIT_FAILURE;
        }
        pthread_exit(NULL);
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

        printf("error of pthread_create()");
        pthread_exit(NULL);
        return EXIT_FAILURE;
    }

}

void* thread(void* structMutex){
    struct StructMutex* myStructMutex = (struct StructMutex*)structMutex;
    for(int i = 0; i < number_of_lines_printed; i++)
    {
        pthread_mutex_lock(&(myStructMutex->mutex_three));
        pthread_mutex_lock(&(myStructMutex->mutex_two));
        printf("text of child %d\n", i);
        pthread_mutex_unlock(&(myStructMutex->mutex_two));
        pthread_mutex_unlock(&(myStructMutex->mutex_three));
    }

    printf("child is canceled\n");
    pthread_exit(NULL);
}
