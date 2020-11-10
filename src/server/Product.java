package server;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentSkipListMap;

public class Product {
	
	private String name;
	private int quantity;
	private int price;
	private ConcurrentSkipListMap<String, LocalDateTime> purchaseHistory;
	
	Product(String name, int quantity, int price) {
		this.name = name;
		this.quantity = quantity;
		this.price = price;
		this.purchaseHistory = new ConcurrentSkipListMap<String, LocalDateTime>();
	}
	
	public ConcurrentSkipListMap<String, LocalDateTime> getPurchaseHistory() {
		return purchaseHistory;
	}
	
	public void setPurchaseHistory(ConcurrentSkipListMap<String, LocalDateTime> purchaseHistory) {
		this.purchaseHistory = purchaseHistory;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getQuantity() {
		return quantity;
	}
	
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	public int getPrice() {
		return price;
	}
	
	public void setPrice(int price) {
		this.price = price;
	}
	
	public String toString() {
		return quantity + " " + price;
	}
}
