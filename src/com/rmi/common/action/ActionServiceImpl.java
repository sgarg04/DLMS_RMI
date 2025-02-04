package com.rmi.common.action;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.rmi.common.User;
import com.rmi.libraries.Concordia;
import com.rmi.libraries.McGill;
import com.rmi.libraries.Montreal;

public class ActionServiceImpl extends UnicastRemoteObject implements ActionService {

	String operation = "";
	static boolean isUserAllowed;
	static int count = 0;
	HashMap<String, String> fetchBook = new HashMap<String, String>();

	public ActionServiceImpl() throws RemoteException {
		super();
	}

	public static boolean isUserAllowedInterLibraryBorrow(String library, ArrayList<User> userList) {
		System.out.println(library);
		System.out.println();
		System.out.println(userList);
		count = 0;
		if (!userList.isEmpty()) {
			userList.forEach((user) -> {
				for (Map.Entry<String, Integer> entry : user.getBookInfo().entrySet()) {
					String key = entry.getKey();
					System.out.println(key + "\n");
					if (key.substring(0, 3).equalsIgnoreCase(library)) {
						count++;
					}
				}
				System.out.println(count + "count");
				System.out.println("value of boolean " + isUserAllowed);
			});
			
			isUserAllowed = count == 1 ? false : true;
//			System.out.println("count outside for" +count);
//			System.out.println();
//			System.out.println("isUserAllowedInterLibraryBorrrrrrow " + isUserAllowed);
		}

		else {
			isUserAllowed = true;
		}
		return isUserAllowed;
	}

	@Override
	public String addItem(String managerID, String itemID, String itemName, int quantity) throws RemoteException {
		itemID = itemID.toUpperCase();
		itemName = itemName.toUpperCase();
		switch (managerID.substring(0, 3)) {
		case "CON":
			if (Concordia.conBooks.containsKey(itemID)) {
				if (Concordia.conBooks.get(itemID).split(",")[0].equals(itemName)) {
					int oldQuantity = Integer.parseInt(Concordia.conBooks.get(itemID).split(",")[1]);
					quantity = oldQuantity + quantity;
					Concordia.conBooks.put(itemID, itemName + "," + quantity);
					System.out.println(Concordia.conBooks);
					operation = "Item " + itemID + " exists, hence increased item's quantity";

				} else {
					operation = "A book already exists with item ID" + itemID;
				}
			} else {
				Concordia.conBooks.put(itemID, itemName + "," + quantity);
				System.out.println(Concordia.conBooks);
				operation = "Item " + itemID + " added to the library";
			}
			break;
		case "MON":
			if (Montreal.monBooks.containsKey(itemID)) {
				if (Montreal.monBooks.get(itemID).split(",")[0].equals(itemName)) {
					int oldQuantity = Integer.parseInt(Montreal.monBooks.get(itemID).split(",")[1]);
					quantity = oldQuantity + quantity;
					Montreal.monBooks.put(itemID, itemName + "," + quantity);
					System.out.println(Montreal.monBooks);
					operation = "Item " + itemID + " exists, hence increased item's quantity";

				} else {
					operation = "A book already exists with item ID" + itemID;
				}
			} else {
				Montreal.monBooks.put(itemID, itemName + "," + quantity);
				System.out.println(Montreal.monBooks);
				operation = "Item " + itemID + " added to the library";
			}
			break;

		case "MCG":
			if (McGill.mcgBooks.containsKey(itemID)) {
				if (McGill.mcgBooks.get(itemID).split(",")[0].equals(itemName)) {
					int oldQuantity = Integer.parseInt(McGill.mcgBooks.get(itemID).split(",")[1]);
					quantity = oldQuantity + quantity;
					McGill.mcgBooks.put(itemID, itemName + "," + quantity);
					System.out.println(McGill.mcgBooks);
					operation = "Item " + itemID + " exists, hence increased item's quantity";

				} else {
					operation = "A book already exists with item ID" + itemID;
				}
			} else {
				McGill.mcgBooks.put(itemID, itemName + "," + quantity);
				System.out.println(McGill.mcgBooks);
				operation = "Item " + itemID + " added to the library";
			}
			break;
		}
		return operation;
	}

