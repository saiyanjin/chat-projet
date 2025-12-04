package client;

import java.io.*;
import java.net.*;
import java.util.function.Consumer;

public class ChatClient {
	
	private final String host;
	private final int port;
	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;
	private Thread readerThread;
	
	public ChatClient(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	public boolean connexion(String username, String password, Consumer<String> onMessage) {
		try {
			socket = new Socket(host, port);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	
			out.println("LOGIN " + username + " " + password);
			String response = in.readLine();
			if (!"LOGIN_OK".equals(response)) {
				fermer();
				return false;
	         }
	
			readerThread = new Thread(() -> {
				try {
					String s;
					while ((s = in.readLine()) != null) {
						onMessage.accept(s);
					}
				} catch (IOException e) {
					onMessage.accept("SYSTEM: déconnecté du serveur");
				}
			}, "ServerReader");
			readerThread.setDaemon(true);
			readerThread.start();
	
			return true;
		} catch (IOException e) {
			fermer();
			return false;
		}
	}
	
	public void envoyerMessage(String message) {
		if (out != null) {
			out.println("MSG " + message);
		}
	}
	
	 public void deconnexion() {
	     if (out != null) out.println("QUIT");
	     fermer();
	 }
	
	 private void fermer() {
		 try {
			 if (out != null) {
				 out.close(); 
			 }
		 } catch (Exception ignored) {}
		 
		 try {
			 if (in != null) {
				 in.close(); 
			 }
		 } catch (Exception ignored) {}
		 
	     try { 
	    	 if (socket != null) {
	    		 socket.close(); 
	    	 }
	     } catch (Exception ignored) {}
	 }
}