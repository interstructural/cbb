package pl.zenit.cbb.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * pl.zenit.koziel.lib.generic
 */
public class ActionSynchronizer implements Runnable {
      
      public final List<Runnable> actions = new ArrayList<>();

      private AtomicInteger counter = new AtomicInteger(0); 
      
      private int threadPriority = Thread.NORM_PRIORITY;
      
      @Override public synchronized void run() {
            List<Runnable> copy = new ArrayList<>();
            copy.addAll(actions);
            
            counter.set(copy.size());
            List<Thread> threads = Collections.synchronizedList(new ArrayList<>());
            copy.forEach(action-> threads.add(getThread(action)));
            threads.forEach(thread-> thread.start());
      
            while ( counter.get() > 0 ) {
                  try {
                        Thread.sleep(1);
                  }
                  catch (InterruptedException ex) {
                        threads.forEach(thread-> {
                              if (thread.isAlive())
                              thread.interrupt();
                        });
                        counter.set(0);
                        break;
                  }
            }
      }
      
      private Thread getThread(Runnable action) {
            Thread thread = new Thread(()-> {
                  try {
                        Thread.sleep(1);
                        action.run();
                  }
                  catch (InterruptedException e) {
                        
                  }
                  finally {
                        counter.decrementAndGet();
                  }
            });
            thread.setPriority(threadPriority);
            return thread;
      }

      public void setThreadPriority(int threadPriority) {
            this.threadPriority = threadPriority;
      }
      
}
