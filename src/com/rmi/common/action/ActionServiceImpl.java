package com.rmi.common.action;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.LinkedHashMap;
import java.util.Map.Entry;


import com.rmi.libraries.Concordia;
import com.rmi.libraries.McGill;
import com.rmi.libraries.Montreal;

public class ActionServiceImpl extends UnicastRemoteObject implements ActionService {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//private String operation = "";
	private int oldQuantity;

	public ActionServiceImpl() throws RemoteException {
		super();
	}

	@Override
	public String addItem(String managerID, String itemID, String itemName, int quantity) throws RemoteException {
		itemID = itemID.toUpperCase();
		String operation = "";
		
		switch (managerID.substring(0, 3)) {
		case "CON":
			Concordia.logger.info("\n*****Entering Concordia Server*****\n");
			Concordia.logger.info("Request Type: Add an item , Request params:\n managerID " + managerID + ", itemID "
					+ itemID + ", itemName " + itemName + ", quantity " + quantity);
			
			if (Concordia.Books.containsKey(itemID)) {
				if (Concordia.Books.get(itemID).split(",")[0].equalsIgnoreCase(itemName)) {
					oldQuantity = Integer.parseInt(Concordia.Books.get(itemID).split(",")[1]);
					quantity = oldQuantity + quantity;
					Concordia.Books.put(itemID, itemName + "," + quantity);
					Concordia.logger.info(itemID + " added successfully to libraray\n");
					if (Concordia.waitlistBook.containsKey(itemID)) {
						Concordia.logger.info("checking for any available in " + itemID + "waitlist.\n");
						HashMap<String, LinkedHashMap<String, Integer>> waitlist = Concordia.waitlistBook;
						LinkedHashMap<String, Integer> ulist = waitlist.get(itemID);
						ArrayList<String> userList= new ArrayList<String>();
						if (!ulist.isEmpty()) {
							for (Entry<String, Integer> items : ulist.entrySet()) {
								userList.add(items.getKey()+"-"+items.getValue());
							}
							Object[] uArray =  userList.toArray();
							for (Object items : uArray) {
								String uID = items.toString().split("-")[0];
								int numberOfDays = Integer.parseInt(items.toString().split("-")[1]);
								if (quantity != 0) {
									operation = borrowItem(uID, itemID, numberOfDays);
									Concordia.logger.info(operation);
									if (operation.contains("successfully")) {
										quantity--;
									}
								}
							}
							Concordia.logger.info(" Users removed from the waitlist\n ");

							operation = "Item " + itemID + " exists, hence increased item's quantity by " + quantity
									+ " Successfully, and assigned to users existed in Waitlist";
							Concordia.logger.info("Request successfully completed,\n");
						}
						if (ulist.isEmpty()) {
							Concordia.waitlistBook.remove(itemID);
						}
					} else {
						operation = "Item " + itemID + " exists, hence increased item's quantity by " + quantity
								+ " Successfully";
						Concordia.logger.info("Request successfully completed,\n");
					}

				} else {
					operation = "A book already exists with item ID: " + itemID + " with a Different Name";
				}
			} else {
				Concordia.Books.put(itemID, itemName + "," + quantity);
				operation = "Item " + itemID + " added to the library Successfully";
			}
			Concordia.logger.info("Response returned: " + operation + ".\n");
			break;

		case "MON":
			Montreal.logger.info("\n*****Entering Concordia Server*****\n");
			Montreal.logger.info("Request Type: Add an item , Request params:\n managerID " + managerID + ", itemID "
					+ itemID + ", itemName " + itemName + ", quantity " + quantity);
			if (Montreal.Books.containsKey(itemID)) {
				if (Montreal.Books.get(itemID).split(",")[0].equalsIgnoreCase(itemName)) {
					oldQuantity = Integer.parseInt(Montreal.Books.get(itemID).split(",")[1]);
					quantity = oldQuantity + quantity;
					Montreal.Books.put(itemID, itemName + "," + quantity);
					Montreal.logger.info(itemID + " added successfully to libraray\n");
					if (Montreal.waitlistBook.containsKey(itemID)) {
						Montreal.logger.info("checking for any available in " + itemID + "waitlist.\n");
						HashMap<String, LinkedHashMap<String, Integer>> waitlist = Montreal.waitlistBook;
						LinkedHashMap<String, Integer> ulist = waitlist.get(itemID);
						ArrayList<String> userList= new ArrayList<String>();
						if (!ulist.isEmpty()) {
							for (Entry<String, Integer> items : ulist.entrySet()) {
								userList.add(items.getKey()+"-"+items.getValue());
							}
							Object[] uArray =  userList.toArray();
							for (Object items : uArray) {
								String uID = items.toString().split("-")[0];
								int numberOfDays = Integer.parseInt(items.toString().split("-")[1]);
								if (quantity != 0) {
									operation = borrowItem(uID, itemID, numberOfDays);
									Montreal.logger.info(operation);
									if (operation.contains("successfully")) {
										quantity--;
									}
								}
							}
							Montreal.logger.info(" Users removed from the waitlist\n ");

							operation = "Item " + itemID + " exists, hence increased item's quantity by " + quantity
									+ " Successfully, and assigned to users existed in Waitlist";
							Montreal.logger.info("Request successfully completed,\n");
						}
						if (ulist.isEmpty()) {
							Montreal.waitlistBook.remove(itemID);
						}
					} else {
						operation = "Item " + itemID + " exists, hence increased item's quantity by " + quantity
								+ " Successfully";
						Montreal.logger.info("Request successfully completed,\n");
					}

				} else {
					operation = "A book already exists with item ID: " + itemID + " with a Different Name";
				}
			} else {
				Montreal.Books.put(itemID, itemName + "," + quantity);
				operation = "Item " + itemID + " added to the library Successfully";
			}
			Montreal.logger.info("Response returned: " + operation + ".\n");
			break;

		case "MCG":
			if (McGill.Books.containsKey(itemID)) {
				if (McGill.Books.get(itemID).split(",")[0].equalsIgnoreCase(itemName)) {
					oldQuantity = Integer.parseInt(McGill.Books.get(itemID).split(",")[1]);
					quantity = oldQuantity + quantity;
					McGill.Books.put(itemID, itemName + "," + quantity);

					if (McGill.waitlistBook.containsKey(itemID)) {
						McGill.logger.info("checking for any available in " + itemID + "waitlist.\n");
						HashMap<String, LinkedHashMap<String, Integer>> waitlist = McGill.waitlistBook;
						LinkedHashMap<String, Integer> ulist = waitlist.get(itemID);
						ArrayList<String> userList= new ArrayList<String>();
						if (!ulist.isEmpty()) {
							for (Entry<String, Integer> items : ulist.entrySet()) {
								userList.add(items.getKey()+"-"+items.getValue());
							}
							Object[] uArray =  userList.toArray();
							for (Object items : uArray) {
								String uID = items.toString().split("-")[0];
								int numberOfDays = Integer.parseInt(items.toString().split("-")[1]);
								if (quantity != 0) {
									operation = borrowItem(uID, itemID, numberOfDays);
									McGill.logger.info(operation);
									if (operation.contains("successfully")) {
										quantity--;
									}
								}
							}
							McGill.logger.info(" Users removed from the waitlist\n ");
							operation = "Item " + itemID + " exists, hence increased item's quantity by " + quantity
									+ " Successfully, and assigned to users existed in Waitlist";
							McGill.logger.info("Request successfully completed,\n");
						}
						if (ulist.isEmpty()) {
							McGill.waitlistBook.remove(itemID);
						}
					} else {
						operation = "Item " + itemID + " exists, hence increased item's quantity by " + quantity
								+ " Successfully";
						McGill.logger.info("Request successfully completed,\n");
					}

				} else {
					operation = "A book already exists with item ID: " + itemID + " with a Different Name";
				}
			} else {
				McGill.Books.put(itemID, itemName + "," + quantity);
				operation = "Item " + itemID + " added to the library Successfully";
			}
			System.out.println(McGill.Books);
			break;
		}
		return operation;
	}

