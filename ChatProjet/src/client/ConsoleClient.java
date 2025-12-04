package client;

import java.io.*;
import java.net.*;
import java.util.*;

public class ConsoleClient {
	
	public static void main(String[] args) throws Exception {
		String host = "localhost";
		int port = 5000;
		try (Socket socket = new Socket(host, port);
		BufferedReader serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				
		Scanner console = new Scanner(System.in)) {
			System.out.print("username: ");
			String user = console.nextLine();
			System.out.print("password: ");
			String pass = console.nextLine();
	
			out.println("LOGIN " + user + " " + pass);
			String response = serverIn.readLine();
			if (!"LOGIN_OK".equals(response)) {
				System.out.println("Échec authentification : " + response);
				return;
			}
			System.out.println("Connecté au chat. Tape tes messages.");
	
			Thread reader = new Thread(() -> {
				try {
					String s;
					while ((s = serverIn.readLine()) != null) {
						System.out.println(s);
					}
				} catch (IOException e) {}
	         });
			reader.setDaemon(true);
			reader.start();
	
			while (true) {
				String msg = console.nextLine();
				if (msg.equalsIgnoreCase("/quit")) {
					out.println("QUIT");
					break;
				} else {
					out.println("MSG " + msg);
	             }
	         }
	     }
	 }
}