package client;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.util.*;
public class StoreClient {
	
	private String userID;
	private String locationID;
	private PrintWriter systemLog;
	
	public StoreClient(String userID) {
		this.userID = userID;
		String locationID = "";
		locationID += userID.charAt(0);
		locationID += userID.charAt(1);
		this.locationID = locationID;
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
			int serverPort = 6789;
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
					//result = storeImpl.addItem(userID, itemID, itemName, quantity, price);
					result = remoteMethodInvocation(command + " " + userID + " " + itemID + " " + itemName + " " + quantity + " " + price + " ");
					System.out.println(result);
					systemLog.println(LocalDateTime.now() + ": " + locationID + " server answer: " + result);
				}
				else if (command.equals("removeItem")) {
					arguments = new StringTokenizer(userInput.nextLine());
					String itemID = arguments.nextToken();
					int quantity = Integer.parseInt(arguments.nextToken());
					systemLog.println("called with arguments " + userID + ", " + itemID + ", and " + quantity);
					//result = storeImpl.removeItem(userID, itemID, quantity);
					result = remoteMethodInvocation(command + " " + userID + " " + itemID + " " + quantity + " ");
					System.out.println(result);
					systemLog.println(LocalDateTime.now() + ": " + locationID + " server answer: " + result);
				}
				else if (command.equals("listItemAvailability")) {
					systemLog.println("called with argument " + userID);
					result = remoteMethodInvocation(command + " " + userID + " ");
					//result = storeImpl.listItemAvailability(userID);
					System.out.println(result);
					systemLog.println(LocalDateTime.now() + ": " + locationID + " server answer: " + result);
				}
				else if (command.equals("purchaseItem")) {
					arguments = new StringTokenizer(userInput.nextLine());
					String itemID = arguments.nextToken();
					systemLog.println("called with arguments " + userID + ", and " + itemID + ".");
					//result = storeImpl.purchaseItem(userID, itemID);
					result = remoteMethodInvocation(command + " " + userID + " " + itemID + " ");
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
					//result = storeImpl.findItem(userID, itemName);
					result = remoteMethodInvocation(command + " " + userID + " " + itemName + " ");
					System.out.println(result);
					systemLog.println(LocalDateTime.now() + ": " + locationID + " server answer: " + result);
				}
				else if (command.equals("returnItem")) {
					arguments = new StringTokenizer(userInput.nextLine());
					String itemID = arguments.nextToken();
					systemLog.println("called with arguments " + userID + ", and " + itemID + ".");
					//result = storeImpl.returnItem(userID, itemID);
					result = remoteMethodInvocation(command + " " + userID + " " + itemID + " ");
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
					//result = storeImpl.exchangeItem(userID, oldItemID, newItemID);
					result = remoteMethodInvocation(command + " " + userID + " " + oldItemID + " " + newItemID + " ");
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
	
	public static void main(String[] args) {
		Scanner userInput = new Scanner(System.in);
		System.out.print("Welcome to Dynamic Super Miracle Store. Please enter your ID: ");
		String userID = userInput.next();
		StoreClient currentUser = new StoreClient(userID);
		try {
			//System.out.println("Obtained a handle on server object: " + storeImpl);
			currentUser.processCommands(userInput);
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
