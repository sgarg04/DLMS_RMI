package com.rmi.libraries;

import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.rmi.common.Book;
import com.rmi.common.Manager;
import com.rmi.common.User;
import com.rmi.common.action.ActionService;
import com.rmi.common.action.ActionServiceImpl;

public class Concordia {

	public static HashMap<String, String> conBooks = new HashMap<String, String>();
	public static ArrayList<User> conUserList = new ArrayList<User>();
	public static HashMap<String, String[]> waitConBook = new HashMap<String, String[]>();
	
//	public static HashMap<String, String> getInstanceHashMap() {
//		if (conBooks != null) {
//			Iterator it = conBooks.entrySet().iterator();
//		    while (it.hasNext()) {
//		        Map.Entry pair = (Map.Entry)it.next();
//		        System.out.println();
//		        System.out.println(pair.getKey() + " = " + pair.getValue());
////		        it.remove(); // avoids a ConcurrentModificationException
//		    }
//			return conBooks;
//		} else
//			return null;
//
//	}

	public Concordia() throws RemoteException {
		super();
	}

	public static final HashMap<String, Integer> managerMap = new HashMap<String, Integer>();

	public static void main(String args[]) throws Exception {
		
		setLibraryDetails();
		startRegistry();
//		populateDetails();

	}

	private synchronized static void startRegistry() throws Exception {

		ActionServiceImpl conStub = new ActionServiceImpl();

		System.out.println("Concordia server started");
		try {
			// special exception handler for registry creation
			LocateRegistry.createRegistry(4444);
			System.out.println("Concordia registry created.");
		} catch (RemoteException e) {
			// do nothing, error means registry already exists
			System.out.println("Concordia registry already exists.");
		}

		// Instantiate Concordia Server and bind this object instance to the name
		// "Concordia Server"
		Naming.rebind("CON", conStub);
		System.out.println("Concordia Server bound in registry");

	}

	private synchronized static void populateDetails() {
//		setManagerDetails();
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
		conUserList.add(userObj);

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
		
		conBooks.put("CON1234", "COMPUTER SCIENCE,9");
		conBooks.put("CON2225", "SOCIAL SCIENCE,4");
	}

	private static synchronized void setWaitingQueueDetails() {

	}
	
}
