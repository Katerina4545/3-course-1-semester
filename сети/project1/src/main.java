import java.io.*;
import java.net.*;
import java.util.*;

//поток, который актуализирует кэш-таблицу
public class main {
    public static void main(String[] args) {
        try {
            //ввод данных с консоли
            Scanner scanner = new Scanner(System.in);
            System.out.print("enter group's ip address: \n");
            final String inet_address = scanner.next();
            InetAddress ADDRESS = InetAddress.getByName(inet_address);
            System.out.print("enter group's port: \n");
            final Integer PORT = scanner.nextInt();

            //присоединяемся к мультикаст группе
            MulticastSocket multicast_socket = new MulticastSocket(PORT);
            multicast_socket.joinGroup(ADDRESS);

         /*
            // запускаем отправку сообщений
            new MessageSender(ADDRESS, PORT, multicast_socket).start();
        */
            /*КЕЙС 1 - DatagramSocket передать в MessageSender вместо мультикаста */

//            DatagramSocket datagramSocket = new DatagramSocket();
//            datagramSocket.connect(ADDRESS, PORT);
//            new MessageSender(ADDRESS, PORT, datagramSocket).start();

            /*КЕЙС 2 - левый DatagramSocket передать в MessageSender вместо мультикаста*/

//            DatagramSocket datagramSocket = new DatagramSocket();
//            InetAddress badADDRESS = InetAddress.getByName("226.0.0.5");
//            Integer badPORT = 35355;
//            datagramSocket.connect(badADDRESS, badPORT);
//            new MessageSender(ADDRESS, PORT, datagramSocket).start();

            //кэш-таблица
            Map<String, Long> cashTable = new HashMap<String, Long>();

            //запускаем функции (приём сообщений + обновление таблицы)
            while (true) {
                receive_message(/*КЕЙС 1,2 multicast_socket*/datagramSocket, cashTable);
                update_of_table(cashTable);
            }
        }
        catch(Exception exc){
            exc.printStackTrace();
        }
    }

    //принимаем сообщения от других
     static void receive_message(/*КЕЙС 1,2 MulticastSocket multicast_socket*/ DatagramSocket multicast_socket, Map<String, Long> cashTable) throws IOException{
        byte[] buf = new byte[1000];
        long start = System.currentTimeMillis();
        long finish = 0;
        while (finish - start < 5000)
        {
            //принимаем сообщение
            DatagramPacket recv = new DatagramPacket(buf, buf.length);
            multicast_socket.receive(recv);
            System.out.println("receive");
            //формируем ключ
            String key = recv.getAddress().toString() + ":" + recv.getPort();
            //кладем элемент с новым временем
            cashTable.put(key, System.currentTimeMillis());
            //очищаем buf
            byte tmp = 0;
            Arrays.fill(buf, tmp);

            finish = System.currentTimeMillis();
        }
    }

    //обновляет таблицу
    static void update_of_table(Map<String, Long> cashTable) throws IOException{
        //перебираем таблицу
        Integer i = 0;
        for(Map.Entry<String, Long> item : cashTable.entrySet()){
            if(System.currentTimeMillis() - item.getValue() > 5000)     //если превышен таймаут, удаляем элемент
            {
                cashTable.remove(item.getKey());
            }
            else
            {
                System.out.println("address number " + i + " : " + item.getKey() + "\n");
                i+=1;
            }
        }
        System.out.println("Amount of other applications: " + (i-1) + "\n");
    }

}