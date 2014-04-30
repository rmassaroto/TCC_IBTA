package com.grupotesudo.tcc.tela;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JFrame;

public class Tela extends JFrame {

	private String host;
	private int port;
	
	private Socket socket;
	
	public static void main(String args[]) {
		Tela tela = new Tela();
	}

	public Tela() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.setSize(320, 240);
		
		this.setVisible(true);
	}
	
	public boolean establishConnection() {
		boolean connectionEstablished;
		
		try {
			socket = new Socket(host, port);
			
			connectionEstablished = true;
		} catch (IOException e) {
			connectionEstablished = false;
			e.printStackTrace();
		}
		
		return connectionEstablished;
	}
}
