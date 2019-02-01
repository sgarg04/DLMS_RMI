package com.rmi.libraries;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.rmi.common.Manager;
import com.rmi.common.User;
import com.rmi.common.action.ActionServiceImpl;

public class McGill extends UnicastRemoteObject {

	
	public static HashMap<String, String> mcgBooks = new HashMap<String, String>();

	public static ArrayList<User> mcgUserList = new ArrayList<User>();
	public static HashMap<String, String[]> waitMcgBook = new HashMap<String, String[]>();

//	public static HashMap<String, String> getInstanceHashMap() {
//		if (mcgBooks != null) {
//			Iterator it = mcgBooks.entrySet().iterator();
//		    while (it.hasNext()) {
//		        Map.Entry pair = (Map.Entry)it.next();
//		        System.out.println();
//		        System.out.println(pair.getKey() + " = " + pair.getValue());
////		        it.remove(); // avoids a ConcurrentModificationException
//		    }
//			return mcgBooks;
//		} else
//			return null;
//
//	}
	
	protected McGill() throws RemoteException {
		super();
	}

	public static void main(String[] args) throws Exception {
		startRegistry();
		setLibraryDetails();
//		populateDetails();
		
	}
	
	private synchronized static void startRegistry() throws Exception {

		ActionServiceImpl mcgStub = new ActionServiceImpl();

		System.out.println("McGill server started");

		try {
			// special exception handler for registry creation
			LocateRegistry.createRegistry(5555);
			System.out.println("McGill registry created.");
		} catch (RemoteException e) {
			// do nothing, error means registry already exists
			System.out.println("McGill registry already exists.");
		}

		// Instantiate McGill Server and bind this object instance to the name
		// "McGill Server"
		try {
			Naming.rebind("MCG", mcgStub);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("McGill Server bound in registry");

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
		mcgUserList.add(userObj);

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
		mcgBooks.put("MCG1234", "COMPUTER SCIENCE,9");
		mcgBooks.put("MCG2225", "SOCIAL SCIENCE,4");
	}

	private static synchronized void setWaitingQueueDetails() {

	}

}
