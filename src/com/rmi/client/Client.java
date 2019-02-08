package com.rmi.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.util.HashMap;


import com.rmi.common.action.ActionService;

public class Client {
	static String library, registryURL, operatorID, userID, managerID, serverName, itemId;
	static char operatorRole;
	static int rmiPort, quantity;
	static boolean isIDCorrect, isItemIdCorrect;

	private static void getregistryURI(String library)
	{
		
		
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
	
	private static boolean isItemIdCorrect(String serverName,String itemId) {
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
	
	

	public static void main(String[] args) throws Exception {
		boolean stopRunning = false;
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		try {
			System.setSecurityManager(new SecurityManager());

			while (!stopRunning) 
			{
				
				System.out.print("Enter your 8 digit User Id or Manager Id : ");
				String operatorID = (reader.readLine()).toUpperCase();
				
				if (operatorID.equalsIgnoreCase("quit"))
				{
					stopRunning = operatorID.equalsIgnoreCase("quit");
					
				}
				else if (!isOperatorIdCorrect(operatorID)) {
					
					System.out.println("Please enter a valid Manager or User ID \n");
					
				}
				else 
				{
					operatorRole = operatorID.charAt(3);
				    serverName = operatorID.substring(0, 3);
				    try 
					  {
						getregistryURI(serverName);
						ActionService serverRef = (ActionService) Naming.lookup(registryURL);
					
						switch (operatorRole) 
						{
						  case 'M': System.out.println("Hello Manager,");
								    String proceedM = "yes";
							        while (proceedM.equalsIgnoreCase("yes")) 
							        {
								      System.out.println(
										"\n Enter your choice : \n "
										+ "1. Type 1 to add an item to Library \n "
										+ "2. Type 2 to remove an item from the Library \n "
										+ "3. Type 3 to list all the available items in the library");
								
								      String managerCommand = (reader.readLine());
								      switch (managerCommand) 
								      {
								      
								   	    case "1": 	System.out.println("Please provide the following details to add an item to Library");
													isItemIdCorrect=false;
													while(!isItemIdCorrect)
													{
									   	    			System.out.println("Enter Item id to be added : ");
														itemId = (reader.readLine());
														isItemIdCorrect=isItemIdCorrect(serverName,itemId);
														if(!isItemIdCorrect) System.err.println("Invalid Item Id: Enter Valid Item id \n\n ");
													}	
													System.out.println("Enter Item name to added : ");
													String itemName = (reader.readLine());
													Boolean loop = true;
													while (loop) 
													{
														System.out.println("Enter the quantity of item to be added :");
														int quantity = Integer.parseInt(reader.readLine());
														if (quantity > 0) {	
														String result = serverRef.addItem(operatorID, itemId, itemName, quantity);
														System.out.println(result);
														loop = false;
													}	
													else {
														System.err.println(
																"Please Enter a valid Quantity. Quantity can not be less than or equal to Zero \n");
	
													}
													}
													
													break;
								   	    
								   	    case "2":   System.out.println( "Please provide the below requested details to remove an item from Library \n");
								   	    			isItemIdCorrect=false;
													while(!isItemIdCorrect)
													{
									   	    			System.out.println("Enter Book id to be Deleted : ");
														itemId = (reader.readLine());
														isItemIdCorrect=isItemIdCorrect(serverName,itemId);
														if(!isItemIdCorrect) System.err.println("Invalid Item Id: Enter Valid Item id \n\n ");
													}
													String output;
													Boolean correctchoice = true;
													while (correctchoice) {
														System.out.println("Do you wish to \n "
																+ "1. Remove the entire item from library \n "
																+ "2. Decrease the quantity of item");
														
														int choice = Integer.parseInt(reader.readLine());
														if (choice == 1) {
															quantity = -1;
															output = serverRef.removeItem(operatorID, itemId, quantity);
															System.out.println(output);
															correctchoice = false;
														} 
														else if (choice == 2) {
															loop = true;
															while (loop) 
															{
																System.out.println("Enter the quantity of item to be Deleted :");
																quantity = Integer.parseInt(reader.readLine());
																if (quantity > 0) {								
																	output =serverRef.removeItem(operatorID, itemId, quantity);
																	if (!output.contains("INVALID")) {
																		System.out.println(output+"\n");
																		loop = false;
																		correctchoice = false;
																	}
																	else if(output.equals("Invalid Quantity"))
																	{
																		System.err.println(
																				"Invalid Quantity . Please Enter a Valid Quantity "
																				+ "(Less Quantity of Books are available than Quantity provided)\n");
																	}
																	else if(output.equals("Invalid Book"))
																	{
																		System.err.println(
																				" Invalid Book ID. Please enter valid BOOK ID.\n");
																		loop = false;
																		correctchoice = false;
																	}
																		
																}	
																else {
																	System.err.println(
																			"Please Enter a valid Quantity. Quantity can not be less than or equal to Zero \n");
				
																}
															}
														}
														else System.err.println("Incorrect Choice . Please make a correct choice ");
													}
													
													break;
													
									    case "3":   HashMap<String, String> bookList = new HashMap<String, String>();
													bookList = serverRef.listItemAvailability(operatorID);
													System.out.println("Books Available in Library are :\n");
													bookList.forEach((k, v) -> System.out.println(
															("** " + k + " " + v.split(",")[0] + " " + v.split(",")[1] + "\n")));
													
													
													break;
													
										default :   System.err.println("Please Make a Valid Choice");
										
									  }
								      
								      System.out.println("Do you want continue further operation - Yes/No ");
								      proceedM = (reader.readLine());
								      if (!proceedM.equalsIgnoreCase("yes"))
								      {System.out.println("Thank You\n");
								       System.out.println("Signing out User...\n");}
								      
									}
					
									break;
									
									
						  case 'U': String userID=operatorID;
									System.out.println("Hello User,");
									String proceeduser = "yes";
									String operation="";
									String itemId="";
									while (proceeduser.equalsIgnoreCase("yes")) 
									{
									  System.out.println("\n Enter your choice : \n"
									  		+ "1. Type 1 to borrow a book from a Library \n"
									  		+ "2. Type 2 to find an item in the in the library \n"
									  		+ "3. Type 3 to return a book to the Library \n  ");
									
									  String managerCommand = (reader.readLine());
									  switch (managerCommand) 
									  {
									  	case "1":	System.out.println("Please provide the following details to borrow book from Library \n");
												  	isItemIdCorrect=false;
													while(!isItemIdCorrect)
													{
										  				System.out.println("Enter item id of the book : ");
														itemId = (reader.readLine());
														isItemIdCorrect=isItemIdCorrect(itemId);
														if(!isItemIdCorrect) System.err.println("Invalid Item Id: Enter Valid Item id \n\n ");
													}
													
													Boolean loop = true;
													while (loop) 
													{
														System.out.println("Enter the number of days you wish to borrow the book :");
														int numberOfDays = Integer.parseInt(reader.readLine());
														if (numberOfDays > 0) {	
															operation = serverRef.borrowItem(userID, itemId, numberOfDays);
															if(operation.contains("Unavailable")) 
															{
																System.out.println("Book with item ID: "+itemId+" is unavailable \n");
																System.out.println("Do you wish to enter into a waitlist?  Yes or No : ");
																String choice = reader.readLine();
																if(choice.equalsIgnoreCase("Yes")) {
																	operation = serverRef.waitList(userID, itemId , numberOfDays);
																	System.out.println(operation+"\n");
																}
																else {
																	System.out.println("User Not Opted to be added to the waiting queue\n");
																}	
															
															}	
															loop = false;
														}
														else {
															System.err.println(
																	"Please Enter a valid Quantity. Quantity can not be less than or equal to Zero \n");
			
														}
													}
													System.out.println(operation);
													
													break;
									
									  	case "2":   System.out.println("Enter item name of the book");
													String itemName = (reader.readLine());
													String bookList = "";
													bookList=serverRef.findItem(userID, itemName);
													System.out.println(bookList);
													if(!bookList.equals("")) 
													{
														System.out.println("Books Available in Library with '"+itemName+ "':\n");
														String[] books= bookList.split("'");
														for (String book : books) {
															System.out.println("**"+book.split("-")[0]+" "+book.split("-")[1]+"\n");
														}
													
													}
													else {
														System.out.println("No book available with the Provided name\n");
													}
													
													break;
													
									  	case "3":	System.out.println("Please provide the following details to return book from Library \n");
												  	isItemIdCorrect=false;
													while(!isItemIdCorrect)
													{
										  				System.out.println("Enter item id of the book : ");
														itemId = (reader.readLine());
														isItemIdCorrect=isItemIdCorrect(itemId);
														if(!isItemIdCorrect) System.err.println("Invalid Item Id: Enter Valid Item id \n\n ");
													}
													operation = serverRef.returnItem(userID, itemId);
													System.out.println(operation);
													
													break;
													
									  	default : System.err.println("PLease Make a Valid Choice");
									  	
									  }
									  
									  System.out.println("Do you want continue further operation - Yes/No ");
									  proceeduser = (reader.readLine());
									  if (!proceeduser.equalsIgnoreCase("yes"))
									  {System.out.println("Thank You\n");
								       System.out.println("Signing out User...\n");}
									
								    }
									
								    break;

						}
					  } 
					  catch (NumberFormatException e) 
					  {e.printStackTrace();}
				}
			}
			} 
		    catch (Exception ex) {
		    	ex.printStackTrace();
			}
		}
	}
