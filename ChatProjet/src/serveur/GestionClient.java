package serveur;

import java.io.*;
import java.net.*;

public class GestionClient implements Runnable {
	private final Socket socket;
	private final ChatServer server;
	private final Authentification auth;
	private String pseudo;
	private PrintWriter out;
	private BufferedReader in;
	
	public GestionClient(Socket socket, ChatServer server, Authentification auth) {
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
				toutFermer();
				return;
			}
	
			if (!line.startsWith("LOGIN ")) {
				out.println("ERROR Protocol");
				toutFermer();
				return;
			}
	
			String[] parts = line.split(" ", 3);
			if (parts.length < 3) {
				out.println("ERROR Login format");
				toutFermer();
				return;
			}
	
			String user = parts[1];
			String pass = parts[2];
	
			if (!auth.authentifier(user, pass)) {
				out.println("LOGIN_FAIL");
				toutFermer();
				return;
			}
	
			this.pseudo = user;
			out.println("LOGIN_OK");
			server.addWriter(out);
			server.broadcast(pseudo + " a rejoint le chat.");
	
			while ((line = in.readLine()) != null) {
				if (line.startsWith("MSG ")) {
					String msg = line.substring(4);
					server.broadcast(pseudo + ": " + msg);
				} else if (line.equals("QUIT")) {
					break;
				} else {
					out.println("ERROR Unknown command");
				}
			}
		} catch (IOException e) {
			System.err.println("Erreur client " + pseudo + " : " + e.getMessage());
		} finally {
			server.removeWriter(out);
			server.broadcast(pseudo + " a quitté le chat.");
			toutFermer();
		}
	}
	
	private void toutFermer() {
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