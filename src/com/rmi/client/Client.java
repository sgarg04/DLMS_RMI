package com.rmi.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Pattern;

import com.rmi.common.action.ActionService;

public class Client {
	private String operatorID = null, itemName = null, instituteName = null;
	private int quantity = 0, numOfDays = 0, choice;

//	public void checkUserType(Scanner scan) {
//		boolean running = false;
//		do {
//			System.out.println("Welcome to Distributed Library Management System");
//			System.out.println("Enter your choice by typing the number corresponding to text");
//			System.out.println("1. Enter as Manager");
//			System.out.println("2. Enter as User");
//
//			choice = scan.nextInt();
//
//			switch (choice) {
//			case 1:
//				System.out.println("Hi Manager \nEnter your 8 digit Manager ID");
//				String managerID = scan.next().toUpperCase();
//				running = (isOperatorIdCorrect(managerID) && managerID.charAt(3)=='M')? true : false;	
//				System.out.println(running);
//				break;
//			case 2:
//				System.out.println("Hi User \nEnter your 8 digit User ID");
//				String userID = scan.next().toUpperCase();
//				running = (isOperatorIdCorrect(userID) && userID.charAt(3)=='M')? true : false;
//				break;
//			}
//		} while (running);
//
//	}

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

//	public static void main(String[] args) throws Exception {
//		Client operator = new Client();
//		Scanner scan = new Scanner(System.in);
//		operator.checkUserType(scan);
//
//	}

//	Backup code written
	public static void main(String[] args) throws Exception {
		boolean stopRunning = false;
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		try {
			System.setSecurityManager(new SecurityManager());

			System.out.print("Enter your 8 digit User Id or Manager Id : ");
			String operatorID = (reader.readLine()).toUpperCase();
			char operator = operatorID.charAt(3);
			String serverName = operatorID.substring(0, 3);
			boolean isIDCorrect = isOperatorIdCorrect(operatorID);

			if (operatorID == null || operatorID.equalsIgnoreCase("quit") && isIDCorrect != true) {
				stopRunning = true;

			} else {

				while (!stopRunning) {
					try {
						ActionService serverRef = (ActionService) Naming.lookup(serverName);
						String temp1 = operatorID;

						int stringLength = temp1.length();
//					System.out.println("tem1"+ temp1+ " length "+ stringLength);
						if (stringLength == 8) {
//						System.out.println("yay");

							switch (operator) {
							case 'M':
								String managerWish = "No";
								while (managerWish.equalsIgnoreCase("No")) {
									System.out.println(
											"Hello Manager, \n Enter your choice : \n 1. Type 1 to add an item to Library \n 2. Type 2 to remove an item from the Library \n 3. Type 3 to list all the available items in the library \n");
									int managerCommand = Integer.parseInt(reader.readLine());
									switch (managerCommand) {
									case 1:
										System.out.println("Please provide the following details to add an item to"
												+ serverName + " Library");
										System.out.println("Enter item id to be added");
										String itemID = (reader.readLine());
										System.out.println("Enter item name to added");
										String itemName = (reader.readLine());
										System.out.println("Enter the quantity of item to be added");
										int quantity = Integer.parseInt(reader.readLine());
										String result = serverRef.addItem(operatorID, itemID, itemName, quantity);
										System.out.println(result);

										break;

									case 2:
										System.out.println(
												"Do you wish to \n 1. Remove the entire item from library \n 2. Decrease the quantity of item \n");
										int choice = Integer.parseInt(reader.readLine());

										System.out.println("Please provide the following details to remove an item from "
												+ serverName + " Library");
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
										String operation = serverRef.removeItem(operatorID, itemID, quantity);
										System.out.println(operation);
										break;
									case 3:
										HashMap<String, String> bookList = new HashMap<String, String>();
										bookList=serverRef.listItemAvailability(operatorID);
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
										String operation = serverRef.borrowItem(operatorID, itemID, numberOfDays);
										System.out.println(operation);
										break;
									case 2:
										System.out.println("Enter item name of the book");
										String itemName = (reader.readLine());
										HashMap<String, String> bookList = new HashMap<String, String>();
										bookList=serverRef.findItem(operatorID, itemName);
										System.out.println("Books Available in Library are :\n");
										bookList.forEach((k, v) -> System.out.println(("** "+k + " " + v.split(",")[0]+" " + v.split(",")[1]+"\n")));
										break;
									case 3:
										System.out.println("Provide the details of the item you wish to return");

										System.out.println("Enter item id to be returned");
										itemID = (reader.readLine());
										serverRef.returnItem(operatorID, itemID);
										break;
									}
									System.out.println("Do you wish to continue? Yes or No");
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
