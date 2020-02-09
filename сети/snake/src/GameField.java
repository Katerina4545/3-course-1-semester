import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class GameField extends JPanel implements ActionListener {
    private final int SIZE = 320;               //размер игрового поля
    private final int DOT_SIZE = 16;            //размер одной единицы змейки в пикселях (и также размер одной еды)
    private final int ALL_DOTS = 400;           //сколько игровых единиц может поместиться в игровое поле
    private Image dot;
    private Image apple;
    private int appleX;                         //x-позиция яблока
    private int appleY;
    private int[] x = new int[ALL_DOTS];        //массив для х-позиции змейки
    private int[] y = new int[ALL_DOTS];
    private int dots;                           //размер змейки в текущий момент времени
    private Timer timer;
    private boolean left = false;
    private boolean right = true;
    private boolean up = false;
    private boolean down = false;
    private boolean inGame = true;

    public GameField() {
        setBackground(Color.white);
        loadImages();
        initialGame();
        addKeyListener(new FileKeyListener());
        setFocusable(true);
    }

    //TODO: загружает картинки
    public void loadImages(){
        ImageIcon imageIcaonApple = new ImageIcon("apple.png");
        apple = imageIcaonApple.getImage();
        ImageIcon imageIcaonDot = new ImageIcon("dot.png");
        dot = imageIcaonDot.getImage();
    }
    //TODO: задает начаьные параметры для игры
    public void initialGame() {
        //начальная длина змейки
        dots=3;
        //начальная позиция для змейки:
        for(int i = 0; i < dots; i++) {
            x[i] = 48 - i*DOT_SIZE;
            y[i] = 48;
        }
        //запускаем игровой таймер
        timer = new Timer(250, this);
        timer.start();
        //создаём яблоко
        createApple();
    }
    //TODO: создаёт яблоко
    public void createApple() {
        //инициализирует координаты яблока
        appleX = new Random().nextInt(20) * DOT_SIZE;
        appleY = new Random().nextInt(20) * DOT_SIZE;
    }

    //TODO:метод, который вызывается каждый раз когда тикает таймер (раз в 250 мс)
    @Override
    public void actionPerformed(ActionEvent e) {
        /*
        проверяем на
        1. столкновение со стенками
        2. встреча яблока => да - увеличить змейку + сгенерировать яблоко
        3. двигать змейку в заданном направлении
        4. перерисовывать поле
         */
        if(inGame){
            checkApple();
            checkCollisions();
            move();
        }
        repaint();
    }

    //TODO:проверяет не встретила ли голова яблоко, если да, то увеличиваем длину змейки, перерисовываем яблоко
    public void checkApple() {
        if(x[0] == appleX && y[0] == appleY){
            dots++;
            createApple();
        }
    }
    //TODO:проверяет не встретила ли голова саму себя
    public void checkCollisions() {
        for (int i = dots; i > 0 ; i--) {
            if(i>4 && x[0]==x[i] && y[0]==y[i]){
                inGame=false;
            }
        }
    }

    //TODO:двигает змейку
    public void move(){
        //сдвиг в массивах x, y
        for(int i = dots; i > 0; i--){
            x[i] = x[i-1];
            y[i] = y[i-1];
        }
        //сдвиг головы
        if(left){
            x[0] -= DOT_SIZE;
        }
        if(right){
            x[0] += DOT_SIZE;
        }
        if(up){
            y[0] -= DOT_SIZE;
        }
        if(down){
            y[0] += DOT_SIZE;
        }
        //проверка: коснулись стенки
        checkWall();

    }

    //TODO: проверка: коснулись стенки - да: двигаем голову по-другому
    public void checkWall() {
        if(x[0] > SIZE){
            x[0] = 0;
        }
        if(x[0] < 0){
            x[0] = SIZE;
        }
        if(y[0] > SIZE){
            y[0] = 0;
        }
        if(y[0] < 0){
            y[0] = SIZE;
        }
    }

    //TODO: отрисовка змейки, яблока, 'game over'
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(inGame){
            g.drawImage(apple, appleX, appleY, this);
            for(int i=0; i<dots; i++){
                g.drawImage(dot, x[i], y[i], this);
            }
        }else {
            String str = "GAME OVER!";
            //Font f = new Font("Arial", 14, Font.BOLD);
            g.setColor(Color.darkGray);
            //g.setFont(f);
            g.drawString(str, 125, SIZE/2);
        }
    }

    //TODO: нажатие клавиш переопределяем
    class FileKeyListener extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e) {
            super.keyPressed(e);
            int key = e.getKeyCode();
            switch(key){
                case(KeyEvent.VK_LEFT):
                    if(!right){
                        left = true;
                        up = false;
                        down = false;
                    }
                    break;
                case(KeyEvent.VK_RIGHT):
                    if(!left){
                        right = true;
                        up = false;
                        down = false;
                    }
                    break;
                case(KeyEvent.VK_UP):
                    if(!down){
                        right = false;
                        up = true;
                        left = false;
                    }
                    break;
                case(KeyEvent.VK_DOWN):
                    if(!up){
                        right = false;
                        down = true;
                        left = false;
                    }
                    break;
            }
        }
    }
}
