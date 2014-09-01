package com.grupotesudo.tcc.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;

public class Server implements Runnable {

	String finalMessage;
	
	private ServerSocket serverSocket;
	
	private Socket sensorSocket;
	private PrintWriter sensorPrintWriter;
	private BufferedReader sensorBufferedReader;

	private Socket frontSocket;
	private PrintWriter frontPrintWriter;
	private BufferedReader frontBufferedReader;
	
	private Socket leftSocket;
	private PrintWriter leftPrintWriter;
	private BufferedReader leftBufferedReader;
	
	private Socket rightSocket;
	private PrintWriter rightPrintWriter;
	private BufferedReader rightBufferedReader;
	
	private Socket backSocket;
	private PrintWriter backPrintWriter;
	private BufferedReader backBufferedReader;
	
	int[] coordenates;
	
	int flag = 0;
	
	public static void main(String args[]) {
		Server server = new Server(4000);
		server.run();
	}
	
	public Server(int port) {
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.out.println("Could not create server socket on port " + port);
		}
	}
	
	
	/**
	 * Método que tenta receber a mensagem do sensor
	 */
	public boolean readMessage() {
		System.out.println("Trying to read a message");

		try {
			String receivedMessage = sensorBufferedReader.readLine();
			
			finalMessage = receivedMessage;		// Adds first part of the message to be written in log
			Calendar calendar = Calendar.getInstance();
			String time = Long.toString(calendar.getTimeInMillis());
			finalMessage = finalMessage.concat(time);
			
			// Extrai as coordenadas do Sensor vetor de rotação
			coordenates = parseSensorMessage(receivedMessage);

			calendar = Calendar.getInstance();
			time = Long.toString(calendar.getTimeInMillis());
			finalMessage = finalMessage.concat(time);
			
			return true;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/*
		try {
			System.out.println("Message: " + sensorBufferedReader.readLine());
		} catch (IOException e) {
			System.out.println("Could not read a message");
			e.printStackTrace();
		}
		*/
		return false;
	}
	
	public int[] parseSensorMessage(String message) {
		int[] coordenates = new int[4];
		String[] coords = message.split(";");
		// Descobrir como calcular as coordenadas de tela e utilizar o primeiro long para definir
		// pra qual monitor vai, ficando:
		// 0 -> frente
		// 1 -> esquerda
		// 2 -> direita
		// 3 -> trás
		
		float coord = Float.parseFloat(coords[1]);
		
		
		System.out.println("coord: " + coord);
		System.out.println("coord <= 0.25f:  " + Boolean.toString(coord <= 0.25f));
		System.out.println("coord >= 0:      " + Boolean.toString(coord >= 0));
		System.out.println("coord <= -0.25f: " + Boolean.toString(coord <= -0.25f));
		
		if((coord <= 0.25f && coord >= 0) || (coord <= 0 && coord >= -0.25f)) {
			System.out.println("Sending to front");
			coordenates[0] = 150;
			coordenates[1] = -100;
			coordenates[2] = -100;
			coordenates[3] = -100;
		}
		
		if(coord <= -0.25f && coord >= -0.75f) {
			System.out.println("Sending to right");
			coordenates[0] = -100;
			coordenates[1] = 150;
			coordenates[2] = -100;
			coordenates[3] = -100;
		}
		
		if(coord <= 0.75f && coord >= 0.25f) {
			System.out.println("Sending to left");
			coordenates[0] = -100;
			coordenates[1] = -100;
			coordenates[2] = 150;
			coordenates[3] = -100;
		}
		
		if((coord >= 0.75f && coord <= 1f) || (coord >= -1f && coord <= -0.75f)) {
			System.out.println("Sending to back");
			coordenates[0] = -100;
			coordenates[1] = -100;
			coordenates[2] = -100;
			coordenates[3] = 150;
		}
		
		
		return coordenates;
	}
	
	public void sendMessage() {
		Calendar calendar = Calendar.getInstance();
		String time = Long.toString(calendar.getTimeInMillis());
		finalMessage = finalMessage.concat(time);
		
			sendMessageToFront(coordenates[0], 150);
			sendMessageToLeft(coordenates[1], 150);
			sendMessageToRight(coordenates[2], 150);
			sendMessageToBack(coordenates[3], 150);
			
			//TODO: Aqui tem que esperar a resposta do monitor para avisar o tempo que ele levou para printar na tela
			//TODO: Depois de receber a mensagem do monitor, tem que colocar na mensagem final e gravar o log no arquivo(talvez armazenar na própria string mesmo para evitar a escrita no disco)
			
	}

	public void sendMessageToFront(int x, int y) {
		Calendar calendar = Calendar.getInstance();
		String time = Long.toString(calendar.getTimeInMillis());
		System.out.println("Sending: " + time + ";" + x + ";" + y);
		frontPrintWriter.println(time + ";" + x + ";" + y);
	}
	
	public void sendMessageToLeft(int x, int y) {
		Calendar calendar = Calendar.getInstance();
		String time = Long.toString(calendar.getTimeInMillis());
		System.out.println("Sending: " + time + ";" + x + ";" + y);
		leftPrintWriter.println(time + ";" + x + ";" + y);
	}
	
	public void sendMessageToRight(int x, int y) {
		Calendar calendar = Calendar.getInstance();
		String time = Long.toString(calendar.getTimeInMillis());
		System.out.println("Sending: " + time + ";" + x + ";" + y);
		rightPrintWriter.println(time + ";" + x + ";" + y);
	}
	
	public void sendMessageToBack(int x, int y) {
		Calendar calendar = Calendar.getInstance();
		String time = Long.toString(calendar.getTimeInMillis());
		System.out.println("Sending: " + time + ";" + x + ";" + y);
		backPrintWriter.println(time + ";" + x + ";" + y);
	}
	
	@Override
	public void run() {
		try {
			System.out.println("Waiting for sensor connection");
			// Aguarda pela conexão do sensor de movimentos
			sensorSocket = serverSocket.accept();
			sensorPrintWriter = new PrintWriter(sensorSocket.getOutputStream(), true);
			sensorBufferedReader = new BufferedReader(new InputStreamReader(sensorSocket.getInputStream()));
			System.out.println("Sensor connected");
			
			
			// Aguarda pela conexão da tela da frente
			frontSocket = serverSocket.accept();
			frontPrintWriter = new PrintWriter(frontSocket.getOutputStream(), true);
			frontBufferedReader = new BufferedReader(new InputStreamReader(frontSocket.getInputStream()));
			
			// Aguarda pela conexão da tela da esquerda
			leftSocket = serverSocket.accept();
			leftPrintWriter = new PrintWriter(leftSocket.getOutputStream(), true);
			leftBufferedReader = new BufferedReader(new InputStreamReader(leftSocket.getInputStream()));
			
			// Aguarda pela conexão da tela da direita
			rightSocket = serverSocket.accept();
			rightPrintWriter = new PrintWriter(rightSocket.getOutputStream(), true);
			rightBufferedReader = new BufferedReader(new InputStreamReader(rightSocket.getInputStream()));
			
			// Aguarda pela conexão da tela de trás
			backSocket = serverSocket.accept();
			backPrintWriter = new PrintWriter(backSocket.getOutputStream(), true);
			backBufferedReader = new BufferedReader(new InputStreamReader(backSocket.getInputStream()));
			

		} catch (IOException e) {
			System.out.println("Could not accept connection.");
		}
		
		while(true) {
			if(readMessage()) {
				sendMessage();
			} else {
				
			}
			
			try {
//				System.out.println("Putting thread to sleep");
				Thread.sleep(1);
			} catch (InterruptedException e) {
//				System.out.println("Could not put thread to sleep");
				e.printStackTrace();
			}
		}
	}

}
