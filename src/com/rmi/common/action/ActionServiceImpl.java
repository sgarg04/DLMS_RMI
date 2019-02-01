package com.rmi.common.action;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.rmi.libraries.Concordia;
import com.rmi.libraries.McGill;
import com.rmi.libraries.Montreal;

public class ActionServiceImpl extends UnicastRemoteObject implements ActionService {

	String operation = "";
	HashMap<String, String> fetchBook = new HashMap<String, String>();

	public ActionServiceImpl() throws RemoteException {
		super();
	}

	private HashMap<String, String> getAllLibraries() {
		Iterator<Map.Entry<String, String>> cIterator = Concordia.conBooks.entrySet().iterator();
		while (cIterator.hasNext()) {
			Map.Entry<String, String> entry = cIterator.next();
			fetchBook.put(entry.getKey(), entry.getValue());
		}
		System.out.println("122" + fetchBook);
		Iterator<Map.Entry<String, String>> mIterator = Montreal.monBooks.entrySet().iterator();
		while (cIterator.hasNext()) {
			Map.Entry<String, String> entry1 = mIterator.next();
			fetchBook.put(entry1.getKey(), entry1.getValue());
		}
		System.out.println("222" + fetchBook);
		Iterator<Map.Entry<String, String>> mcIterator = McGill.mcgBooks.entrySet().iterator();
		while (cIterator.hasNext()) {
			Map.Entry<String, String> entry2 = mcIterator.next();
			fetchBook.put(entry2.getKey(), entry2.getValue());
		}
		System.out.println("333" + fetchBook);
		return fetchBook;
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
		switch (userID.substring(0, 3)) {
		case "CON":
			fetchBook = Concordia.conBooks;
			if (Concordia.conBooks.containsKey(itemID)) {
				int quantity = Integer.parseInt(Concordia.conBooks.get(itemID).split(",")[1]);
				System.out.println("Quantity is" + quantity);
				if (quantity > 0) {
					Concordia.setUserDetails(userID, itemID, numberOfDays);
					quantity--;
					System.out.println("Quantity after reduction" + quantity);
					System.out.println(fetchBook.get(itemID).split(",")[0]);
					System.out.println("before" + Concordia.conBooks.get(itemID));
					Concordia.conBooks.put(itemID, fetchBook.get(itemID).split(",")[0] + "," + quantity);
					operation = "Item issued";
					System.out.println("Concordia User borrow list" + Concordia.conUserList);
					System.out.println("after books" + Concordia.conBooks);
				} else {
					operation = "Item not available";
				}
			}
			break;
		case "MON":
			fetchBook = Montreal.monBooks;
			if (Montreal.monBooks.containsKey(itemID)) {
				itemID = itemID.toUpperCase();
				int quantity = Integer.parseInt(Montreal.monBooks.get(itemID).split(",")[1]);
				if (quantity > 0) {
					Montreal.setUserDetails(userID, itemID, numberOfDays);
					quantity -= 1;
					Montreal.monBooks.put(itemID, fetchBook.get(itemID).split(",")[0] + "," + quantity);
					operation = "Item issued";
					System.out.println("Montreal User borrow list" + Montreal.monUserList);
				} else {
					operation = "Item not available";
				}
			}
			break;
		case "MCG":
			if (McGill.mcgBooks.containsKey(itemID)) {
				int quantity = Integer.parseInt(McGill.mcgBooks.get(itemID).split(",")[1]);
				if (quantity > 0) {
					McGill.setUserDetails(userID, itemID, numberOfDays);
					quantity -= 1;
					McGill.mcgBooks.put(itemID, fetchBook.get(itemID).split(",")[0] + "," + quantity);
					operation = "Item issued";
					System.out.println("McGill user borrow list" + McGill.mcgUserList);
					System.out.println("books" + McGill.mcgBooks);
				} else {
					operation = "Item not available";
				}
			}
			break;

		}
		return operation;
	}

	@Override
	public HashMap<String, String> findItem(String userId, String itemName) {
		fetchBook = getAllLibraries();
		System.out.println(fetchBook + "all lib books");
		return fetchBook;
	}

	@Override
	public String returnItem(String userID, String itemID) {
		itemID = itemID.toUpperCase();
		switch (userID.substring(0, 3)) {
		case "CON":
			if (Concordia.conUserList.contains(userID)
					&& Concordia.conUserList.containsKey(itemID)) {
				Concordia.borrowConBook.remove(itemID);
				System.out.println(Concordia.borrowConBook+"33");
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
