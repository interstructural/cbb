package pl.zenit.cbb.renderer;

import java.util.function.BiPredicate;

public interface BoundaryCondition extends BiPredicate<Coords, Integer> {
    
      public static final BoundaryCondition TRUE = (c, i)-> true;
      public static final BoundaryCondition FALSE = (c, i)-> false;
      
      public static BoundaryCondition absoluteLimit(double limit) {
            return (coords, i)-> Math.abs(coords.x) > limit || Math.abs(coords.y) > limit;
      }

      public static BoundaryCondition difference(double limit) {
            return (coords, i)-> Math.abs(coords.x - coords.y) > limit;
      }
      
      /** def 4 */
      public static BoundaryCondition mandelbrotLimit(double limit) {
            return (coords, i)-> {
                  if (coords.x == 0 && coords.y == 0)
                        return false;

//                  final double scale = 10000;
                  double real = coords.x;
                  double imag = coords.y;
                  
                  return (real * real + imag * imag) < limit;
            };
      }

      public static BoundaryCondition powedDiff(double rate) {
            return (c, i)->
                  Math.pow(Math.abs(c.x), rate) > Math.abs(c.y)
                  && Math.pow( Math.abs(c.y), rate) > Math.abs(c.x);
      }

      public static BoundaryCondition circle(double rate) {
            double size = 2;
        return (c, i)->
            Math.pow( Math.abs(c.x), rate) + Math.pow( Math.abs(c.y), rate) < Math.pow( Math.abs(size), rate);
      }

}
