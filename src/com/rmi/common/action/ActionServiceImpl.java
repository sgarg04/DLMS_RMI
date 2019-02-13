package com.rmi.common.action;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import com.rmi.libraries.Concordia;
import com.rmi.libraries.McGill;
import com.rmi.libraries.Montreal;

public class ActionServiceImpl extends UnicastRemoteObject implements ActionService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String operation = "";
	private int oldQuantity;

	public ActionServiceImpl() throws RemoteException {
		super();
	}

	@Override
	public String addItem(String managerID, String itemID, String itemName, int quantity) throws RemoteException {
		itemID = itemID.toUpperCase();
		operation = "";
		switch (managerID.substring(0, 3)) {
		case "CON":
			Concordia.logger.info("*****Entering Concordia Server*****");
			Concordia.logger.info("Request Type: Add an item , Request params: managerID" + managerID + ", itemID "
					+ itemID + ", itemName " + itemName + ", quantity " + quantity);
			if (Concordia.Books.containsKey(itemID)) {
				if (Concordia.Books.get(itemID).split(",")[0].equalsIgnoreCase(itemName)) {
					oldQuantity = Integer.parseInt(Concordia.Books.get(itemID).split(",")[1]);
					quantity = oldQuantity + quantity;
					Concordia.Books.put(itemID, itemName + "," + quantity);
					operation = "Item " + itemID + " exists, hence increased item's quantity by " + quantity
							+ " Successfully";
					Concordia.logger.info("Request successfully completed");
				} else {
					operation = "A book already exists with item ID: " + itemID + " with a Different Name";
					Concordia.logger.info("Request failed");
				}
			} else {
				Concordia.Books.put(itemID, itemName + "," + quantity);
				operation = "Item " + itemID + " added to the library Successfully";
				Concordia.logger.info("Request successfully completed");
			}
			System.out.println(Concordia.Books);
			Concordia.logger.info("Response returned: " + operation);
			break;

		case "MON":
			Montreal.logger.info("*****Entering Montreal Server*****");
			Montreal.logger.info("Request Type: Add an item , Request params: managerID" + managerID + ", itemID "
					+ itemID + ", itemName " + itemName + ", quantity " + quantity);
			if (Montreal.Books.containsKey(itemID)) {
				if (Montreal.Books.get(itemID).split(",")[0].equalsIgnoreCase(itemName)) {
					oldQuantity = Integer.parseInt(Montreal.Books.get(itemID).split(",")[1]);
					quantity = oldQuantity + quantity;
					Montreal.Books.put(itemID, itemName + "," + quantity);
					operation = "Item " + itemID + " exists, hence increased item's quantity";
					Montreal.logger.info("Request successfully completed");
				} else {
					operation = "A book already exists with item ID: " + itemID + " with a Different Name";
					Montreal.logger.info("Request failed");
				}
			} else {
				Montreal.Books.put(itemID, itemName + "," + quantity);
				Montreal.waitlistBook.put(itemID, new HashMap<String, Integer>());
				operation = "Item " + itemID + " added to the library";
				Montreal.logger.info("Request successfully completed");
			}
			System.out.println(Montreal.Books);
			Montreal.logger.info("Response returned: " + operation);
			break;

		case "MCG":
			McGill.logger.info("*****Entering McGill Server*****");
			McGill.logger.info("Request Type: Add an item , Request params: managerID" + managerID + ", itemID "
					+ itemID + ", itemName " + itemName + ", quantity " + quantity);
			if (McGill.Books.containsKey(itemID)) {
				if (McGill.Books.get(itemID).split(",")[0].equalsIgnoreCase(itemName)) {
					oldQuantity = Integer.parseInt(McGill.Books.get(itemID).split(",")[1]);
					quantity = oldQuantity + quantity;
					McGill.Books.put(itemID, itemName + "," + quantity);
					System.out.println(McGill.Books);
					operation = "Item " + itemID + " exists, hence increased item's quantity";
					McGill.logger.info("Request successfully completed");

				} else {
					operation = "A book already exists with item ID: " + itemID + " with a Different Name";
					McGill.logger.info("Request failed");
				}
			} else {
				McGill.Books.put(itemID, itemName + "," + quantity);
				McGill.waitlistBook.put(itemID, new HashMap<String, Integer>());
				operation = "Item " + itemID + " added to the library";
				McGill.logger.info("Request successfully completed");
			}
			System.out.println(McGill.Books);
			McGill.logger.info("Response returned: " + operation);
			break;
		}
		return operation;
	}

	@Override
	public String removeItem(String managerID, String itemID, int quantity) {
		itemID = itemID.toUpperCase();
		switch (managerID.substring(0, 3)) {
		case "CON":
			Concordia.logger.info("*****Entering Concordia Server*****");
			Concordia.logger.info("Request Type: Remove an item , Request params: managerID" + managerID + ", itemID "
					+ itemID +", quantity " + quantity);
			operation = Concordia.removeItemFromLibrary(itemID, quantity);
			Concordia.logger.info("Response returned: " + operation);
			break;

		case "MON":
			Montreal.logger.info("*****Entering Montreal Server*****");
			Montreal.logger.info("Request Type: Remove an item , Request params: managerID" + managerID + ", itemID "
					+ itemID +", quantity " + quantity);
			operation = Montreal.removeItemFromLibrary(itemID, quantity);
			Montreal.logger.info("Response returned: " + operation);
			break;

		case "MCG":
			McGill.logger.info("*****Entering McGill Server*****");
			McGill.logger.info("Request Type: Remove an item , Request params: managerID" + managerID + ", itemID "
					+ itemID +", quantity " + quantity);
			operation = McGill.removeItemFromLibrary(itemID, quantity);
			McGill.logger.info("Response returned: " + operation);
			break;
		}
		return operation;
	}

	@Override
	public HashMap<String, String> listItemAvailability(String managerID) {
		// TODO Auto-generated method stub
		HashMap<String, String> bookList = new HashMap<String, String>();

		switch (managerID.substring(0, 3)) {
		case "CON":
			Concordia.logger.info("*****Entering Concordia Server*****");
			Concordia.logger.info("Request Type: List items , Request params: managerID" + managerID);
			System.out.println(Concordia.Books);
			bookList = Concordia.Books;
			Concordia.logger.info("Response returned: " + bookList);
			break;
		case "MON":
			Montreal.logger.info("*****Entering Montreal Server*****");
			Montreal.logger.info("Request Type: List items , Request params: managerID" + managerID);
			System.out.println(Montreal.Books);
			bookList = Montreal.Books;
			Montreal.logger.info("Response returned: " + bookList);
			break;
		case "MCG":
			McGill.logger.info("*****Entering McGill Server*****");
			McGill.logger.info("Request Type: List items , Request params: managerID" + managerID);
			System.out.println(McGill.Books);
			bookList = McGill.Books;
			McGill.logger.info("Response returned: " + bookList);
			break;
		}
		return bookList;
	}

	@Override
	public String borrowItem(String userID, String itemID, int numberOfDays) {
		operation = "";
		itemID = itemID.toUpperCase();
		System.out.println(userID + "," + itemID);
		switch (userID.substring(0, 3)) {
		case "CON":
			Concordia.logger.info("*****Entering Concordia Server*****");
			Concordia.logger.info("Request Type: Add an item , Request params: userID" + userID + ", itemID "
					+ itemID + ", numberOfDays " + numberOfDays);
			operation = Concordia.borrowBookToUser(userID, itemID, numberOfDays);
			Concordia.logger.info("Response returned: " + operation);
			break;
		case "MON":
			Montreal.logger.info("*****Entering Montreal Server*****");
			Montreal.logger.info("Request Type: Add an item , Request params: userID" + userID + ", itemID "
					+ itemID + ", numberOfDays " + numberOfDays);
			operation = Montreal.borrowBookToUser(userID, itemID, numberOfDays);
			Montreal.logger.info("Response returned: " + operation);
			break;
		case "MCG":
			McGill.logger.info("*****Entering McGill Server*****");
			McGill.logger.info("Request Type: Add an item , Request params: userID" + userID + ", itemID "
					+ itemID + ", numberOfDays " + numberOfDays);
			operation = McGill.borrowBookToUser(userID, itemID, numberOfDays);
			McGill.logger.info("Response returned: " + operation);
			break;
		}
		return operation;
	}

	@Override
	public String waitList(String userID, String itemID, int numberOfdays) throws RemoteException {
		operation = "";
		itemID = itemID.toUpperCase();
		switch (userID.substring(0, 3)) {
		case "CON":
			operation = Concordia.addUserToWaitlist(userID, itemID, numberOfdays);
			break;
		case "MON":
			operation = Montreal.addUserToWaitlist(userID, itemID, numberOfdays);
			break;
		case "MCG":
			operation = McGill.addUserToWaitlist(userID, itemID, numberOfdays);
			break;
		}
		return operation;
	}

	@Override
	public String findItem(String userId, String itemName) {
		String booklist = new String();

		switch (userId.substring(0, 3)) {
		case "CON":
			booklist = Concordia.findItem(userId, itemName);
			break;
		case "MON":
			booklist = Montreal.findItem(userId, itemName);
			break;
		case "MCG":
			booklist = McGill.findItem(userId, itemName);
			break;
		}
		if (!booklist.equals("")) {
			int length = booklist.length();
			booklist.substring(0, length - 1);
			System.out.println("booklist here");
			System.out.println(booklist);
		}
		return booklist;

	}

	@Override
	public String returnItem(String userID, String itemID) {
		operation = "";
		itemID = itemID.toUpperCase();
		switch (userID.substring(0, 3)) {
		case "CON":
			operation = Concordia.returnBookFromUser(userID, itemID);

			if (operation.contains("Borrow")) {
				String uId = operation.split(",")[1];
				int numberOfDay = Integer.parseInt(operation.split(",")[2]);
				borrowItem(uId, itemID, numberOfDay);
				operation = itemID + " returned successfully to the Library by " + userID + " "
						+ " and removed from user borrowed list. Assigned to " + uId + " user, waiting in the WaitList";
			}
			break;

		case "MON":
			operation = Montreal.returnBookFromUser(userID, itemID);

			if (operation.contains("Borrow")) {
				String uId = operation.split(",")[1];
				int numberOfDay = Integer.parseInt(operation.split(",")[2]);
				borrowItem(uId, itemID, numberOfDay);
				operation = itemID + " returned successfully to the Library by " + userID + " "
						+ " and removed from user borrowed list. Assigned to " + uId + " user, waiting in the WaitList";

			}
			break;

		case "MCG":
			operation = McGill.returnBookFromUser(userID, itemID);

			if (operation.contains("Borrow")) {
				String uId = operation.split(",")[1];
				int numberOfDay = Integer.parseInt(operation.split(",")[2]);
				borrowItem(uId, itemID, numberOfDay);
				operation = itemID + " returned successfully to the Library by " + userID + " "
						+ " and removed from user borrowed list. Assigned to " + uId + " user, waiting in the WaitList";
			}
			break;
		}

		return operation;
	}

}
