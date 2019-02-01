package com.rmi.common.action;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

/*
 * This Interface populates the method used by different managers for implementation
 * @author Shresthi Garg
 * */

public interface ActionService extends Remote {

	//Manager
	
	// Adds item to the Library
	public String addItem(String managerID, String itemID, String itemName, int quantity)throws RemoteException;

	// Removes item from the Library
	public String removeItem(String managerID, String itemID, int quantity)throws RemoteException;

	// Lists Item of the Library
	public HashMap<String, String> listItemAvailability(String managerID)throws RemoteException;

	//User	
	
	// Borrows an Item to the User
	public String borrowItem(String userID, String itemID, int numberOfDays)throws RemoteException;

	// Finds an Item from the Library
	public HashMap<String, String> findItem(String userId, String itemName)throws RemoteException;

	// Returns Item to the Library
	public String returnItem(String userID, String itemID)throws RemoteException;

}
