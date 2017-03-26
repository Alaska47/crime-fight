import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.lang.Math;

public class GetData
{

   public static void main(String[] args) throws FileNotFoundException, IOException {

      try {
         Process pro = Runtime.getRuntime().exec("python3 sync.py");
         try {
            pro.waitFor();
         }
         catch(Exception e) {}
      }
      catch(Exception epx) {
         Process pro = Runtime.getRuntime().exec("python sync.py");
         try {
            pro.waitFor();
         }
         catch(Exception e) {}
      }
   }
}