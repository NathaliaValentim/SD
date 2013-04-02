import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Queue;


public class AgenciaDeletarArquivos extends UnicastRemoteObject implements AgenciaInterface{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final int porta=1099;
	private static final String nome="AgenciaDeletarArquivos";
	private String registro="";
	private String nomeAgente="";
	private String erro="Connection reset";
	
	protected AgenciaDeletarArquivos() throws RemoteException {
		super();
	}
	
	private static String diretorioAtual="";
	private static AgenciaDeletarArquivos agencia=null;
	private static RegistradoraInterface registradora=null;
	
	public static boolean lerIpRegistradora(){
		String entrada = "";
		System.out.println("\nDigite o IP da registradora: ");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("=> ");
		try {
			entrada=in.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while(!Utilidades.getInstance().validarIP(entrada)){
			System.out.print("\n*** IP invalido ***\n\n=> Digite novamente: ");
			try {
				entrada=in.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return testarConexaoRegistradora(entrada);
	}
	
	public static boolean testarConexaoRegistradora(String ip){
		try{
			int indice=-1,i=0;
			String[] vetor = Naming.list("//"+ip);
			
			for(String str: vetor){
				if(str.endsWith("/registradora")){
					indice=i;
					break;
				}
				i++;
			}
			if(i==-1)
				return false;
			
			registradora = (RegistradoraInterface) Naming.lookup(vetor[indice]);
			
			return true;
		}
		catch(Exception e){
			return false;
		}
	}
	
	public static void main(String[] args) {
		
		try {
			servicos.add(new ServicoDeletarArquivos());
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
			agencia = new AgenciaDeletarArquivos();
			agencia.registro="//"+Utilidades.getInstance().ip()+":"+porta+"/"+nome;
			Naming.rebind("//"+Utilidades.getInstance().ip()+"/"+nome, agencia);
  		}
  		catch(Exception e){
  			System.out.println("Catch no registro");
  		}
		
		System.out.println("AgenciaDeletarArquivos Ligada!");
		
		while(!lerIpRegistradora()){
			System.out.println("\nNao foi possivel conectar a registradora!\n\nRepita o procedimento!");
		}
		
		System.out.println("\nConexao com a registradora realizada com sucesso!");	
		senders.add(registradora);
		
		
		try {
			if(registradora.registrarAgencia(agencia)){
				System.out.println("\nAgencia Registrada!\n");
				
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public ArrayList<ServicoInterface> getServicos() throws RemoteException{
		return servicos;
	}

	@Override
	public String getRegistro() {
		return registro;
	}

	@Override
	public void atualizarAgencias(SenderInterface s) {
		if(!senders.contains(s))
			senders.add(s);
		
	}
	

	@Override
	public void moverAgente(AgenteMovelInterface agenteMovel,
			Queue<AgenciaInterface> filaAgencias) throws RemoteException {
		
		if(filaAgencias==null || filaAgencias.size()==0){
			System.out.println("\nUltima agencia requisitada!\n");
			if(agenteMovel.voltarLancadora()){
				AgenteRetornarLancadoraInterface aux=(AgenteRetornarLancadoraInterface) agenteMovel;
				String caminhoLancadora=aux.getCaminhoLancadora();
				AgenciaLancadoraInterface lancadora = testarConexaoLancadora(caminhoLancadora);
				if(lancadora==null){
					System.out.println("\n*** Agente precisa retornar para a lancadora, mas nao foi possivel conectar a ela ***");
					System.out.println("Exibindo resultado de retorno nessa agencia...");
					aux.executarRetorno();
					return;
				}
				else{
					lancadora.receberAgenteRetorno(aux);
					return;
				}
			}
			else
				return;
		}
		else{
			AgenciaInterface agenciaDestino=null;
			try{
				agenciaDestino=filaAgencias.poll();
				if(agenciaDestino==null){
					System.out.println("\nProblema ao mover!\n");
					return;
				}
				System.out.println("\nMovendo agente para a agencia: "+agenciaDestino.getRegistro());
				
				if(!agenciaDestino.verificarCodigoAgente(nomeAgente)){
					System.out.println("A agencia: "+agenciaDestino.getRegistro()+" nao conseguiu obter o codigo do agente! Pulando...");
					moverAgente(agenteMovel, filaAgencias);
					return;
				}
				
				agenciaDestino.receberAgente(agenteMovel, filaAgencias);
				System.out.println("Moveu!\n");
				return;
			}
			catch(Exception e){
				if(e.toString().contains(erro)){
					System.out.println("Moveu!\n");
					return;
				}
				System.out.println("Nao foi possivel mover o agente!");
				moverAgente(agenteMovel, filaAgencias);
			}		
			return;
		}
		
	}
	
	public AgenciaLancadoraInterface testarConexaoLancadora(String caminhoLancadora){
		try{
			int indice=-1,i=0;
			String[] vetor = Naming.list(caminhoLancadora);
					
			for(String str: vetor){
				if(str.endsWith("/AgenciaLancadora")){
					indice=i;
					break;
				}
				i++;
			}
			if(i==-1)
				return null;
			
			
			AgenciaLancadoraInterface lancadora = null;
			
			lancadora=(AgenciaLancadoraInterface) Naming.lookup(vetor[indice]);
			
			return lancadora;
		}
		catch(Exception e){
			return null;
		}
	}
	
	
	
	@Override
	public void receberAgente(AgenteMovelInterface agenteMovel,
			Queue<AgenciaInterface> filaAgencias) throws RemoteException {

		for(ServicoInterface s: servicos){
			if(s.getDescricao().equals(agenteMovel.getServico())){
				agenteMovel.executarAgente(s);
				break;
			}
		}
		moverAgente(agenteMovel, filaAgencias);
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
	
	@Override
	public void receberArquivo(UtilitarioArquivo utilitario)
			throws RemoteException {
		
		System.out.println("\nRecebendo o arquivo: "+utilitario.getName());
		
		try{
	       	utilitario.writeTo(new FileOutputStream(diretorioAtual+"\\"+utilitario.getName() ) );
	       	System.out.println("Recebido!");
	       	
	    }catch( Exception e ){
	      e.printStackTrace();
	    }
		
	}
	
	@Override
	public SenderInterface procurarSender(String nomeArquivo)
			throws RemoteException {
				
		for(SenderInterface sen:senders){
			try{
				if(sen.verificarExistenciaArquivo(nomeArquivo))
					return sen;
			}
			catch(Exception e){
				
			}
		}
		
		return null;

	}
	

	public boolean verificarCodigoAgente(String nomeAgente) throws RemoteException {		
		File diretorio = new File(".");
		this.nomeAgente=nomeAgente;
		String[] arquivos = diretorio.list();
			
		for(String s:arquivos){
			if(s.equals(nomeAgente))
				return true;
		}
				
		SenderInterface sender=null;
		
		try {
			sender = null;
			sender = procurarSender(nomeAgente);
			if (sender == null) {
				System.out.println("\nNenhum sender com o arquivo: "
						+ nomeAgente);
				System.exit(0);
				return false;
			}
			sender.enviarArquivo(agencia, nomeAgente);
			return true;
		} catch (Exception e) {
			System.out.println("\nProblema na transferencia do arquivo: "
					+ nomeAgente);
			System.exit(0);
			return false;
		}
		
	}

}
