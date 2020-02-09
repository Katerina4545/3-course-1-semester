#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <string.h>

#define count_iterations 200000000

struct argument_thread {
    int number_thread;
    int start_iteration;
    int finish_iteration;
    double path_result;
};

void* calculate_partial_sum (void*);

int main(int argc, char* argv[]) {

    int count_threads = 0;
    int correct_number_argument = 2;
    int input_number_threads = 1;
    int correct_number_threads = 1;

    if(argc >= correct_number_argument) {
        int userInput = atoi(argv[input_number_threads]);
        if(userInput < correct_number_threads) {
            printf("error input data\nenter count_threads again please\n");
            return EXIT_FAILURE;
        }
        count_threads = atoi(argv[input_number_threads]);
    } else {
        printf("error input data\nenter count_threads again please\n");
        return EXIT_FAILURE;
    }

    struct argument_thread** ArgumentThread = (struct argument_thread**)calloc(count_threads, sizeof(struct argument_thread*));
    if(ArgumentThread == NULL) {
        printf("error calloc\n");

	for(int i = 0; i < count_threads; i++) {
             free(ArgumentThread[i]);
        }
        free(ArgumentThread);

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
        
	for(int i = 0; i < count_threads; i++) {
             free(ArgumentThread[i]);
        }
        free(ArgumentThread);

        return EXIT_FAILURE;
    }

    pthread_t id_thread[count_threads];
    for(int i = 0; i < count_threads; i++) {
        ArgumentThread[i]->number_thread = i;
        int error_create = pthread_create(&id_thread[i], &attribute, calculate_partial_sum, ArgumentThread[i]);
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

	    for(int i = 0; i < count_threads; i++) {
                free(ArgumentThread[i]);
            }
            free(ArgumentThread);

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

	    for(int i = 0; i < count_threads; i++) {
                free(ArgumentThread[i]);
            }
            free(ArgumentThread);

            printf("error from join\n");
            return EXIT_FAILURE;
        }
    }

    printf("RESULT IS %0.50lf\n", result);

    for(int i = 0; i < count_threads; i++) {
        free(ArgumentThread[i]);
    }
    free(ArgumentThread);

    return EXIT_SUCCESS;
}

void* calculate_partial_sum (void* arg) {

    if(NULL == arg) {
        printf("NULL pointer exception of function_thread\n");
        return NULL;
    }

    struct argument_thread* ArgumentThread = (struct argument_thread*)arg;
    int number_thread = ArgumentThread->number_thread;


//    printf("thread number %d started \n\n", number_thread);

    int start_iteration =  ArgumentThread->start_iteration;
    int finish_iteration = ArgumentThread->finish_iteration;
    ArgumentThread->path_result = 0;

    for(int i = start_iteration; i <=  finish_iteration; i++) {
        ArgumentThread->path_result += 1.0/(i*4.0 + 1.0);
        ArgumentThread->path_result -= 1.0/(i*4.0 + 3.0);
    }

//    printf("thread number %d finished \n\n", number_thread);

    pthread_exit((void*)ArgumentThread);
}
