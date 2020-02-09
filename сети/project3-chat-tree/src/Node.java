import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/*
* задача Node - создать ноду, отправить первое сообщение для соседа, слушать поседующие сообщения от пользователя, отправлять их соседу
*
* формат для первого сообщения - first message:имя_отправляющего:порт:ip адрес
* формат для последующих сообщений - имя_отправляющего:имя_пишущего:сообщение
*
* */

public class Node {
    public static void main(String[] arg) {
        try{
            //ввод данных для создания узла
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter your name:");
            final String nameNode = scanner.next();
            System.out.println("Enter the percent of lost:");
            final Double percentLost = scanner.nextDouble();
            System.out.println("Enter the own port for node:");
            final Integer ownPort = scanner.nextInt();
            System.out.println("Enter the own address for node:");
            String addr = scanner.next();
            final InetAddress ipAddressOwn = InetAddress.getByName(addr);

            //куча переменных для дальнейшей работы
            String address = "";
            Integer neighborPort = 0;
            InetAddress ipAddressNeighbor;
            String nameNode_neighbor = "";
            DatagramSocket mySocket = new DatagramSocket(ownPort, ipAddressOwn);

            //спрашиваем у пользователя хочет ли он создать одинокую ноду
            System.out.println("Do you want to create the lone node? (y/n)");
            String tmp = scanner.next();

            if(tmp.equals("n")) {
                System.out.println("Enter the ip address for neighbor:");
                address = scanner.next();
                ipAddressNeighbor = InetAddress.getByName(address);

                System.out.println("Enter the port for neighbor:");
                neighborPort = scanner.nextInt();

                System.out.println("Enter the name for neighbor:");
                nameNode_neighbor = scanner.next();

                //открываем сокет, отправляем первое сообщение сообщение о нашем существовании соседу
                String message = String.format("first message:%s:%s:%s", nameNode, ownPort, addr);
                DatagramPacket packetSend= new DatagramPacket(message.getBytes(StandardCharsets.UTF_8), message.getBytes(StandardCharsets.UTF_8).length, ipAddressNeighbor, neighborPort);
                mySocket.send(packetSend);
                System.out.println(nameNode + " join in your chat!");

            }

            System.out.println("Let's communicate!");

            //поток, который слушает сообщения от соседей
            final MessageThread thread = new MessageThread(/*свои данные*/mySocket, nameNode, ownPort.toString(), addr,
                                                            /*данные соседа*/ nameNode_neighbor, neighborPort.toString(), address, percentLost);
            thread.start();

            //считываем последующие сообщения от пользователя и отправляем самому себе, MessageThread разошлет соседям
            while(true) {
                String message = scanner.nextLine();
                if(!message.matches("\\s") && !message.isEmpty()) {
                    String readyMessage = nameNode + ':' + nameNode + ':' + message;
                    DatagramPacket packetSend = new DatagramPacket(readyMessage.getBytes(StandardCharsets.UTF_8), readyMessage.getBytes(StandardCharsets.UTF_8).length, ipAddressOwn, ownPort);
                    mySocket.send(packetSend);
                    if (message.equals("ой всё")) {
                        System.out.println("you leaved the chat");
                        thread.interrupt();
                        mySocket.close();
                        return;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
