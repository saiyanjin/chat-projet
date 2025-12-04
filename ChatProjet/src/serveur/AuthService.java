package serveur;

import java.io.*;
import java.util.*;

public class AuthService {
	private final Map<String, String> users = new HashMap<>();
	
	public AuthService(String usersFilePath) throws IOException {
		loadUsers(usersFilePath);
	}
	
	private void loadUsers(String path) throws IOException {
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			String line;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty() || line.startsWith("#")) continue;
				String[] parts = line.split(":", 2);
				if (parts.length == 2) {
					users.put(parts[0].trim(), parts[1].trim());
				}
			}
		}
	}
	
	public boolean authenticate(String username, String password) {
		return password != null && password.equals(users.get(username));
	}
}