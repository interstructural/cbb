package pl.zenit.cbb.util;

import java.util.concurrent.atomic.AtomicBoolean;

public class Throttler {

      private final AtomicBoolean semaphore = new AtomicBoolean(false);
      
      public boolean run(Runnable r) {
            if (!semaphore.compareAndSet(false, true)) {
                  return false;
            }
            try {
                  r.run();
            }
            finally {
                  semaphore.set(false);
            }
            return true;
      }
      
}
