package Common;

import Common.SnakeData;
import Network.SenderAsNormal;
import me.ippolitov.fit.snakes.SnakesProto;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

public class GameField extends JPanel implements ActionListener {
    private final int SIZE = 320;               //размер игрового поля
    private final int DOT_SIZE = 16;            //размер одной единицы змейки в пикселях (и также размер одной еды)
    private Image dot;
    private Image apple;
    private int appleX;                         //x-позиция яблока
    private int appleY;
    private Timer timer;
    private ArrayList<SnakeData> snakes = new ArrayList<>();      //экземпляры классов змеек, на 0-ой позиции змейка, которую изменяет листенер нажатия клавиш
    private Integer count = 1;                                    //сколько змеек сейчас в игре
    private SnakesProto.NodeRole role;
    private SenderAsNormal senderNormal;

    //ToDo: конструктор для Master
    public GameField(SnakesProto.NodeRole role_) {
        role = role_;
        setBackground(Color.white);
        loadImages();
        initialGame();
        addKeyListener(new FileKeyListener());
        setFocusable(true);
    }

    //ToDo: конструктор для Join
    public GameField(ArrayList<SnakeData> snakes_, int[] appleLocation, SnakesProto.NodeRole role_, SenderAsNormal sender) {
        role = role_;
        senderNormal = sender;
        //устанавливаем полученные данные для поля
        createSnake();
        pushSnake(snakes_);
        appleX = appleLocation[0];
        appleY = appleLocation[1];
        count = snakes.size();

        //создаем поле
        setBackground(Color.white);
        loadImages();
        initialJoinGame();
        addKeyListener(new FileKeyListener());
        setFocusable(true);
    }

    //ToDo: создает первую змейку для Join и кладет её в массив
    private void createSnake() {
        SnakeData snake = new SnakeData();
        snakes.add(snake);
    }

    //ToDo: добавляет поступившие змейки в общий массив
    private void pushSnake(ArrayList<SnakeData> snakes_) {
        for (int i = 0; i < snakes_.size(); i++) {
            snakes.add(snakes_.get(i));
        }
    }

    //TODO: загружает картинки
    private void loadImages() {
        ImageIcon imageIconApple = new ImageIcon(getClass().getResource("/apple.png"));
        apple = imageIconApple.getImage();
        ImageIcon imageIconDot = new ImageIcon(getClass().getResource("/dot.png"));
        dot = imageIconDot.getImage();
    }

    //TODO: задает начаьные параметры для игры Master
    private void initialGame() {
        //создаем первую змейку - змейку мастера
        SnakeData snake = new SnakeData();
        snakes.add(snake);

        //запускаем игровой таймер
        timer = new Timer(250, this);
        timer.start();
        //создаём яблоко
        createApple();
    }

    //TODO: задает начаьные параметры для игры Join
    private void initialJoinGame() {
        //запускаем игровой таймер
        timer = new Timer(250, this);
        timer.start();
    }

    //TODO: создаёт яблоко
    private void createApple() {
        //инициализирует координаты яблока
        if(role == SnakesProto.NodeRole.MASTER){
            appleX = new Random().nextInt(20) * DOT_SIZE;
            appleY = new Random().nextInt(20) * DOT_SIZE;
        }
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
        for (int i = 0; i < count; i++) {
            if (snakes.get(i).inGame) {
                checkApple(snakes.get(i));
                if(i==0){
                    checkCollisions(snakes.get(i));
                }
                move(snakes.get(i));
            }
        }
        repaint();
    }

    //TODO:проверяет не встретила ли голова яблоко, если да, то увеличиваем длину змейки, перерисовываем яблоко
    private void checkApple(SnakeData snake) {
        if (snake.x[0] == appleX && snake.y[0] == appleY) {
            snake.dots++;
            createApple();
        }
    }

