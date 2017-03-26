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
import java.util.Date;

public class CrimeFighterServer
{
   public static final int PORT_NUMBER = 914;

   public static int lastItemID = 111;

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
         int userID, itemID;
         Document docUpdate;
         Document queryDoc;
         String itemName,itemDesc;
         switch(requestType) {
         	case 0:	
               userID = Integer.parseInt(params[1]);
               String authKey = params[2];
               queryDoc = CrimeFighterServer.userInfo.find(Filters.eq("userID", userID)).first();
               docUpdate = new Document("userID", userID).append("curLat", -1.0).append("curLong",-1.0).append("authKey",authKey);
               if(queryDoc == null) {
               	   CrimeFighterServer.userInfo.insertOne(docUpdate);
               } else {
               	   CrimeFighterServer.userInfo.findOneAndReplace(Filters.eq("userID", userID), docUpdate);
               }
               break;
            case 1:

               userID = Integer.parseInt(params[1]);
               lat = Double.parseDouble(params[2]);
               lon = Double.parseDouble(params[3]);
               queryDoc = CrimeFighterServer.userInfo.find(Filters.eq("userID", userID)).first();
               if(queryDoc == null) {
               	   docUpdate = new Document("userID",userID).append("curLat", lat).append("curLong", lon).append("authKey","000000000000");
               	   CrimeFighterServer.userInfo.insertOne(docUpdate);
               } else {
               	   docUpdate = new Document("userID",userID).append("curLat", lat).append("curLong", lon).append("authKey",queryDoc.get("authKey"));
                   CrimeFighterServer.userInfo.findOneAndReplace(Filters.eq("userID", userID), docUpdate);
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
               queryDoc = CrimeFighterServer.userInfo.find(Filters.eq("userID", userID)).first();
               if(queryDoc == null) {
               	   docUpdate = new Document("userID",userID).append("curLat", lat).append("curLong", lon).append("authKey","000000000000");
               	   CrimeFighterServer.userInfo.insertOne(docUpdate);
               } else {
               	   docUpdate = new Document("userID",userID).append("curLat", lat).append("curLong", lon).append("authKey",queryDoc.get("authKey"));
                   CrimeFighterServer.userInfo.findOneAndReplace(Filters.eq("userID", userID), docUpdate);
               }

               String responseMessage = "";
               int c = 0;
               for(Document d : CrimeFighterServer.watchItems.find()) {
               	   c ++;
               	   responseMessage += d.get("itemName") + "," + d.get("itemDesc") + ",";
               	   responseMessage += "" + distance(lat, lon, (Double) d.get("curLat"), (Double) d.get("curLong")) + ",";
               	   responseMessage += "" + d.get("curLat") + "," + d.get("curLong") + ",";
               	   responseMessage += d.get("itemID") + ",";
               }
               responseMessage = "" + c + "," + responseMessage.substring(0, responseMessage.length() - 1);
               System.out.println("Responding to userID " + userID + " with responseMsg: ");
               System.out.println(responseMessage);
            
               break;
         

            case 3:


               userID = Integer.parseInt(params[1]);
               itemName = params[2];
               itemDesc = params[3];
               lat = Double.parseDouble(params[4]);
               lon = Double.parseDouble(params[5]);

               docUpdate = new Document("ownerID",userID).append("itemID", CrimeFighterServer.lastItemID++).append("itemName",itemName).append("itemDesc",itemDesc)
                                               .append("curLat",lat).append("curLong", lon).append("lastTime", new Date().getTime());
               CrimeFighterServer.watchItems.insertOne(docUpdate);

               break;

            case 4:
               itemID = Integer.parseInt(params[1]);
               queryDoc = CrimeFighterServer.watchItems.find(Filters.eq("itemID",itemID)).first();
               if(params[2].equalsIgnoreCase("y")) {
               	  // confirm seen, just update time
               	   long newLastSeenTime = new Date().getTime();
               	   docUpdate = new Document("ownerID",queryDoc.get("ownerID")).append("itemID",queryDoc.get("itemID")).append("itemName",queryDoc.get("itemName"))
               	   								.append("itemDesc",queryDoc.get("itemDesc")).append("curLat",queryDoc.get("curLat")).append("curLong",queryDoc.get("curLong"))
               	   								.append("lastTime", newLastSeenTime);
               } else {
               		// oh no, it's stolen 
               		int ownerID = (Integer) queryDoc.get("ownerID");
               		Document randomDoc = CrimeFighterServer.userInfo.find(Filters.eq("userID",ownerID)).first();
               		String stolenMessage = "Your item [" + queryDoc.get("itemName") + "] has been reported missing/stolen";
               		stolenMessage = stolenMessage.replace(" ","%20");
               		NotificationSender.sendNotification(stolenMessage, new String[] {(String) randomDoc.get("authKey")});
               }
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