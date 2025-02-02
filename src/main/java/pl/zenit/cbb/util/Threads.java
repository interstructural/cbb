package pl.zenit.cbb.util;

public class Threads {
    public static Thread lazy(Runnable r) {
        return new Thread(()-> {
            try {
                Thread.sleep(1);
                r.run();
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }
}
