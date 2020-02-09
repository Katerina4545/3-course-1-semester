package Common;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class SnakeData {
    private final int ALL_DOTS = 400;           //сколько игровых единиц может поместиться в игровое поле
    private final int DOT_SIZE = 16;
    public int[] x = new int[ALL_DOTS];         //массив для х-позиции змейки
    public int[] y = new int[ALL_DOTS];
    public int dots;                            //размер змейки в текущий момент времени
    public int direction_number;
    public boolean left;                //3
    public boolean right;                //4
    public boolean up;                  //1
    public boolean down;                //2
    public boolean inGame = true;

    public SnakeData(){
        dots = 3;
        //начальная позиция для змейки:
        for(int i = 0; i < dots; i++) {
            x[i] = 48 - i*DOT_SIZE;
            y[i] = 48;
        }
        //начальное направление для змейки
        direction_number = 4;
        left = false;
        right = true;
        up = false;
        down = false;
    }

    public SnakeData(boolean tmp){

    }

    public void changeDirection(String direction){
        switch(direction){
            case ("Left"):
                if(!right){
                    left = true;
                    up = false;
                    down = false;
                    direction_number = 3;
                }
                break;
            case ("Right"):
                if(!left){
                    right = true;
                    up = false;
                    down = false;
                    direction_number = 4;
                }
                break;
            case ("Up"):
                if(!down){
                    right = false;
                    up = true;
                    left = false;
                    direction_number = 1;
                }
                break;
            case ("Down"):
                if(!up){
                    right = false;
                    down = true;
                    left = false;
                    direction_number = 2;
                }
                break;
        }
    }


}
