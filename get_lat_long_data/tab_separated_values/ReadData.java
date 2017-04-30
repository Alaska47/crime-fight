import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class ReadData {
    public static void main(String[] args) {
        System.out.println("Why Are you calling main? Call 'getdata'. I'll do it for you.");
        getdata(0);
    }
    public static ArrayList<Double> getdata(int input) {
        String[] cities = {"dc", "philadelphia", "los_angeles", "minneapolis", "salt_lake_city", "chicago"};
        try{
            BufferedReader buf = new BufferedReader(new FileReader(cities[input]+".txt")); //change to closest city
            ArrayList<Double> all_data = new ArrayList<>();
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
                            all_data.add(Double.parseDouble(coord));
                        }
                    }
                }
            }

            for(Double coord : all_data){
                System.out.println(coord);
            }

            buf.close();
            //System.out.println(all_data);
            return all_data;
            
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return null;
    }
}
