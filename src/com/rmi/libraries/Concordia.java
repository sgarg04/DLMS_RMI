package com.rmi.libraries;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.rmi.common.Book;
import com.rmi.common.Manager;
import com.rmi.common.User;
import com.rmi.common.action.ActionService;
import com.rmi.common.action.ActionServiceImpl;

public class Concordia {

	public static HashMap<String, String> conBooks = new HashMap<String, String>();
	public static ArrayList<User> conUserList = new ArrayList<User>();
	static List<String> waitConUserList = new ArrayList<String>();
	public static HashMap<String, List<String>> waitConBook = new HashMap<String, List<String>>();
	static String sendRequestMessage;
	static String sendRequestReceived;
	static String dataReceived;
	static String message;

	public Concordia() throws RemoteException {
		super();
	}

	public static void startConcordiaServer() throws Exception {

		Runnable task = () -> {
			receive();
		};
		Thread thread = new Thread(task);
		thread.start();
		ActionServiceImpl conStub = new ActionServiceImpl();

		System.out.println("Concordia server started");
		try {
			// special exception handler for registry creation
			LocateRegistry.createRegistry(4444);
			System.out.println("Concordia registry created.");
		} catch (RemoteException e) {
			// do nothing, error means registry already exists
			System.out.println("Concordia registry already exists.");
		}

		// Instantiate Concordia Server and bind this object instance to the name
		// "Concordia Server"
		Naming.rebind("rmi://localhost:4444/CON", conStub);
		System.out.println("Concordia Server bound in registry");

		System.out.println("Setting Library Details");
		setLibraryDetails();

	}

//	private static synchronized void setManagerDetails() {
//		ArrayList<Manager> manager = new ArrayList<Manager>();
//
//	}

	private static void sendMessage(int serverPort) {
		DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket();
			byte[] message = sendRequestMessage.getBytes();
			InetAddress aHost = InetAddress.getByName("localhost");
			System.out.println("sem length is:" + sendRequestMessage.length());
			DatagramPacket request = new DatagramPacket(message, sendRequestMessage.length(), aHost, serverPort);
			aSocket.send(request);
			System.out.println("Request message sent from the client to server with port number " + serverPort + " is: "
					+ new String(request.getData()));
			byte[] buffer = new byte[1000];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			aSocket.receive(reply);
			dataReceived = null;
			dataReceived = new String(reply.getData()).trim();
			System.out.println("Reply received from the server with port number " + serverPort
					+ " to Concordia server is: " + dataReceived);
		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("IO: " + e.getMessage());
		} finally {
			if (aSocket != null)
				aSocket.close();
		}
	}

	private static void receive() {
		DatagramSocket aSocket = null;
		try {
			String func = null;
			String repMessage = "";
			aSocket = new DatagramSocket(1111);

			System.out.println("Server 1111 Started............");
			while (true) {
				byte[] buffer = new byte[1000];
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				sendRequestReceived = new String(request.getData());
				System.out.println("Request received is" + sendRequestReceived);
				String[] params = sendRequestReceived.split(",");
				func = params[0].trim().toUpperCase();
				switch (func) {
				case "BORROW":
					String userID = params[1].trim();
					String itemID = params[2].trim();
					int numberOfDays = Integer.parseInt(params[3].trim());
					repMessage = borrowBookToUser(userID, itemID, numberOfDays);
					break;
				case "WAIT":
					userID = params[1].trim();
					itemID = params[2].trim();
					repMessage = addUserToWaitlist(userID, itemID);
					break;
				}

				buffer = repMessage.getBytes();
				DatagramPacket reply = new DatagramPacket(buffer, buffer.length, request.getAddress(),
						request.getPort());
				aSocket.send(reply);
			}
		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		} finally {
			if (aSocket != null)
				aSocket.close();
		}
	}

	public static void setUserDetails(String userID, String itemID, int numberOfDays) {

		HashMap<String, Integer> temp = new HashMap<String, Integer>();
		temp.put(itemID, numberOfDays);
		User userObj = new User(userID, temp);
		conUserList.add(userObj);

	}

	private static synchronized void setLibraryDetails() {

		conBooks.put("CON1234", "COMPUTER SCIENCE,9");
		conBooks.put("CON2225", "SOCIAL SCIENCE,4");
	}

	public static String borrowBookToUser(String userID, String itemID, int numberOfDays) {
		String message = "";
		String Lib = itemID.substring(0, 3).toUpperCase();
		switch (Lib) {
		case "CON":
			if (conBooks.containsKey(itemID)) {
				int quantity = Integer.parseInt(conBooks.get(itemID).split(",")[1]);
				System.out.println("Quantity is" + quantity);
				if (quantity > 0) {
					if (userID.substring(0, 3).equalsIgnoreCase("CON")) {
						setUserDetails(userID, itemID, numberOfDays);
					} else {
						message = "";
					}
					quantity--;
					System.out.println("Quantity after reduction \n" + quantity);
					System.out.println("Books in Concordia Library before user request " + conBooks.get(itemID));
					conBooks.put(itemID, conBooks.get(itemID).split(",")[0] + "," + quantity);
					message = "pass";
					System.out.println("Concordia User borrow list" + conUserList);
					System.out.println("Books in Concordia Library after user request" + conBooks);
				} else {
					message = "unavailable";
				}
			} else {
				message = "invalid";
			}
			break;

		case "MON":
			sendRequestMessage = "BORROW" + "," + userID + "," + itemID + "," + numberOfDays;
			sendMessage(2222);
			if (dataReceived.equalsIgnoreCase("pass")) {
				setUserDetails(userID, itemID, numberOfDays);
				message = "pass";
				System.out.println(conUserList + " after Montreal lib operation");
			} else if (dataReceived.equalsIgnoreCase("unavailable")) {
				message = "unavailable";
			} else if (dataReceived.equalsIgnoreCase("failed")) {
				message = "invalid";
			}

			break;
		case "MCG":
			sendRequestMessage = "BORROW" + "," + userID + "," + itemID + "," + numberOfDays;
			sendMessage(3333);
			if (dataReceived.equalsIgnoreCase("pass")) {
				setUserDetails(userID, itemID, numberOfDays);
				System.out.println(conUserList + " after McGill lib operation");
				message = "pass";
			} else if (dataReceived.equalsIgnoreCase("unavailable")) {
				message = "unavailable";
			} else if (dataReceived.equalsIgnoreCase("failed")) {
				message = "invalid";
			}
			break;
		}

		return message;
	}

	public static String addUserToWaitlist(String userID, String itemID) {
		String library = itemID.substring(0, 3).toUpperCase();
		switch (library) {
		case "CON":
			waitConUserList.add(userID);
			waitConBook.put(itemID, waitConUserList);
			message = "Added user to Concordia wait list";
			System.out.println("Wait list of Concordia : " + waitConBook);
			break;

		case "MON":
			sendRequestMessage = "WAIT" + "," + userID + "," + itemID;
			sendMessage(2222);
			message = dataReceived;
			System.out.println(message);
			break;
		case "MCG":
			sendRequestMessage = "WAIT" + "," + userID + "," + itemID;
			sendMessage(3333);
			message = dataReceived;
			System.out.println(message);
			break;
		}
		return message;
	}

}
