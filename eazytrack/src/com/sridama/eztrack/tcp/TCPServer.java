package com.sridama.eztrack.tcp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

class TCPServer
{
	public static void main(final String argv[]) throws Exception
	{
		String clientSentence;
		String capitalizedSentence = "EC025012544";
		final ServerSocket welcomeSocket = new ServerSocket(6600);
		final Socket connectionSocket = welcomeSocket.accept();

		while (true)
		{
			Thread.sleep(2000);

			final BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			final DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
			clientSentence = inFromClient.readLine();
			System.out.println("Received: " + clientSentence);
			capitalizedSentence = clientSentence.toUpperCase() + '\n';
			outToClient.writeBytes(capitalizedSentence);
			System.out.println("Done");
		}
	}
}