	@Override
	public String removeItem(String managerID, String itemID, int quantity) {
		String operation = "";
		itemID = itemID.toUpperCase();
		switch (managerID.substring(0, 3)) {
		case "CON":
			Concordia.logger.info("\n*****Entering Concordia Server*****");
			Concordia.logger.info("Request Type: Remove an item , Request params: managerID " + managerID + ", itemID "
					+ itemID + ", quantity " + quantity);
			operation = Concordia.removeItemFromLibrary(itemID, quantity);
			Concordia.logger.info("Response returned: " + operation);
			break;

		case "MON":
			Montreal.logger.info("\n*****Entering Montreal Server*****");
			Montreal.logger.info("Request Type: Remove an item , Request params: managerID " + managerID + ", itemID "
					+ itemID + ", quantity " + quantity);
			operation = Montreal.removeItemFromLibrary(itemID, quantity);
			Montreal.logger.info("Response returned: " + operation);
			break;

		case "MCG":
			McGill.logger.info("\n*****Entering McGill Server*****");
			McGill.logger.info("Request Type: Remove an item , Request params: managerID " + managerID + ", itemID "
					+ itemID + ", quantity " + quantity);
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
			Concordia.logger.info("\n*****Entering Concordia Server*****");
			Concordia.logger.info("Request Type: List items , Request params: managerID " + managerID);
			System.out.println(Concordia.Books);
			bookList = Concordia.Books;
			Concordia.logger.info("Response returned: " + bookList);
			break;
		case "MON":
			Montreal.logger.info("\n*****Entering Montreal Server*****");
			Montreal.logger.info("Request Type: List items , Request params: managerID " + managerID);
			System.out.println(Montreal.Books);
			bookList = Montreal.Books;
			Montreal.logger.info("Response returned: " + bookList);
			break;
		case "MCG":
			McGill.logger.info("\n*****Entering McGill Server*****");
			McGill.logger.info("Request Type: List items , Request params: managerID " + managerID);
			System.out.println(McGill.Books);
			bookList = McGill.Books;
			McGill.logger.info("Response returned: " + bookList);
			break;
		}
		return bookList;
	}

