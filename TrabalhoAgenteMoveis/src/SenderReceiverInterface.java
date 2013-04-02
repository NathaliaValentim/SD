import java.rmi.RemoteException;
import java.util.ArrayList;


public interface SenderReceiverInterface extends ReceiverInterface,SenderInterface {	
	public String getRegistro() throws RemoteException;

}
