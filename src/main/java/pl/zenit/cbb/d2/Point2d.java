package pl.zenit.cbb.d2;

public final class Point2d {
    
    private final int x;

    private final int y;
    
    public Point2d(final int x, final int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    
    public Point2d withX(final int x) {
        return new Point2d(x, y);
    }
    
    public Point2d withY(final int y) {
        return new Point2d (x, y);
    }
    
    public static PointBuilder builder() {
        return new PointBuilder();
    }

    public static final class PointBuilder {
        
        private PointBuilder() {}

        private int x;
        private int y;
        
        public PointBuilder x(final int x) {
            this.x = x;
            return this;
        }
        
        public PointBuilder y(final int y) {
            this.y = y;
            return this;
        }
        
        public Point2d build() {
            return new Point2d(x, y);
        }
    }

}