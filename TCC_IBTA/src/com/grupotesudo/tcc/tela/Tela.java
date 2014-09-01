package com.grupotesudo.tcc.tela;

import java.awt.Color;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Calendar;

import javax.swing.JFrame;

public class Tela extends JFrame implements Runnable {

	public static void main(String args[]) {
		Tela tela = new Tela("127.0.0.1", 4000);
		tela.run();
	}
	
	// Screen size
	private int height = 300;
	private int width = 300; 
	
	// Variable that tells the program to keep drawing the screen
	private boolean keepDrawing = true;
	
	// Server socket, ip address and port
	private Socket socket;
	private String host;
	private int port;

	// Variables to write and read messages from server
	private PrintWriter socketWriter;
	private BufferedReader socketReceiver;

	// Last received message from the server
	private String receivedMessage;
	
	// Message that will be sent to server
	private String returnMessage = "";
	
	// Point where the circle will be drawn
	private Point point;

	public Tela(String host, int port) {
		this.host = host;
		this.port = port;
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(width, height);
		point = new Point();
		point.setLocation(height / 2, width / 2);
		this.setVisible(true);
	}
	
//	public String addTime(String message) {
//		Calendar calendar = Calendar.getInstance();
//		String timeInMillis = Long.toString(calendar.getTimeInMillis());
//		return message.concat(":" + timeInMillis + ":");
//	}
	
	public boolean establishConnection() {
		boolean connectionEstablished;
		
		try {
//			System.out.println("Waiting for connection...");
			
			socket = new Socket(host, port);
	
//			System.out.println("Connected!");
			
			socketWriter = new PrintWriter(socket.getOutputStream(), true);
			socketReceiver = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			connectionEstablished = true;
			
		} catch (IOException e) {
			connectionEstablished = false;
			e.printStackTrace();
		}
		
		return connectionEstablished;
	}

	public void getMessageFromServer() {
		
		try {
			receivedMessage = socketReceiver.readLine();
			
			returnMessage = "";
			
			//TODO: Definir se o tempo em que a mensagem chegou na tela deve ser enviado na string de resposta
			//returnMessage = Long.toString(ProtocolHandler.getReceivedTime(receivedMessage));
			
			Long time = ProtocolHandler.getReceivedTime(receivedMessage);
			returnMessage = Long.toString(time) + ";";
			
			point = ProtocolHandler.extractCoordinates(receivedMessage);
			
		} catch (IOException e) {
			
//			System.out.println("No message to read");
			e.printStackTrace();
		}
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(Color.RED);
		g.fillArc(point.getX(), point.getY(), 10, 10, 0, 360);
		g.drawString("x: " + point.getX(), 20, 60);
		g.drawString("y: " + point.getY(), 20, 80);
		g.drawString(receivedMessage, 20, 100);
	}
	
	public void sendReturnMessage() {
		
		String time = Long.toString(Calendar.getInstance().getTimeInMillis());
		
		returnMessage = returnMessage.concat(time);
		
//		System.out.println("Time received:" + ProtocolHandler.getReceivedTime(receivedMessage));
//		System.out.println("Finished drawing: " + time);
		
//		System.out.println("Return message: " + returnMessage);
	}
	
	@Override
	public void run() {
		if(establishConnection()) {
			while(true) {
//				System.out.println("Updating...");
				updateCoordinates();
				this.repaint();
				sendReturnMessage();
				
				try {
					
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} else {
//			System.out.println("Client could not connect, exiting...");
			System.exit(0);
		}
	}
	
	public void updateCoordinates() {
		getMessageFromServer();
		
		point = ProtocolHandler.extractCoordinates(receivedMessage);
	}
}
