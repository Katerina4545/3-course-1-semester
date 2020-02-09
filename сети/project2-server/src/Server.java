import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;

class Server extends Thread{

    final private Socket client;
    final private String file_path;
    private final AtomicLong counter = new AtomicLong(0);

    public Server(Socket client_, String file_path_) {
        this.client = client_;
        this.file_path = file_path_;
    }

    public  void run() {
        try {
            InputStream inputStream = client.getInputStream();

            //принимаем сообщение с метаданными и выводим
            byte[] container_for_message = new byte[10000];
            inputStream.read(container_for_message);
            String message = new String(container_for_message, StandardCharsets.UTF_8);
            System.out.println(client.getPort() + " : server get a message:");
            System.out.println( client.getPort() + " : " + message);

            //отсылаем подтверждение клиенту
            OutputStream outputStream = client.getOutputStream();
            outputStream.write("ок".getBytes());
            System.out.println(client.getPort() + " : server put a confirmation");

            //парсим сообщение
            String[] array = message.split(";");
            Long file_length = Long.parseLong(array[0]);

            //создаём файл, куда будем записывать данные
            File file = new File(file_path);
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);

            //запускаем поток, кот будет трекать время
            Timer timer = new Timer(counter, client.getPort());
            timer.start();

            //чтение данных из потока
            byte[] container = new byte[10000];        //сюда будем считывать данные из потока
            int tmp;

            //отсылаем подтверждение о готовности принимать данные
            outputStream.write("ок".getBytes());

            while ((tmp = inputStream.read(container)) != -1) {
                //подтверждаем получение кусочка данных
                //outputStream.write("ок".getBytes());
                //записываем кусочек данных в файл
                fileOutputStream.write(container);
                //записываем сколько байт мы считали
                counter.addAndGet(tmp);
            }

            outputStream.write("ок".getBytes());


            client.close();
            fileOutputStream.flush();
            fileOutputStream.close();

            System.out.println(client.getPort() + " : " + "File saved successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
