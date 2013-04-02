import java.io.File;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;  


public class Registradora extends UnicastRemoteObject implements RegistradoraInterface{

	private static final int porta=1099;
	private static String diretorioAtual="";
	private static Registradora registradora=null;
	private static AgenciaLancadoraInterface lancadora=null;
	 
	
	protected Registradora() throws RemoteException {
		super();
	}
	
	
	public static void main(String[] args) {
		
		try {

			File dir = new File(".");
			diretorioAtual = dir.getCanonicalPath();
			System.setProperty("java.security.policy", "file:/"
					+ diretorioAtual + "\\policy.policy");

		} catch (Exception e) {
			System.out.println("Catch no setProperty");
		}
		
		
		try{
  			LocateRegistry.createRegistry(porta);
  		}
  		catch(Exception e){
  			System.out.println("Catch no createRegistry");
  		}
		
		try{
			if (System.getSecurityManager() == null) 
				System.setSecurityManager(new RMISecurityManager());
  		}
  		catch(Exception e){
  			System.out.println("Catch no securityManager");
  		}
		
		try{
			registradora = new Registradora();
			Naming.rebind("//"+Utilidades.getInstance().ip()+"/registradora", registradora);
  		}
  		catch(Exception e){
  			System.out.println("Catch no registro");
  		}
		
		System.out.println("Registradora Ligada!");
		
		
		
		
		
	}


	@Override
	public boolean registrarAgencia(AgenciaInterface agencia) throws RemoteException {
		
		System.out.println("\nRegistrando agencia: "+agencia.getRegistro());
		int i=0;
		try {
			ArrayList<ServicoInterface> servicos = agencia.getServicos();

			if (servicos != null && servicos.size()>0) {
				
				if(servicos.size()==1)
					System.out.print("Servico disponibilizado pela agencia: ");
				else
					System.out.print("Servicos disponibilizados pela agencia: ");
				
				for (ServicoInterface s : servicos) {
					System.out.print(s.getDescricao());
					if(i<servicos.size()-1)
						System.out.print(", ");
					ArrayList<AgenciaInterface> aux = null;
					if (servicoAgencia.containsKey(s.getDescricao())) {
						aux = servicoAgencia.get(s.getDescricao());
						aux.add(agencia);
						servicoAgencia.remove(s.getDescricao());

					} else {
						aux = new ArrayList<AgenciaInterface>();
						aux.add(agencia);
					}
					servicoAgencia.put(s.getDescricao(), aux);
					i++;
				}
				System.out.println("\nAgencia \""+agencia.getRegistro()+"\" Registrada!\n");
				
				
				try{
					atualizarAgencias(agencia);
				}
				catch(Exception e){
					System.out.println("Problema do atualizar na "+agencia.getRegistro());
				}
				
				return true;
			} else {
				System.out.println("\n\nA agencia nao possui servico! Registro nao realizado!\n");
				return false;
			}
			
		} catch (Exception e) {
			return false;
		}
			
	}


	@Override
	public boolean registrarAgenciaLancadora(AgenciaLancadoraInterface lancadora)
			throws RemoteException {

		try {
			System.out.println("\nRegistrando agencia lancadora...");
			this.lancadora = lancadora;
			System.out.println("Agencia lancadora registrada!\n");
			
			try{
				atualizarAgencias(lancadora);
			}
			catch(Exception e){
				System.out.println("Problema do atualizar na lancadora");
			}
			
			
			return true;

		} catch (Exception e) {
			return false;
		}
	}


	@Override
	public void atualizarAgencias(SenderReceiverInterface s) {
		ArrayList<SenderReceiverInterface> agencias = new ArrayList<SenderReceiverInterface>();		
		
		if(lancadora!=null)
			agencias.add(lancadora);
		
		Iterator iterator =  servicoAgencia.keySet().iterator();
		
		while (iterator.hasNext()) { 
			String servico = (String) iterator.next();
			ArrayList<AgenciaInterface> agencia = servicoAgencia.get(servico);
			for(AgenciaInterface a:agencia)
				if(!agencias.contains(a))
					agencias.add(a);
		}
		
		
		for(SenderReceiverInterface sri: agencias){
			try{
				if(!s.equals(sri))
					s.atualizarAgencias(sri);
			}
			catch(Exception e){
				//System.out.println("1 Catch na atualizacao de: "+sri.getClass());
			}
		}
		
		for(SenderReceiverInterface sri: agencias){
			try{
				if(!s.equals(sri))
					sri.atualizarAgencias(s);
			}
			catch(Exception e){
				//System.out.println("2 Catch na atualizacao de: "+sri.getClass());
			}
		}
		
		
			
	}


	@Override
	public ArrayList<AgenciaInterface> agenciasServico(String servico)
			throws RemoteException {
		return servicoAgencia.get(servico);
	}


	@Override
	public void enviarArquivo(ReceiverInterface receiver, String nome)
			throws RemoteException {
		 
		UtilitarioArquivo utilitario = new UtilitarioArquivo(nome, diretorioAtual);
   		utilitario.readIn();
   		try {
   			System.out.println("\nEnviando arquivo: "+utilitario.getName());
			receiver.receberArquivo(utilitario);
			System.out.println("Enviado!");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	@Override
	public boolean verificarExistenciaArquivo(String nome)
			throws RemoteException {
		 
		try{
		File dir1 = new File (".");
			for(File f:dir1.listFiles()){
				if(nome.equals(f.getName()) && !f.isDirectory()){
					return true;
				}
			}
		}
		catch(Exception e){
			return false;
		}
		
		return false;
	}
	
	

}
