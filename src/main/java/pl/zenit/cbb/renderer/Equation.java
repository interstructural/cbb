package pl.zenit.cbb.renderer;

import java.util.function.UnaryOperator;

public interface Equation extends UnaryOperator<Coords> {
      
      public static final Equation FLAT = k-> k;
      
      public static Equation unary(UnaryOperator<Double> mod) {
            return coords-> new Coords(mod.apply(coords.x), mod.apply(coords.y));
      }
      
}
