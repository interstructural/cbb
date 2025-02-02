package pl.zenit.cbb.d2;

public class Rect2d {

      public final int left;
      public final int top;
      public final int right;
      public final int bottom;

      public Rect2d(int left, int top, int right, int bottom) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
      }
      public Rect2d(int width, int height) {
            this.left = 0;
            this.top = 0;
            this.right = width;
            this.bottom = height;
      }
      public Rect2d(final Point2d p, final Size2d s) {
            left = p.getX();
            top = p.getY();
            right = s.getWidth() + p.getX();
            bottom = s.getHeight() + p.getY();
      }
      
      public int width() {
            return right-left;
      }
      public int height() {
            return bottom-top;
      }
      public Size2d size() {
            return new Size2d(width(), height());
      }
      //---------------------------------------------------------------------------------------------------
      public Rect2d setLeft(int newLeft) {
            return new Rect2d(newLeft, top, right, bottom);
      }
      public Rect2d setTop(int newTop) {
            return new Rect2d(left, newTop, right, bottom);
      }
      public Rect2d setRight(int newRight) {
            return new Rect2d(left, top, newRight, bottom);
      }
      public Rect2d setBottom(int newBottom) {
            return new Rect2d(left, top, right, newBottom);
      }
      //---------------------------------------------------------------------------------------------------
      public final PointsOnRect points = new PointsOnRect();
      public final class PointsOnRect {
            public Point2d center() {
                  return new Point2d(left + width()/2, top + height()/2);
            }
            public Point2d leftCenter() {
                  return new Point2d(left, top + height()/2);
            }
            public Point2d topCenter() {
                  return new Point2d(left + width()/2, top);
            }
            public Point2d rightCenter() {
                  return new Point2d(right, top + height()/2);
            }
            public Point2d bottomCenter() {
                  return new Point2d(left + width()/2, bottom);
            }
      
            public Point2d leftTop() {
                  return new Point2d(left, top);
            }
            public Point2d rightTop() {
                  return new Point2d(right, top);
            }
            public Point2d leftBottom() {
                  return new Point2d(left, bottom);
            }
            public Point2d rightBottom() {
                  return new Point2d(right, bottom);
            }
      }
      //---------------------------------------------------------------------------------------------------
      public final MoveRect move = new MoveRect();
      public final class MoveRect {
            public Rect2d by(final Point2d p) {
                  return new Rect2d(left + p.getX(), top + p.getY(), right + p.getX(), bottom + p.getY());
            }
            public Rect2d leftTopTo(final Point2d p) {
                  return by(Points2d.getTravel(points.leftTop(), p));
            }
            public Rect2d rightTopTo(final Point2d p) {
                  return by(Points2d.getTravel(points.rightTop(), p));
            }
            public Rect2d leftBottomTo(final Point2d p) {
                  return by(Points2d.getTravel(points.leftBottom(), p));
            }
            public Rect2d rightBottomTo(final Point2d p) {
                  return by(Points2d.getTravel(points.rightBottom(), p));
            }
            public Rect2d centerTo(final Point2d p) {
                  return by(Points2d.getTravel(points.center(), p));
            }
            public Rect2d alignLeftTo(int level) {
                  int width = width();
                  return new Rect2d(level, top, level + width, bottom);
            }
            public Rect2d alignTopTo(int level) {
                  int height = height();
                  return new Rect2d(left, level, right, level + height);
            }
            public Rect2d alignRightTo(int level) {
                  int width = width();
                  return new Rect2d(level-width, top, level, bottom);
            }
            public Rect2d alignBottomTo(int level) {
                  int height = height();
                  return new Rect2d(left, level-height, right, level);
            }
      }
      //---------------------------------------------------------------------------------------------------
      public final RectsOnRect rects = new RectsOnRect();
      public final class RectsOnRect {            
            public Rect2d halfLeft() {
                  return verticalStrip(0, 2);
            }
            public Rect2d halfRight() {                   
                  return verticalStrip(1, 2);
            }
            public Rect2d halfTop() { 
                  return horizontalStrip(0, 2);
            }
            public Rect2d halfBottom() { 
                  return horizontalStrip(1, 2);
            }
      
            public Rect2d quarterLT() { 
                  return matrixStrip(0, 0, 2, 2);
            }
            public Rect2d quarterRT() { 
                  return matrixStrip(1, 0, 2, 2);
            }
            public Rect2d quarterLB() { 
                 return matrixStrip(0, 1, 2, 2);
            }
            public Rect2d quarterRB() { 
                  return matrixStrip(1, 1, 2, 2);
            }
      
            public Rect2d horizontalStrip(final int stripIndex, final int stripCount) {
                  //left, right bez zmian
                  
                  //wyliczam wysokosc samego paska
                  int stripStart = stripIndex * height() / stripCount; 
                  int stripEnd = (stripIndex+1) * height() / stripCount;
                  
                  return new Rect2d(left, top + stripStart, right, top + stripEnd);
            }
            public Rect2d verticalStrip(final int stripIndex, final int stripCount) {
                  //top, bottom bez zmian
                  
                  //wyliczam szerokosc samego paska
                  int stripStart = stripIndex * width() / stripCount;
                  int stripEnd = (stripIndex+1) * width() / stripCount;
                  return new Rect2d(left + stripStart, top, left + stripEnd, bottom);
            }
            public Rect2d matrixStrip(final int X, final int Y, final int rowCount, final int colCount) {                   
                  Rect2d hs = horizontalStrip(Y, rowCount);                  
                  Rect2d vs = verticalStrip(X, colCount);
                  return new Rect2d(vs.left, hs.top, vs.right, hs.bottom);
            }
      }
      //---------------------------------------------------------------------------------------------------
      public final TransformRect transform = new TransformRect();
      public final class TransformRect {
            public Rect2d expand(final int amount) {
                  return new Rect2d(left - amount, top - amount, right + amount, bottom + amount);
            }
            public Rect2d shrink(final int amount) {
                  return expand(-amount);
            }
      }
      //---------------------------------------------------------------------------------------------------
      
}