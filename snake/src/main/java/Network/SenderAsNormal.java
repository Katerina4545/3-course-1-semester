package Network;

import me.ippolitov.fit.snakes.SnakesProto;

import java.io.IOException;
import java.net.*;

public class SenderAsNormal {
    DatagramSocket socket;
    InetAddress addressSelectedGame_;
    Integer portSelectedGame_;
    public SenderAsNormal(DatagramSocket socket_){
        socket= socket_;
    }

    public boolean sendJoinMsg(InetAddress addressSelectedGame, Integer portSelectedGame) throws IOException {
        addressSelectedGame_ = addressSelectedGame;
        portSelectedGame_ = portSelectedGame;

        boolean answer = false;
        //конструктор общего сообщения
        SnakesProto.GameMessage.Builder gameMessageBuilder = SnakesProto.GameMessage.newBuilder(SnakesProto.GameMessage.getDefaultInstance());
        gameMessageBuilder.setMsgSeq(101);
        //конструктор JoinMsg
        SnakesProto.GameMessage.JoinMsg.Builder msg = SnakesProto.GameMessage.JoinMsg.newBuilder();
        msg.setPlayerType(SnakesProto.PlayerType.HUMAN).setOnlyView(false).setName("player first");

        //формируем сообщение
        gameMessageBuilder.setJoin(msg.build());
        byte[] data = gameMessageBuilder.build().toByteArray();
        DatagramPacket packet = new DatagramPacket(data, data.length, addressSelectedGame, portSelectedGame);
        byte[] dataRec = new byte[1024];
        DatagramPacket packetRec = new DatagramPacket(dataRec, 0, dataRec.length);

        //пытаемся отправить пять раз
        System.out.println("send JoinMsg from normal");
        socket.setSoTimeout(1000);
        for (int i = 0; i < 5; i++) {
            socket.send(packet);
            try {
                socket.receive(packetRec);
                answer = true;
                System.out.println("master answered");
                break;
            } catch (Exception e) {
                System.out.println("master not answer " + i);
                continue;
            }
        }
        return answer;
    }

    //ToDo: отправляет сообщение о смене направления у змеи
    public void changeDirection(int direction){
        //конструктор общего сообщения
        SnakesProto.GameMessage.Builder gameMessageBuilder = SnakesProto.GameMessage.newBuilder(SnakesProto.GameMessage.getDefaultInstance());
        gameMessageBuilder.setMsgSeq(101);

        //конструктор SteerMsg
        SnakesProto.GameMessage.SteerMsg.Builder msg = SnakesProto.GameMessage.SteerMsg.newBuilder();
        msg.setDirection(SnakesProto.Direction.valueOf(direction));

        //формируем сообщение
        gameMessageBuilder.setSteer(msg.build());
        byte[] data = gameMessageBuilder.build().toByteArray();
        DatagramPacket packet = new DatagramPacket(data, data.length, addressSelectedGame_, portSelectedGame_);

        //отправляем
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("normal send steer msg");
    }
}
