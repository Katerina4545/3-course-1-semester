#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <string.h>
#include <signal.h>

#define count_iterations 200000000

int globalThreadFlag = 0;

struct argument_thread {
    int number_thread;
    int start_iteration;
    int finish_iteration;
    double path_result;
};

void* function_thread (void*);
void signalHandler(int tmp){
    printf("\ncatched a signal\n");
    globalThreadFlag = 1;
    printf("tmp = %d\nglobalThreadFlag = %d\n", tmp, globalThreadFlag);
}

int main(int argc, char* argv[]) {

    ////set handler for signal SIGINT
    signal(SIGINT, &signalHandler);
    // if(sigError == SIG_ERR) {
    //     printf("error of signal\n");
    //     return EXIT_FAILURE;
    // }
    ////
    int count_threads = 0;
    if(argc >= 2) {
        int userInput = atoi(argv[1]);
        if(userInput < 1) {
            printf("error input data\nenter count_threads again please\n");
            return EXIT_FAILURE;
        }
        count_threads = atoi(argv[1]);
    } else {
        printf("error input data\nenter count_threads again please\n");
        return EXIT_FAILURE;
    }

    struct argument_thread** ArgumentThread = (struct argument_thread**)calloc(count_threads, sizeof(struct argument_thread*));
    if(ArgumentThread == NULL) {
        printf("error calloc\n");
        return EXIT_FAILURE;
    }

    //initial array with count iterations for each threads
    int whole_path = count_iterations / count_threads;
    int remains_path = count_iterations % count_threads;

    int start_iteration = 0, i;
    for(i = 0; i < count_threads - 1; i++) {
        ArgumentThread[i] = (struct argument_thread*)calloc(1, sizeof(struct argument_thread));
        ArgumentThread[i]->start_iteration = start_iteration;
        ArgumentThread[i]->finish_iteration = start_iteration + whole_path - 1;
        start_iteration = start_iteration + whole_path;
    }

    ArgumentThread[i] = (struct argument_thread*)calloc(1, sizeof(struct argument_thread));
    ArgumentThread[i]->start_iteration = start_iteration;
    ArgumentThread[i]->finish_iteration = start_iteration + whole_path + remains_path - 1;

    //created threads
    pthread_attr_t attribute;
    int error_attribute = pthread_attr_init(&attribute);
    if(error_attribute != 0) {
        printf("error from pthread_attr_init\n");
        return EXIT_FAILURE;
    }

    pthread_t id_thread[count_threads];
    for(int i = 0; i < count_threads; i++) {
        ArgumentThread[i]->number_thread = i;
        int error_create = pthread_create(&id_thread[i], &attribute, function_thread, ArgumentThread[i]);
        if(error_create != 0) {
            //destroy attribute
            int error_destroy = pthread_attr_destroy(&attribute);
            if(error_destroy != 0) {
                printf("error from pthread_attr_destroy\n");
            }

            //join created threads
            for(int j=i-1; j>=0; j--){
                int error = pthread_join(id_thread[j], NULL);
                if(error != 0) {
                    printf("error from join\n");
                }
            }

            printf("error from created\n");
            return EXIT_FAILURE;
        }
    }

    //join threads
    double result = 0;
    for (int i = 0; i < count_threads; i++) {
        void* status;
        int error_join = pthread_join(id_thread[i], &status);

        struct argument_thread* resultThread = (struct argument_thread*)status;
        result += (resultThread->path_result)*4;

        if(error_join != 0) {

            //destroy attributes
            int error_destroy = pthread_attr_destroy(&attribute);
            if(error_destroy != 0) {
                printf("error from pthread_attr_destroy\n");
            }

            printf("error from join\n");
            return EXIT_FAILURE;
        }
    }

    printf("\nRESULT IS %0.50lf\n", result);

    for(int i = 0; i < count_threads; i++) {
        free(ArgumentThread[i]);
    }
    free(ArgumentThread);

    return EXIT_SUCCESS;
}

void* function_thread (void* arg) {

    if(arg == NULL) {
        printf("NULL pointer exception of function_thread\n");
        return NULL;
    }

    struct argument_thread* ArgumentThread = (struct argument_thread*)arg;
    int number_thread = ArgumentThread->number_thread;


    printf("thread number %d started \n", number_thread);

    int start_iteration =  ArgumentThread->start_iteration;
    int finish_iteration = ArgumentThread->finish_iteration;
    ArgumentThread->path_result = 0;

    //degug
    printf("%d:start_iteration is %d\n", number_thread, start_iteration);
    printf("%d:finish_iteration is %d\n", number_thread, finish_iteration);
    //

    ////code for check signal
    int checkSignalIteration = start_iteration + 100000;
    ////

    for(int i = start_iteration; i <=  finish_iteration; i++) {

        ////code for check signal
        if(i == checkSignalIteration) {
            if(globalThreadFlag) {
                printf("\nthread number %d in if \n", number_thread);
                printf("iteation is %d \n", i);
                printf("path is %0.50lf \n", ArgumentThread->path_result);
                printf("thread number %d finished \n", number_thread);
                pthread_exit((void*)ArgumentThread);
            } else {
                checkSignalIteration += 100000;
            }
        } 
        ////

        ArgumentThread->path_result += 1.0/(i*4.0 + 1.0);
        ArgumentThread->path_result -= 1.0/(i*4.0 + 3.0);
    }

    printf("thread number %d finished \n", number_thread);

    pthread_exit((void*)ArgumentThread);
}
