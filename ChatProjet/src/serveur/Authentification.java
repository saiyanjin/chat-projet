package serveur;

import java.io.*;
import java.util.*;

public class Authentification {
	private final Map<String, String> utilisateurs = new HashMap<>();
	
	public Authentification(String usersFilePath) throws IOException {
		chargerUsers(usersFilePath);
	}
	
	private void chargerUsers(String path) throws IOException {
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			String line;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty() || line.startsWith("#")) continue;
				String[] parts = line.split(":", 2);
				if (parts.length == 2) {
					utilisateurs.put(parts[0].trim(), parts[1].trim());
				}
			}
		}
	}
	
	public boolean authentifier(String username, String password) {
		return password != null && password.equals(utilisateurs.get(username));
	}
}