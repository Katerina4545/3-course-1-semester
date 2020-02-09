package Network;

import com.google.protobuf.InvalidProtocolBufferException;
import me.ippolitov.fit.snakes.SnakesProto;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//ToDo: занимается прослушиванием разных сообщений
public class ListenerAsMaster {
    public boolean newPlayer = false;
    public InetAddress newPlayerInetAddress;
    private MulticastSocket socket;
    private HashMap<InetAddress, Integer> mapPlayers = new HashMap<InetAddress, Integer>();
    private HashMap<Integer, Integer> mapChanges = new HashMap<Integer, Integer>();
    private Integer lastNewPort = null;
    public boolean newChange = false;



    public ListenerAsMaster(MulticastSocket socket_) {
        socket = socket_;
        //поток который слушает сообщения
        Thread listener = new Thread(new Runnable() {
            public void run() {
                try {
                    listen();
                } catch (Exception exp) {
                    exp.printStackTrace();
                }

            }
        });
        listener.start();
    }

    //ToDo: принимает сообщения от игроков, обновляет данные в мапах
    public void listen() throws IOException {
        //слушать
        //если join msg
        //записать нового игрока
        //создать змейку
        //подтвердить получение

        while (true) {
            byte[] data = new byte[1024];
            DatagramPacket packet = new DatagramPacket(data, 0, data.length);
            try {
                socket.receive(packet);
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
            if (msg.hasJoin()) {
                actionJoinMsg(packet, msg);
            }
            if (msg.hasSteer()) {
                actionSteerMsg(packet, msg);
            }
        }
    }

    //ToDo: действия после join msg
    private void actionJoinMsg(DatagramPacket packet, SnakesProto.GameMessage msg) {
        System.out.println("Master get join msg");
        ///запоминаем присоединившегося игрока
        InetAddress address = packet.getAddress();
        int port = packet.getPort();
        lastNewPort = port;
        mapPlayers.put(address, port);

        ///отправляем ответное сообщение
        //конструктор общего сообщения
        long seq = msg.getMsgSeq();
        SnakesProto.GameMessage.Builder gameMessageBuilder = SnakesProto.GameMessage.newBuilder(SnakesProto.GameMessage.getDefaultInstance());
        gameMessageBuilder.setMsgSeq(seq);
        //конструктор AckMsg
        SnakesProto.GameMessage.AckMsg.Builder msgAck = SnakesProto.GameMessage.AckMsg.newBuilder();
        //формируем сообщение
        gameMessageBuilder.setAck(msgAck.build()).setReceiverId(1);

        byte[] dataSecond = gameMessageBuilder.build().toByteArray();
        DatagramPacket packetAckMsg = new DatagramPacket(dataSecond, dataSecond.length, address, port);
        try {
            socket.send(packetAckMsg);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Master send ackMsg");

        newPlayer = true;
    }

    //ToDo: действия после steer msg
    private void actionSteerMsg(DatagramPacket packet, SnakesProto.GameMessage msg) {
        //запомнить
        SnakesProto.GameMessage.SteerMsg steer = msg.getSteer();
        mapChanges.put(packet.getPort(), steer.getDirection().getNumber());
        //выставить флаг
        newChange = true;
        System.out.println("Master get steerMsg");
        //debug
        System.out.println(mapChanges);
    }

    //ToDo: отдает изменения полученные в steer msg
    public HashMap<Integer, Integer> getMapChanges() {
        return mapChanges;
    }

    //ToDo: подчищает массив от пустых байт
    public byte[] getRealBytes(byte[] oldData, int size) {
        byte[] newData = new byte[size];
        for (int i = 0; i < size; i++) {
            newData[i] = oldData[i];
        }
        return newData;
    }

//    //ToDo: удаляет из мапы игроков, которые долго не отвечают
//    public void removeWhoNotRequest(Long time) {
//    }

    //ToDo: отдает mapPlayers (для sender)
    public HashMap<InetAddress, Integer> getMapPlayers() {
        return mapPlayers;
    }

    //ToDo: отдает порт последнего добавленного игрока
    public Integer getLastNewPort(){
        return lastNewPort;
    }
}
