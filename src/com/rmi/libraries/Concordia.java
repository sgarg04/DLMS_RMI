package com.rmi.libraries;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.rmi.common.action.ActionService;
import com.rmi.common.action.ActionServiceImpl;

public class Concordia {

	public static HashMap<String, String> Books = new HashMap<String, String>();
	public static HashMap<String, HashMap<String, Integer>> userlist = new HashMap<String, HashMap<String, Integer>>();

	public static HashMap<String, Integer> waitUserList = new HashMap<String, Integer>();
	public static HashMap<String, HashMap<String, Integer>> waitlistBook = new HashMap<String, HashMap<String, Integer>>();

	private static String sendRequestMessage;
	private static String sendRequestReceived;
	private static String dataReceived;
	private static String message;
	public static Logger logger;
	static FileHandler fileHandler;

	public Concordia() throws RemoteException {
		super();
	}

	public static void startConcordiaServer() throws Exception {

		Runnable task = () -> {
			receive();
		};
		Thread thread = new Thread(task);
		thread.start();
		ActionService conStub = new ActionServiceImpl();
		logger = Logger.getLogger(Concordia.class.getName());
		logger.setUseParentHandlers(false);
		logger.info("Concordia server started");
		try {
			try {
				// This block configure the logger with handler and formatter
				fileHandler = new FileHandler(
						"/Users/SGarg/Shresthi/Winter 2019/DS-COMP 6231/assignment/DLMS_DS2019/DistributedLibraryManagementSystem/Logs/Server/Concordia.log");
				logger.addHandler(fileHandler);
				SimpleFormatter formatter = new SimpleFormatter();
				fileHandler.setFormatter(formatter);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// special exception handler for registry creation
			LocateRegistry.createRegistry(4444);
			logger.info("Concordia registry created.");
			System.out.println("Concordia registry created.");
		} catch (RemoteException e) {
			// do nothing, error means registry already exists
			System.out.println("Concordia registry already exists.");
		}

		// Instantiate Concordia Server and bind this object instance to the name
		// "Concordia Server"
		Naming.rebind("rmi://localhost:4444/CON", conStub);
		logger.info("Concordia Server bound in registry");
		System.out.println("Concordia Server bound in registry");

		logger.info("** Setting Library Details **");
		System.out.println("Setting Library Details");
		setLibraryDetails();

	}

	private static void sendMessage(int serverPort) {
		DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket();
			byte[] message = sendRequestMessage.getBytes();
			InetAddress aHost = InetAddress.getByName("localhost");

			DatagramPacket request = new DatagramPacket(message, sendRequestMessage.length(), aHost, serverPort);
			aSocket.send(request);
			logger.info("Request message sent from the Concordia to server with port number " + serverPort + " is: "
					+ new String(request.getData()));
			byte[] buffer = new byte[1000];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			aSocket.receive(reply);
			dataReceived = null;
			dataReceived = new String(reply.getData()).trim();

			logger.info("Reply received from the server with port number " + serverPort + " to Concordia server is: "
					+ dataReceived);
		} catch (SocketException e) {
			logger.info("Socket: " + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			logger.info("IO: " + e.getMessage());
		} finally {
			if (aSocket != null) {
				aSocket.close();
				fileHandler.close();
			}
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
				logger.info("************************************");
				logger.info("Request received at Concordia Server");
				String[] params = sendRequestReceived.split(",");
				func = params[0].trim().toUpperCase();
				logger.info("Request received is for " + func);
				switch (func) {
				case "BORROW":
					String userID = params[1].trim();
					String itemID = params[2].trim();
					int numberOfDays = Integer.parseInt(params[3].trim());
					message = params[4].trim();
					repMessage = borrowBookToUser(userID, itemID, numberOfDays);
					break;
				case "WAIT":
					userID = params[1].trim();
					itemID = params[2].trim();
					numberOfDays = Integer.parseInt(params[3].trim());
					repMessage = addUserToWaitlist(userID, itemID, numberOfDays);
					break;
				case "RETURN":
					userID = params[1].trim();
					itemID = params[2].trim();
					message = params[3].trim();
					repMessage = returnBookFromUser(userID, itemID);
					break;
				case "FIND":
					String itemName = params[1].trim();
					repMessage = fetchonItemName(Books, itemName).toString();
					break;
				case "REMOVE":
					itemID = params[1].trim();
					removeItemFromuserlist(itemID);
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
			if (aSocket != null) {
				aSocket.close();
				fileHandler.close();
			}
		}
	}

//	public static boolean isUserInWaitlistForSameItemID(String userID) {
//		boolean isUserInWaitList = false;
//		HashMap<String, Integer> waitUserInfo;
//		int count;
//		if (!waitlistBook.isEmpty()) {
//			System.out.println("waitlistBook" + waitlistBook);
//			count = 0;
//			Iterator<Entry<String, HashMap<String, Integer>>> waitListIterator = waitlistBook.entrySet().iterator();
//			while (waitListIterator.hasNext()) {
//				count = 0;
//				Entry<String, HashMap<String, Integer>> thisEntry = waitListIterator.next();
//				waitUserInfo = thisEntry.getValue();
//				System.out.println("waitUserInfo " + waitUserInfo);
//				if (waitUserInfo.containsKey(userID)) {
//					count++;
//					System.out.println("waitUserInfo : " + waitUserInfo + "userID : " + userID);
//					System.out.println("count :" + count);
//				}
//			}
//			isUserInWaitList = count == 1 ? true : false;
//		} else {
//			isUserInWaitList = false;
//
//		}
//		System.out.println("isUserWaitListValid" + isUserInWaitList);
//		return isUserInWaitList;
//	}

	public static boolean isUserAllowedInterLibraryBorrow(String library, String userID) {

		String key;
		HashMap<String, Integer> userinfo;
		logger.info("Checking User Info for accessibilty for requested book");
		Boolean isUserAllowed = false;
		int count = 0;
		userinfo = userlist.get(userID);
		if (!userinfo.isEmpty()) {
			Iterator<Entry<String, Integer>> iterator = userinfo.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, Integer> thisEntry = iterator.next();
				key = thisEntry.getKey();
				if (key.substring(0, 3).equalsIgnoreCase(library)) {
					count++;
				}
			}
			isUserAllowed = count == 1 ? false : true;
		} else {
			isUserAllowed = true;
		}
		if (isUserAllowed)
			message = "Successfully";
		return isUserAllowed;
	}

//	private static boolean isUserAllowedInterLibraryBorrow(String library, String userID, String itemID) {
//		logger.info("Checking User Info for accessibilty for requested book");
//		boolean isUserAllowed = false;
//		
//
//		isUserAllowed = (isUserListValid && isUserWaitListValid) ? true : false;
//
//		if (isUserAllowed)
//			message = "Successfully";
//
//		System.out.println("isUserAllowed " + isUserAllowed);
//		return isUserAllowed;
//	}

	private static String setUserDetails(String userID, String itemID, int numberOfDays) {
		HashMap<String, Integer> temp = new HashMap<String, Integer>();
		if (userlist.containsKey(userID)) {
			temp = userlist.get(userID);
			if (!temp.containsKey(itemID) || temp.isEmpty() || temp == null) {
				temp.put(itemID, numberOfDays);
				userlist.put(userID, temp);
				logger.info("Book with book id " + itemID + " successfully borrowed by user " + userID
						+ ". Also, added the book to user's borrowed list");
				return "Book with book id " + itemID + " successfully borrowed by user " + userID
						+ ". Also, added the book to user's borrowed list";

			} else {
				System.err.println("Item already available in user's burrowed list");
				logger.info("Item already available in user's burrowed list");
				return "Requested book already exists in user's borrowed list. Cannot borrow the same item again";
			}

		} else {
			logger.info("User with User ID : " + userID + " does not exist\n");
			return "User with User ID : " + userID + " does not exist.";
		}

	}

	private static String updateUserBookDetails(String userID, String itemID) {
		HashMap<String, Integer> temp = new HashMap<String, Integer>();
		if (userlist.containsKey(userID)) {
			temp = userlist.get(userID);
			if (temp.containsKey(itemID)) {
				temp.remove(itemID);
				userlist.put(userID, temp);
				logger.info(" Item returned Successfully to the Library and removed from user borrowed list.");
				return "Item returned Successfully to the Library and removed from user borrowed list.";
			} else {
				logger.info(" Item with Item ID : " + itemID + " does not exist in User's borrowed List of books\n");
				return "BookNotPresent : Item with Item ID : " + itemID
						+ " does not exist in User's borrowed List of books.";
			}
		} else {
			logger.info("User with User ID : " + userID + " does not exist\n");
			return "User with User ID : " + userID + " does not exist.";
		}

	}

	private static synchronized void setLibraryDetails() {

		Books.put("CON7878", "Complier Design,4");
		Books.put("CON2565", "Discrete Mathmatical Structure,0");

		HashMap<String, Integer> temp = new HashMap<String, Integer>();
		temp.put("CON7878", 12);
		temp.put("CON2565", 23);
		temp.put("MCG4592", 18);

		userlist = new HashMap<String, HashMap<String, Integer>>();
		userlist.put("CONU1234", temp);
		HashMap<String, Integer> temp1 = new HashMap<String, Integer>();
		userlist.put("CONU4774", temp1);

		logger.info(" ** Books registered while initialization\n");
		Books.forEach((k, v) -> logger.info(("**  " + k + " " + v.split(",")[0] + " " + v.split(",")[1] + "\n")));
		logger.info(" ** User registered while initialization\n");
		userlist.forEach((k, v) -> logger.info(("**  " + k + " " + v + "\n")));

		HashMap<String, Integer> waitUserList1 = new HashMap<String, Integer>();

		waitUserList1.put("MONU7474", 17);
		waitlistBook.put("CON2565", waitUserList1);

		logger.info(" ** Books WaitList registered while initialization\n");
		waitlistBook.forEach((k, v) -> logger.info(("**  " + k + " " + v + "\n")));

	}

	public static String borrowBookToUser(String userID, String itemID, int numberOfDays) {
		String lib = itemID.substring(0, 3).toUpperCase();
		switch (lib) {
		case "CON":
			if (Books.containsKey(itemID)) {
				int quantity = Integer.parseInt(Books.get(itemID).split(",")[1]);
				if (quantity > 0) {
					logger.info("Books in Concordia Library before user request " + Books);
					if (userID.contains("CON")) {
						message = setUserDetails(userID, itemID, numberOfDays);
						logger.info(userID + "User borrowed book details after borrowing Concordia library book "
								+ userlist.get(userID));
					}
					if (message.contains("Successfully")) {
						quantity--;
						Books.put(itemID, Books.get(itemID).split(",")[0] + "," + quantity);
						logger.info("Request completed successfully");
					}

					logger.info("Books in Concordia Library after user request" + Books);
				} else {
					message = "Unavailable :  Book requested is currently not available";
					logger.info("Request failed");
				}
			} else {
				message = "InvalidBook: Book ID is Invalid. PLease Provide a Valid Item Id ";
				logger.info("Request failed");
			}
			break;

		case "MON":
			if (isUserAllowedInterLibraryBorrow(lib, userID)) {
				logger.info("User is allowed to borrow requested book");
				logger.info("***********************************************");
				if (message.contains("Successfully")) {
					sendRequestMessage = "BORROW" + "," + userID + "," + itemID + "," + numberOfDays + "," + message;
					sendMessage(2222);
					message = dataReceived;
					logger.info(message);
					if (message.contains("Successfully")) {
						message = setUserDetails(userID, itemID, numberOfDays);
						logger.info(userID + "User borrowed book details after borrowing Montreal library book "
								+ userlist.get(userID));
					}

				}
			} else {
				message = "User has already borrowed one Montreal Library book";
			}
			break;
		case "MCG":
			if (isUserAllowedInterLibraryBorrow(lib, userID)) {
				logger.info("User is allowed to borrow requested book");
				logger.info("***********************************************");

				if (message.contains("Successfully")) {
					sendRequestMessage = "BORROW" + "," + userID + "," + itemID + "," + numberOfDays + "," + message;
					sendMessage(3333);
					message = dataReceived;
					if (message.contains("Successfully")) {
						message = setUserDetails(userID, itemID, numberOfDays);
						logger.info(userID + "User borrowed book details after borrowing MCGILL library book "
								+ userlist.get(userID));
					}

				}
			} else {
				message = "User has already borrowed one MCGIll Library book";
			}
			break;
		}

		return message;
	}

	public static String addUserToWaitlist(String userID, String itemID, int numberOfdays) {
		String library = itemID.substring(0, 3).toUpperCase();
		switch (library) {
		case "CON":
			waitUserList.put(userID, numberOfdays);
			waitlistBook.put(itemID, waitUserList);
			message = "Added user " + userID + " to " + itemID + " waitlist Successfully !!";
			logger.info("Request completed successfully");
			logger.info("Wait list of Concordia Book List : ");
			waitlistBook.forEach((k, v) -> logger.info(("**  " + k + " " + v + "\n")));
			break;

		case "MON":
			sendRequestMessage = "WAIT" + "," + userID + "," + itemID + "," + numberOfdays;
			sendMessage(2222);
			message = dataReceived;
			logger.info(message);
			break;

		case "MCG":
			sendRequestMessage = "WAIT" + "," + userID + "," + itemID + "," + numberOfdays;
			sendMessage(3333);
			message = dataReceived;
			logger.info(message);
			break;
		}
		return message;

	}

	public static String returnBookFromUser(String userID, String itemID) {

		String lib = itemID.substring(0, 3).toUpperCase();
		switch (lib) {
		case "CON":
			if (Books.containsKey(itemID)) {
				logger.info("Returning Book at Concordia Library\n");
				int quantity = Integer.parseInt(Books.get(itemID).split(",")[1]);
				logger.info("Books in Concordia Library before user request " + Books);
				if (userID.contains("CON")) {
					message = updateUserBookDetails(userID, itemID);
				}
				if (message.contains("Successfully")) {
					quantity++;
					Books.put(itemID, Books.get(itemID).split(",")[0] + "," + quantity);
					if (waitlistBook.containsKey(itemID)) {
						logger.info(" Fetching available users from returned book (" + itemID + ") waitlist");
						logger.info(" Wait List of Books in Concordia Library After user request " + waitlistBook);
						HashMap<String, Integer> ulist = (HashMap<String, Integer>) waitlistBook.get(itemID);
						if (!ulist.isEmpty()) {

							String uID = ulist.keySet().toArray()[0].toString();
							int numberOfDays = ulist.get(uID);
							ulist.remove(uID);
							waitlistBook.put(itemID, ulist);
							logger.info(" User removed from the waitlist\n ");
							logger.info(" Wait List of Books in Concordia Library After user request " + waitlistBook);
							message = "Borrow," + uID + "," + numberOfDays;
						}
					}
					logger.info("Request completed successfully");
				}

				logger.info(" Concordia User borrow list" + userlist);
				logger.info(" Books in Concordia Library after user request" + Books);

			} else {
				message = "InvalidBook : Book Id is Invalid.";
				logger.info("Request failed");
			}
			break;

		case "MON":

			logger.info("***********************************************");
			message = updateUserBookDetails(userID, itemID);
			if (message.contains("Successfully")) {
				sendRequestMessage = "RETURN" + "," + userID + "," + itemID + "," + message;
				sendMessage(2222);
				message = dataReceived;
				if (dataReceived.equalsIgnoreCase("pass")) {
					message = dataReceived;
					logger.info(userlist + " after returning Montreal library book");
				}
			}
			break;

		case "MCG":
			logger.info("***********************************************");
			message = updateUserBookDetails(userID, itemID);
			if (message.contains("Successfully")) {
				sendRequestMessage = "RETURN" + "," + userID + "," + itemID + "," + message;
				sendMessage(3333);
				message = dataReceived;
				if (dataReceived.equalsIgnoreCase("pass")) {
					message = dataReceived;
					logger.info(userlist + " after returning MCGILL library book");
				}
			}
			break;
		}
		return message;
	}

	public static String findItem(String UserId, String itemName) {
		String display = "";
		display = fetchonItemName(Concordia.Books, itemName);
		logger.info("***********************************************");
		sendRequestMessage = "FIND" + "," + itemName;
		sendMessage(2222);
		display = display.concat(dataReceived);
		logger.info("***********************************************");
		sendRequestMessage = "FIND" + "," + itemName;
		sendMessage(3333);
		display = display.concat(dataReceived);

		return display;

	}

	public static String fetchonItemName(HashMap<String, String> books, String itemName) {
		String result = "";
		logger.info("Fetching " + itemName + " details from Concordia Library");
		for (Map.Entry<String, String> entry : books.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			if (value.split(",")[0].trim().equalsIgnoreCase(itemName)) {
				result = result.concat(key + "-" + value + "'");
			}
		}
		logger.info(itemName + " details available in Concordia Library:" + result);
		logger.info("Request completed successfully");
		return result;
	}

	public static String removeItemFromLibrary(String itemID, int quantity) {

		String[] itemInfo;
		String operation = "";
		if (Books.containsKey(itemID)) {
			HashMap<String, String> conBooks = Books;
			itemInfo = conBooks.get(itemID).split(",");
			int oldquantity = Integer.parseInt(itemInfo[1]);
			String itemName = itemInfo[0];
			if (oldquantity >= quantity) {
				int newQuantity = oldquantity - quantity;
				if (quantity != -1) {
					String keyValue = itemName + "," + newQuantity;
					conBooks.put(itemID, keyValue);
					operation = "Book's quantity decreased by " + quantity
							+ " Successfully  from the avaialable list! ";
					logger.info("After removal" + Books.toString());
					logger.info("Request completed successfully");
				} else if (quantity == -1) {
					Books.remove(itemID);
					removeItemFromuserlist(itemID);
					logger.info("***********************************************");
					sendRequestMessage = "REMOVE" + "," + itemID;
					sendMessage(2222);
					logger.info("***********************************************");
					sendRequestMessage = "REMOVE" + "," + itemID;
					sendMessage(3333);

					operation = "Book removed Successfully and Borrowed List of available User's if aplicable!";
				}

			}

			else if (oldquantity < quantity) {
				operation = "Invalid Quantity , Please enter a valid Quantity to be deleted";
				logger.info("Request failed");
			}
		} else {
			operation = "Invalid Book : Book is not available in Library";
			logger.info("Request failed");
		}
		return operation;
	}

	private static void removeItemFromuserlist(String itemId) {
		logger.info("Before Removal of Item Id from library, Concordia userList:" + userlist.toString());
		logger.info("Before Removal of Item Id from library, Concordia waitList:" + waitlistBook.toString());
		Iterator<Entry<String, HashMap<String, Integer>>> coIterator = userlist.entrySet().iterator();
		while (coIterator.hasNext()) {
			Entry<String, HashMap<String, Integer>> pair = coIterator.next();
			HashMap<String, Integer> bookChecklist = (HashMap<String, Integer>) pair.getValue();
			if (bookChecklist.containsKey(itemId)) {
				bookChecklist.remove(itemId);
				userlist.put(pair.getKey(), bookChecklist);
				if (waitlistBook.containsKey(itemId)) {
					waitlistBook.remove(itemId);
				}

			}
		}
		logger.info("After Removal of Item Id from library, Concordia userList:" + userlist.toString());
		logger.info("After Removal of Item Id from library, Concordia waitList:" + waitlistBook.toString());
		logger.info("Request completed successfully");
	}

}
