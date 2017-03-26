import com.mongodb.client.model.Filters;
import com.mongodb.client.*;
import com.mongodb.*;
import org.bson.*;
import java.net.ServerSocket;
import java.net.Socket;
import javax.imageio.ImageIO;
import java.util.*;
import java.lang.*;
import java.lang.Math;
import java.io.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CrimeFighterServer
{
   public static final int PORT_NUMBER = 914;

   public static MongoCollection<Document> userInfo;
   public static MongoCollection<Document> watchItems;

   public static void main(String[] args) {
      try {
         CrimeFighterServer myServ = new CrimeFighterServer(PORT_NUMBER);
         myServ.serverMain();
      }
      catch(Exception e) {
      	 e.printStackTrace();
      }
   }

   private int myPort;

   public CrimeFighterServer(int port) {
      myPort = port;
      System.out.println("CrimeFighterServer Initiated on Port #" + myPort);
   }

   public void serverMain() throws Exception {
      System.out.println("[CrimeFighterServer] starting main");
      System.out.println("[CrimeFighterServer] connecting to database");
   	  MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
      MongoDatabase database = mongoClient.getDatabase("mydb");
      userInfo = database.getCollection("userInfo");
      watchItems = database.getCollection("watchItems");
      System.out.println("[CrimeFighterServer] database collection successfully retrieved");

      ServerSocket serverSocket;
      Socket curSocket;
      ConnectionHandler curHandler;
   
      serverSocket = new ServerSocket(myPort);
   

      int curConnID = 1;
      while(true) {
         System.out.println("[CrimeFighterServer] Listening, next connection ID = " + curConnID);
      
         curSocket = serverSocket.accept();  // blocks
      
         System.out.println("[CrimeFighterServer] Received connection, ID = " + curConnID);
         System.out.println("[CrimeFighterServer] IP Address: " + curSocket.getRemoteSocketAddress().toString());
      
         curHandler = new ConnectionHandler(curSocket, curConnID);
         new Thread(curHandler).start();
      
         curConnID ++;
      
         Thread.sleep(25);
      }
   
   }
}


class ConnectionHandler implements Runnable {

   private Socket mySocketClient;
   private int myID;

   public ConnectionHandler(Socket myClient, int nID) {
      mySocketClient = myClient;
      myID = nID;
      System.out.println("New connection handler created!");
   }


