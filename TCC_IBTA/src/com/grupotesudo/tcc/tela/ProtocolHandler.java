package com.grupotesudo.tcc.tela;

public class ProtocolHandler {
	
//	Sevidor -> Tela
//		Tempo (long)
//		Coordenada
//			X (int)
//			Y (int)
//	Tela -> Servidor
//		Tempo (long)
//		Tempo (long)


	/**
	 * Extracts and returns the time received from the server message
	 * @param message received from the server
	 * @return the extracted time from the message
	 */
	public static long getReceivedTime(String message) {
		String contents[] = message.split(";");
		
		return Long.parseLong(contents[0]);
	}
	
	/**
	 * Extracts and returns the coordinates received from the server message
	 * @param message received from the server
	 * @return the extracted coordinates in a Point object 
	 */
	public static Point extractCoordinates(String message) {
		String contents[] = message.split(";");
		Point point = new Point();
		point.setX(Integer.parseInt(contents[1]));
		point.setY(Integer.parseInt(contents[2]));
		
		return point;
	}
	
	/**
	 * Prepares the message to send to server
	 * @param receivedTime time sent by the server to the Tela
	 * @param sentTime time when Tela finished its work
	 * @return the prepared message to be sent to the server
	 */
	public static String prepareMessageToSend(long receivedTime, long sentTime) {
		String message = Long.toString(receivedTime)
				+ ";" + Long.toString(sentTime) + ";";
		
		return message;
	}
}