    //TODO:проверяет не встретила ли голова саму себя или другую змейку
    private void checkCollisions(SnakeData snake) {
        for (int i = snake.dots; i > 0; i--) {

            //столкнулись ли мы сами с собой?
            if (i > 4 && snake.x[0] == snake.x[i] && snake.y[0] == snake.y[i]) {
                snake.inGame = false;
                count--;
            }

/*            //стокнулись ли мы с другой змейкой?
            //итерация по всем змейкам в игре
            for(int j = 1; i < snakes.size(); i++){
                //итерация по точкам другой змейки
                for (int k = 0; k < snakes.get(j).dots && snakes.get(j).dots!=3; k++) {
                    if (snake.x[i] == snakes.get(j).x[k] && snake.y[i] == snakes.get(j).y[k]) {
                        snake.inGame = false;
                        count--;
                    }
                }
            }*/

        }
    }

    //TODO:двигает змейку
    private void move(SnakeData snake) {
        //сдвиг в массивах x, y
        for (int i = snake.dots; i > 0; i--) {
            snake.x[i] = snake.x[i - 1];
            snake.y[i] = snake.y[i - 1];
        }
        //сдвиг головы
        if (snake.left) {
            snake.x[0] -= DOT_SIZE;
        }
        if (snake.right) {
            snake.x[0] += DOT_SIZE;
        }
        if (snake.up) {
            snake.y[0] -= DOT_SIZE;
        }
        if (snake.down) {
            snake.y[0] += DOT_SIZE;
        }
        //проверка: коснулись стенки
        checkWall(snake);
    }

    //TODO: проверка: коснулись стенки - да: двигаем голову по-другому
    private void checkWall(SnakeData snake) {
        if (snake.x[0] > SIZE) {
            snake.x[0] = 0;
        }
        if (snake.x[0] < 0) {
            snake.x[0] = SIZE;
        }
        if (snake.y[0] > SIZE) {
            snake.y[0] = 0;
        }
        if (snake.y[0] < 0) {
            snake.y[0] = SIZE;
        }
    }

    //TODO: отрисовка змейки, яблока, 'game over'
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int i = 0; i < count; i++) {
            if (snakes.get(0).inGame) {
                g.drawImage(apple, appleX, appleY, this);
                for (int j = 0; j < snakes.get(i).dots; j++) {
                    g.drawImage(dot, snakes.get(i).x[j], snakes.get(i).y[j], this);
                }
            } else {
                String str = "GAME OVER!";
                //Font f = new Font("Arial", 14, Font.BOLD);
                g.setColor(Color.darkGray);
                //g.setFont(f);
                g.drawString(str, 125, SIZE / 2);
            }
        }
    }

    //TODO: нажатие клавиш переопределяем
    class FileKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            super.keyPressed(e);
            int key = e.getKeyCode();
            switch (key) {
                case (KeyEvent.VK_LEFT): {
                    snakes.get(0).changeDirection("Left");
                    if (role == SnakesProto.NodeRole.NORMAL) {
                        senderNormal.changeDirection(3);
                    }
                    break;
                }
                case (KeyEvent.VK_RIGHT): {
                    snakes.get(0).changeDirection("Right");
                    if (role == SnakesProto.NodeRole.NORMAL) {
                        senderNormal.changeDirection(4);
                    }
                    break;
                }
                case (KeyEvent.VK_UP): {
                    snakes.get(0).changeDirection("Up");
                    if (role == SnakesProto.NodeRole.NORMAL) {
                        senderNormal.changeDirection(1);
                    }
                    break;
                }
                case (KeyEvent.VK_DOWN): {
                    snakes.get(0).changeDirection("Down");
                    if (role == SnakesProto.NodeRole.NORMAL) {
                        senderNormal.changeDirection(2);
                    }
                    break;
                }
            }
        }
    }

    public void addNewSnake(SnakeData snake) {
        snakes.add(snake);
        count++;
    }

    public int getAppleX() {
        return appleX;
    }

    public int getAppleY() {
        return appleY;
    }

    public void setAppleX(int x) {
        appleX = x;
    }

    public void setAppleY(int y) {
        appleY = y;
    }

    public ArrayList<SnakeData> getSnakes() {
        return snakes;
    }

    public void updateSnakes(ArrayList<SnakeData> newSnakes) {
        for(int i = 1; i <newSnakes.size()+1; i++){
            snakes.set(i, newSnakes.get(i));
        }
    }
}
