package com.rmi.libraries;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.rmi.common.Manager;
import com.rmi.common.User;
import com.rmi.common.action.ActionServiceImpl;

public class McGill extends UnicastRemoteObject {

	public static HashMap<String, String> mcgBooks = new HashMap<String, String>();
	public static ArrayList<User> mcgUserList = new ArrayList<User>();
	static List<String> waitMcgUserList = new ArrayList<String>();
	static HashMap<String, List<String>> waitMcgBook = new HashMap<String, List<String>>();
	static String sendRequestMessage, sendRequestReceived, dataReceived, message;

	protected McGill() throws RemoteException {
		super();
	}

	public static void main(String[] args) throws Exception {

//		populateDetails();

	}

	public static void startMcGillServer() throws Exception {

		Runnable task = () -> {
			receive();
		};
		Thread thread = new Thread(task);
		thread.start();

		ActionServiceImpl mcgStub = new ActionServiceImpl();

		System.out.println("McGill server started");

		try {
			// special exception handler for registry creation
			LocateRegistry.createRegistry(6666);
			System.out.println("McGill registry created.");
		} catch (RemoteException e) {
			// do nothing, error means registry already exists
			System.out.println("McGill registry already exists.");
		}

		// Instantiate McGill Server and bind this object instance to the name
		// "McGill Server"
		try {
			Naming.rebind("rmi://localhost:6666/MCG", mcgStub);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("McGill Server bound in registry \n");

		System.out.println("Setting Library Details \n");
		setLibraryDetails();

	}

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
					+ " to McGill server is: " + dataReceived);
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
			aSocket = new DatagramSocket(3333);

			System.out.println("Server 3333 Started............");
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

	private static synchronized void setManagerDetails() {
		ArrayList<Manager> manager = new ArrayList<Manager>();

	}

	public static synchronized void setUserDetails(String userID, String itemID, int numberOfDays) {

		HashMap<String, Integer> temp = new HashMap<String, Integer>();
		temp.put(itemID, numberOfDays);
		User userObj = new User(userID, temp);
		mcgUserList.add(userObj);

	}

	private static synchronized void setLibraryDetails() {
		mcgBooks.put("MCG1234", "COMPUTER SCIENCE,9");
		mcgBooks.put("MCG2225", "SOCIAL SCIENCE,4");
	}

	public static String borrowBookToUser(String userID, String itemID, int numberOfDays) {
		String message = "";
		String Lib = itemID.substring(0, 3).toUpperCase();
		switch (Lib) {
		case "MCG":
			if (mcgBooks.containsKey(itemID)) {
				int quantity = Integer.parseInt(mcgBooks.get(itemID).split(",")[1]);
				System.out.println("Quantity is" + quantity);
				if (quantity > 0) {
					if (userID.substring(0, 3).equalsIgnoreCase("MCG")) {
						setUserDetails(userID, itemID, numberOfDays);
					} else {
						message = "";
					}
					quantity--;
					System.out.println("Quantity after reduction" + quantity + "\n");
					System.out.println("Books in McGill Library before user request" + mcgBooks.get(itemID) + "\n");
					mcgBooks.put(itemID, mcgBooks.get(itemID).split(",")[0] + "," + quantity);
					message = "pass";
					System.out.println("Concordia User borrow list" + mcgUserList + "\n");
					System.out.println("Books in McGill Library after user request" + mcgBooks + "\n");
				} else {
					message = "unavailable";
				}
			} else {
				message = "invalid";
			}
			break;

		case "CON":
			if (ActionServiceImpl.isUserAllowedInterLibraryBorrow("CON", mcgUserList)) {
				sendRequestMessage = "BORROW" + "," + userID + "," + itemID + "," + numberOfDays;
				sendMessage(1111);
				if (dataReceived.equalsIgnoreCase("pass")) {
					setUserDetails(userID, itemID, numberOfDays);
					message = "pass";
					System.out.println(mcgUserList + " after concordia lib operation" + "\n");
				} else if (dataReceived.equalsIgnoreCase("unavailable")) {
					message = "unavailable";
				} else if (dataReceived.equalsIgnoreCase("failed")) {
					message = "invalid";
				}
			} else {
				message = "User has already borrowed Concordia Library book";
			}
			break;
		case "MON":
			if (ActionServiceImpl.isUserAllowedInterLibraryBorrow("MON", mcgUserList)) {
				sendRequestMessage = "BORROW" + "," + userID + "," + itemID + "," + numberOfDays;
				sendMessage(2222);
				if (dataReceived.equalsIgnoreCase("pass")) {
					setUserDetails(userID, itemID, numberOfDays);
					message = "pass";
					System.out.println(mcgUserList + " after Montreal lib operation" + "\n");
				} else if (dataReceived.equalsIgnoreCase("unavailable")) {
					message = "unavailable";
				} else if (dataReceived.equalsIgnoreCase("failed")) {
					message = "invalid";
				}
			} else {
				message = "User has already borrowed Montreal Library book";
			}
			break;
		}

		return message;
	}

	public static String addUserToWaitlist(String userID, String itemID) {
		String library = itemID.substring(0, 3).toUpperCase();
		switch (library) {
		case "MCG":
			waitMcgUserList.add(userID);
			waitMcgBook.put(itemID, waitMcgUserList);
			message = "Added user to McGill wait list";
			System.out.println("Wait list of McGill : " + waitMcgBook + "\n");
			break;

		case "CON":
			sendRequestMessage = "WAIT" + "," + userID + "," + itemID;
			sendMessage(1111);
			message = dataReceived;
			System.out.println(message);
			break;
		case "MON":
			sendRequestMessage = "WAIT" + "," + userID + "," + itemID;
			sendMessage(2222);
			message = dataReceived;
			System.out.println(message);
			break;
		}
		return message;
	}

	private static synchronized void setWaitingQueueDetails() {

	}

}
