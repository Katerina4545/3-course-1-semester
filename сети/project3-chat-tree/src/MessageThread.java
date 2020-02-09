import javax.swing.text.html.HTMLDocument;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
/*
 * задача MessageThread - слушать входящие сообщения, выводить их, передавать соседям
 * */


public class MessageThread extends Thread {


    private String nameNode_spare_neighbor = ""; //запасной вариант подключения для нас (не наш сосед)
    private Integer port_spare_neighbor = 0;
    private String address_spare_neighbor = "";
    private String nameNode_another_neighbor = ""; //запасной вариант подключения для других (наш сосед)
    private Integer port_another_neighbor = 0;
    private String address_another_neighbor = "";

    private String ownPort;                        //собственные данные
    private String ownAddress;
    private DatagramSocket mySocket;
    private String nameNode = "";
    private double percent;
    private String parentName;

    //заводим мапу для соседей, ключ - имя соседа, значение - порт:адрес
    Map<String, String> myNeighbors = new HashMap<String, String>();

    public MessageThread(DatagramSocket mySocket_, String nameNode_, String ownPort_, String ownAddress_, String name_another, String port_another, String address_another, double percent_) {
        this.mySocket = mySocket_;
        this.nameNode = nameNode_;
        this.ownPort = ownPort_;
        this.ownAddress = ownAddress_;

        this.nameNode_another_neighbor = name_another;
        this.port_another_neighbor = Integer.parseInt(port_another);
        this.address_another_neighbor = address_another;
        this.percent = percent_;
    }

    private void enter_another_neighbor(String name, String port, String address) {
        this.nameNode_another_neighbor = name;
        this.port_another_neighbor = Integer.parseInt(port);
        this.address_another_neighbor = address;
    }

