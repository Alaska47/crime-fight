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

public class testconnect {
	public static void main(String[] args) throws Exception {
		URL url = new URL("http://crimefight-server-shrucis1.c9users.io");
		String serverAddress = "192.168.1.181"; //:8080
		//String serverAddress = "crimefight-server-shrucis1.c9users.io"; //:8080
		System.out.println("2Connecting to " + url.getHost());//InetAddress.getByName(serverAddress));
		Socket socket = new Socket("crimefight-server-shrucis1.c9users.io", 8081);

		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));


		ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());


		//PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		System.out.println("Sending...");
		oos.writeObject("aay faf\n");
		//out.flush();
		System.out.println("Done");
	}
}
