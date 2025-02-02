package pl.zenit.cbb.renderer;

import java.util.HashMap;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public strictfp class UnaryFunctions extends HashMap<String, UnaryOperator<Double>> {

      private final Supplier<Double> param;

      public UnaryFunctions(Supplier<Double> param) {
            this.param = param;
            put("POW", f-> functionOf(f, this::pow));
            put("SIN", f-> functionOf(f, this::sin));
            put("COS", f-> functionOf(f, this::cos));
            put("ROOT", f-> functionOf(f, this::root));
            put("ROOT3", f-> functionOf(f, this::root3));
            put("SIGNUM", f-> functionOf(f, this::sig));
            put("EXP", f-> functionOf(f, this::exp));
            put("SINABS", f-> functionOf(f, this::sinabs));
            put("ARCCOS", f-> functionOf(f, this::arccos));
            put("CHAOS", f-> functionOf(f, this::chaos));            
            
      }

      private double functionOf(double input, UnaryOperator<Double> f) {
            input = f.apply(input);
            return input;
      }
      
      private double pow(double input) {
            return Math.pow(input, arg()-input);
      }
      private double sin(double input) {
            return Math.sin(input) * (arg()-input);
      }
      private double cos(double input) {
            return Math.cos(input) * (arg()-input);
      }    
      private double root(double input) {
            return Math.sqrt(input) * (arg()-input);
      }    
      private double root3(double input) {
            return Math.cbrt(input) * (arg()-input);
      }    
      private double exp(double input) {
            return input/Math.exp(input);
      }
      private double sig(double input) {
            
            return Math.pow(Math.sinh(Math.random()), arg())/input;
      }
      private double sinabs(double input){
            return Math.sin(arg()*input)*Math.abs(Math.cos(input));
      }
      private double arccos(double input){
            return Math.acos(arg()*input) * (arg()-input);
      }
      private double chaos(double input) {
            return arg() * input * (1f - input);
      }
      
      private double arg() {
            return param.get();
      }
      
      
}
