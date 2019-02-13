package com.rmi.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.rmi.common.action.ActionService;

public class Client {
	static String library, registryURL, operatorID, userID, managerID, serverName, itemId;
	static char operatorRole;
	static int rmiPort, quantity;
	static boolean isIDCorrect, isItemIdCorrect;
	static Logger logger = Logger.getLogger(Client.class.getName());
	static private FileHandler fileHandler;
	static private SimpleFormatter formatterTxt;
	static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	static ActionService serverRef;
	static boolean isValidManagerFlag;
	static boolean isValidUserFlag;

	private static void getregistryURI(String library)
			throws MalformedURLException, RemoteException, NotBoundException, IOException {

		if (library.equals("CON")) {
			rmiPort = 4444;
			registryURL = "rmi://localhost:" + rmiPort + "/CON";
		} else if (library.equals("MON")) {
			rmiPort = 5555;
			registryURL = "rmi://localhost:" + rmiPort + "/MON";
		} else if (library.equals("MCG")) {
			rmiPort = 6666;
			registryURL = "rmi://localhost:" + rmiPort + "/MCG";
		}
		serverRef = (ActionService) Naming.lookup(registryURL);
	}

//	Function to check whether the entered manager Id is authorized
//	@return boolean value of isValidManagerFlag
//	@params String array of manager and manager id entered

	private static boolean isValidManager(String[] managerIDs, String managerID) {
		int c = 0;
		for (String id : managerIDs) {
			c = id.equalsIgnoreCase(managerID) ? c = c + 1 : c;
		}
		isValidManagerFlag = (c == 1) ? true : false;
		return isValidManagerFlag;
	}

//	Function to check whether the entered user Id is authorised
//	@return boolean value of isValidUserFlag
//	@params String array of user and user id entered

//	private static boolean isValidUser(String[] userIDs, String userID) {
//		int c = 0;
//		for (String id : userIDs) {
//			c = id.equalsIgnoreCase(userID) ? c = c + 1 : c;
//		}
//		isValidUserFlag = (c == 1) ? true : false;
//		return isValidUserFlag;
//	}

	private static boolean isOperatorIdCorrect(String operatorID) {
		isIDCorrect = false;

		if (operatorID.length() == 8 && operatorID != null) {
			String serverName = operatorID.substring(0, 3);
			if (serverName.equalsIgnoreCase("CON") || serverName.equalsIgnoreCase("MON")
					|| serverName.equalsIgnoreCase("MCG")) {
				if (operatorID.charAt(3) == 'M' || operatorID.charAt(3) == 'U') {
					if ((operatorID.substring(4, 8)).matches("[0-9]+")) {
						isIDCorrect = true;
					}
				}
			}
		} else {

			isIDCorrect = false;
		}
		return isIDCorrect;

	}

	private static boolean isItemIdCorrect(String serverName, String itemId) {
		isItemIdCorrect = false;

		if (itemId.length() == 7 && itemId != null) {
			String libraryName = itemId.substring(0, 3);
			if (libraryName.equalsIgnoreCase(serverName)) {
				if ((itemId.substring(3, 7)).matches("[0-9]+")) {
					isItemIdCorrect = true;
				}
			}

		} else {

			isItemIdCorrect = false;
		}
		return isItemIdCorrect;

	}

	private static boolean isItemIdCorrect(String itemId) {
		isItemIdCorrect = false;
		if (itemId.length() == 7 && itemId != null) {
			String serverName = itemId.substring(0, 3);
			if (serverName.equalsIgnoreCase("CON") || serverName.equalsIgnoreCase("MON")
					|| serverName.equalsIgnoreCase("MCG")) {
				if ((itemId.substring(3, 7)).matches("[0-9]+")) {
					isItemIdCorrect = true;
				}
			}
		} else {
			isItemIdCorrect = false;
		}
		return isItemIdCorrect;
	}

