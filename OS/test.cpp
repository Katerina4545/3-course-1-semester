#include <cstdint>
#include <stdio.h>

int main() {
uint16_t checking = 0x0001;
printf("%c\n", ((uint8_t*)&checking)[0] == 1 ? 'L' : 'B');
return 0;
}