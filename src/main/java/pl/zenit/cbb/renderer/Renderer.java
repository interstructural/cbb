package pl.zenit.cbb.renderer;

import pl.zenit.cbb.d2.Point2d;
import pl.zenit.cbb.d2.Rect2d;
import pl.zenit.cbb.util.ThreadSplitter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

public class Renderer {

      private static final int MAX_ITERATIONS = 1000000;
      
      private CameraViewport viewport;
      private BufferedImage frontBuffer = null;
      private BufferedImage backBuffer = null;
      private Equation equation = null;
      private BoundaryCondition boundaryCondition = null;
      private int iterationDepth = 20;
      private Coloring coloring = null;
      private final Object monitor = new Object();
      private boolean multithreading = true;
      
      private BiConsumer<Integer, Integer> progressMessage = null;
      
      public Renderer() {
            viewport = new CameraViewport(Coords.ZERO, Zoom.DEFAULT);
            setBitmapSize(new Dimension(100, 100));
            setEquation(Equation.FLAT);
            setBoundaryCondition(BoundaryCondition.FALSE);
            setColoring(Coloring.plain16());
      }
      
      public CameraViewport getViewport() {
            return viewport;
      }
      public void setViewport(CameraViewport viewport) {
            this.viewport = viewport;
      }

      public void setProgressMessage(BiConsumer<Integer, Integer> progressMessage) {
            this.progressMessage = progressMessage;
      }

      public void setIterationDepth(int iterationDepth) {
            this.iterationDepth = iterationDepth;
      }
      public void setEquation(Equation equation) {
            this.equation = equation;
      }
      public void setBoundaryCondition(BoundaryCondition boundaryCondition) {
            this.boundaryCondition = boundaryCondition;
      }
      public void setColoring(Coloring coloring) {
            this.coloring = coloring;
      }

    public void setMultithreading(boolean multithreading) {
        this.multithreading = multithreading;
    }
      
      public void setBitmapSize(Dimension bitmapSize) {
            synchronized(monitor) {
                  frontBuffer = new BufferedImage(bitmapSize.width, bitmapSize.height, BufferedImage.TYPE_4BYTE_ABGR);
                  backBuffer = new BufferedImage(bitmapSize.width, bitmapSize.height, BufferedImage.TYPE_4BYTE_ABGR);
            }
      }      
      public BufferedImage getFrontBuffer() {
            return frontBuffer;
      }
      
      public synchronized void render() {
            refreshBuffer();
            swapBuffer();
      }

      private void refreshBuffer() {
            if (!multithreading)
                singleThreadRefreshBuffer();
            else 
                multiThreadRefreshBuffer(12);
            
            drawCursor();
      }      
      private void singleThreadRefreshBuffer() {
          for ( int col = 0 ; col < backBuffer.getWidth() ; ++col ) {
                  for ( int row = 0 ; row < backBuffer.getHeight(); ++row )
                  refreshPixel(col, row);    
                  
                  if (progressMessage != null)
                  progressMessage.accept(col+1, backBuffer.getWidth());
            }
      }
      private void multiThreadRefreshBuffer(int threads) {
            List<Runnable> actions = new ArrayList<>();
            AtomicInteger cntr = new AtomicInteger(0);
            final int totalActions = backBuffer.getWidth()*backBuffer.getHeight();

            for ( int col = 0; col < backBuffer.getWidth(); ++col ) {
                for ( int row = 0; row < backBuffer.getHeight(); ++row ) {
                final int c = col;
                final int r = row;
                actions.add(()-> {
                    refreshPixel(c, r);
                    int total = cntr.incrementAndGet();
                    if ( progressMessage != null ) 
                    progressMessage.accept(total, totalActions);
                });
            }
        }
        new ThreadSplitter(threads).run(actions);
        progressMessage.accept(100, 100);
    }
      private void refreshPixel(int x, int y) {
            Coords value = pixelToCoords(new Point(x, y));
            int iterations = iterationsForCoords(value);
            Color color = coloring.apply(iterations);
            backBuffer.setRGB(x, y, color.getRGB());
          
      }
      private int iterationsForCoords(Coords coords) {
            for ( int i = 0 ; i < iterationDepth ; ++i ) 
            if (!boundaryCondition.test(coords, i)) 
                  return i;
            else  
                  coords = equation.apply(coords);
                  
            return MAX_ITERATIONS;
      }
      private void drawCursor() {
          Rect2d area = new Rect2d(0,0, backBuffer.getWidth(), backBuffer.getHeight());
          line (area.points.topCenter(), area.points.bottomCenter());
          line (area.points.leftCenter(), area.points.rightCenter());
      }
      private void line(Point2d from, Point2d to) {
          Graphics2D g = backBuffer.createGraphics();
          g.setColor(Color.red);
          g.drawLine(from.getX(), from.getY(), to.getX(), to.getY());
          g.dispose();
      }

      private Coords pixelToCoords(Point pixel) {
            //return new Coords(pixel.x, pixel.y);
            double outputX = viewport.coords.x;
            outputX += (double)pixel.x / viewport.zoom.dpp;

            double outputY = viewport.coords.y;
            outputY += (double)pixel.y / viewport.zoom.dpp;
            
            return new Coords(outputX, outputY);
      }

      private void swapBuffer() {
            synchronized(monitor) {
                  BufferedImage temp = frontBuffer;
                  frontBuffer = backBuffer;
                  backBuffer = temp;
            }
      }

      public GraphInfo getGraphInfo() {
            return new GraphInfo();
      }
      
      public class GraphInfo {
            
            public int widthPx() {
                  return frontBuffer.getWidth();
            }
            public double widthValues() {
                  return widthPx() * 1d/viewport.zoom.dpp;
            }
 
            public int heightPx() {
                  return frontBuffer.getHeight();
            }
            public double heightValues() {
                  return heightPx() * 1d/viewport.zoom.dpp;
            }

      }
      
      
}