	@Override
	public String removeItem(String managerID, String itemID, int quantity) {
		itemID = itemID.toUpperCase();
		System.out.println(Concordia.conBooks);
		switch (managerID.substring(0, 3)) {
		case "CON":
			HashMap<String, String> conBooks = Concordia.conBooks;
			if (quantity == -1) {
				conBooks.remove(itemID);
				operation = "Item removed!";
			} else {
				String[] itemInfo = conBooks.get(itemID).split(",");
				if (Integer.parseInt(itemInfo[1]) > quantity) {
					quantity = Integer.parseInt(itemInfo[1]) - quantity;
					String keyValue = itemInfo[0] + "," + quantity;
					conBooks.put(itemID, keyValue);
					operation = "Item's quantity decreased!";
					System.out.println(Concordia.conBooks);
				} else {
					operation = "Item's quantity avaiable is less than or equal to the value asked to remove!";
				}

			}
			break;
		case "MON":
			HashMap<String, String> monBooks = Montreal.monBooks;
			if (quantity == -1) {
				monBooks.remove(itemID);
				operation = "Item removed!";
			} else {
				String[] itemInfo = monBooks.get(itemID).split(",");
				if (Integer.parseInt(itemInfo[1]) > quantity) {
					quantity = Integer.parseInt(itemInfo[1]) - quantity;
					String keyValue = itemInfo[0] + "," + quantity;
					monBooks.put(itemID, keyValue);
					operation = "Item's quantity decreased!";
				} else {
					operation = "Item's quantity avaiable is less than or equal to the value asked to remove!";
				}

			}
			break;

		case "MCG":
			HashMap<String, String> mcgBooks = McGill.mcgBooks;

			if (quantity == -1) {
				mcgBooks.remove(itemID);
				operation = "Item removed!";
			} else {
				String[] itemInfo = mcgBooks.get(itemID).split(",");
				if (Integer.parseInt(itemInfo[1]) > quantity) {
					quantity = Integer.parseInt(itemInfo[1]) - quantity;
					String keyValue = itemInfo[0] + "," + quantity;
					mcgBooks.put(itemID, keyValue);
					operation = "Item's quantity decreased!";
				} else {
					operation = "Item's quantity avaiable is less than or equal to the value asked to remove!";
				}

			}
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
			System.out.println(Concordia.conBooks);
			bookList = Concordia.conBooks;
			break;
		case "MON":
			System.out.println(Montreal.monBooks);
			bookList = Montreal.monBooks;
			break;
		case "MCG":
			System.out.println(McGill.mcgBooks);
			bookList = McGill.mcgBooks;
			break;
		}
		return bookList;
	}

	@Override
	public String borrowItem(String userID, String itemID, int numberOfDays) {

		itemID = itemID.toUpperCase();
		if (userID.substring(0, 3).equals("CON")) {
			operation = Concordia.borrowBookToUser(userID, itemID, numberOfDays);
		} else if (userID.substring(0, 3).equals("MON")) {
			operation = Montreal.borrowBookToUser(userID, itemID, numberOfDays);
		}
		if (userID.substring(0, 3).equals("MCG")) {
			operation = McGill.borrowBookToUser(userID, itemID, numberOfDays);
		}
		return operation;
	}

	@Override
	public String waitList(String userID, String itemID) throws RemoteException {
		itemID = itemID.toUpperCase();
		if (userID.substring(0, 3).equals("CON")) {
			operation = Concordia.addUserToWaitlist(userID, itemID);
		} else if (userID.substring(0, 3).equals("MON")) {
			operation = Montreal.addUserToWaitlist(userID, itemID);
		}
		if (userID.substring(0, 3).equals("MCG")) {
			operation = McGill.addUserToWaitlist(userID, itemID);
		}
		return operation;
	}

	@Override
	public HashMap<String, String> findItem(String userId, String itemName) {
		HashMap<String, String> displayBooks = new HashMap<String, String>();
		for (Map.Entry<String, String> entry : Concordia.conBooks.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			if (value.split(",")[0].trim().equalsIgnoreCase(itemName)) {
				displayBooks.put(key, value);
			}
		}
		for (Map.Entry<String, String> entry : Montreal.monBooks.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			if (value.split(",")[0].trim().equalsIgnoreCase(itemName)) {
				displayBooks.put(key, value);
			}
		}
		for (Map.Entry<String, String> entry : McGill.mcgBooks.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			if (value.split(",")[0].trim().equalsIgnoreCase(itemName)) {
				displayBooks.put(key, value);
			}
		}
		return displayBooks;
	}

	@Override
	public String returnItem(String userID, String itemID) {
		itemID = itemID.toUpperCase();
		switch (userID.substring(0, 3)) {
		case "CON":
			if (Concordia.conUserList.contains(userID) && Concordia.conUserList.contains(itemID)) {
				Concordia.conBooks.remove(itemID);
				System.out.println(Concordia.conUserList + "33");
			}
			break;
		case "MON":
			break;
		case "MCG":
			break;
		}

		return null;
	}

}
