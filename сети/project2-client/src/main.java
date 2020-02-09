import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.rmi.UnknownHostException;
import java.util.*;
public class main {
    public static void main(String[] args) throws IOException, InterruptedException, FileNotFoundException {
        try {

            //считываем данные для соединения
            Scanner scanner = new Scanner(System.in);
            System.out.print("enter file's path: \n");
            final String file_path = scanner.next();
            System.out.print("enter file's name: \n");
            final String file_name = scanner.next();
            System.out.print("enter port for connection: \n");
            final Integer port = scanner.nextInt();
            System.out.print("enter ip for connection: \n");
            final String inet_address = scanner.next();
            InetAddress address = InetAddress.getByName(inet_address);

            //открываем сокет по указанному порту, открываем выходной канал
            Socket socket = new Socket(address, port);
            OutputStream outStream = socket.getOutputStream();

            //открываем файл по указанному пути файла
            File file = new File(file_path);
            FileInputStream fileStream = new FileInputStream(file_path);
            //нужен, чтобы нормально читать из файла
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileStream);

            //отправляем сообщение с метаданными о файле
            String message = String.format("%s;%s", file.length(), file_name);
            outStream.write(message.getBytes(StandardCharsets.UTF_8));
            System.out.println("client send a message:");
            System.out.println(message);

//            outStream.flush();

            //ждём подтверждения от сервера
            InputStream inputStream = socket.getInputStream();
            if(inputStream.read() == -1)
            {
                System.out.println("client didn't get a confirmation, stop a programme");
                return;
            }
            System.out.println("client got a confirmation, continue a programme");

            long fileLength = file.length();        //сколько нам надо передать
            long amount_transferred_bytes = 0;      //сколько передали на текущий момент
            int size_of_container = 10000;          //размер принимающего буфера - 10 Килобайт
            byte[] container;                       //куда будем считывать байты из файла
            Long information_about_sending_file_new;
            Long information_about_sending_file_old = Long.valueOf("0");

            //ждём подтверждения от сервера о том, что он готов к передачи
            while(inputStream.read() == -1)
            {
                Thread.sleep(10);
            }

            while (amount_transferred_bytes != fileLength) {    //пока не передали весь файл

                if (fileLength - amount_transferred_bytes >= size_of_container) {
                    amount_transferred_bytes += size_of_container;
                } else {
                    size_of_container = (int) (fileLength - amount_transferred_bytes);
                    amount_transferred_bytes = fileLength;
                }

                container = new byte[size_of_container];
                fileStream.read(container);
                outStream.write(container);

                //подтверждаем отправку - ждём ответа от сервера
//                while(inputStream.read() == -1)
//                {
//                    Thread.sleep(10);
//                }

                //чтобы пользователю показывалась информация о доставке только если она изменилась
                    //считаем текущее (или новое) значение информации
                information_about_sending_file_new = (amount_transferred_bytes * 100) / fileLength;
                    //если не равно старой информации, то выводим
                if(information_about_sending_file_new.compareTo(information_about_sending_file_old) != 0) {
                    System.out.println("Sending file" + information_about_sending_file_new + "% complete");
                    information_about_sending_file_old = information_about_sending_file_new;
                }
            }
//            outStream.flush();

            //ждём подтверждения от сервера о том, что он получил всё
            byte[] buf = new byte[128];
            while(inputStream.read(buf) == -1)
            {
                Thread.sleep(10);
            }

            inputStream.close();
            outStream.flush();
            outStream.close();
            socket.close();
            System.out.println("end programme");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}