	@Override
	public String borrowItem(String userID, String itemID, int numberOfDays) {
		String operation = "";
		itemID = itemID.toUpperCase();
		System.out.println(userID + "," + itemID);
		switch (userID.substring(0, 3)) {
		case "CON":
			Concordia.logger.info("\n*****Entering Concordia Server*****");
			Concordia.logger.info("Request Type: Borrow an item , Request params: userID " + userID + ", itemID "
					+ itemID + ", numberOfDays " + numberOfDays);
			operation = Concordia.borrowBookToUser(userID, itemID, numberOfDays);
			Concordia.logger.info("Response returned: " + operation);
			break;
		case "MON":
			Montreal.logger.info("\n*****Entering Montreal Server*****");
			Montreal.logger.info("Request Type: Borrow an item , Request params: userID " + userID + ", itemID "
					+ itemID + ", numberOfDays " + numberOfDays);
			operation = Montreal.borrowBookToUser(userID, itemID, numberOfDays);
			Montreal.logger.info("Response returned: " + operation);
			break;
		case "MCG":
			McGill.logger.info("\n*****Entering McGill Server*****");
			McGill.logger.info("Request Type: Borrow an item , Request params: userID " + userID + ", itemID " + itemID
					+ ", numberOfDays " + numberOfDays);
			operation = McGill.borrowBookToUser(userID, itemID, numberOfDays);
			McGill.logger.info("Response returned: " + operation);
			break;
		}
		return operation;
	}

	@Override
	public String waitList(String userID, String itemID, int numberOfDays) throws RemoteException {
		String operation = "";
		itemID = itemID.toUpperCase();
		switch (userID.substring(0, 3)) {
		case "CON":
			Concordia.logger.info("\n*****Entering Concordia Server*****");
			Concordia.logger.info("Request Type: Waitlist , Request params: userID " + userID + ", itemID " + itemID
					+ ", numberOfDays " + numberOfDays);
			operation = Concordia.addUserToWaitlist(userID, itemID, numberOfDays);
			Concordia.logger.info("Response returned: " + operation);
			break;
		case "MON":
			Montreal.logger.info("\n*****Entering Montreal Server*****");
			Montreal.logger.info("Request Type: Waitlist , Request params: userID " + userID + ", itemID " + itemID
					+ ", numberOfDays " + numberOfDays);
			operation = Montreal.addUserToWaitlist(userID, itemID, numberOfDays);
			Montreal.logger.info("Response returned: " + operation);
			break;
		case "MCG":
			McGill.logger.info("\n*****Entering McGill Server*****");
			McGill.logger.info("Request Type: Waitlist , Request params: userID " + userID + ", itemID " + itemID
					+ ", numberOfDays " + numberOfDays);
			operation = McGill.addUserToWaitlist(userID, itemID, numberOfDays);
			McGill.logger.info("Response returned: " + operation);
			break;
		}
		return operation;
	}

	@Override
	public String findItem(String userID, String itemName) {
		String booklist = new String();

		switch (userID.substring(0, 3)) {
		case "CON":
			Concordia.logger.info("\n*****Entering Concordia Server*****");
			Concordia.logger
					.info("Request Type: Find an item , Request params: userID " + userID + ", itemName " + itemName);
			booklist = Concordia.findItem(userID, itemName);
			Concordia.logger.info("Response returned: " + booklist);
			break;
		case "MON":
			Montreal.logger.info("\n*****Entering Montreal Server*****");
			Montreal.logger
					.info("Request Type: Find an item , Request params: userID " + userID + ", itemName " + itemName);
			booklist = Montreal.findItem(userID, itemName);
			Montreal.logger.info("Response returned: " + booklist);
			break;
		case "MCG":
			McGill.logger.info("\n*****Entering McGill Server*****");
			McGill.logger
					.info("Request Type: Find an item , Request params: userID " + userID + ", itemName " + itemName);
			booklist = McGill.findItem(userID, itemName);
			McGill.logger.info("Response returned: " + booklist);
			break;
		}
		if (!booklist.equals("")) {
			int length = booklist.length();
			booklist.substring(0, length - 1);
		}
		return booklist;

	}

