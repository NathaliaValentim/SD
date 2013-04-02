import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;


public interface ReceiverInterface extends Remote{
	public void receberArquivo(UtilitarioArquivo utilitario) throws RemoteException;
	public SenderInterface procurarSender(String nomeArquivo) throws RemoteException;
	public void atualizarAgencias(SenderInterface s)  throws RemoteException;
	ArrayList<SenderInterface> senders = new ArrayList<SenderInterface>();
}
