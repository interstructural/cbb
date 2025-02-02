package pl.zenit.cbb.d2;

public class Size2d {
    
    private final int width;

    private final int height;
    
    public Size2d(final int width, final int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
    
    public Size2d withWidth(final int width) {
        return new Size2d(width, height);
    }
    
    public Size2d withHeight(final int height) {
        return new Size2d(width, height);
    }
    
    public static final SizeBuilder builder() {
        return new SizeBuilder();
    }

    public static final class SizeBuilder {
        
        private SizeBuilder() {}
        
        private int width;
        private int height;
        
        public SizeBuilder width(final int width) {
            this.width = width;
            return this;
        }
        
        public SizeBuilder height(final int height) {
            this.height = height;
            return this;
        }
        
        public Size2d build() {
            return new Size2d(width, height);
        }
    }

}
