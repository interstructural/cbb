package pl.zenit.cbb.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * pl.zenit.koziel.lib.concurrent
 */

/**
 * moze i jestem brzydki, ale za to nie mam overheadu synchronizacji
 */
public class ThreadSplitter {

      private final int threadCount;

      private int threadPriority = Thread.NORM_PRIORITY;

      public ThreadSplitter(int threadCount) {
            this.threadCount = threadCount;
      }

      public void run(List<Runnable> actions) {
            List<List<Runnable>> slots = new ArrayList<>();
            for ( int i = 0 ; i < threadCount ; ++i )
                  slots.add(new ArrayList<>());
            for ( int i = 0 ; i < actions.size() ; ++i )
                  slots.get(i%threadCount).add(actions.get(i));

            ActionSynchronizer as = new ActionSynchronizer();

            for ( int i = 0 ; i < slots.size() ; ++i ) {
                  int index = i;
                  as.actions.add(()-> runSync(slots.get(index)));
            }
            as.setThreadPriority(threadPriority);
            as.run();
      }

      public void runSync(List<Runnable> tasks) {

            for ( int i = 0 ; i < tasks.size() ; ++i )
                  tasks.get(i).run();
      }

      public <ItemType> LoadedRunner load(List<ItemType> list, Consumer<ItemType> action) {
            return new LoadedRunner(list, action);
      }

      public class LoadedRunner<ItemType> extends WrappedRunner {
            private final List<ItemType> list;
            private final Consumer<ItemType> action;

            public LoadedRunner(List<ItemType> list, Consumer<ItemType> action) {
                  this.list = list;
                  this.action = action;
                  this.progress = progress;
            }

            public void run() {
                  List<Runnable> actions = new ArrayList<>();
                  for ( int i = 0 ; i < list.size() ; ++i ) {
                        int idx = i;
                        actions.add(()-> {
                              if (progress != null)
                                    progress.accept(idx, list.size());
                              action.accept(list.get(idx));
                        });
                  }
                  ThreadSplitter.this.threadPriority = this.priority;
                  ThreadSplitter.this.run(actions);
            }
      }

      public abstract class WrappedRunner implements Runnable {

            protected BiConsumer<Integer, Integer> progress;

            protected int priority = Thread.NORM_PRIORITY;

            public WrappedRunner setProgressEvent(BiConsumer<Integer, Integer> progress) {
                  this.progress = progress;
                  return this;
            }

            public WrappedRunner setPriority(int threadPriority) {
                  this.priority = threadPriority;
                  return this;
            }

      }

}
