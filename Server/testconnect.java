import java.net.ServerSocket;
import java.net.Socket;
import javax.imageio.ImageIO;
import java.util.*;
import java.lang.*;
import java.lang.Math;
import java.io.*;
import java.awt.image.BufferedImage;
import java.net.InetAddress;
import java.util.concurrent.ConcurrentLinkedQueue;

public class testconnect {
	public static void main(String[] args) throws Exception {
		String serverAddress = "crimefight-server-shrucis1.c9users.io"; //:8080
		System.out.println("Connecting to " + InetAddress.getByName(serverAddress));
		Socket socket = new Socket(InetAddress.getByName(serverAddress), 8080);
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		System.out.println("Sending...");
		out.println("gay faf");
		System.out.println("Done");
	}
}