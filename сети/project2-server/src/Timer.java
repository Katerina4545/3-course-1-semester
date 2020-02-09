import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;

class Timer extends Thread {
    private Integer port;
    private final AtomicLong counter;
    public Timer(AtomicLong counter, Integer port_) {
        this.port = port_;
        this.counter = counter;
    }

    public void run() {
        try {
            Long total_read_bytes = Long.parseLong("0");  //сколько считали байт за все время передачи
            Double tmp;             //сюда будем считать скорость
            Long start = System.currentTimeMillis();
            Long finish;
            Long all_time;
            while(true) {
                Thread.sleep(3000);
                if(counter.get() != 0) { //если данные упели накопиться
                    tmp = (counter.doubleValue()) / 3;
                    System.out.println(this.port + " : " + "Instantaneous speed : " + tmp.toString());

                    finish = System.currentTimeMillis();
                    all_time = finish - start;
                    total_read_bytes += counter.get();
                    tmp = (double) total_read_bytes / all_time;
                    System.out.println(this.port + " : " + "Average speed : " + tmp.toString() + " ");
                    //обнуляем переменную
                    counter.lazySet(Long.parseLong("0"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
