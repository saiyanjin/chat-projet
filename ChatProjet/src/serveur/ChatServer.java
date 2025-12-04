package serveur;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class ChatServer {
	private final int port;
	private final Authentification authentification;
	private final Set<PrintWriter> writers = ConcurrentHashMap.newKeySet();
	
	public ChatServer(int port, Authentification authService) {
		this.port = port;
		this.authentification = authService;
	}
	
	public void start() throws IOException {
		ServerSocket serverSocket = new ServerSocket(port);
		System.out.println("ChatServer démarré sur le port " + port);
		try {
			while (true) {
				Socket clientSocket = serverSocket.accept();
				System.out.println("Connexion entrante : " + clientSocket.getRemoteSocketAddress());
				new Thread(new GestionClient(clientSocket, this, authentification)).start();
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
		Authentification auth = new Authentification(usersFile);
		ChatServer server = new ChatServer(port, auth);
		server.start();
	}
}