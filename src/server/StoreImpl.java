package server;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class StoreImpl extends Thread {
	
	private int portNumber;
	private Map<String, Product> inventory;
	private Map<String, Queue<String>> waitList;
	private PrintWriter log;
	private String serverID;
	private TreeMap<String, Integer> wallets;
	
	public StoreImpl(String serverID, int portNumber) {
		
		this.portNumber = portNumber;
		this.serverID = serverID;
		this.inventory = Collections.synchronizedMap(new HashMap<String, Product>());
		this.waitList = Collections.synchronizedMap(new HashMap<String, Queue<String>>());
		this.wallets = new TreeMap<String, Integer>();
		try {
			this.log = new PrintWriter(new File("src\\logs\\" + serverID + "log.txt"));	
		}
		catch (FileNotFoundException exception) {
			System.out.println("File: " + exception.getMessage());
			System.exit(1);
		}
		log.println(LocalDateTime.now() + ": Server is started");
		log.flush();
	}
	
	public int getPortNumber() {
		return portNumber;
	}
	
	public String addItem(String managerID, String itemID, String itemName, int quantity, int price) {
		log.println(LocalDateTime.now() + ": Request to add item with arguments " + managerID + ", " + itemID + ", " + itemName + ", " + quantity + ", and " + price + ".");
		String toReturn = "";
		if(managerID.charAt(2) != 'M')
			toReturn += "This ID is not a manager, access denied.";
		else {
			synchronized(this){
				if(inventory.containsKey(itemID))
					inventory.get(itemID).setQuantity(inventory.get(itemID).getQuantity() + quantity);
				else
					inventory.put(itemID, new Product(itemName, quantity, price));
			}
			toReturn += "Add operation successful.";
			if (waitList.containsKey(itemID)) {
				Queue<String> itemQueue = waitList.get(itemID);
				for(String customer: itemQueue) {
					purchaseItem(customer, itemID);
				}
				for(int i = 0; i < itemQueue.size(); ++i) {
					itemQueue.remove();
				}
			}
		}
		log.println(LocalDateTime.now() + ": Server answer: " + toReturn);
		log.flush();
		return toReturn;
	}
	
	public String removeItem(String managerID, String itemID, int quantity) {
		String toReturn = "";
		log.println(LocalDateTime.now() + ": Request to remove item with arguments " + managerID + ", " + itemID + ", and " + quantity + ".");
		if(managerID.charAt(2) != 'M')
			toReturn += "This ID is not a manager, access denied.";
		else {
			synchronized(this){
				if (inventory.containsKey(itemID)) {
					if (quantity == -1 || inventory.get(itemID).getQuantity() < quantity)
						inventory.remove(itemID);
					else
						inventory.get(itemID).setQuantity(inventory.get(itemID).getQuantity() - quantity);
				}
			}
			toReturn += "Remove operation successful.";
		}
		log.println(LocalDateTime.now() + ": Server answer: " + toReturn);
		log.flush();
		return toReturn;
	}
	
	public String listItemAvailability(String managerID) {
		String toReturn = "";
		log.println(LocalDateTime.now() + ": Request to list server item availability with argument " + managerID + ".");
		if(serverID.charAt(0) != managerID.charAt(0) && serverID.charAt(1) != managerID.charAt(1))
			toReturn += "This server does not belong to this ID.";
		else if(managerID.charAt(2) != 'M')
			toReturn += "This ID is not a manager, access denied.";
		else {
			for(Map.Entry<String, Product> entry: inventory.entrySet()) {
				toReturn += entry.getKey().toString() + " " + entry.getValue().toString() + " ";
			}
		}
		log.println(LocalDateTime.now() + ": Server answer: " + toReturn);
		log.flush();
		return toReturn;
	}
	
	public String purchaseItem(String customerID, String itemID) {
		String toReturn = "";
		log.println(LocalDateTime.now() + ": Request to purchase item with arguments " + customerID + ", and " + itemID + ".");
		if(customerID.charAt(2) != 'U')
			toReturn += "This ID is not a customer, access denied.";
		else {
			if (serverID.charAt(0) == customerID.charAt(0) && serverID.charAt(1) == customerID.charAt(1) && !wallets.containsKey(customerID)) {
				wallets.put(customerID, 1000);
			}
			if(inventory.containsKey(itemID)) {
				synchronized(this) {
					Product itemToPurchase = inventory.get(itemID);
					itemToPurchase.setQuantity(inventory.get(itemID).getQuantity() - 1);
					itemToPurchase.getPurchaseHistory().put(customerID, LocalDateTime.now());
					Integer budget = wallets.get(customerID);
					budget -= inventory.get(itemID).getPrice();
					wallets.put(customerID, budget);
					toReturn += "Purchase successful. You have $" + budget + " remaining.";
				}
			}
			else {
				toReturn += "Item unavailable, do you want to be placed on the waiting list for this item?";
			}
		}
		log.println(LocalDateTime.now() + ": Server answer: " + toReturn);
		log.flush();
		return toReturn;
	}
	
	public String purchaseItemResponse(String answer, String itemID, String customerID) {
		String toReturn = "";
		if (answer.equals("Yes")) {
			Queue<String> itemWaitList = new ConcurrentLinkedQueue<String>();
			itemWaitList.add(customerID);
			waitList.put(itemID, itemWaitList);
			toReturn += "Successful added " + customerID + " to queue.";
		}
		else if (answer.equals("No")) {
			toReturn += "No action taken.";
		}
		else {
			toReturn += "Answer not recognized, no action taken.";
		}
		log.println(LocalDateTime.now() + ": Server answer: " + toReturn);
		log.flush();
		return toReturn;
	}
	
	public String findItem(String customerID, String itemName) {
		String toReturn = "";
		log.println(LocalDateTime.now() + ": Request to find item with arguments " + customerID + ", and " + itemName + ".");
		if(customerID.charAt(2) != 'U')
			toReturn += "This ID is not a customer, access denied.";
		else {
			for(Map.Entry<String, Product> entry: inventory.entrySet()) {
				if (itemName.contains(entry.getValue().getName())) {
					toReturn += entry.getKey() + " ";
					toReturn += entry.getValue();
				}
			}
		}
//		if (serverID.charAt(0) == customerID.charAt(0) && serverID.charAt(1) == customerID.charAt(1)) {
//			if (customerID.charAt(0) == 'Q' && customerID.charAt(1) == 'C') {
//				toReturn = this.getFromOtherStores("findItem " + customerID + " itemName", 5678);
//			}
//		}
		log.println(LocalDateTime.now() + ": Server answer: " + toReturn);
		log.flush();
		return toReturn;
	}
	
	public String returnItem(String customerID, String itemID) {
		String toReturn = "";
		log.println(LocalDateTime.now() + ": Request to return item with arguments " + customerID + ", and " + itemID + ".");
		if(customerID.charAt(2) != 'U')
			toReturn += "This ID is not a customer, access denied.";
		else if(serverID.charAt(0) != customerID.charAt(0) && serverID.charAt(1) != customerID.charAt(1))
			toReturn += "This server does not belong to this ID.";
		else {
			synchronized(this) {
				if(inventory.containsKey(itemID) && inventory.get(itemID).getPurchaseHistory().containsKey(customerID)) {
						inventory.get(itemID).setQuantity(inventory.get(itemID).getQuantity() + 1);
						Integer budget = wallets.get(customerID);
						budget += inventory.get(itemID).getPrice();
						wallets.put(customerID, budget);
						toReturn += "Return successful. You have $" + budget + " remaining.";
				}
				else {
					toReturn += "Item not returned.";
				}
			}
		}
		log.println(LocalDateTime.now() + ": Server answer: " + toReturn);
		log.flush();
		return toReturn;
	}
	
	public String exchangeItem(String customerID, String oldItemID, String newItemID) {
		String toReturn = "";
		log.println(LocalDateTime.now() + ": Request to exchange items with arguments " + customerID + ", " + newItemID + ", and " + oldItemID + ".");
		if (!wallets.containsKey(customerID)) {
			toReturn += "The client has not purchased any items at this store.";
		}
		else if (!inventory.containsKey(newItemID) || !inventory.containsKey(oldItemID)) {
			toReturn += "One of the items is no longer available in the store.";
		}
		else {
			synchronized(this) {
				inventory.get(oldItemID).setQuantity(inventory.get(oldItemID).getQuantity() + 1);
				int budget = wallets.get(customerID);
				budget += inventory.get(oldItemID).getPrice();
				Product itemToPurchase = inventory.get(newItemID);
				itemToPurchase.setQuantity(inventory.get(newItemID).getQuantity() - 1);
				itemToPurchase.getPurchaseHistory().put(customerID, LocalDateTime.now());
				budget -= inventory.get(newItemID).getPrice();
				wallets.put(customerID, budget);
				toReturn += "Exchange successful. You have $" + budget + " remaining.";
			}
		}
		log.println(LocalDateTime.now() + ": Server answer: " + toReturn);
		log.flush();
		return toReturn;
	}
	
	public String getFromOtherStores(String command, int storePort) {
		DatagramSocket aSocket = null;
		String receivedData = null;
		try {
			aSocket = new DatagramSocket();
			byte [] m = command.getBytes();
			InetAddress aHost = InetAddress.getByName("127.0.0.1");
			int serverPort = storePort;
			DatagramPacket request = new DatagramPacket(m, command.length(), aHost, serverPort);
			aSocket.send(request);
			byte[] buffer = new byte[1000];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			aSocket.receive(reply);
			receivedData = (new String(reply.getData()));
		}
		catch (SocketException e){
			System.out.println("Socket: " + e.getMessage());
		}
		catch (IOException e){
			System.out.println("IO: " + e.getMessage());
		}
		finally {
			if(aSocket != null)
				aSocket.close();
		}
		return receivedData;
	}
	
	public void run() {
		DatagramSocket socket = null;
		try{
			socket = new DatagramSocket(getPortNumber());
			byte[] buffer = new byte[1000];
			while(true){
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				socket.receive(request);
				String result = new String(request.getData());
				StringTokenizer factory = new StringTokenizer(result);
				String command = factory.nextToken();
				if (command.equals("findItem")) {
					String customerID = factory.nextToken();
					String itemName = factory.nextToken();
					result = findItem(customerID, itemName);
				}
				else if (command.equals("purchaseItem")) {
					String customerID = factory.nextToken();
					String itemID = factory.nextToken();
					result = purchaseItem(customerID, itemID);
				}
				else if (command.equals("exchangeItem")) {
					String customerID = factory.nextToken();
					String oldItemID = factory.nextToken();
					String newItemID = factory.nextToken();
					result = exchangeItem(customerID, oldItemID, newItemID);
				}
				else if (command.equals("exit")) {
					break;
				}
				else {
					result = "Command not recognized.";
				}
				byte[] m = result.getBytes();
				DatagramPacket reply = new DatagramPacket(m, result.length(), request.getAddress(), request.getPort());
				socket.send(reply);
			}
		}
		catch (SocketException e){
			System.out.println("Socket: " + e.getMessage());
		}
		catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		}
		finally {
			if(socket != null) 
				socket.close();
		}
	}
}
