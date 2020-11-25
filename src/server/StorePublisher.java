package server;

import javax.xml.ws.Endpoint;

public class StorePublisher {
	public static void main(String[] args) {
		StoreImpl QC = new StoreImpl("QC", 6789);
		StoreImpl BC = new StoreImpl("BC", 4567);
		StoreImpl ON = new StoreImpl("ON", 5678);
		Endpoint.publish("http://localhost:4567/ws/store", BC);
		Endpoint.publish("http://localhost:6789/ws/store", QC);
		Endpoint.publish("http://localhost:5678/ws/store", ON);
		QC.run();
		BC.run();
		ON.run();
	}
}
