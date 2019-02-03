package com.rmi.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Pattern;

import com.rmi.common.action.ActionService;

public class Client {
	static String library, registryURL, operatorID, userID, managerID;
	static int rmiPort;

	private static boolean isOperatorIdCorrect(String operatorID) {
		boolean isIDCorrect = false;
		if (operatorID.length() == 8) {
			String serverName = operatorID.substring(0, 3);
			if (serverName.equalsIgnoreCase("CON") || serverName.equalsIgnoreCase("MON")
					|| serverName.equalsIgnoreCase("MCG")) {
				if (operatorID.charAt(3) == 'M' || operatorID.charAt(3) == 'U') {
					if ((operatorID.substring(4, 8)).matches("[0-9]+")) {
//						System.out.println(operatorID.substring(4, 8));
						isIDCorrect = true;
					}
				}
			}
		} else {
			isIDCorrect = false;
			System.out.println("Invalid ID, Try again with valid credentials! \n");
		}
		return isIDCorrect;

	}

//	Backup code written
	public static void main(String[] args) throws Exception {
		boolean stopRunning = false;
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		try {
			System.setSecurityManager(new SecurityManager());

			System.out.print("Enter your 8 digit User Id or Manager Id : ");
			String operatorID = (reader.readLine()).toUpperCase();
			char operator = operatorID.charAt(3);
			String library = operatorID.substring(0, 3).trim().toUpperCase();
			boolean isIDCorrect = isOperatorIdCorrect(operatorID);

			if (operatorID == null || operatorID.equalsIgnoreCase("quit") && isIDCorrect != true) {
				stopRunning = true;

			} else {

				while (!stopRunning) {
					try {
						System.out.println("Library is :"+ library);
						if(library.equals("CON")) {
							rmiPort = 4444;
							registryURL =
					        		 "rmi://localhost:" + rmiPort + "/CON";
						}
						else if(library.equals("MON")) {
							rmiPort = 5555;
							registryURL = 
					        		 "rmi://localhost:" + rmiPort + "/MON";
						}
						else if(library.equals("MCG")) {
							rmiPort = 6666;
							registryURL = 
					        		 "rmi://localhost:" + rmiPort + "/MCG";
						}
						ActionService serverRef = (ActionService) Naming.lookup(registryURL);
						String temp1 = operatorID;

						int stringLength = temp1.length();
						if (stringLength == 8) {
							switch (operator) {
							case 'M':
								managerID = operatorID;
								String managerWish = "No";
								while (managerWish.equalsIgnoreCase("No")) {
									System.out.println(
											"Hello Manager, \n Enter your choice : \n 1. Type 1 to add an item to Library \n 2. Type 2 to remove an item from the Library \n 3. Type 3 to list all the available items in the library \n");
									int managerCommand = Integer.parseInt(reader.readLine());
									switch (managerCommand) {
									case 1:
										System.out.println("Please provide the following details to add an item to"
												+ library + " Library");
										System.out.println("Enter item id to be added");
										String itemID = (reader.readLine());
										System.out.println("Enter item name to added");
										String itemName = (reader.readLine());
										System.out.println("Enter the quantity of item to be added");
										int quantity = Integer.parseInt(reader.readLine());
										String result = serverRef.addItem(managerID, itemID, itemName, quantity);
										System.out.println(result);

										break;

									case 2:
										System.out.println(
												"Do you wish to \n 1. Remove the entire item from library \n 2. Decrease the quantity of item \n");
										int choice = Integer.parseInt(reader.readLine());

										System.out.println("Please provide the following details to remove an item from "
												+ library + " Library");
										if (choice == 1) {
											System.out.println("Enter item id to be removed");
											itemID = (reader.readLine());
											quantity = -1;
										} else {
											System.out.println("Enter item id to be reduced");
											itemID = (reader.readLine());
											System.out.println("Enter the quantity of item to be reduced");
											quantity = Integer.parseInt(reader.readLine());
										}
										String operation = serverRef.removeItem(managerID, itemID, quantity);
										System.out.println(operation);
										break;
									case 3:
										HashMap<String, String> bookList = new HashMap<String, String>();
										bookList=serverRef.listItemAvailability(managerID);
										System.out.println("Books Available in Library are :\n");
										bookList.forEach((k, v) -> System.out.println(("** "+k + " " + v.split(",")[0]+" " + v.split(",")[1]+"\n")));
										break;
									}
									System.out.println("Do you wish to continue? Yes or No");
									managerWish = reader.readLine();
								} 
								System.out.println("Thank you!");
								break;
							case 'U':
								userID = operatorID;
								String userWish = "No";
								while (userWish.equalsIgnoreCase("No")) {
									System.out.println(
											"Hello User, \n Enter your choice : \n 1. Type 1 to borrow an item to Library \n 2. Type 2 to find an item in the Library \n 3. Type 3 to return an item to the library");
									int userCommand = Integer.parseInt(reader.readLine());
									switch (userCommand) {
									case 1:
										System.out.println(
												"Please provide the following details to borrow book from Library \n");
										System.out.println("Enter item id of the book");
										String itemID = (reader.readLine());
										System.out.println("Enter the number of days you wish to borrow the book \n");
										int numberOfDays = Integer.parseInt(reader.readLine());
										String operation = serverRef.borrowItem(userID, itemID, numberOfDays);
										if(operation.equalsIgnoreCase("unavailable")) {
											System.out.println("Book with item ID: "+itemID+" is unavailable \n");
											System.out.println("Do you wish to enter into a waitlist?  Yes or No");
											String choice = reader.readLine();
											if(choice.equalsIgnoreCase("Yes")) {
												operation = serverRef.waitList(userID, itemID);
												System.out.println(operation);
											}
											else {
												System.out.println("Not added to the queue");
											}
										
										}
										System.out.println(operation);
										break;
									case 2:
										System.out.println("Enter item name of the book");
										String itemName = (reader.readLine());
										HashMap<String, String> bookList = new HashMap<String, String>();
										bookList=serverRef.findItem(userID, itemName);
										if(!bookList.isEmpty()) {
											System.out.println("Books Available in Library with"+itemName+ ":\n");
											bookList.forEach((k, v) -> System.out.print((k +" " + v.split(",")[1]+","+" ")));
											System.out.println();
										}
										else {
											System.out.println("No book available with that name!");
										}
										
										break;
									case 3:
										System.out.println("Provide the details of the item you wish to return");

										System.out.println("Enter item id to be returned");
										itemID = (reader.readLine());
										serverRef.returnItem(userID, itemID);
										break;
									}
									System.out.println("\n Do you wish to continue? Yes or No");
									userWish = reader.readLine();
								}
								System.out.println("Thank you!");
								break;

							}
						} else {
							System.out.println("Please enter a valid 8 digit ID \n");
							temp1 = null;
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
