package Join;

import Common.GameField;
import Common.SnakeData;
import Network.ListenerAsNormal;
import Network.SenderAsNormal;
import me.ippolitov.fit.snakes.SnakesProto;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class JoinGame extends JFrame {
    MulticastSocket multicastSocket;
    DatagramSocket socket;
    InetAddress addressSelectedGame;
    SenderAsNormal sender;
    ListenerAsNormal listener;
    GameField gameField;

    public JoinGame() throws InterruptedException, IOException {
        setTitle("Snake");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(800, 470);
        setLocation(300, 150);

        SnakesProto.NodeRole role = SnakesProto.NodeRole.NORMAL;
        //создание сокета
        createMulticastSocket();
        createSocket();

//        String groupAddress = "224.0.0.7";
//        Integer groupPort = 9192;

        //создем листенер
        listener = new ListenerAsNormal(multicastSocket, socket);
        //создаем сендер
        sender = new SenderAsNormal(socket);

        //получить список доступных игр
        ArrayList<String> listGame = listener.getListGame();

        //вывести их на экран
        JPanel root = new JPanel();
        for(int i = 0; i < listGame.size(); i++){
            JButton newButton = new JButton(listGame.get(i));
            root.add(newButton);
            //добавляем листенер на нажатие
            addActionListenerAddressButton(newButton);
        }
        add(root);
        setVisible(true);
    }

    //ToDo: создает сокет для приёма мультикаст сообщений
    public void createMulticastSocket(){
        try {
            multicastSocket = new MulticastSocket(9192);
            multicastSocket.joinGroup(InetAddress.getByName("224.0.0.7"));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //ToDo: создает сокет для приёма и отправки не мультикаст сообщений
    public void createSocket(){
        try {
            socket = new DatagramSocket();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //ToDo: добавляет реакцию на нажатие (выбранная игра)
    public void addActionListenerAddressButton(JButton button) throws IOException {
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //запоминаем выбранный адрес
                String buttonText = button.getText().replaceAll("/", "");
                try {
                    addressSelectedGame = InetAddress.getByName(buttonText);
                } catch (UnknownHostException ex) {
                    ex.printStackTrace();
                }
                //получаем порт мастера для этой игры
                Integer portSelectedGame = listener.getPortMaster(addressSelectedGame);
                //посылаем JoinMsg
                boolean answer = false;
                try {
                    answer = sender.sendJoinMsg(addressSelectedGame, portSelectedGame);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                //когда у нас есть информация
                listener.inGame = true;
                while(!listener.dataUpdate){
                    try {
                        sleep(250);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }

                if(answer && listener.dataUpdate) {
                    System.out.println("normal have information");
                    //вытаскиваем информацию об игре чтобы создать игровое поле
                    int[] appleLocation = listener.getAppleLocation();
                    ArrayList<SnakeData> snakes = listener.getSnakes();
                    listener.dataUpdate = false;

                    //создаем игровое поле
                    gameFieldForJoin field = new gameFieldForJoin(snakes, appleLocation);

                    //запускаем перерисовку поля (по новым game state)
                    Thread repaint = new Thread(new Runnable() {
                        public void run() {
                            try {
                                field.repaintGameField();
                            } catch (Exception exp) {
                                exp.printStackTrace();
                            }

                        }
                    });
                    repaint.start();
                }
            }
        });
    }

    class gameFieldForJoin extends JFrame{
        public gameFieldForJoin(ArrayList<SnakeData> snakes, int[] appleLocation){
            //создание окна
            setTitle("Snake");
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            setSize(800, 470);
            setLocation(300, 150);
            //создание игрового поля
            JPanel root = new JPanel();
            gameField = new GameField(snakes, appleLocation, SnakesProto.NodeRole.NORMAL, sender);
            gameField.setPreferredSize(new Dimension(325, 340));
            root.add(gameField);
            //окончание
            add(root);
            setVisible(true);
        }

        private void repaintGameField(){
            while(true){
                while(!listener.dataUpdate){
                    try {
                        sleep(250);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }

                if(listener.dataUpdate) {
                    gameField.setAppleX(listener.getAppleLocation()[0]);
                    gameField.setAppleY(listener.getAppleLocation()[1]);
                    gameField.updateSnakes(listener.getSnakes());
                }
            }
        }

    }


}