package server;

public class StoreServer {
	public static void main(String[] args) {
		try {
			StoreImpl storeImpl1 = new StoreImpl("QC", 6789);
			storeImpl1.start();
			System.out.println("QC StoreServer ready and waiting ...");
			StoreImpl storeImpl2 = new StoreImpl("ON", 5678);
			storeImpl2.start();
			System.out.println("ON StoreServer ready and waiting ...");
			StoreImpl storeImpl3 = new StoreImpl("BC", 4567);
			storeImpl3.start();
			System.out.println("BC StoreServer ready and waiting ...");
			storeImpl1.join();
			storeImpl2.join();
			storeImpl3.join();
		}
		catch (Exception e) {
			System.err.println("ERROR: " + e);
			e.printStackTrace(System.out);
		}
		finally {
			System.out.println("QC Store Server Exiting ...");
			System.out.println("ON Store Server Exiting ...");
			System.out.println("BC Store Server Exiting ...");
		}
	}
}
