import java.rmi.Remote;
import java.rmi.RemoteException;


public interface SenderInterface extends Remote{
	public void enviarArquivo(ReceiverInterface receiver,String nome) throws RemoteException;
	public boolean verificarExistenciaArquivo(String nome) throws RemoteException;
}
