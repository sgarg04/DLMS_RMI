package com.rmi.libraries;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.rmi.common.Manager;
import com.rmi.common.User;
import com.rmi.common.action.ActionServiceImpl;

public class Montreal {
	
	public static HashMap<String, String> monBooks = new HashMap<String, String>();
	public static ArrayList<User> monUserList = new ArrayList<User>();
	public static HashMap<String, String[]> waitMonBook = new HashMap<String, String[]>();

//	public static HashMap<String, String> getInstanceHashMap() {
//		if (monBooks != null) {
//			Iterator it = monBooks.entrySet().iterator();
//		    while (it.hasNext()) {
//		        Map.Entry pair = (Map.Entry)it.next();
//		        System.out.println();
//		        System.out.println(pair.getKey() + " = " + pair.getValue());
////		        it.remove(); // avoids a ConcurrentModificationException
//		    }
//			return monBooks;
//		} else
//			return null;
//
//	}

	protected Montreal() throws RemoteException {
		super();
	}

	public static void main(String args[]) throws Exception {
		startRegistry();
		setLibraryDetails();
	}

	private synchronized static void startRegistry() throws Exception {

		ActionServiceImpl monStub = new ActionServiceImpl();
		System.out.println("Montreal server started");

		try {
			// special exception handler for registry creation
			LocateRegistry.createRegistry(7777);
			System.out.println("Montreal registry created.");
		} catch (RemoteException e) {
			// do nothing, error means registry already exists
			System.out.println("Montreal registry already exists.");
		}

		// Instantiate Montreal Server and bind this object instance to the name
		// "Concordia Server"
		Naming.rebind("MON", monStub);
		System.out.println("Montreal Server bound in registry");

	}

	private synchronized static void populateDetails() {
		setManagerDetails();
//		setUserDetails();
		setLibraryDetails();
		setWaitingQueueDetails();

	}

	private static synchronized void setManagerDetails() {
		ArrayList<Manager> manager = new ArrayList<Manager>();

	}

	public static synchronized void setUserDetails(String userID, String itemID, int numberOfDays) {
		
		HashMap<String, Integer> temp = new HashMap<String, Integer>();
		temp.put(itemID, numberOfDays);
		User userObj = new User(userID, temp);
		monUserList.add(userObj);

//		HashMap<String, Integer> temp = new HashMap<String, Integer>();
//		temp.put("CON1234", 3);
//		temp.put("CON2225", 4);
//		User userObj1 = new User("CONU1234", temp);
//
//		HashMap<String, Integer> temp1 = new HashMap<String, Integer>();
//		temp1.put("CONU1235", 3);
//		temp1.put("CONU2226", 4);
//
//		User userObj2 = new User("CONU1234", temp1);
//
//		userList.add(userObj1);
//		userList.add(userObj2);
	}

	private static synchronized void setLibraryDetails() {
		monBooks.put("MON1234", "COMPUTER SCIENCE,9");
		monBooks.put("MON2225", "SOCIAL SCIENCE,4");
	}

	private static synchronized void setWaitingQueueDetails() {

	}

}
