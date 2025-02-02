package pl.zenit.cbb;

import javax.swing.JOptionPane;

public class Main {

      private static FdMainFrame handle;

      public static final String NAME = "Chaotic Behavior Bathyscaphe 1.1";;
      
      public static void main(String args[]) {
            Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
            handle = new FdMainFrame();
            handle.setVisible(true);
      }
      
      private static class ExceptionHandler implements Thread.UncaughtExceptionHandler {

            @Override public void uncaughtException(Thread t, Throwable e) {                  
                  JOptionPane.showMessageDialog(null, t.getName() + ": " + e.getMessage());
                  System.out.println(t.getName() + ": " + e.getMessage());
                  e.printStackTrace();
            }
      }

}
