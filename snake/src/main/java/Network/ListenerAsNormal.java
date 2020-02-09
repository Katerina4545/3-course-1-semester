package Network;

import Common.SnakeData;
import me.ippolitov.fit.snakes.SnakesProto;

import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.lang.Thread.sleep;

public class ListenerAsNormal {
    private MulticastSocket multicastSocket;
    private DatagramSocket datagramSocket;
    private HashMap<InetAddress, SnakesProto.GameConfig> mapGameConfig = new HashMap<InetAddress, SnakesProto.GameConfig>();
    private HashMap<InetAddress, Integer> mapMasters = new HashMap<InetAddress, Integer>();
    private int appleX;
    private int appleY;
    private ArrayList<SnakeData> snakes;
    public boolean dataUpdate = false;
    public boolean inGame = false;

    public ListenerAsNormal(MulticastSocket socket_, DatagramSocket datagramSocket_) {
        multicastSocket = socket_;
        datagramSocket = datagramSocket_;
        //поток который слушает сообщения на мультикасте
        Thread listenerMulticast = new Thread(new Runnable() {
            public void run() {
                try {
                    listenMulticast();
                } catch (Exception exp) {
                    exp.printStackTrace();
                }

            }
        });
        listenerMulticast.start();

        //поток который слушает юникаст
        Thread listenerUnicast = new Thread(new Runnable() {
            public void run() {
                try {
                    listenUnicast();
                } catch (Exception exp) {
                    exp.printStackTrace();
                }

            }
        });
        listenerUnicast.start();
    }

    //ToDo: слушаем по групповому адресу чтобы получить список текущих игр
    private void listenMulticast() {
        while (true) {
            byte[] data = new byte[1024];
            DatagramPacket packet = new DatagramPacket(data, 0, data.length);
            try {
                multicastSocket.receive(packet);
            } catch (Exception e) {
                e.printStackTrace();
            }
            byte[] newData = getRealBytes(packet.getData(), packet.getLength());
            SnakesProto.GameMessage msg = null;
            try {
                msg = SnakesProto.GameMessage.parseFrom(newData);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (msg.hasAnnouncement()) {
                SnakesProto.GameMessage.AnnouncementMsg anMsg = msg.getAnnouncement();
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                SocketAddress port2 = packet.getSocketAddress();
                SnakesProto.GameConfig config = anMsg.getConfig();
                mapGameConfig.put(address, config);
                mapMasters.put(address, port);
            }
        }
    }

    //ToDo: слушаем по локальному сокету чтобы получить текущий gameState
    private void listenUnicast() {
        System.out.println("listenUnicast start");
        while (true) {
            if (inGame) {
                System.out.println("normal listen datagram");
                byte[] data = new byte[4096];
                DatagramPacket packet = new DatagramPacket(data, 0, data.length);
                try {

                    datagramSocket.receive(packet);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                byte[] newData = getRealBytes(packet.getData(), packet.getLength());
                SnakesProto.GameMessage msg = null;
                try {
                    msg = SnakesProto.GameMessage.parseFrom(newData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (msg.hasState()) {
                    System.out.println("normal have state msg");
                    SnakesProto.GameMessage.StateMsg stateMsg = msg.getState();
                    SnakesProto.GameState state = stateMsg.getState();
                    appleX = state.getFoods(0).getX();
                    appleY = state.getFoods(0).getY();
                    List<SnakesProto.GameState.Snake> snakeList = state.getSnakesList();

                    actionFirstState(snakeList);

                    dataUpdate = true;
                }
            }
            try {
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

/*    //ToDo: действия если пришел первый state msg
    private void actionCurrentState(List<SnakesProto.GameState.Snake> snakeList){
        for (int i = 0; i < snakeList.size(); i++) {
            SnakesProto.GameState.Snake one_snake = snakeList.get(i);

        }
    }*/

    //ToDo: действия если пришел первый state msg
    private void actionFirstState(List<SnakesProto.GameState.Snake> snakeList) {
        ArrayList<SnakeData> mySnakes = new ArrayList<SnakeData>();
        for (int i = 0; i < snakeList.size(); i++) {
            SnakeData newSnake = new SnakeData(true);
            SnakesProto.GameState.Snake one_snake = snakeList.get(i);
            setXYarray(one_snake.getPointsList(), newSnake);
            setDirection(one_snake.getHeadDirection(), newSnake);
            mySnakes.add(newSnake);
        }
        snakes = mySnakes;
    }

    //ToDo: устанавливавет массив x,y позиции змейки, dots
    private void setXYarray(List<SnakesProto.GameState.Coord> pointsList, SnakeData snake) {
        int myDots = 0;
        for (int i = 0; i < pointsList.size(); i++) {
            SnakesProto.GameState.Coord point = pointsList.get(i);
            snake.x[i] = point.getX();
            snake.y[i] = point.getY();
            myDots++;
        }
        snake.dots = myDots;
    }

    //ToDo: устанавливавет напрвление движения змейки
    private void setDirection(SnakesProto.Direction directionData, SnakeData snake) {
        int number = directionData.getNumber();
        switch (number) {
            case (1): {
                snake.direction_number = 1;
                snake.right = false;
                snake.left = false;
                snake.up = true;
                snake.down = false;
                break;
            }
            case (2): {
                snake.direction_number = 2;
                snake.right = false;
                snake.left = false;
                snake.up = false;
                snake.down = true;
                break;
            }
            case (3): {
                snake.direction_number = 3;
                snake.right = false;
                snake.left = true;
                snake.up = false;
                snake.down = false;
                break;
            }
            case (4): {
                snake.direction_number = 4;
                snake.right = true;
                snake.left = false;
                snake.up = false;
                snake.down = false;
                break;
            }
        }
    }


    //ToDo: подчищает массив от пустых байт
    public byte[] getRealBytes(byte[] oldData, int size) {
        byte[] newData = new byte[size];
        for (int i = 0; i < size; i++) {
            newData[i] = oldData[i];
        }
        return newData;
    }

    //ToDo: отдает список доступных игр
    public ArrayList getListGame() {
        ArrayList<String> array = new ArrayList<String>();
        boolean flag = true;
        while (flag) {
            if (mapGameConfig.size() != 0) {
                flag = false;
                for (HashMap.Entry<InetAddress, SnakesProto.GameConfig> entry : mapGameConfig.entrySet()) {
                    array.add(entry.getKey().toString());
                }
            }
            try {
                sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return array;
    }

    //ToDo: отдает порт мастера из mapMasters (для senderAsNormal)
    public Integer getPortMaster(InetAddress address) {
        return mapMasters.get(address);
    }

    //ToDo: возвращает массив с коррдинатами яблока на игровом поле
    public int[] getAppleLocation() {
        int[] location = new int[2];
        location[0] = appleX;
        location[1] = appleY;
        return location;
    }

    //ToDo: возвращает массив со змейками в игре
    public ArrayList<SnakeData> getSnakes() {
        return snakes;
    }
}
