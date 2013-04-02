import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Queue;


public interface AgenciaInterface extends SenderReceiverInterface {
	public ArrayList<ServicoInterface> getServicos() throws RemoteException;
	public void receberAgente(AgenteMovelInterface agenteMovel, Queue<AgenciaInterface> filaAgencias) throws RemoteException;
	public void moverAgente(AgenteMovelInterface agenteMovel, Queue<AgenciaInterface> filaAgencias) throws RemoteException;
	public boolean verificarCodigoAgente(String nomeAgente) throws RemoteException;
	ArrayList<ServicoInterface> servicos = new ArrayList<ServicoInterface>();
}
