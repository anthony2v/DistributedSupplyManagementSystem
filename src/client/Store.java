package client;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
 
// SEI
@WebService
@SOAPBinding(style = Style.RPC)
public interface Store {
	@WebMethod String addItem(String managerID, String itemID, String itemName, int quantity, int price);
	
	@WebMethod String removeItem(String managerID, String itemID, int quantity);
	
	@WebMethod String listItemAvailability(String managerID);
	
	@WebMethod String purchaseItem(String customerID, String itemID, boolean doWaitlist);
	
	@WebMethod String findItem(String customerID, String itemName);
	
	@WebMethod String returnItem(String customerID, String itemID);
	
	@WebMethod String exchangeItem(String customerID, String oldItemID, String newItemID, boolean doWaitlist);
}
