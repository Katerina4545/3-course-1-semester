#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <unistd.h>

#define COUNT_OF_PHILOSOPHER 5
#define TIME_TO_EAT 30000
#define COMMON_FOOD 50

struct struct_mutex {
    pthread_mutex_t mutex_for_forks[COUNT_OF_PHILOSOPHER];
    pthread_t id_thread_philosopher[COUNT_OF_PHILOSOPHER];
    pthread_mutex_t mutex_for_food;
    pthread_mutex_t mutex_for_take_forks;
    int id;
};

void *philosopher_thread(void *arg);

int take_food_on_table(struct struct_mutex *myStructMutex);

void get_fork(int phil, int fork, char *hand, struct struct_mutex *myStructMutex);

void put_forks(int f1, int f2, struct struct_mutex *myStructMutex);

int destroy_mutex(pthread_mutex_t *mutex);

int main() {

    struct struct_mutex myStructMutex;

    int i;

    //initialization attributes
    pthread_attr_t attribute;
    int error_attribute = pthread_attr_init(&attribute);
    if (error_attribute != 0) {
        printf("error from pthread_attr_init\n");
        return EXIT_FAILURE;
    }

    //initialization mutex

    int error_init_1 = pthread_mutex_init(&myStructMutex.mutex_for_food, NULL);
    int error_init_2 = pthread_mutex_init(&myStructMutex.mutex_for_take_forks, NULL);

    if (error_init_1 != 0 || error_init_2 != 0) {

        //destroy attributes
        int error_destroy = pthread_attr_destroy(&attribute);
        if (error_destroy != 0) {
            printf("error from pthread_attr_destroy\n");
        }

        printf("error from pthread_mutex_init\n");
        return EXIT_FAILURE;
    }


    for (i = 0; i < COUNT_OF_PHILOSOPHER; i++) {
        error_init_1 = pthread_mutex_init(&myStructMutex.mutex_for_forks[i], NULL);
        if (error_init_1 != 0) {

            //destroy attributes
            int error_destroy = pthread_attr_destroy(&attribute);
            if (error_destroy != 0) {
                printf("error from pthread_attr_destroy\n");
            }

            //destroy all initialized mutex
            destroy_mutex(&myStructMutex.mutex_for_food);
            destroy_mutex(&myStructMutex.mutex_for_take_forks);
            for (int j = i - 1; j >= 0; j--) {
                destroy_mutex(&(myStructMutex.mutex_for_forks[j]));
            }

            printf("error from pthread_mutex_init\n");
            return EXIT_FAILURE;
        }
    }

    //create threads
    for (i = 0; i < COUNT_OF_PHILOSOPHER; i++) {
        myStructMutex.id = i;
        int error_create = pthread_create(&(myStructMutex.id_thread_philosopher[i]), &attribute, philosopher_thread,
                                          &myStructMutex);
        sleep(1);
        if (error_create != 0) {
            //destroy attributes
            int error_destroy = pthread_attr_destroy(&attribute);
            if (error_destroy != 0) {
                printf("error from pthread_attr_destroy\n");
            }

            //destroy all initialized mutex
            destroy_mutex(&myStructMutex.mutex_for_food);
            destroy_mutex(&myStructMutex.mutex_for_take_forks);
            for (i = 0; i < COUNT_OF_PHILOSOPHER; i++) {
                destroy_mutex(&(myStructMutex.mutex_for_forks[i]));
            }

            //join all created threads
            for (int j = i - 1; j >= 0; j--) {
                int error = pthread_join(myStructMutex.id_thread_philosopher[j], NULL);
                if (error != 0) {
                    printf("error from join\n");
                }
            }

            printf("error from pthread_create\n");
            return EXIT_FAILURE;
        }
    }

    //join threads
    for (i = 0; i < COUNT_OF_PHILOSOPHER; i++) {
        int error_join = pthread_join(myStructMutex.id_thread_philosopher[i], NULL);
        if (error_join != 0) {

            //destroy mutex
            destroy_mutex(&myStructMutex.mutex_for_food);
            destroy_mutex(&myStructMutex.mutex_for_take_forks);
            for (i = 0; i < COUNT_OF_PHILOSOPHER; i++) {
                destroy_mutex(&(myStructMutex.mutex_for_forks[i]));
            }

            //destroy attributes
            int error_destroy = pthread_attr_destroy(&attribute);
            if (error_destroy != 0) {
                printf("error from pthread_attr_destroy\n");
            }

            printf("error from join\n");
            return EXIT_FAILURE;
        }
    }

    pthread_exit(NULL);
    return EXIT_SUCCESS;
}

void *philosopher_thread(void *arg) {

    if (arg == NULL) {
        printf("NULL pointer exception in philosopher_thread");
        return NULL;
    }

    struct struct_mutex *myStructMutex = (struct struct_mutex *) arg;
    int id = myStructMutex->id;
    int left_fork, right_fork, f;

    printf("Philosopher %d sitting down to dinner.\n", id);
    right_fork = id;
    left_fork = id + 1;

    /* Wrap around the forks. */
    if (left_fork == COUNT_OF_PHILOSOPHER)
        left_fork = 0;

    while (f = take_food_on_table(myStructMutex)) {

        printf("Philosopher %d: get dish %d.\n", id, f);
        pthread_mutex_lock(&(myStructMutex->mutex_for_take_forks));
        get_fork(id, right_fork, "right", myStructMutex);
        get_fork(id, left_fork, "left ", myStructMutex);
        pthread_mutex_unlock(&(myStructMutex->mutex_for_take_forks));

        printf("Philosopher %d: eating.\n", id);
        usleep(TIME_TO_EAT * (COMMON_FOOD - f + 1));
        put_forks(left_fork, right_fork, myStructMutex);
    }
    printf("Philosopher %d is done eating.\n", id);
    return NULL;
}

int take_food_on_table(struct struct_mutex *myStructMutex) {

    if (myStructMutex == NULL) {
        printf("NULL pointer exception in take_food_on_table\n");
        return EXIT_FAILURE;
    }

    static int food = COMMON_FOOD;
    int myfood;

    pthread_mutex_lock(&(myStructMutex->mutex_for_food));
    if (food > 0) {
        food--;
    }
    myfood = food;
    printf("food is %d\n", food);
    pthread_mutex_unlock(&(myStructMutex->mutex_for_food));
    return myfood;
}

void get_fork(int phil, int fork, char *hand, struct struct_mutex *myStructMutex) {

    if (hand == NULL) {
        printf("NULL pointer exception from get_fork");
        return;
    }

    if (myStructMutex == NULL) {
        printf("NULL pointer exception from get_fork");
        return;
    }

    if ((phil < 0) || (fork < 0)) {
        printf("negative value from get_fork");
        return;
    }

    pthread_mutex_lock(&(myStructMutex->mutex_for_forks[fork]));
    printf("Philosopher %d: got %s fork %d\n", phil, hand, fork);
}

void put_forks(int f1, int f2, struct struct_mutex *myStructMutex) {

    if (myStructMutex == NULL) {
        printf("NULL pointer exception from put_forks");
        return;
    }

    if ((f1 < 0) || (f2 < 0)) {
        printf("negative value from put_forks");
        return;
    }

    pthread_mutex_unlock(&(myStructMutex->mutex_for_forks[f1]));
    pthread_mutex_unlock(&(myStructMutex->mutex_for_forks[f2]));
}

int destroy_mutex(pthread_mutex_t *mutex) {
    int error_destroy = pthread_mutex_destroy(mutex);
    if (error_destroy != 0) {
        printf("error from pthread_mutex_destroy\n");
    }
}
