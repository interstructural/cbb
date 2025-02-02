package pl.zenit.cbb.renderer;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public interface Coloring extends Function<Integer, Color>  {
      
      public static Coloring plain16() {
            return i-> {
                  i = Math.min(i, 16)*15;
                  return new Color(i | i << 8 | i << 16);
            };
      }
      
      public static Coloring hue10(int maxdepth) {
            final int H = 255;
            final int M = 128;
            final int L = 0;
            
            return i-> { 
                  i = Math.min(i, maxdepth);
                  if (i == maxdepth) 
                        return Color.BLACK;
                  int percentage = 10 * i / maxdepth;
                  switch (percentage) {
                        case 0: return new Color(H, L, L);
                        case 1: return new Color(H, M, L);
                        case 2: return new Color(H, H, L);
                        case 3: return new Color(M, H, L);
                        case 4: return new Color(L, H, L);
                        case 5: return new Color(L, H, M);
                        case 6: return new Color(L, H, H);
                        case 7: return new Color(L, M, H);
                        case 8: return new Color(L, L, H);
                        case 9: return new Color(L, L, M);
                        default: return Color.GRAY;
                  }
            };
      }
      
      public static Coloring hue(int maxdepth) {
            final int H = 255;
            final int M = 128;
            final int L = 0;
            List<Color> list = new ArrayList<>();
            list.add(new Color(H, L, L));
            list.add(new Color(H, M, L));
            list.add(new Color(H, H, L));
            list.add(new Color(M, H, L));
            list.add(new Color(L, H, L));
            list.add(new Color(L, H, M));
            list.add(new Color(L, H, H));
            list.add(new Color(L, M, H));
            list.add(new Color(L, L, H));
            list.add(new Color(L, L, M));
            
            List<Color> filled = new ArrayList<>();            
            for (int i = 0 ; i < list.size()-1 ; ++i) {
                  Color base = list.get(i);
                  Color next = list.get(i+1);
                  filled.add(base);
                  
                  int spectrum = 5;
                  for (int j = 1 ; j < spectrum; ++j) {
                        Color blend = flatColorBlend(base, next, (100/spectrum)*i);
                        filled.add(blend);
                  }
            }
            
            filled.add(list.get(list.size()-1));
            list.clear();
            list.addAll(filled);
                  
            return i-> { 
                  int x = Math.min(i, maxdepth);
                  if (x == maxdepth) 
                        return Color.BLACK;
                  int perc = list.size() * x / maxdepth;
                  perc = Math.max(0, perc);
                  perc = Math.min(perc, list.size()-1);
                  return list.get(perc);
            };
      }
      
      public static Coloring grayscale(int maxdepth) {
            return i-> { 
                  i = Math.min(i, maxdepth);
                  float d = (float)i * 255f/ (float)maxdepth;
                  i = Math.min(255, (int)d);
                  return new Color(i | i << 8 | i << 16);
            };
      }
      
      public static Coloring spectrum(int maxdepth) {
            return i-> {
                  i = Math.min(i, maxdepth);
                  int share = maxdepth/3;
                  int small = i/3;
                  int med = Math.min(i-share, share);
                  int large = Math.max(i - 2 * share, 0);
                  if (large > 0) {
                        return new Color(255, 255, Math.min(255, 255*large/share) );
                  }
                  if (med > 0) {
                        return new Color(255, Math.min(255, 255*large/share), 0 );
                  }
                  return new Color(Math.min(255, 255*large/share), 0, 0 );
            };
      }

      public static Coloring moduloHue(final int cycle) {
            Coloring c = Coloring.hue10(cycle);
            return i-> c.apply(i%cycle);
      }

      public static Coloring moduloGrayscale(final int cycle) {
            Coloring c = Coloring.grayscale(cycle);
            return i-> c.apply(i%cycle);
      }
      
      static Color flatColorBlend(Color from, Color to, int percent) {
            if (percent < 0) percent = 0;
            else if (percent > 100) percent = 100;
            
            int r = to.getRed() - from.getRed();
            int g = to.getGreen() - from.getGreen();
            int b = to.getBlue() - from.getBlue();            
            r *= percent; r /= 100;
            g *= percent; g /= 100;
            b *= percent; b /= 100;

            return new Color(from.getRed()+r, from.getGreen() + g, from.getBlue()+b);
      }      
}
