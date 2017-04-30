import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class ReadData {
    public static void main(String[] args) {
        String[] cities = {"dc", "philadelphia", "los_angeles", "minneapolis", "salt_lake_city", "chicago"};
        try{
            BufferedReader buf = new BufferedReader(new FileReader(cities[0]+".txt")); //change to closest city
            ArrayList<String> all_data = new ArrayList<>();
            String lineJustFetched = null;
            String[] latLongArray;
            String skip_first_line = buf.readLine(); //ignore headers
            while(true){
                lineJustFetched = buf.readLine();
                if(lineJustFetched == null){  
                    break; 
                }else{
                    latLongArray = lineJustFetched.split("\t");
                    for(String coord : latLongArray){
                        if(!"".equals(coord)){
                            all_data.add(coord);
                        }
                    }
                }
            }

            for(String coord : all_data){
                System.out.println(coord);
            }

            buf.close();

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}  