	@Override
	public String returnItem(String userID, String itemID) {
		String operation = "";
		String uID = null;

		itemID = itemID.toUpperCase();
		switch (userID.substring(0, 3)) {
		case "CON":

			Concordia.logger.info("\n*****Entering Concordia Server*****");
			Concordia.logger
					.info("Request Type: Return an item , Request params: userID " + userID + ", itemID " + itemID);
			operation = Concordia.returnBookFromUser(userID, itemID);
			Concordia.logger.info(operation);
			if (operation.contains("Borrow")) {
				int length = operation.length();

				String[] userArray = operation.substring(6, length - 1).split(",");

				for (String info : userArray) {
					uID = info.split("-")[0];
					int numberOfDay = Integer.parseInt(info.split("-")[1]);
					if (borrowItem(uID, itemID, numberOfDay).contains("Successfully")) {
						operation = itemID + " returned successfully to the Library by " + userID + " "
								+ " and removed from user borrowed list. Assigned to " + uID + " user, waiting in the WaitList";
						break;
					}

				}
						
			}
			if (operation.contains("Borrow"))
			{
				operation = itemID + " returned successfully to the Library by " + userID + " "
						+ " and removed from user borrowed list";
			}

			break;

		case "MON":

			Montreal.logger.info("\n*****Entering Montreal Server*****");
			Montreal.logger
					.info("Request Type: Return an item , Request params: userID " + userID + ", itemID " + itemID);
			operation = Montreal.returnBookFromUser(userID, itemID);
			

			if (operation.contains("Borrow")) {
				int length = operation.length();

				String[] uesrArray = operation.substring(6, length - 1).split(",");

				for (String info : uesrArray) {
					uID = info.split("-")[0];
					int numberOfDay = Integer.parseInt(info.split("-")[1]);
					if (borrowItem(uID, itemID, numberOfDay).contains("Successfully")) {
						operation = itemID + " returned successfully to the Library by " + userID + " "
								+ " and removed from user borrowed list. Assigned to " + uID + " user, waiting in the WaitList";
						break;
					}

				}

				
			}
			if (operation.contains("Borrow"))
			{
				operation = itemID + " returned successfully to the Library by " + userID + " "
						+ " and removed from user borrowed list";
			}

			break;

		case "MCG":
			McGill.logger.info("\n*****Entering McGill Server*****");
			McGill.logger
					.info("Request Type: Return an item , Request params: userID " + userID + ", itemID " + itemID);
			operation = McGill.returnBookFromUser(userID, itemID);

			if (operation.contains("Borrow")) {
				int length = operation.length();

				String[] uesrArray = operation.substring(6, length - 1).split(",");

				for (String info : uesrArray) {
					uID = info.split("-")[0];
					int numberOfDay = Integer.parseInt(info.split("-")[1]);
					if (borrowItem(uID, itemID, numberOfDay).contains("Successfully"))
					{
						operation = itemID + " returned successfully to the Library by " + userID + " "
								+ " and removed from user borrowed list. Assigned to " + uID + " user, waiting in the WaitList";
						break;
					}

				}

				
			}
			if (operation.contains("Borrow"))
			{
				operation = itemID + " returned successfully to the Library by " + userID + " "
						+ " and removed from user borrowed list";
			}

			break;
		}

		return operation;
	}

	@Override
	public boolean validateUser(String userID) throws RemoteException {
		// TODO Auto-generated method stub
		boolean flag = false;
		switch (userID.substring(0, 3)) {
		case "CON":
			if (userID.charAt(3) == 'U') {
				if (Concordia.userlist.containsKey(userID))
					flag = true;
			} else {
				if (Concordia.managerUserList.contains(userID)) {
					flag = true;
				}
			}
			break;
		case "MON":
			if (userID.charAt(3) == 'U') {
				if (Montreal.userlist.containsKey(userID))
					flag = true;
			} else {
				if (Montreal.managerUserList.contains(userID)) {
					flag = true;
				}
			}
			break;

		case "MCG":
			if (userID.charAt(3) == 'U') {
				if (McGill.userlist.containsKey(userID))
					flag = true;
			} else {
				if (McGill.managerUserList.contains(userID)) {
					flag = true;
				}
			}
			break;
		}
		return flag;
	}

}
