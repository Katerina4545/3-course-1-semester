#include "stdlib.h"
#include "stdio.h"

int main(){
	char string[3][15];
	string = {"hello", "user", "23"};
	//string = "my name is...";
	printf("%s\n", string[0]);
	return 0;
}
