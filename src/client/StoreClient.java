package client;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.util.*;

import server.Store;

import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

public class StoreClient {
	
	private String userID;
	private String locationID;
	private int serverPort;
	private PrintWriter systemLog;
	URL url;
	Store store;
	
	public StoreClient(String userID) throws Exception {
		this.userID = userID;
		String locationID = "";
		locationID += userID.charAt(0);
		locationID += userID.charAt(1);
		this.locationID = locationID;
		if (locationID.equals("QC"))
			serverPort = 6789;
		else if (locationID.equals("ON"))
			serverPort = 5678;
		else
			serverPort = 4567;
		url = new URL("http://localhost:" + serverPort + "/ws/store?wsdl");
		QName qname = new QName("http://server/", "StoreImplService");
		Service service = Service.create(url, qname);
        store = service.getPort(Store.class);
		try {
			systemLog = new PrintWriter(new File("src\\logs\\" + userID + "log.txt"));
		}
		catch (FileNotFoundException exception) {
			System.out.println("File: " + exception.getMessage());
			System.exit(1);
		}
	}
	
	public String getUserID() {
		return userID;
	}
	
	public void setUserID(String userID) {
		this.userID = userID;
	}
	
	public String getLocationID() {
		return locationID;
	}
	
	public void setLocationID(String locationID) {
		this.locationID = locationID;
	}
	
	public String remoteMethodInvocation(String command) {
		DatagramSocket aSocket = null;
		String receivedData = null;
		try {
			aSocket = new DatagramSocket();
			byte [] m = command.getBytes();
			InetAddress aHost = InetAddress.getByName("127.0.0.1");
			DatagramPacket request = new DatagramPacket(m, command.length(), aHost, serverPort);
			aSocket.send(request);
			byte[] buffer = new byte[1000];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			aSocket.receive(reply);
			receivedData = new String(reply.getData()).trim();
		} catch (SocketException e){
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e){
			System.out.println("IO: " + e.getMessage());
		} finally {
			if(aSocket != null)
				aSocket.close();
		}
		return receivedData;
	}
	
	public void processCommands(Scanner userInput) {
		systemLog.println(LocalDateTime.now() + ": Successful login to " + getLocationID() + " server.");
		String command = null;
		String result = null;
		StringTokenizer arguments = null;
		while(true) {
			result = "";
			System.out.println("Available commands: addItem(M), removeItem(M), listItemAvailability(M), purchaseItem(U), findItem(U), returnItem(U), exchangeItem(U), exit(M/U).");
			try {
				command = userInput.next();
				systemLog.print(LocalDateTime.now() + ": " + command + " ");
				if (command.equals("exit")) {
					systemLog.println("called. Program terminated.");
					systemLog.flush();
					break;
				}
				else if (command.equals("addItem")) {
					arguments = new StringTokenizer(userInput.nextLine());
					String itemID = arguments.nextToken();
					String itemName = arguments.nextToken();
					int quantity = Integer.parseInt(arguments.nextToken());
					int price = Integer.parseInt(arguments.nextToken());
					systemLog.println("called with arguments " + userID + ", " + itemID + ", " + itemName + ", " + quantity + ", and " + price + ".");
					result = store.addItem(userID, itemID, itemName, quantity, price);
					//result = remoteMethodInvocation(command + " " + userID + " " + itemID + " " + itemName + " " + quantity + " " + price + " ");
					System.out.println(result);
					systemLog.println(LocalDateTime.now() + ": " + locationID + " server answer: " + result);
				}
				else if (command.equals("removeItem")) {
					arguments = new StringTokenizer(userInput.nextLine());
					String itemID = arguments.nextToken();
					int quantity = Integer.parseInt(arguments.nextToken());
					systemLog.println("called with arguments " + userID + ", " + itemID + ", and " + quantity);
					result = store.removeItem(userID, itemID, quantity);
					//result = remoteMethodInvocation(command + " " + userID + " " + itemID + " " + quantity + " ");
					System.out.println(result);
					systemLog.println(LocalDateTime.now() + ": " + locationID + " server answer: " + result);
				}
				else if (command.equals("listItemAvailability")) {
					systemLog.println("called with argument " + userID);
					result = store.listItemAvailability(userID);
					//result = remoteMethodInvocation(command + " " + userID + " ");
					System.out.println(result);
					systemLog.println(LocalDateTime.now() + ": " + locationID + " server answer: " + result);
				}
				else if (command.equals("purchaseItem")) {
					arguments = new StringTokenizer(userInput.nextLine());
					String itemID = arguments.nextToken();
					systemLog.println("called with arguments " + userID + ", and " + itemID + ".");
					result = store.purchaseItem(userID, itemID, true);
					//result = remoteMethodInvocation(command + " " + userID + " " + itemID + " ");
					System.out.println(result);
					systemLog.println(new Date() + ": " + locationID + " server answer: " + result);
					if (result.contains("unavailable")) {
						String reserveResponse = userInput.next();
						systemLog.println(LocalDateTime.now() + ": purchaseItemResponse called with arguments " + reserveResponse + ", " + itemID + ", and " + userID);
						//result = storeImpl.purchaseItemResponse(reserveResponse, itemID, userID);
						System.out.println(result);
						systemLog.println(LocalDateTime.now() + ": " + locationID + " server answer: " + result);
					}
				}
				else if (command.equals("findItem")) {
					arguments = new StringTokenizer(userInput.nextLine());
					String itemName = arguments.nextToken();
					systemLog.println("called with argument " + itemName);
					//result = remoteMethodInvocation(command + " " + userID + " " + itemName + " ");
					result = store.findItem(userID, itemName);
					System.out.println(result);
					systemLog.println(LocalDateTime.now() + ": " + locationID + " server answer: " + result);
				}
				else if (command.equals("returnItem")) {
					arguments = new StringTokenizer(userInput.nextLine());
					String itemID = arguments.nextToken();
					systemLog.println("called with arguments " + userID + ", and " + itemID + ".");
					//result = remoteMethodInvocation(command + " " + userID + " " + itemID + " ");
					result = store.returnItem(userID, itemID);
					System.out.println(result);
					systemLog.println(LocalDateTime.now() + ": " + locationID + " server answer: " + result);
					if (result.contains("successful")) {
						
					}
				}
				else if (command.equals("exchangeItem")) {
					arguments = new StringTokenizer(userInput.nextLine());
					String oldItemID = arguments.nextToken();
					String newItemID = arguments.nextToken();
					systemLog.println("called with arguments " + userID + ", " + oldItemID + ", and " + newItemID + ".");
					result = store.exchangeItem(userID, oldItemID, newItemID, true);
					//result = remoteMethodInvocation(command + " " + userID + " " + oldItemID + " " + newItemID + " ");
					System.out.println(result);
					systemLog.println(LocalDateTime.now() + ": " + locationID + " server answer: " + result);
				}
				else {
					System.out.println("Command not recognized. Please try again.");
					systemLog.println("called. Command not recognized.");
				}
			}
			catch (NoSuchElementException e) {
				System.out.println("Too few arguments, please try again.");
				systemLog.println("called with too few arguments.");
			}
			catch (NumberFormatException e) {
				System.out.println("Incorrect input format, please try again.");
				systemLog.println("called with incorrect input format.");
			}
			finally {
				systemLog.flush();
			}
		}
	}
	
