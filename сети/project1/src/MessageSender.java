import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

//поток, который отправляет сообщения
class MessageSender extends Thread {

    final private InetAddress groupADDRESS;
    final private Integer groupPORT;
    final private /*MulticastSocket*/ /*КЕЙС 1,2*/ DatagramSocket multicast_socket;

    public MessageSender(final InetAddress ADDRESS, final Integer gPORT, /*MulticastSocket mSocket,*/ /*КЕЙС 1,2*/ DatagramSocket mSocket) {
        this.groupADDRESS = ADDRESS;
        this.groupPORT = gPORT;
        this.multicast_socket = mSocket;
    }

    public void run() {
        try {
            //формируем сообщение
            String message = "i'm alive";
            //отправляем сообщение с интервалом в секунду
            while (true) {
                DatagramPacket message_packet = new DatagramPacket(message.getBytes(), message.length(),
                                                                    this.groupADDRESS, this.groupPORT);
                this.multicast_socket.send(message_packet);
                System.out.println("send");
                Thread.sleep(1000);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}