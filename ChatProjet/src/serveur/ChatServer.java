package serveur;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class ChatServer {
	private final int port;
	private final AuthService authService;
	private final Set<PrintWriter> writers = ConcurrentHashMap.newKeySet();
	
	public ChatServer(int port, AuthService authService) {
		this.port = port;
		this.authService = authService;
	}
	
	public void start() throws IOException {
		ServerSocket serverSocket = new ServerSocket(port);
		System.out.println("ChatServer démarré sur le port " + port);
		try {
			while (true) {
				Socket clientSocket = serverSocket.accept();
				System.out.println("Connexion entrante : " + clientSocket.getRemoteSocketAddress());
				new Thread(new ClientHandler(clientSocket, this, authService)).start();
			}
		} finally {
			serverSocket.close();
		}
	}
	
	public void addWriter(PrintWriter out) {
		writers.add(out);
	}
	
	public void removeWriter(PrintWriter out) {
		writers.remove(out);
	}
	
	public void broadcast(String message) {
		System.out.println("Broadcast: " + message);
		for (PrintWriter out : writers) {
			out.println(message);
			out.flush();
		}
	}
	
	public static void main(String[] args) throws Exception {
		int port = 5000;
		String usersFile = "users.txt";
		AuthService auth = new AuthService(usersFile);
		ChatServer server = new ChatServer(port, auth);
		server.start();
	}
}