   public void run() {
   
   	  System.out.println("ConnectionHandler " + myID + " DOES in fact exist");

      ObjectInputStream ois = null;
      ObjectOutputStream oos = null;
   
      try {
         /*
      	OutputStream os = mySocketClient.getOutputStream();
      	PrintWriter pw = new PrintWriter(os, true);
      	BufferedReader br = new BufferedReader(new InputStreamReader(mySocketClient.getInputStream()));
      
      	String request = br.readLine();
         */
         
         ois = new ObjectInputStream(mySocketClient.getInputStream());
         oos = new ObjectOutputStream(mySocketClient.getOutputStream());
         
         String request = "";
         
         try {
            request = (String) ois.readObject();
         } 
         catch (ClassNotFoundException e) {
            e.printStackTrace();
         }
         
         System.out.println("Here is raw request line: " + request);
      
         String[] params = request.split(",");
         System.out.println(params);
         int requestType = Integer.parseInt(params[0]);
         System.out.println("Identified request type as " + requestType);
         double lat;
         double lon;
         int userID;
         Document locationUpdate;
         switch(requestType) {
            case 1:

               userID = Integer.parseInt(params[1]);
               lat = Double.parseDouble(params[2]);
               lon = Double.parseDouble(params[3]);
               locationUpdate = new Document("userID",userID).append("curLat", lat).append("curLong", lon);
               if(CrimeFighterServer.userInfo.find(Filters.eq("userID", userID)).first() == null) {
               	   CrimeFighterServer.userInfo.insertOne(locationUpdate);
               } else {
                   CrimeFighterServer.userInfo.findOneAndReplace(Filters.eq("userID", userID), locationUpdate);
               }

               /*oos.writeObject(10);
              // startup, give us 10 buffered images and ???
              // technically they gave us location, but we're just sending last 10 soz
               BufferedImage[] recentTen = myParent.getRecentTenPhotos();
               for(BufferedImage bufimg : recentTen) {
                  ByteArrayOutputStream baos = new ByteArrayOutputStream();
                  ImageIO.write( bufimg, "png", baos );
                  baos.flush();
                  byte[] imageInByte = baos.toByteArray();
                  baos.close();
                  oos.writeObject(imageInByte);
               }*/
            
               break;
         
            case 2: // case 2, given location and radius, send back all the beacons, their coords, their ids, 
                    // and all their feature images
            
              // reading what user sent me

               userID = Integer.parseInt(params[1]);
               lat = Double.parseDouble(params[2]);
               lon = Double.parseDouble(params[3]);
               locationUpdate = new Document("userID",userID).append("curLat", lat).append("curLong", lon);
               if(CrimeFighterServer.userInfo.find(Filters.eq("userID", userID)).first() == null) {
               	   CrimeFighterServer.userInfo.insertOne(locationUpdate);
               } else {
                   CrimeFighterServer.userInfo.findOneAndReplace(Filters.eq("userID", userID), locationUpdate);
               }
               String responseMessage = "";
               int c = 0;
               for(Document d : CrimeFighterServer.watchItems.find()) {
               	   c ++;
               	   responseMessage += d.get("itemName") + ",";
               	   responseMessage += "" + distance(lat, lon, (Double) d.get("lat"), (Double) d.get("long")) + ",";
               }
               responseMessage = "" + c + responseMessage.substring(0, responseMessage.length() - 1);
               System.out.println("Responding to userID " + userID + " with responseMsg: ");
               System.out.println(responseMessage);

              
              // now we send the images (after string has been received and parsed)
               /*for(Beacon tmp : nearbyBeacons) {
                           // (Buffered Image)   , 
                  BufferedImage bi = tmp.getFeatureImage();
                  ByteArrayOutputStream baos = new ByteArrayOutputStream();
                  ImageIO.write( bi, "png", baos );
                  baos.flush();
                  byte[] imageInByte = baos.toByteArray();
                  baos.close();
                  oos.writeObject(imageInByte); 
                  //ImageIO.write(tmp.getFeatureImage(), "png", os);
               }*/
            
               break;
         


/*




            case 3:
               int beaconID = Integer.parseInt(params[1]);
               Beacon target = myParent.getBeaconById(beaconID);
               ArrayList<PhotoWrapper> photos = target.getPhotos();
               String response = "" + photos.size();
               if(target.isBusiness()) {
                  response = response + " 1";
               }
               else {
                  response = response + " 0";
               }
               //pw.println(response);
               oos.writeObject(response);
               
               if(target.isBusiness()) {
               // next 3 lines are business crap
                  
                  //pw.println(target.getBusiness().getName());
                  //pw.println(target.getBusiness().getDescription());
                  //pw.println(target.getBusiness().getAddress());
                  
                  oos.writeObject(target.getBusiness().getName());
                  oos.writeObject(target.getBusiness().getDescription());
                  oos.writeObject(target.getBusiness().getAddress());
               }
               for(int j = 0; j < photos.size(); j ++) {
               // new line for each photo, 1st thing on the line is an int, num of likes, rest are tags (strings)
                  String thisline = "" + (new Random(j)).nextInt(11);
                  for(String s : photos.get(j).getTags()) {
                     thisline = thisline + " " + s;
                  }
                  //pw.println(thisline);
                  oos.writeObject(thisline);
               }
               for(int i = 0; i < photos.size(); i ++) {
                  BufferedImage bi = photos.get(i).getImage();
                  //ImageIO.write(photos.get(i).getImage(), "png", os);
                  ByteArrayOutputStream baos = new ByteArrayOutputStream();
                  ImageIO.write( bi, "png", baos );
                  baos.flush();
                  byte[] imageInByte = baos.toByteArray();
                  baos.close();
                  oos.writeObject(imageInByte);
               }
               break;
            case 5:
              // guy is sending us stuff, format:
              // 5 [lat] [lon] [tag1] [tag2] ... [tag3]
               lat = Double.parseDouble(params[1]);
               lon = Double.parseDouble(params[2]);
               System.out.println("Parsed lat and lon as " + lat + ", " + lon);
               String[] tags = new String[(params.length - 3)];
               for(int i = 0; i < params.length - 3; i ++) {
                  tags[i] = params[3+i];
               }
            
              // receive actual image (just one)
               System.out.println("Now attempting to read image");
               
               byte[] bb = null;
               try {
                  bb = (byte[]) ois.readObject();
               } 
               catch(ClassNotFoundException e) {
                  e.printStackTrace();
               }
               
               InputStream in = new ByteArrayInputStream(bb);
               BufferedImage img = ImageIO.read(in);
               
              //BufferedImage img = ImageIO.read(mySocketClient.getInputStream());
            
              //BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
              //DataInputStream dis = new DataInputStream
            
               System.out.println("Image read");
              // done
               myParent.addPhoto(img, tags, new Location(lat, lon));
            
               break;
            case 6:
              // request to add a business
              // format: 
              // 5 [lat] [lon]
              // then, as SEPARATE LINE
              // [Business Name] (space supported)
              // then, as SEPARATE LINE
              // [Mini description] (space supported)
               lat = Double.parseDouble(params[1]);
               lon = Double.parseDouble(params[2]);
               
               //String businessName = br.readLine();
               //String businessDescription = br.readLine();
               //String businessAddress = br.readLine();
               
               
               String businessName = "";
               String businessDescription = "";
               String businessAddress = "";
               
               try {
                  businessName = (String) ois.readObject();
                  businessDescription = (String) ois.readObject();
                  businessAddress = (String) ois.readObject();
               } 
               catch (ClassNotFoundException e) {
                  e.printStackTrace();
               }
               
               Business newBus = new Business(businessName, businessDescription, businessAddress, new Location(lat,lon));
               myParent.addBusiness(newBus);
               break;*/
            default:
               break;
         }
      	// pw.println("xd xd lmfao");
      }
      //catch(Exception e) {
      //}
      catch(IOException ioe) {
         System.out.println("Something went wrong, Handler " + myID);
         ioe.printStackTrace();
      }
   
      try {
         mySocketClient.close();
         ois.close();
         oos.close();
      }
      catch(IOException ioe) {
         System.out.println("[Handler " + myID + "] could not close connection (???)");
         ioe.printStackTrace();
      }
      System.out.println("[Handler " + myID + "] Connection closed, thread done");
   
   }

	// ---------------
	// credit for this method goes to http://www.geodatasource.com/developers/java
	// ---------------
   public static double distance(double lat1, double lon1, double lat2, double lon2) {
      double theta = lon1 - lon2;
      double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
      dist = Math.acos(dist);
      dist = rad2deg(dist);
      dist = dist * 60 * 1.1515;
      return (dist);
   }

   public static double deg2rad(double deg) {
      return (deg * Math.PI / 180.0);
   }

   public static double rad2deg(double rad) {
      return (rad * 180 / Math.PI);
   }
}