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
		fileHandler = new FileHandler("Logs/Client/" + operator
				+ "/" + operatorID + ".log");

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
			System.out.println("\nEnter your choice : ");
			String managerCommand = (reader.readLine());
			switch (managerCommand) {

			case "1":
				logger.info("Manager with manager id " + managerID + "opted to add a book");
				System.out.println("\nPlease provide the following details to add a book in the library:");
				isItemIdCorrect = false;
				Boolean loop = true;
				while (loop) {
					System.out.println("\nEnter the book id : ");
					itemId = reader.readLine();
					isItemIdCorrect = isItemIdCorrect(serverName, itemId);
					if (!isItemIdCorrect) {
						logger.log(Level.SEVERE, "\nThe entered book id has an invalid format\n");
						System.out.println(
								"The given book id has an invalid format. Please try again with a valid book id.\n");
						break;
					}
					System.out.println("Enter the associated book name : ");
					String itemName = (reader.readLine());
					if (itemName.isEmpty()) {
						System.out.println("Sorry! The entered book name cannot be blank \n");
						break;
					}
					if (itemName.trim().isEmpty()) {
						System.out.println("Invalid Book Name provided. Contains only contains whitespace (ie. spaces, tabs or line breaks)\n");
						break;
					}

					System.out.println("\nEnter the quantity of book(s) to be added : ");

					try {
						quantity = Integer.parseInt(reader.readLine());
					} catch (NumberFormatException ex) {
						System.out.println("Quantity should be a valid Digit.\n");
						break;
					}
					if (quantity > 0) {
						/*
						 * System.out.println("\nAdding book with book id " + itemId + " and book name "
						 * + itemName + " and quantity " + quantity);
						 */
						logger.info("***** Manager with manager ID " + managerID
								+ "initiated an add book request for book id \n" + itemId + " book name " + itemName
								+ " quantity " + quantity + " in " + serverName + " library");
						logger.info("***** Entering addItem operation ****");
						String result = serverRef.addItem(operatorID, itemId, itemName, quantity);
						logger.info("Response received from server : " + result);
						System.out.println("\n" + result);
						loop = false;
					} else {
						logger.log(Level.SEVERE, "\nInvalid quantity entered. Entered book's quantity is " + quantity);
						System.out.println("\nPlease enter a valid quantity. It cannot be less than or equal to zero.\n");
						break;
					}
				}

				break;

			case "2":
				logger.info("Manager with manager id " + managerID + "opted to delete/reduce a book");
				System.out.println("\nPlease provide the below details to perform the requested operation in library:");
				isItemIdCorrect = false;
				String output;
				int choice;
				Boolean correctchoice = true;
				while (correctchoice) {
					System.out.println("\nEnter the book id : ");
					itemId = (reader.readLine());
					isItemIdCorrect = isItemIdCorrect(serverName, itemId);
					if (!isItemIdCorrect) {
						System.out.println(
								"The given book id has an invalid format. Please try again with a valid book id.\n");
						logger.log(Level.SEVERE, "\nInvalid Item Id, Enterred Item id : " + itemId);
						break;
					}

					System.out.println("\nPlease chose the following operation for removal : "
							+ "\nType 1 to Remove the entire item from library."
							+ "\nType 2 to Decrease the quantity of the book.");
					System.out.println("\nEnter your choice : ");
					try {
						choice = Integer.parseInt(reader.readLine());
					} catch (NumberFormatException ex) {
						System.out.println("Enter a valid choice.");
						break;
					}
					if (choice == 1) {
						quantity = -1;
						logger.info("***** Manager with manager ID " + managerID
								+ "initiated an remove book request for book id " + itemId + " in " + serverName
								+ " library");
						logger.info("***** Entering removeItem operation to remove the entire book ****");
						output = serverRef.removeItem(operatorID, itemId, quantity);
						logger.info("Response received from server : " + output);
						System.out.println(output);
						correctchoice = false;
					} else if (choice == 2) {
						
							System.out.println("\nEnter the quantity by which the book's quantity needs to be reduced [Enter -1 to remove the book itself]:");
							try {
								quantity = Integer.parseInt(reader.readLine());
							} catch (NumberFormatException ex) {
								System.out.println("\nQuantity should be a valid Digit.\n");
								break;
							}
							if (quantity > 0 || quantity == -1) {
								logger.info("***** Manager with manager ID " + managerID
										+ "initiated an reduce quantity of book request for book id " + itemId
										+ " with quantity " + quantity + " in " + serverName + " library");
								logger.info("***** Entering removeItem operation to remove the entire book ****");
								output = serverRef.removeItem(operatorID, itemId, quantity);
								logger.info("Response received from server : " + output);
								if (!output.contains("Invalid")) {
									System.out.println("\n" + output+"\n");
									logger.log(Level.SEVERE,output);
									
									correctchoice = false;
								} else  {
									logger.log(Level.SEVERE, output+"\n");
									System.out.println(output);
									
									correctchoice = false;
								} 

							} else if(quantity<-1|| quantity==0){
								logger.log(Level.SEVERE, "Invalid quantity, Entered quantity : " + quantity);
								System.out.println(
										"\nPlease enter a valid quantity. It can not be less than or equals to zero \n");
								
								correctchoice = false;

							}
						
					} else
						System.out.println("\nSorry, it was an incorrect choice. Please enter a correct choice.");
				}

				break;

			case "3":
				logger.info("Manager with manager id " + managerID + "opted to list all the books in the library");
				HashMap<String, String> bookList = new HashMap<String, String>();
				logger.info("***** Entering listItemAvailability operation to list all the books in library ****");
				bookList = serverRef.listItemAvailability(operatorID);
				logger.info("Response received from server : " + bookList);
				System.out.println("\nBooks Available in Library are : \n");
				bookList.forEach(
						(k, v) -> System.out.println(("\n* " + k + " " + v.split(",")[0] + " " + v.split(",")[1])));

				break;

			default:
				logger.log(Level.SEVERE, "\nInvalid choice entered by user");
				System.out.println("\nPlease enter a valid choice.\n");

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
			System.out.println("Enter your choice : ");
			String managerCommand = (reader.readLine());
			switch (managerCommand) {
			case "1":
				logger.info("User with user id " + userID + "opted for borrow a book");
				System.out.println("\nPlease provide the following details to borrow book from library: \n");
				isItemIdCorrect = false;
				Boolean loop = true;
				int numberOfDays = 0;
				while (loop) {
					System.out.println("\nEnter item id of the book : ");
					itemId = (reader.readLine());
					isItemIdCorrect = isItemIdCorrect(itemId);
					if (!isItemIdCorrect) {
						System.out.println(
								"The given book id has an invalid format. Please try again with a valid book id.\n");
						break;
					}
					System.out.println("\nEnter the number of days you wish to borrow the book : ");
					try {
						numberOfDays = Integer.parseInt(reader.readLine());
					} catch (NumberFormatException ex) {
						System.out.println("\nQuantity should be a valid Digit.\n");System.err.flush();;
						break;
					}
					if (numberOfDays > 0) {
						logger.info("***** User with user ID " + userID + "initiated a borrow request for a book "
								+ itemId + "in " + serverName + " library");
						logger.info("***** Entering borrowItem operation ****");
						operation = serverRef.borrowItem(userID, itemId, numberOfDays);
						if (operation.contains("Unavailable")) {
							System.out.println("\nBook with item ID: " + itemId + " is unavailable!");
							logger.info("Response received from server : " + operation);
							System.out.println("\nDo you wish to enter into a waitlist ?  Yes or No : ");
							String choice = reader.readLine();
							if (choice.equalsIgnoreCase("Yes")) {
								logger.info("User opted to enter a waitlist");
								logger.info("***** User with user ID " + userID
										+ "initiated a waitlist request for a book " + itemId + "in " + serverName
										+ "library for number of days: " + numberOfDays);
								logger.info("***** Entering waitList operation ****");
								operation = serverRef.waitList(userID, itemId, numberOfDays);
								logger.info("Response received from server : " + operation);
								System.out.println("\n" + operation);
							} else {
								System.out.println("\nAlright! We did not add you in wait list.\n");
								logger.info("User did not opt to enter a waitlist");
							}
						} 
						else
						{
							logger.info("Response received from server : " + operation);
							System.out.println("\n" + operation);System.err.flush();
						}
						loop = false;
					} else {
						logger.log(Level.SEVERE, "Invalid number of days entered");
						System.out.println(
								"Please enter a valid number. You cannot borrow a book for less than or equals to Zero days\n");
						break;
					}
				}
				break;

			case "2":
				logger.info("User with user id " + userID + "opted to find a book");
				System.out.println("\nEnter item name of the book : ");
				String itemName = (reader.readLine());
				if (itemName.isEmpty()) {
					System.out.println("\nSorry! The entered book name cannot be blank \n");
					break;
				}
				if (itemName.trim().isEmpty()) {
					System.out.println("Invalid Book Name provided. Contains only contains whitespace (ie. spaces, tabs or line breaks)\n");
					break;
				}
				String bookList = "";
				logger.info("User with user id " + userID + "opted to find a book with name as " + itemName);
				logger.info("***** Entering findItem operation ****");
				bookList = serverRef.findItem(userID, itemName);
				logger.info("Response received from server : " + bookList);
				if (!bookList.equals("")) {
					System.out.println("\nBooks Available in Library with '" + itemName + "':\n");
					String[] books = bookList.split("'");
					for (String book : books) {
						System.out.println("\n* " + book.split("-")[0] + " " + book.split("-")[1]);
					}

				} else {
					logger.log(Level.SEVERE, "\nNo book available with the entered name");
					System.out.println("\nNo book available with the entered name");
					break;
				}

				break;

			case "3":
				logger.info("User with user id " + userID + "opted to return a book");
				System.out.println("\nPlease provide the following details to return back the book to library:");
				isItemIdCorrect = false;
				while (!isItemIdCorrect) {
					System.out.println("Enter item id of the book : ");
					itemId = (reader.readLine());
					isItemIdCorrect = isItemIdCorrect(itemId);
					if (!isItemIdCorrect)
						System.out.println(
								"The given book id has an invalid format. Please try again with a valid book id.\n");
				}
				logger.info("User with user id " + userID + "opted to return a book with item id " + itemId);
				logger.info("***** Entering returnItem operation ****");
				operation = serverRef.returnItem(userID, itemId);
				System.out.println(operation);
				logger.info("Response received from server : " + operation);

				break;

			default:
				logger.log(Level.SEVERE, "\nInvalid choice entered by user");
				System.out.println("\nPlease make a valid choice.");

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

		boolean stopRunning = false;

		try {
			System.setSecurityManager(new SecurityManager());

			while (!stopRunning) {
				System.out.println("\n** Welcome to Library **");
				System.out.println("\n(At any point of time type 'Quit' to exit)");
				System.out.println("\nPlease enter a valid User Id or Manager Id : ");
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
					if (!serverRef.validateUser(operatorID)) {
						System.out.println("User ID does not exist in System\n");
					} else {
						try {
							switch (operatorRole) {
							case 'M':

								loggingOperator("Manager", operatorID);
								managerOperation(operatorID);

								break;

							case 'U':

								loggingOperator("User", operatorID);
								userOperation(operatorID);

								break;
							}
						} catch (NumberFormatException e) {
							e.printStackTrace();
						}
					}
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
