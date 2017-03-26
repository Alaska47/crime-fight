import java.net.ServerSocket;
import java.net.Socket;
import javax.imageio.ImageIO;
import java.util.*;
import java.lang.*;
import java.lang.Math;
import java.io.*;
import java.awt.image.BufferedImage;
import java.net.InetAddress;
import java.net.URL;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConnectionTester {
	public static void main(String[] args) throws Exception {

		String serverAddress = "71.171.96.88"; 
		Socket socket = new Socket(serverAddress, 914);

		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
		ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

		//PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		//String query = "2,18109,38.8190096,-77.168153";
		String query = "3,42069,MLH Alienware Laptop,Left at Hardware booth,51.5074,0.1278\n";
		System.out.println("Sending <" + query + ">");
		oos.writeObject(query);
		//out.println("gay faf");
		//out.flush();
		System.out.println("Received: ");
		System.out.println(ois.readObject());
		System.out.println("Received: ");
		System.out.println(ois.readObject());
		in.close();
		oos.close();
		System.out.println("Done");
	}
}