import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;


public interface RegistradoraInterface extends SenderInterface {
	public boolean registrarAgencia(AgenciaInterface agencia) throws RemoteException;
	public boolean registrarAgenciaLancadora(AgenciaLancadoraInterface lancadora) throws RemoteException;
	public void atualizarAgencias(SenderReceiverInterface s) throws RemoteException;
	public ArrayList<AgenciaInterface> agenciasServico(String servico) throws RemoteException;
	
	HashMap<String,ArrayList<AgenciaInterface>> servicoAgencia = new HashMap<String,ArrayList<AgenciaInterface>>();
}