	private static void loggingOperator(String operator, String operatorID) throws SecurityException, IOException {
		fileHandler = new FileHandler(
				"/Users/SGarg/Shresthi/Winter 2019/DS-COMP 6231/assignment/DLMS_DS2019/DistributedLibraryManagementSystem/Logs/Client/"
						+ operator + "/" + operatorID + ".log");

		formatterTxt = new SimpleFormatter();
		fileHandler.setFormatter(formatterTxt);
		logger.addHandler(fileHandler);
		logger.setUseParentHandlers(false);
	}

	public static void managerOperation(String managerID) throws IOException {
		System.out.println("\nHello Manager,");
		String proceedM = "yes";
		while (proceedM.equalsIgnoreCase("yes")) {
			System.out.println("\nEnter your choice : " + "\n1. Type 1 to add a book to the library."
					+ "\n2. Type 2 to remove a book from the library."
					+ "\n3. Type 3 to list all the available books in the library.");
			System.out.print("\nEnter your choice : ");
			String managerCommand = (reader.readLine());
			switch (managerCommand) {

			case "1":
				logger.info("\nManager with manager id " + managerID + "opted to add a book");
				System.out.println("\nPlease provide the following details to add a book in the library:");
				isItemIdCorrect = false;
				Boolean loop = true;
				while (loop) {
					System.out.print("\nEnter the book id : ");
					itemId = reader.readLine();
					isItemIdCorrect = isItemIdCorrect(serverName, itemId);
					if (!isItemIdCorrect) {
						logger.log(Level.SEVERE, "\nThe entered book id has an invalid format\n");
						System.out.println("\nSorry! The entered book id has an invalid format \n");
						break;
					}
					System.out.print("\nEnter the associated book name : ");
					String itemName = (reader.readLine());
					if (itemName.isEmpty()) {
						System.out.println("\nSorry! The entered book name cannot be blank \n");
						break;
					}
					System.out.print("\nEnter the quantity of book(s) to be added : ");

					try {
						quantity = Integer.parseInt(reader.readLine());
					} catch (NumberFormatException ex) {
						System.out.println("Quantity cannot be empty.");
						break;
					}
					if (quantity > 0) {
						System.out.println("\nAdding book with book id " + itemId + " and book name " + itemName
								+ " and quantity " + quantity);
						logger.info("\n***** Manager with manager ID " + managerID
								+ "initiated an add book request for book id \n" + itemId + " book name " + itemName
								+ " quantity " + quantity + " in " + serverName + " library");
						logger.info("\n***** Entering addItem operation ****");
						logger.info("\nRequest: addItem method with params managerID "+operatorID+", itemId "+ itemId+ ", itemName "+itemName+", quantity "+quantity);
						String result = serverRef.addItem(operatorID, itemId, itemName, quantity);
						logger.info("\nResponse received from server : " + result);
						System.out.println("\n" + result);
						loop = false;
					} else {
						logger.log(Level.SEVERE, "\nInvalid quantity entered. Entered book's quantity is " + quantity);
						System.out.println("\nPlease enter a valid quantity. It cannot be less than or equal to zero.");
						break;
					}
				}

				break;

			case "2":
				logger.info("\nManager with manager id " + managerID + "opted to delete/reduce a book");
				System.out.println("\nPlease provide the below details to perform the requested operation in library:");
				isItemIdCorrect = false;
				String output;
				int choice;
				Boolean correctchoice = true;
				while (correctchoice) {
					System.out.print("\nEnter the book id : ");
					itemId = (reader.readLine());
					isItemIdCorrect = isItemIdCorrect(serverName, itemId);
					if (!isItemIdCorrect) {
						System.out.println(
								"The given book id has an invalid format. Please try again with a valid book id.");
						logger.log(Level.SEVERE, "\nInvalid Item Id, Enterred Item id : " + itemId);
						break;
					}

					System.out.println("\nPlease chose the following operation for removal : "
							+ "\nType 1 to Remove the entire item from library."
							+ "\nType 2 to Decrease the quantity of the book.");
					System.out.print("\nEnter your choice : ");
					try {
						choice = Integer.parseInt(reader.readLine());
					} catch (NumberFormatException ex) {
						System.out.println("Choice cannot be empty.");
						break;
					}
					if (choice == 1) {
						quantity = -1;
						logger.info("\n***** Manager with manager ID " + managerID
								+ "initiated an remove book request for book id " + itemId + " in " + serverName
								+ " library");
						logger.info("\n***** Entering removeItem operation to remove the entire book ****");
						logger.info("\nRequest: removeItem method with params managerID "+operatorID+", itemId "+ itemId+", quantity "+quantity);
						output = serverRef.removeItem(operatorID, itemId, quantity);
						logger.info("\nResponse received from server : " + output);
						System.out.println(output);
						correctchoice = false;
					} else if (choice == 2) {
						loop = true;
						while (loop) {
							System.out.print("\nEnter the quantity by which the book's quantity be reduced :");
							try {
								quantity = Integer.parseInt(reader.readLine());
							} catch (NumberFormatException ex) {
								System.out.println("\nQuantity cannot be empty.");
								break;
							}
							if (quantity > 0) {
								logger.info("\n***** Manager with manager ID " + managerID
										+ "initiated an reduce quantity of book request for book id " + itemId
										+ " with quantity " + quantity + " in " + serverName + " library");
								logger.info("\n***** Entering removeItem operation to reduce the quantity of the book ****");
								logger.info("\nRequest: removeItem method with params managerID "+operatorID+", itemId "+ itemId+", quantity "+quantity);
								output = serverRef.removeItem(operatorID, itemId, quantity);
								logger.info("\nResponse received from server : " + output);
								if (!output.contains("INVALID")) {
									System.out.println("\n" + output);
									loop = false;
									correctchoice = false;
								} else if (output.equals("Invalid Quantity")) {
									logger.log(Level.SEVERE, "(The quantity of books available for the book id" + itemId
											+ " is lesser than the quantity provided) \nInvalid quantity, Entered quantity : "
											+ quantity);
									System.out.println("\nThe entered quantity is invalid."
											+ "(The quantity of books available for the book id" + itemId
											+ " is lesser than the quantity provided)");
								} else if (output.equals("Invalid Book")) {
									logger.log(Level.SEVERE, "Invalid book id, Entered book id : " + itemId);
									System.out.println(
											"\nThe entered book id has an invalid format. Please try again with a valid book id.");
									loop = false;
									correctchoice = false;
								}

							} else {
								logger.log(Level.SEVERE, "Invalid quantity, Entered quantity : " + quantity);
								System.err.println(
										"\nPlease enter a valid quantity. It can not be less than or equals to zero \n");

							}
						}
					} else
						System.err.println("\nSorry, it was an incorrect choice. Please enter a correct choice.");
				}

				break;

			case "3":
				logger.info("\nManager with manager id " + managerID + "opted to list all the books in the library");
				HashMap<String, String> bookList = new HashMap<String, String>();
				logger.info("\n***** Entering listItemAvailability operation to list all the books in library ****");
				logger.info("\nRequest: listItemAvailability method with params managerID "+operatorID);
				bookList = serverRef.listItemAvailability(operatorID);
				logger.info("\nResponse received from server : " + bookList);
				System.out.println("\nBooks Available in Library are : ");
				bookList.forEach(
						(k, v) -> System.out.println(("\n* " + k + " " + v.split(",")[0] + " " + v.split(",")[1])));

				break;

			default:
				logger.log(Level.SEVERE, "\nInvalid choice entered by user");
				System.err.println("\nPlease enter a valid choice.");

			}

			System.out.println("\nDo you want continue further operation - Yes/No ");
			proceedM = (reader.readLine());
			if (!proceedM.equalsIgnoreCase("yes")) {
				System.out.println("Thank You\n");
				System.out.println("Signing out User...\n");
			}

		}

	}

