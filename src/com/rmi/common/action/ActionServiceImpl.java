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
			if (Concordia.Books.containsKey(itemID)) {
				if (Concordia.Books.get(itemID).split(",")[0].equalsIgnoreCase(itemName)) {
					oldQuantity = Integer.parseInt(Concordia.Books.get(itemID).split(",")[1]);
					quantity = oldQuantity + quantity;
					Concordia.Books.put(itemID, itemName + "," + quantity);
					operation = "Item " + itemID + " exists, hence increased item's quantity by " + quantity
							+ " Successfully";
				} else {
					operation = "A book already exists with item ID: " + itemID + " with a Different Name";
				}
			} else {
				Concordia.Books.put(itemID, itemName + "," + quantity);
				operation = "Item " + itemID + " added to the library Successfully";
			}
			System.out.println(Concordia.Books);
			break;

		case "MON":
			if (Montreal.Books.containsKey(itemID)) {
				if (Montreal.Books.get(itemID).split(",")[0].equalsIgnoreCase(itemName)) {
					oldQuantity = Integer.parseInt(Montreal.Books.get(itemID).split(",")[1]);
					quantity = oldQuantity + quantity;
					Montreal.Books.put(itemID, itemName + "," + quantity);
					operation = "Item " + itemID + " exists, hence increased item's quantity";
				} else {
					operation = "A book already exists with item ID: " + itemID + " with a Different Name";
				}
			} else {
				Montreal.Books.put(itemID, itemName + "," + quantity);
				Montreal.waitlistBook.put(itemID, new HashMap<String,Integer>());
				operation = "Item " + itemID + " added to the library";
			}
			System.out.println(Montreal.Books);
			break;

		case "MCG":
			if (McGill.Books.containsKey(itemID)) {
				if (McGill.Books.get(itemID).split(",")[0].equalsIgnoreCase(itemName)) {
					oldQuantity = Integer.parseInt(McGill.Books.get(itemID).split(",")[1]);
					quantity = oldQuantity + quantity;
					McGill.Books.put(itemID, itemName + "," + quantity);
					System.out.println(McGill.Books);
					operation = "Item " + itemID + " exists, hence increased item's quantity";

				} else {
					operation = "A book already exists with item ID: " + itemID + " with a Different Name";
				}
			} else {
				McGill.Books.put(itemID, itemName + "," + quantity);
				McGill.waitlistBook.put(itemID, new HashMap<String,Integer>());
				operation = "Item " + itemID + " added to the library";
			}
			break;
		}
		return operation;
	}

	@Override
	public String removeItem(String managerID, String itemID, int quantity) {
		itemID = itemID.toUpperCase();
		switch (managerID.substring(0, 3)) {
		case "CON":
			operation=Concordia.removeItemFromLibrary(itemID, quantity);
			break;
			
		case "MON":
			operation=Montreal.removeItemFromLibrary(itemID, quantity);
			break;

		case "MCG":
			operation=McGill.removeItemFromLibrary(itemID, quantity);
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
			System.out.println(Concordia.Books);
			bookList = Concordia.Books;
			break;
		case "MON":
			System.out.println(Montreal.Books);
			bookList = Montreal.Books;
			break;
		case "MCG":
			System.out.println(McGill.Books);
			bookList = McGill.Books;
			break;
		}
		return bookList;
	}

	@Override
	public String borrowItem(String userID, String itemID, int numberOfDays) {
		operation = "";
		itemID = itemID.toUpperCase();
		System.out.println(userID+","+itemID);
		switch (userID.substring(0, 3)) {
		case "CON":
			operation = Concordia.borrowBookToUser(userID, itemID, numberOfDays);
			break;
		case "MON":
			operation = Montreal.borrowBookToUser(userID, itemID, numberOfDays);
			break;
		case "MCG":
			operation = McGill.borrowBookToUser(userID, itemID, numberOfDays);
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
