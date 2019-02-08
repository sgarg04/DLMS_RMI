package com.rmi.common;

import java.util.HashMap;


public class User {
	private String userID;
	private HashMap<Book, Integer> bookInfo;
	
	

	public User() {
		super();
		// TODO Auto-generated constructor stub
	}
	public User(String userID, HashMap<Book, Integer> bookInfo) {
		super();
		this.userID = userID;
		this.bookInfo = bookInfo;
	}
	@Override
	public String toString() {
		return "User [userID=" + userID + ", bookInfo=" + bookInfo + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bookInfo == null) ? 0 : bookInfo.hashCode());
		result = prime * result + ((userID == null) ? 0 : userID.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (bookInfo == null) {
			if (other.bookInfo != null)
				return false;
		} else if (!bookInfo.equals(other.bookInfo))
			return false;
		if (userID == null) {
			if (other.userID != null)
				return false;
		} else if (!userID.equals(other.userID))
			return false;
		return true;
	}
	public HashMap<Book, Integer> getBookInfo() {
		return bookInfo;
	}
	public void setBookInfo(HashMap<Book, Integer> bookInfo) {
		this.bookInfo = bookInfo;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	


	
}