	public static void userOperation(String userID) throws IOException {
		System.out.println("\nHello User,");
		String proceeduser = "yes";
		String operation = "";
		String itemId = "";
		while (proceeduser.equalsIgnoreCase("yes")) {
			System.out.println("\nEnter your choice : \n" + "\n1. Type 1 to borrow a book from the library."
					+ "\n2. Type 2 to find a book in the library."
					+ "\n3. Type 3 to return a book back to the library \n");
			System.out.print("Enter your choice : ");
			String managerCommand = (reader.readLine());
			switch (managerCommand) {
			case "1":
				logger.info("\nUser with user id " + userID + "opted for borrow a book");
				System.out.println("\nPlease provide the following details to borrow book from library: \n");
				isItemIdCorrect = false;
				Boolean loop = true;
				int numberOfDays = 0;
				while (loop) {
					System.out.print("\nEnter item id of the book : ");
					itemId = (reader.readLine());
					isItemIdCorrect = isItemIdCorrect(itemId);
					if (!isItemIdCorrect) {
						System.out.println("\nInvalid Item Id: Enter Valid Item id \n");
						break;
					}
					System.out.print("\nEnter the number of days you wish to borrow the book : ");
					try {
						numberOfDays = Integer.parseInt(reader.readLine());
					} catch (NumberFormatException ex) {
						System.out.println("\nQuantity cannot be empty.");
						break;
					}
					if (numberOfDays > 0) {
						logger.info("\n***** User with user ID " + userID + "initiated a borrow request for a book "
								+ itemId + "in " + serverName + " library");
						logger.info("\n***** Entering borrowItem operation ****");
						logger.info("\nRequest: borrowItem method with params userID "+userID+", itemId "+ itemId+", numberOfDays "+numberOfDays);
						operation = serverRef.borrowItem(userID, itemId, numberOfDays);
						logger.info("\nResponse received from server : " + operation);
						if (operation.contains("Unavailable")) {
							System.out.println("\nBook with item ID: " + itemId + " is unavailable!");
							logger.info("\nBook with item ID: " + itemId + " is unavailable!");
							System.out.print("\nDo you wish to enter into a waitlist?  Yes or No : ");
							String choice = reader.readLine();
							if (choice.equalsIgnoreCase("Yes")) {
								logger.info("\nUser opted to enter a waitlist");
								logger.info("\n***** User with user ID " + userID
										+ "initiated a waitlist request for a book " + itemId + "in " + serverName
										+ "library for number of days: " + numberOfDays);
								logger.info("\n***** Entering waitList operation ****");
								logger.info("\nRequest: waitList method with params userID "+userID+", itemId "+ itemId+", numberOfDays "+numberOfDays);
								operation = serverRef.waitList(userID, itemId, numberOfDays);
								logger.info("\nResponse received from server : " + operation);
								System.out.println("\n" + operation);
							} else {
								System.out.println("\nAlright! We did not add you in wait list.\n");
								logger.info("\nUser did not opt to enter a waitlist");
							}

						} else {
							logger.info("\nResponse received from server : " + operation);
							System.out.println("\n" + operation);
						}
						loop = false;
					} else {
						logger.log(Level.SEVERE, "Invalid number of days entered");
						System.err.println(
								"Please enter a valid number. You cannot borrow a book for less than or equals to Zero \n");
					}
				}
				break;

			case "2":
				logger.info("\nUser with user id " + userID + "opted to find a book");
				System.out.print("\nEnter item name of the book : ");
				String itemName = (reader.readLine());
				String bookList = "";
				logger.info("\nUser with user id " + userID + "opted to find a book with name as " + itemName);
				logger.info("\n***** Entering findItem operation ****");
				logger.info("\nRequest: findItem method with params userID "+userID+", itemName "+ itemName);
				bookList = serverRef.findItem(userID, itemName);
				logger.info("\nResponse received from server : " + bookList);
				System.out.println(bookList);
				if (!bookList.equals("")) {
					System.out.println("\nBooks Available in Library with '" + itemName + "':\n");
					String[] books = bookList.split("'");
					for (String book : books) {
						System.out.println("\n* " + book.split("-")[0] + " " + book.split("-")[1]);
					}

				} else {
					logger.log(Level.SEVERE, "\nNo book available with the entered name");
					System.out.println("\nNo book available with the entered name");
				}

				break;

			case "3":
				logger.info("\nUser with user id " + userID + "opted to return a book");
				System.out.println("\nPlease provide the following details to return back the book to library:");
				isItemIdCorrect = false;
				while (!isItemIdCorrect) {
					System.out.println("Enter item id of the book : ");
					itemId = (reader.readLine());
					isItemIdCorrect = isItemIdCorrect(itemId);
					if (!isItemIdCorrect)
						System.err.println("The entered book id has an invalid book format.\n");
				}
				logger.info("\nUser with user id " + userID + "opted to return a book with item id " + itemId);
				logger.info("\n***** Entering returnItem operation ****");
				logger.info("\nRequest: returnItem method with params userID "+userID+", itemId "+ itemId);
				operation = serverRef.returnItem(userID, itemId);
				System.out.println(operation);
				logger.info("\nResponse received from server : " + operation);

				break;

			default:
				logger.log(Level.SEVERE, "\nInvalid choice entered by user");
				System.err.println("\nPlease make a valid choice.");

			}

			System.out.println("\nDo you want continue further operation? Yes or No ");
			proceeduser = (reader.readLine());
			if (!proceeduser.equalsIgnoreCase("Yes")) {
				System.out.println("\nThank You");
				System.out.println("\nSigning out ...");
			}

		}

	}

