package Network;

import Common.GameField;
import Common.SnakeData;
import me.ippolitov.fit.snakes.SnakesProto;

import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//ToDo: занимается рассылкой разных сообщений
public class SenderAsMaster {
    ListenerAsMaster listener;
    MulticastSocket socket;
    Boolean canJoin = true;
    SnakesProto.GameConfig gameConfig;
    String groupAddress;
    Integer groupPort;
    int stateOrder = 0;

    public SenderAsMaster(ListenerAsMaster listener_, MulticastSocket socket_, SnakesProto.GameConfig gameConfig_, String groupAddress_, Integer groupPort_) {
        listener = listener_;
        socket = socket_;
        gameConfig = gameConfig_;
        groupAddress = groupAddress_;
        groupPort = groupPort_;
        sendAnMsg();
    }

    //TODO: рассылает сообщения AnnouncementMsg с интервалом в 1 секунду
    public void sendAnMsg() {
        Thread sender = new Thread(new Runnable() {
            public void run() {
                Long start = System.currentTimeMillis();
                Long finish;
                while (true) {
                    HashMap<InetAddress, Integer> map = listener.getMapPlayers();
                    SnakesProto.GamePlayers gamePlayers = getGamePlayers(map);

                    //конструктор общего сообщения
                    SnakesProto.GameMessage.Builder gameMessageBuilder = SnakesProto.GameMessage.newBuilder(SnakesProto.GameMessage.getDefaultInstance());
                    gameMessageBuilder.setMsgSeq(100);
                    //конструктор AnnouncementMsg
                    SnakesProto.GameMessage.AnnouncementMsg.Builder msg = SnakesProto.GameMessage.AnnouncementMsg.newBuilder();
                    msg.setCanJoin(canJoin).setConfig(gameConfig).setPlayers(gamePlayers);

                    gameMessageBuilder.setAnnouncement(msg.build());
                    byte[] data = gameMessageBuilder.build().toByteArray();

                    finish = System.currentTimeMillis();
                    if (finish - start >= 1000) {
                        try {
                            DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(groupAddress), groupPort);
                            socket.send(packet);
                            start = System.currentTimeMillis();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        sender.start();
    }

    //ToDo: рассылает StateMsg (один раз)
    public void sendStateMsg(int appleX, int appleY, ArrayList<SnakeData> snakes) {

        //TODO: СОЗДАНИЕ СООБЩЕНИЯ

        HashMap<InetAddress, Integer> mapPlayers = listener.getMapPlayers();
        SnakesProto.GamePlayers gamePlayers = getGamePlayers(mapPlayers);

        //создание state
        SnakesProto.GameState.Coord coord = SnakesProto.GameState.Coord.newBuilder().setX(appleX).setY(appleY).build();
        SnakesProto.GameState.Builder state = SnakesProto.GameState.newBuilder()
                .setStateOrder(stateOrder)
                .setConfig(gameConfig)
                .addFoods(coord)
                .setPlayers(gamePlayers);
        stateOrder++;
        for (int i = 0; i < snakes.size(); i++) {
            SnakesProto.GameState.Snake.Builder snakeMsg = SnakesProto.GameState.Snake.newBuilder()
                    .setHeadDirection(getNewDirection(snakes.get(i).direction_number))
                    .setPlayerId(0)
                    .setState( SnakesProto.GameState.Snake.SnakeState.ALIVE);
            setPoints(snakeMsg, snakes.get(i));
            state.addSnakes(snakeMsg.build());
        }

        //конструктор общего сообщения
        SnakesProto.GameMessage.Builder gameMessageBuilder = SnakesProto.GameMessage.newBuilder(SnakesProto.GameMessage.getDefaultInstance());
        gameMessageBuilder.setMsgSeq(100);
        //конструктор StateMsg
        SnakesProto.GameMessage.StateMsg.Builder msg = SnakesProto.GameMessage.StateMsg.newBuilder();
        msg.setState(state);

        gameMessageBuilder.setState(msg.build());


        //TODO: ОТПРАВКА СООБЩЕНИЯ ВСЕМ ИГРОКАМ
        byte[] data = gameMessageBuilder.build().toByteArray();
        int l = data.length;
        for (Map.Entry<InetAddress, Integer> entry : mapPlayers.entrySet()) {
            try {
                DatagramPacket packet = new DatagramPacket(data, data.length, entry.getKey(), entry.getValue());
                socket.send(packet);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //System.out.println("Master Sender sendStateMsg, mapPlayers = " + mapPlayers.size());
    }

    //ToDo: возвращает преобразованный direction_number
    public SnakesProto.Direction getNewDirection(int direction) {
        return SnakesProto.Direction.valueOf(direction);
    }

    //ToDo: возвращает преобразованный массив с координатами змейки
    public int[] getNewArray(int[] array) {
        int[] newArr = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            if (i == 0) {
                newArr[i] = array[i];
            } else {
                newArr[i] = array[i] - array[i - 1];
            }
        }
        return newArr;
    }

    //ToDo: устанавливает массив с координатами змейки для отправки
    public SnakesProto.GameState.Snake.Builder setPoints(SnakesProto.GameState.Snake.Builder snakeMsg, SnakeData snake) {
        int[] x = getNewArray(snake.x);
        int[] y = getNewArray(snake.y);
        SnakesProto.GameState.Coord.Builder coord = SnakesProto.GameState.Coord.newBuilder();
        for (int i = 0; i < snake.dots; i++) {
            coord.setX(x[i]).setY(y[i]);
            snakeMsg.addPoints(coord.build());
        }
         return snakeMsg;
    }

    //ToDo: формирует gamePlayers по текущей мапе
    public SnakesProto.GamePlayers getGamePlayers(HashMap<InetAddress, Integer> map) {
        //кладем себя в gamePlayer
        SnakesProto.GamePlayer myDataPlayer = SnakesProto.GamePlayer.newBuilder()
                .setId(0).setPort(getPort())
                .setRole(getRole()).setScore(100)
                .setType(SnakesProto.PlayerType.HUMAN)
                .setIpAddress("").setName("master").build();
        SnakesProto.GamePlayers Players = SnakesProto.GamePlayers.newBuilder().addPlayers(0, myDataPlayer).build();
        return Players;
    }

    //ToDo: отдает порт собственного сокета
    public int getPort() {
        return 4000;
    }

    //ToDo: отдает свою роль
    public SnakesProto.NodeRole getRole() {
        SnakesProto.NodeRole role = SnakesProto.NodeRole.MASTER;
        return role;
    }

}