	public void programTest() {
		System.out.println("---- User: QCM4444 ----");
		System.out.println("addItem QC1111 Laptop 35 400");
		System.out.println(store.addItem("QCM4444", "QC1111", "Laptop", 35, 400));
		System.out.println("listItemAvailability");
		System.out.println(store.listItemAvailability("QCM4444"));
		System.out.println("addItem QC6655 Tea 50 40");
		System.out.println(store.addItem("QCM4444", "QC6655", "Tea", 50, 40));
		System.out.println("listItemAvailability");
		System.out.println(store.listItemAvailability("QCM4444"));
		System.out.println("removeItem QC1111 13");
		System.out.println(store.removeItem("QCM4444", "QC1111", 13));
		System.out.println("listItemAvailability");
		System.out.println(store.listItemAvailability("QCM4444"));
		System.out.println("---- User: QCU5555 ----");
		System.out.println("findItem Laptop");
		System.out.println(store.findItem("QCU5555", "Laptop"));
		System.out.println("findItem Tea");
		System.out.println(store.findItem("QCU5555", "Tea"));
		System.out.println("purchaseItem QC6655 true");
		System.out.println(store.purchaseItem("QCU5555", "QC6655", true));
		System.out.println("exchangeItem QC6655 QC1111");
		System.out.println(store.exchangeItem("QCU5555", "QC6655", "QC1111", true));
		System.out.println("purchaseItem QC6655 true");
		System.out.println(store.purchaseItem("QCU5555", "QC6655", true));
		System.out.println("returnItem QC6655");
		System.out.println(store.returnItem("QCU5555", "QC6655"));
		System.out.println("purchaseItem QC7777 true");
		System.out.println(store.purchaseItem("QCU5555", "QC7777", true));
		System.out.println("---- User: QCM6767 ----");
		System.out.println("addItem QC7777 Desktop 60 500");
		System.out.println(store.addItem("QCM6767", "QC7777", "Desktop", 60, 500));
		System.out.println("listItemAvailability");
		System.out.println(store.listItemAvailability("QCM7777"));
		System.out.println("---- User: QCU5555 ----");
		System.out.println("purchaseItem QC6655 true");
		System.out.println(store.purchaseItem("QCU5555", "QC6655", true));
		System.out.println("returnItem QC7777");
		System.out.println(store.returnItem("QCU5555", "QC7777"));
	}
	
	public static void main(String[] args) {
		Scanner userInput = new Scanner(System.in);
		System.out.print("Welcome to Dynamic Super Miracle Store. Please enter your ID: ");
		String userID = userInput.next();
		StoreClient currentUser = null;
		try {
			currentUser = new StoreClient(userID);
			//System.out.println("Obtained a handle on server object: " + storeImpl);
			currentUser.processCommands(userInput);
			//currentUser.programTest();
		}
		catch (Exception e) {
			System.out.println("ERROR : " + e) ;
			e.printStackTrace(System.out);
		}
		finally {
			System.out.println("Do you want to shutdown the server (y/n)?");
			String doShutdown = userInput.next();
			if (doShutdown.equals("y")) {
				currentUser.remoteMethodInvocation("exit");
			}
			else if (doShutdown.equals("n"))
				System.out.println("Server will not shutdown.");
			else
				System.out.println("Input not recognized, not action taken.");
			userInput.close();
		}
	}
}