	public static void main(String[] args) throws Exception {
		String[] managerIDs = { "CONM1122", "CONM2233", "CONM3344", "CONM4455", "MONMS1122", "MONM2233", "MONM3344",
				"MONM4455", "MCGM1122", "MCGM2233", "MCGM3344", "MCGM4455" };
//		String[] userIDs = { "CONU1122", "CONU2233", "CONU3344", "CONU4455", "MONUS1122", "MONU2233", "MONU3344",
//				"MONU4455", "MCGU1122", "MCGU2233", "MCGU3344", "MCGU4455" };
		boolean stopRunning = false;

		try {
			System.setSecurityManager(new SecurityManager());

			while (!stopRunning) {
				System.out.println("\n** Welcome to Library **");
				System.out.println("\n(At any point of time type 'Quit' to exit)");
				System.out.print("\nPlease enter a valid User Id or Manager Id : ");
				operatorID = (reader.readLine()).toUpperCase();

				if (operatorID.equalsIgnoreCase("quit")) {
					stopRunning = operatorID.equalsIgnoreCase("quit");
					System.out.println("\nTada! Thank you for visiting us! ");

				} else if (!isOperatorIdCorrect(operatorID)) {

					System.out.println("Please enter a valid Manager or User ID \n");

				} else {
					operatorRole = operatorID.charAt(3);
					serverName = operatorID.substring(0, 3);
					getregistryURI(serverName);
					try {
						switch (operatorRole) {
						case 'M':
							if (!isValidManager(managerIDs, operatorID)) {
								System.out.println(
										"\nSorry! You are not an authorized Manager to avail library service.");
							} else {
								loggingOperator("Manager", operatorID);
								managerOperation(operatorID);
							}

							break;

						case 'U':
//							if (!isValidUser(userIDs, operatorID)) {
//								System.out.println("\nSorry! You are not an authorized User to avail library service.");
//							} else {
							loggingOperator("User", operatorID);
							userOperation(operatorID);
//							}
							break;
						}
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
