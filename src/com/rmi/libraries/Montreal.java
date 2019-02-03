package com.rmi.libraries;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.rmi.common.Manager;
import com.rmi.common.User;
import com.rmi.common.action.ActionServiceImpl;

public class Montreal {

	public static HashMap<String, String> monBooks = new HashMap<String, String>();
	public static ArrayList<User> monUserList = new ArrayList<User>();
	static List<String> waitMonUserList = new ArrayList<String>();
	static HashMap<String, List<String>> waitMonBook = new HashMap<String, List<String>>();
	static String sendRequestMessage, sendRequestReceived, dataReceived, message;

	protected Montreal() throws RemoteException {
		super();
	}

	public static void startMontrealServer() throws Exception {

		Runnable task = () -> {
			receive();
		};
		Thread thread = new Thread(task);
		thread.start();

		ActionServiceImpl monStub = new ActionServiceImpl();
		System.out.println("Montreal server started" + "\n");

		try {
			// special exception handler for registry creation
			LocateRegistry.createRegistry(5555);
			System.out.println("Montreal registry created." + "\n");
		} catch (RemoteException e) {
			// do nothing, error means registry already exists
			System.out.println("Montreal registry already exists." + "\n");
		}

		// Instantiate Montreal Server and bind this object instance to the name
		// "Montreal Server"
		Naming.rebind("rmi://localhost:5555/MON", monStub);
		System.out.println("Montreal Server bound in registry \n");

		System.out.println("Setting Library Details \n");
		setLibraryDetails();

	}

	private static synchronized void setManagerDetails() {
		ArrayList<Manager> manager = new ArrayList<Manager>();

	}

	public static synchronized void setUserDetails(String userID, String itemID, int numberOfDays) {

		HashMap<String, Integer> temp = new HashMap<String, Integer>();
		temp.put(itemID, numberOfDays);
		User userObj = new User(userID, temp);
		monUserList.add(userObj);

	}

	private static synchronized void setLibraryDetails() {
		monBooks.put("MON1234", "COMPUTER SCIENCE,9");
		monBooks.put("MON2225", "SOCIAL SCIENCE,2");
	}

	public static String borrowBookToUser(String userID, String itemID, int numberOfDays) {
		String message = "";
		String Lib = itemID.substring(0, 3).toUpperCase();
		switch (Lib) {
		case "MON":
			if (monBooks.containsKey(itemID)) {
				int quantity = Integer.parseInt(monBooks.get(itemID).split(",")[1]);
				System.out.println("Quantity is" + quantity + "\n");
				if (quantity > 0) {
					if (userID.substring(0, 3).equalsIgnoreCase("MON")) {
						setUserDetails(userID, itemID, numberOfDays);
					} else {
						message = "";
					}
					quantity--;
					System.out.println("Quantity after reduction" + quantity + "\n");
					System.out.println("Books in Montreal Library before user request" + monBooks.get(itemID) + "\n");
					monBooks.put(itemID, monBooks.get(itemID).split(",")[0] + "," + quantity);
					message = "pass";
					System.out.println("Montreal User borrow list" + monUserList + "\n");
					System.out.println("Books in Montreal Library after user request" + monBooks + "\n");
				} else {
					message = "unavailable";
				}
			} else {
				message = "invalid";
			}
			break;

		case "CON":
			if (ActionServiceImpl.isUserAllowedInterLibraryBorrow("CON", monUserList)) {
				sendRequestMessage = "BORROW" + "," + userID + "," + itemID + "," + numberOfDays;
				sendMessage(1111);
				if (dataReceived.equalsIgnoreCase("pass")) {
					setUserDetails(userID, itemID, numberOfDays);
					message = "Issued the book";
					System.out.println(monUserList + " after Concordia lib operation" + "\n");
				} else if (dataReceived.equalsIgnoreCase("unavailable")) {
					message = "Item not available";
				} else if (dataReceived.equalsIgnoreCase("failed")) {
					message = "Invalid Item ID";
				}
			} else {
				message = "User has already borrowed Concordia Library book";
			}
			break;
		case "MCG":
			if (ActionServiceImpl.isUserAllowedInterLibraryBorrow("MCG", monUserList)) {
				sendRequestMessage = "BORROW" + "," + userID + "," + itemID + "," + numberOfDays;
				sendMessage(3333);
				if (dataReceived.equalsIgnoreCase("pass")) {
					setUserDetails(userID, itemID, numberOfDays);
					message = "Issued the book";
					System.out.println(monUserList + " after McGill lib operation" + "\n");
				} else if (dataReceived.equalsIgnoreCase("unavailable")) {
					message = "Item not available";
				} else if (dataReceived.equalsIgnoreCase("failed")) {
					message = "Invalid Item ID";
				}
			}
			else {
				message = "User has already borrowed McGill Library book";
			}
			break;
		}

		return message;
	}

	private static void sendMessage(int serverPort) {
		DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket();
			byte[] message = sendRequestMessage.getBytes();
			InetAddress aHost = InetAddress.getByName("localhost");
			System.out.println("sem length is:" + sendRequestMessage.length() + "\n");
			DatagramPacket request = new DatagramPacket(message, sendRequestMessage.length(), aHost, serverPort);
			aSocket.send(request);
			System.out.println("Request message sent from the client to server with port number " + serverPort + " is: "
					+ new String(request.getData()) + "\n");
			byte[] buffer = new byte[1000];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			aSocket.receive(reply);
			dataReceived = null;
			dataReceived = new String(reply.getData()).trim();
			System.out.println("Reply received from the server with port number " + serverPort
					+ " to Montreal server is: " + dataReceived + "\n");
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
			aSocket = new DatagramSocket(2222);

			System.out.println("Server 2222 Started............");
			while (true) {
				byte[] buffer = new byte[1000];
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				sendRequestReceived = new String(request.getData());
				System.out.println("Request received is" + sendRequestReceived + "\n");
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

	private static synchronized void setWaitingQueueDetails() {

	}

	public static String addUserToWaitlist(String userID, String itemID) {
		String library = itemID.substring(0, 3).toUpperCase();
		switch (library) {
		case "MON":
			waitMonUserList.add(userID);
			waitMonBook.put(itemID, waitMonUserList);
			message = "Added user to Montreal wait list";
			System.out.println("Wait list of Montreal : " + waitMonBook + "\n");
			break;

		case "CON":
			sendRequestMessage = "WAIT" + "," + userID + "," + itemID;
			sendMessage(1111);
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
