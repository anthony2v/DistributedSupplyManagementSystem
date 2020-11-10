package server;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.*;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import StoreApp.*;

public class ONStoreServer {
	public static void main(String[] args) {
		try {
			ORB orb = ORB.init(args, null);
			POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			rootpoa.the_POAManager().activate();
			StoreImpl storeImpl = new StoreImpl("ON", 5678);
			storeImpl.setORB(orb);
			org.omg.CORBA.Object ref = rootpoa.servant_to_reference(storeImpl);
			Store href = StoreHelper.narrow(ref);
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
			String name = "ON-STORE";
			NameComponent path[] = ncRef.to_name(name);
			ncRef.rebind(path, href);
			System.out.println("ON StoreServer ready and waiting ...");
			orb.run();
		}
		catch (Exception e) {
			System.err.println("ERROR: " + e);
			e.printStackTrace(System.out);
		}
		finally {
			System.out.println("ONStoreServer Exiting ...");
		}
	}
}
