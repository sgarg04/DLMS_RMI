package com.rmi.libraries;

public class StartServer {
	public static void main(String[] args) throws Exception {
		
		Concordia.startConcordiaServer();
		Montreal.startMontrealServer();
		McGill.startMcGillServer();
	}

}
