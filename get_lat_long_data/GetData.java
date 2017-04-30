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
             Process pro = Runtime.getRuntime().exec("python3 bigparser.py");

           BufferedReader stdInput = new BufferedReader(new InputStreamReader(pro.getInputStream()));
           BufferedReader stdError = new BufferedReader(new InputStreamReader(pro.getErrorStream()));

           System.out.println("Here is standard output");
           String s = null;
           while((s=stdInput.readLine())!=null) {
              System.out.println(s);
           }

           System.out.println("Here is standard error");
           while((s=stdError.readLine())!=null) {
              System.out.println(s);
           }
           try {
              pro.waitFor();
           }
           catch(Exception e) {}
        } catch(Exception epx) {
            Process pro = Runtime.getRuntime().exec("python bigparser.py");

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(pro.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(pro.getErrorStream()));

            System.out.println("Here is standard output");
            String s = null;
            while((s=stdInput.readLine())!=null) {
               System.out.println(s);
            }

            System.out.println("Here is standard error");
            while((s=stdError.readLine())!=null) {
               System.out.println(s);
            }
            try {
               pro.waitFor();
            }
            catch(Exception e) {}
        }
        System.out.println("done, in java file");
      }
}
