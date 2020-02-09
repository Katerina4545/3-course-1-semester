package Master;

import Common.GameField;
import Common.SnakeData;
import Network.ListenerAsMaster;
import Network.SenderAsMaster;
import me.ippolitov.fit.snakes.SnakesProto;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Thread.sleep;

public class NewGame extends JFrame {
    private MulticastSocket multicastSocket;
    private DatagramSocket datagramSocket;
    private ListenerAsMaster listener;
    private SenderAsMaster sender;
    private HashMap<InetAddress, Integer> mapPlayers = new HashMap<InetAddress, Integer>();
    private GameField gameField;
    private HashMap<Integer, SnakeData> mapPortPlayer = new HashMap<Integer, SnakeData>();
    private HashMap<Integer, Integer> mapChanges = new HashMap<Integer, Integer>();

    public NewGame() throws IOException {
        setTitle("Snake");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(800, 470);
        setLocation(300, 150);

        //наполнение окна
        JPanel root = new JPanel();
        gameField = new GameField(SnakesProto.NodeRole.MASTER);
        gameField.setPreferredSize(new Dimension(325, 340));
        root.add(gameField);
        add(root);
        setVisible(true);

        //базовые настройки игры
        SnakesProto.GameConfig gameConfig = SnakesProto.GameConfig.newBuilder()
                .setWidth(325).setHeight(340).setFoodStatic(1)
                .setFoodPerPlayer(1).setStateDelayMs(250).setDeadFoodProb(0)
                .setPingDelayMs(100).setNodeTimeoutMs(800).build();
        SnakesProto.NodeRole role = SnakesProto.NodeRole.MASTER;

        //создание мульткаст сокета
        multicastSocket = new MulticastSocket(9192);
        multicastSocket.joinGroup(InetAddress.getByName("224.0.0.7"));
        String groupAddress = "224.0.0.7";
        Integer groupPort = 9192;

        //создание листенера
        listener = new ListenerAsMaster(multicastSocket);
        //создание сендера
        sender = new SenderAsMaster(listener, multicastSocket, gameConfig, groupAddress, groupPort);

        //проверка присоединения новых игроков
        new Thread(new Runnable() {
            public void run() {
                try {
                    checkNewPlayer();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        //рассылка State
        new Thread(new Runnable() {
            public void run() {
                sendStateMsg();
            }
        }).start();
    }

    //ToDo: добавляет новых игроков на поле
    public void checkNewPlayer() throws InterruptedException {
        while (true) {
            sleep(1000);
            if (listener.newPlayer) {
                listener.newPlayer = false;

                //запоминаем данные игрока и
                mapPlayers = listener.getMapPlayers();
                int newPort = listener.getLastNewPort();
                SnakeData newSnake = new SnakeData();
                mapPortPlayer.put(newPort, newSnake);

                gameField.addNewSnake(newSnake);
            }
        }
    }

    //ToDo: отправляет state об игре раз в 250 миллисекунд
    public void sendStateMsg(){
        while (true) {
            try {
                sleep(250);
            }catch(InterruptedException e) {
                e.printStackTrace();
            }

            applyChanges();

            sender.sendStateMsg(gameField.getAppleX(), gameField.getAppleY(), gameField.getSnakes());
            //System.out.println("Master NewGame sendStateMsg");
        }
    }

    //ToDo: устанавливает накопившиеся изменения
    private void applyChanges(){
        if(listener.newChange){
            listener.newChange = false;
            mapChanges = listener.getMapChanges();

            //получаем текущий список змеек
            //ArrayList<SnakeData> lastSnakes = gameField.getSnakes();
            //обновляем его у себя в соответствии с полученными изменениями
            for (Map.Entry<Integer, Integer> entry : mapChanges.entrySet()) {
                //достаем изменения
                int currentPort = entry.getKey();
                int currentDirection = entry.getValue();
                //достаем змейку к которой эти изменения применимы
                SnakeData currentSnake = mapPortPlayer.get(currentPort);
                //применяем
                String currentDirectionString = getStringDirection(currentDirection);
                currentSnake.changeDirection(currentDirectionString);
            }
        }
    }

    //ToDo: отдает напрвление в форме строки
    private String getStringDirection(int dir){
        switch (dir){
            case(1):
                return "Up";
            case(2):
                return "Down";
            case(3):
                return "Left";
            case(4):
                return "Right";
        }
        return "";
    }
}



















