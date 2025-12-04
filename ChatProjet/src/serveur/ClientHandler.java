package serveur;

import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
	private final Socket socket;
	private final ChatServer server;
	private final AuthService auth;
	private String username;
	private PrintWriter out;
	private BufferedReader in;
	
	public ClientHandler(Socket socket, ChatServer server, AuthService auth) {
		this.socket = socket;
		this.server = server;
		this.auth = auth;
	}
	
	@Override
	public void run() {
		try {
			in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
	
			String line = in.readLine();
			if (line == null) {
				closeEverything();
				return;
			}
	
			if (!line.startsWith("LOGIN ")) {
				out.println("ERROR Protocol");
				closeEverything();
				return;
			}
	
			String[] parts = line.split(" ", 3);
			if (parts.length < 3) {
				out.println("ERROR Login format");
				closeEverything();
				return;
			}
	
			String user = parts[1];
			String pass = parts[2];
	
			if (!auth.authenticate(user, pass)) {
				out.println("LOGIN_FAIL");
				closeEverything();
				return;
			}
	
			this.username = user;
			out.println("LOGIN_OK");
			server.addWriter(out);
			server.broadcast("SYSTEM: " + username + " a rejoint le chat.");
	
			while ((line = in.readLine()) != null) {
				if (line.startsWith("MSG ")) {
					String msg = line.substring(4);
					server.broadcast(username + ": " + msg);
				} else if (line.equals("QUIT")) {
					break;
				} else {
					out.println("ERROR Unknown command");
				}
			}
		} catch (IOException e) {
			System.err.println("Erreur client " + username + ": " + e.getMessage());
		} finally {
			server.removeWriter(out);
			server.broadcast("SYSTEM: " + username + " a quitté le chat.");
			closeEverything();
		}
	}
	
	private void closeEverything() {
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