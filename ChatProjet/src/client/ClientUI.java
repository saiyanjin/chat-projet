package client;

import javax.swing.*;
import java.awt.*;

public class ClientUI {
	
	private final JFrame frame = new JFrame("Chat Client");
	private final JTextArea chatArea = new JTextArea();
	private final JTextField inputField = new JTextField();
	private final JTextField usernameField = new JTextField(10);
	private final JPasswordField passwordField = new JPasswordField(10);
	private final JButton loginButton = new JButton("Login");
	private final JButton sendButton = new JButton("Envoyer");
	
	private final ChatClient client;
	
	public ClientUI(ChatClient client) {
		this.client = client;
		buildGui();
	}
	
	private void buildGui() {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600, 400);
	
		JPanel top = new JPanel();
		top.add(new JLabel("User:"));
		top.add(usernameField);
		top.add(new JLabel("Pass:"));
		top.add(passwordField);
		top.add(loginButton);
	
		chatArea.setEditable(false);
		chatArea.setLineWrap(true);
		JScrollPane scroll = new JScrollPane(chatArea);
	
		JPanel bottom = new JPanel(new BorderLayout());
		bottom.add(inputField, BorderLayout.CENTER);
		bottom.add(sendButton, BorderLayout.EAST);
	
		frame.getContentPane().add(top, BorderLayout.NORTH);
		frame.getContentPane().add(scroll, BorderLayout.CENTER);
		frame.getContentPane().add(bottom, BorderLayout.SOUTH);
	
		loginButton.addActionListener(e -> doLogin());
		sendButton.addActionListener(e -> sendMessage());
		inputField.addActionListener(e -> sendMessage());
	
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	private void doLogin() {
		String user = usernameField.getText().trim();
		String pass = new String(passwordField.getPassword()).trim();
		if (user.isEmpty() || pass.isEmpty()) {
			JOptionPane.showMessageDialog(frame, "Remplis user et pass");
			return;
		}
	
		loginButton.setEnabled(false);
		boolean ok = client.connectAndLogin(user, pass, this::appendMessage);
		if (!ok) {
			JOptionPane.showMessageDialog(frame, "Échec de connexion / authentification");
			loginButton.setEnabled(true);
		} else {
			appendMessage("SYSTEM: connecté en tant que " + user);
			usernameField.setEnabled(false);
			passwordField.setEnabled(false);
			loginButton.setEnabled(false);
		}
	}
	
	 private void sendMessage() {
	     String text = inputField.getText().trim();
	     if (text.isEmpty()) return;
	     client.sendMessage(text);
	     inputField.setText("");
	 }
	
	private void appendMessage(String msg) {
		SwingUtilities.invokeLater(() -> {
			chatArea.append(msg + "\n");
			chatArea.setCaretPosition(chatArea.getDocument().getLength());
		});
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			ChatClient client = new ChatClient("localhost", 5000);
			new ClientUI(client);
		});
	}
}