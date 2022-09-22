package com.revature.chat.server;

import java.net.*;
import java.util.*;
import java.io.*;

public class SimpleChatServer {
	ArrayList<PrintWriter> clientOutputStreams;

	public static void main(String[] args) {
		new SimpleChatServer().go();
	}

	public class ClientHandler implements Runnable {
		BufferedReader reader;
		Socket socket;

		public ClientHandler(Socket clientSocket) {
			try {
				socket = clientSocket;
				InputStreamReader isReader = new InputStreamReader(socket.getInputStream());
				reader = new BufferedReader(isReader);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		@Override
		public void run() {
			String message;
			try {

				while ((message = reader.readLine()) != null) {
					System.out.println("read " + message);
					tellEveryone(message);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void tellEveryone(String message) {
			Iterator<PrintWriter> it = clientOutputStreams.iterator();
			while (it.hasNext()) {
				try {
					PrintWriter writer = (PrintWriter) it.next();
					writer.println(message);
					writer.flush();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}

	}

	public void go() {
		clientOutputStreams = new ArrayList<PrintWriter>();

		
			try (ServerSocket serverSock = new ServerSocket(5000)) {
				while (true) {
					Socket clientSocket = serverSock.accept();
					PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
					clientOutputStreams.add(writer);

					Thread t = new Thread(new ClientHandler(clientSocket));
					t.start();
					System.out.println("got a connection");
				}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
}
