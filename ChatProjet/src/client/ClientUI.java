package client;

import javax.swing.*;
import java.awt.*;

public class ClientUI {
	
	private final JFrame frame = new JFrame("Chat Client");
	private final JTextArea zoneText = new JTextArea();
	private final JTextField champInput = new JTextField();
	private final JTextField champPseudo = new JTextField(10);
	private final JPasswordField champMDP = new JPasswordField(10);
	private final JButton connexionBTN = new JButton("Connexion");
	private final JButton envoyerBTN = new JButton("Envoyer");
	
	private final ChatClient client;
	
	public ClientUI(ChatClient client) {
		this.client = client;
		GUI();
	}
	
	private void GUI() {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600, 400);
	
		JPanel top = new JPanel();
		top.add(new JLabel("Pseudo :"));
		top.add(champPseudo);
		top.add(new JLabel("Mot de passe :"));
		top.add(champMDP);
		top.add(connexionBTN);
	
		zoneText.setEditable(false);
		zoneText.setLineWrap(true);
		JScrollPane scroll = new JScrollPane(zoneText);
	
		JPanel bottom = new JPanel(new BorderLayout());
		bottom.add(champInput, BorderLayout.CENTER);
		bottom.add(envoyerBTN, BorderLayout.EAST);
	
		frame.getContentPane().add(top, BorderLayout.NORTH);
		frame.getContentPane().add(scroll, BorderLayout.CENTER);
		frame.getContentPane().add(bottom, BorderLayout.SOUTH);
	
		connexionBTN.addActionListener(e -> seConnecter());
		envoyerBTN.addActionListener(e -> envoyerMessage());
		champInput.addActionListener(e -> envoyerMessage());
	
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	private void seConnecter() {
		String user = champPseudo.getText().trim();
		String pass = new String(champMDP.getPassword()).trim();
		if (user.isEmpty() || pass.isEmpty()) {
			JOptionPane.showMessageDialog(frame, "Remplis pseudo et mot de passe");
			return;
		}
	
		connexionBTN.setEnabled(false);
		boolean ok = client.connexion(user, pass, this::ajouterMessage);
		if (!ok) {
			JOptionPane.showMessageDialog(frame, "Échec de connexion / authentification");
			connexionBTN.setEnabled(true);
		} else {
			ajouterMessage("Connecté en tant que " + user);
			champPseudo.setEnabled(false);
			champMDP.setEnabled(false);
			connexionBTN.setEnabled(false);
		}
	}
	
	 private void envoyerMessage() {
	     String text = champInput.getText().trim();
	     if (text.isEmpty()) return;
	     client.envoyerMessage(text);
	     champInput.setText("");
	 }
	
	private void ajouterMessage(String msg) {
		SwingUtilities.invokeLater(() -> {
			zoneText.append(msg + "\n");
			zoneText.setCaretPosition(zoneText.getDocument().getLength());
		});
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			ChatClient client = new ChatClient("localhost", 5000);
			new ClientUI(client);
		});
	}
}