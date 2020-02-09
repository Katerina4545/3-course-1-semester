import java.util.*;
import java.net.*;

public class MultiThreadServer {
    public static void main(String[] args) {
        try{
            //считываем данные для соединения
            Scanner scanner = new Scanner(System.in);
            System.out.print("enter full path for new file: \n");
            final String file_path = scanner.next();
            System.out.print("enter ip: \n");
            final String ip = scanner.next();
            InetAddress address = InetAddress.getByName(ip);
            System.out.print("enter port for listening: \n");
            final Integer port = scanner.nextInt();

            //открываем сокет по указанному порту, открываем входной канал
            ServerSocket server = new ServerSocket(port, 50, address);

            while(true) {
                //ждем клиента
                Socket client = server.accept();
                //передаём клиента(client) в класс Server
                new Server(client, file_path).start();
            }
        }catch(Exception exc){
            exc.printStackTrace();
        }
    }
}
