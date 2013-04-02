import java.rmi.RemoteException;
import java.util.Queue;
import java.util.LinkedList;

public interface AgenciaLancadoraInterface extends SenderReceiverInterface{
	Queue<AgenciaInterface> filaAgencias = new LinkedList<AgenciaInterface>();
	public boolean lancarAgente() throws RemoteException;;
	public boolean possiveisAgencias() throws RemoteException;
	public void receberAgenteRetorno(AgenteRetornarLancadoraInterface agente)  throws RemoteException;
}