    private void reconnection() {
        try {
            //если запасной сосед есть
            if (!nameNode_spare_neighbor.equals("")) {

                //добавляем запасного соседа к себе в соседи
                myNeighbors.put(nameNode_spare_neighbor, port_spare_neighbor + ":" + address_spare_neighbor);

                //отправляем первое сообщение для нового соседа
                String message = String.format("first message:%s:%s:%s", nameNode, ownPort, ownAddress);
                DatagramPacket packetSend = new DatagramPacket(message.getBytes(StandardCharsets.UTF_8),
                        message.getBytes(StandardCharsets.UTF_8).length, InetAddress.getByName(address_spare_neighbor), port_spare_neighbor);
                mySocket.send(packetSend);

                //обновляем свои данные
                nameNode_another_neighbor = nameNode_spare_neighbor;
                port_another_neighbor = port_spare_neighbor;
                address_another_neighbor = address_spare_neighbor;
                parentName = nameNode_spare_neighbor;

            } else {
                //если его нет
                if (myNeighbors.size() > 1) {
                    boolean answer;
                    do {
                        //берем любого соседа
                        Map.Entry<String, String> entry = myNeighbors.entrySet().iterator().next();
                        //обновляем свои данные
                        nameNode_another_neighbor = entry.getKey();
                        String[] value = entry.getValue().split(":");
                        port_another_neighbor = Integer.parseInt(value[0]);
                        address_another_neighbor = value[1];
                        //проверяем что сосед рабочий
                        answer = checkAnotherNeighbor();
                        //если нет - удаляем
                        if (answer == false) {
                            myNeighbors.remove(nameNode_another_neighbor);
                        }
                    } while (answer == false && myNeighbors.size() > 1);
                }
            }

            //говорим всем соседям о новом запасном соседе
            // (если есть еще соседи помимо мертвого родителя и выбранного соседа и если мы нашли рабочего соседа)
            if (myNeighbors.size() > 2 && !nameNode_another_neighbor.equals("")) {
                for (Map.Entry<String, String> entry : myNeighbors.entrySet()) {
                    if (!entry.getKey().equals(parentName)) {
                        //парсим данные
                        String[] value = entry.getValue().split(":");
                        //формируем сообщение
                        String message2;
                        if (!entry.getKey().equals(nameNode_another_neighbor)) {
                            message2 = String.format("spare" + ":" + nameNode_another_neighbor + ":" + port_another_neighbor.toString() + ":" + address_another_neighbor);
                        } else {
                            message2 = String.format("spare" + ":" + "none" + ":" + "none" + ":" + "none");
                        }
                        //отправляем
                        DatagramPacket packetSend = new DatagramPacket(message2.getBytes(StandardCharsets.UTF_8),
                                message2.getBytes(StandardCharsets.UTF_8).length, InetAddress.getByName(value[1]), Integer.parseInt(value[0]));
                        mySocket.send(packetSend);
                    }
                }
            }

            //"обнуляем" своего запасного соседа - мы им уже воспользовались
            nameNode_spare_neighbor = "";
            port_spare_neighbor = 0;
            address_spare_neighbor = "";

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean checkAnotherNeighbor() throws SocketException, UnknownHostException {

        String messageStr = String.format(nameNode + ":" + "check");
        InetAddress address = InetAddress.getByName(address_another_neighbor);
        Integer port = port_another_neighbor;
        DatagramPacket packetSend = new DatagramPacket(messageStr.getBytes(StandardCharsets.UTF_8),
                messageStr.getBytes(StandardCharsets.UTF_8).length, address, port);
        byte[] response = new byte[32];
        DatagramPacket packetReceive = new DatagramPacket(response, response.length);

        mySocket.setSoTimeout(2000);
        for (int i = 0; i < 5; i++) {
            try {
                mySocket.send(packetSend);
                mySocket.receive(packetReceive);
                mySocket.setSoTimeout(300000);
                return true;
            } catch (Exception e) {
                //если кто-то тотально не отвечает, то считаем его умершим
                if (i == 4) {
                    mySocket.setSoTimeout(300000);
                    return false;
                }
                continue;
            }
        }
        return false;
    }

    public boolean findAnotherNeighbor() throws SocketException, UnknownHostException {
        boolean emptyData = false;
        while (!checkAnotherNeighbor() && !emptyData) {
            //если вариант не рабочий, то удаляем его из соседей и из another данных
            myNeighbors.remove(nameNode_another_neighbor);
            //если есть еще сосед, добавляем его в another
            if (myNeighbors.size() > 0) {
                Map.Entry<String, String> entry = myNeighbors.entrySet().iterator().next();
                nameNode_another_neighbor = entry.getKey();
                String[] str = entry.getValue().split(":");
                port_another_neighbor = Integer.parseInt(str[0]);
                address_another_neighbor = str[1];
            } else {
                emptyData = true;
                nameNode_another_neighbor = "";
                port_another_neighbor = 0;
                address_another_neighbor = "";
            }
        }
        if (!nameNode_another_neighbor.equals("")) {
            return true;
        } else {
            return false;
        }
    }


    public void run() {
        try {
            while (true) {
                //проверка на то, что нас не завершили
                if (Thread.interrupted()) return;

                //слушаем входящие сообщения от других соседей
                byte[] message = new byte[256];
                DatagramPacket packetReceive = new DatagramPacket(message, message.length);
                mySocket.setSoTimeout(300000);
                mySocket.receive(packetReceive);
                String Message = new String(packetReceive.getData(), StandardCharsets.UTF_8);
                String MessageNoSpace = Message.trim();
                String[] array = MessageNoSpace.split(":");

                //если получили первое сообщение от нового соседа, то парсим его и кладем в мапу
                if (MessageNoSpace.contains("first message")) {

                    //если появился первый сосед у одинокой ноды, то кладем его в "запасной сосед для других"
                    if (myNeighbors.size() == 0 && nameNode_another_neighbor.equals(""))
                        enter_another_neighbor(array[1], array[2], array[3]);

                    if (myNeighbors.size() > 0) {
                        //поиск рабочего запасного варианта
                        boolean answer = findAnotherNeighbor();

                        //отправляем запасной вариант если нашли его
                        String message2;
                        if (answer == true) {
                            message2 = String.format("spare" + ":" + nameNode_another_neighbor + ":" + port_another_neighbor.toString() + ":" + address_another_neighbor);
                        } else {
                            message2 = String.format("spare" + ":" + "none" + ":" + "none" + ":" + "none");
                        }

                        DatagramPacket packetSend = new DatagramPacket(message2.getBytes(StandardCharsets.UTF_8),
                                message2.getBytes(StandardCharsets.UTF_8).length, InetAddress.getByName(array[3]), Integer.parseInt(array[2]));
                        mySocket.send(packetSend);

                        if (answer == true) {
                            //отправляем запасному "пустого" запасного
                            String message3 = String.format("spare" + ":" + "none" + ":" + "none" + ":" + "none");
                            DatagramPacket packetSend2 = new DatagramPacket(message3.getBytes(StandardCharsets.UTF_8),
                                    message3.getBytes(StandardCharsets.UTF_8).length, InetAddress.getByName(address_another_neighbor), port_another_neighbor);
                            mySocket.send(packetSend2);
                        }

                    }

                    //заводим новую пару ключ-значение в мапе
                    myNeighbors.put(array[1], array[2] + ':' + array[3]);
                    System.out.println(array[1] + " join in your chat!");

                    //присылаем свои данные, чтобы он нас в мапу добавил
                    String message2 = String.format("data" + ":" + nameNode + ":" + ownPort + ":" + ownAddress);
                    DatagramPacket packetSend = new DatagramPacket(message2.getBytes(StandardCharsets.UTF_8),
                            message2.getBytes(StandardCharsets.UTF_8).length, InetAddress.getByName(array[3]), Integer.parseInt(array[2]));
                    mySocket.send(packetSend);

                } else if (MessageNoSpace.contains("spare")) {
                    //если нам пришел запасной вариант, то мы его запоминаем
                    //если пришли пустые данные
                    if (array[1].equals("none")) {
                        nameNode_spare_neighbor = "";
                        port_spare_neighbor = 0;
                        address_spare_neighbor = "";
                    } else {
                        nameNode_spare_neighbor = array[1];
                        port_spare_neighbor = Integer.parseInt(array[2]);
                        address_spare_neighbor = array[3];
                    }
                } else if (MessageNoSpace.contains("data")) {
                    //если пришли данные , то добавляем соседа, к которому мы "подцепились"
                    parentName = array[1];
                    String value = array[2] + ":" + array[3];
                    myNeighbors.put(array[1], value);
                } else if (MessageNoSpace.contains("check")) {
                    String[] value = myNeighbors.get(array[0]).split(":");
                    String message2 = String.format("ок");
                    DatagramPacket packetReceive2 = new DatagramPacket(message2.getBytes(), message2.getBytes().length, InetAddress.getByName(value[1]), Integer.parseInt(value[0]));
                    mySocket.send(packetReceive2);
                } else {

                    /*
                     * array[0] - от кого пришло нам сообщение
                     * array[1] - кто написал сообщение
                     * array[2] - сообщение
                     */

                    //дебаг
                    System.out.println("our spare variant:" + nameNode_spare_neighbor);

                    //имитация потери пакета
                    double rand = Math.random() * 100;
                    if (rand < percent) {
                        System.out.println("упс");
                        continue;
                    }

                    //если получили обычное сообщение, то выводим его и отправляем другим соседям, также отправляем подтверждение
                    System.out.println(array[1] + ": " + array[2]);

                    //подтверждение (пропускаем ситуацию когда сами себе сообщение в этот поток шлём)
                    if (!array[0].equals(nameNode)) {
                        String[] value = myNeighbors.get(array[0]).split(":");
                        String message2 = String.format("ок");
                        DatagramPacket packetReceive2 = new DatagramPacket(message2.getBytes(), message2.getBytes().length, InetAddress.getByName(value[1]), Integer.parseInt(value[0]));
                        mySocket.send(packetReceive2);
                    }

                    for (Map.Entry<String, String> entry : myNeighbors.entrySet()) {
                        if (!(entry.getKey().equals(array[0])) && !(entry.getKey().equals(array[1]))) {

                            //достаем данные для отправки
                            String value2 = entry.getValue();
                            String[] array2 = value2.split(":");
                            Integer port = Integer.parseInt(array2[0]);
                            InetAddress address = InetAddress.getByName(array2[1]);

                            //формируем сообщение для соседа пытаемся отправить 5 раз
                            String messageStr = String.format(nameNode + ":" + array[1] + ":" + array[2]);
                            DatagramPacket packetSend = new DatagramPacket(messageStr.getBytes(StandardCharsets.UTF_8),
                                    messageStr.getBytes(StandardCharsets.UTF_8).length, address, port);
                            byte[] response = new byte[32];
                            DatagramPacket packetReceive2 = new DatagramPacket(response, response.length);
                            mySocket.setSoTimeout(1000);
                            for (int i = 0; i < 5; i++) {
                                mySocket.send(packetSend);
                                try {
                                    mySocket.receive(packetReceive2);
                                    break;
                                } catch (Exception e) {
                                    System.out.println(entry.getKey() + " not answer" + i);
                                    //если кто-то тотально не отвечает, то считаем его умершим
                                    //если этот кто-то - наш родитель, то переподключаемся по запасному варианту
                                    if (i == 4) {

                                        if (entry.getKey().equals(parentName)) {
                                            System.out.println("parent died");
                                            System.out.println("reconnection");
                                            reconnection();
                                            myNeighbors.remove(entry.getKey());

                                        } else if (entry.getKey().equals(nameNode_another_neighbor)) {
                                            System.out.println("sheet died");
                                            //удаляем соседа
                                            myNeighbors.remove(entry.getKey());
                                            try {
                                                //ищем нового запасного соседа
                                                boolean answ = findAnotherNeighbor();
                                                //отправляем новые данные остальным
                                                if (myNeighbors.size() > 1) {
                                                    for (Map.Entry<String, String> entry2 : myNeighbors.entrySet()) {
                                                        //парсим данные
                                                        String[] value = entry2.getValue().split(":");
                                                        String message2;
                                                        if (answ) {
                                                            message2 = String.format("spare" + ":" + nameNode_another_neighbor + ":" + port_another_neighbor.toString() + ":" + address_another_neighbor);
                                                        } else {
                                                            message2 = String.format("spare" + ":" + "none" + ":" + "none" + ":" + "none");
                                                        }
                                                        DatagramPacket packetSend2 = new DatagramPacket(message2.getBytes(StandardCharsets.UTF_8),
                                                                message2.getBytes(StandardCharsets.UTF_8).length, InetAddress.getByName(value[1]), Integer.parseInt(value[0]));
                                                        mySocket.send(packetSend2);
                                                    }
                                                }
                                            } catch (Exception exc) {
                                                exc.printStackTrace();
                                            }
                                        }
                                        //fixme: add default remove
                                    }
                                    continue;
                                }


                            }
                        }

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
