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


public class AgenciaLancadora extends UnicastRemoteObject implements AgenciaLancadoraInterface{

	private static final int porta=1099;
	private static final String nome="AgenciaLancadora";
	private static String registro="";	
	private static String diretorioAtual="";
	private static AgenciaLancadora agenciaLancadora=null;
	private static RegistradoraInterface registradora=null;
	private static AgenteMovelInterface agenteMovel=null;
	private static String nomeAgente="";
	
	
	public void atualizarAgencias(SenderInterface s) {
		if(!senders.contains(s))
			senders.add(s);	
	}
	
	protected AgenciaLancadora() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}
	

	@Override
	public String getRegistro() {
		return registro;
	}
	
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
	
	public static boolean lerNomeAgente(){
		String entrada = "";
		System.out.println("\nDigite o nome do agente movel: ");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("=> ");
		try {
			entrada=in.readLine();
		} catch (IOException e) {
			return false;
		}
		
		try {
			nomeAgente=entrada+".class";
			if(!agenciaLancadora.verificarCodigoAgente(nomeAgente)){
				System.out.println("\nAgencia nao conseguiu obter o codigo do agente! Encerrando...");
				System.exit(0);
			}
		} catch (RemoteException e) {
			System.out.println("\nAgencia nao conseguiu obter o codigo do agente! Encerrando...");
			System.exit(0);
		}
		
		try{
			
			agenteMovel=(AgenteMovelInterface) Class.forName(entrada).newInstance();
		}
		catch(Exception e){
			 return false;
		}
		
		return true;
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
	
	public boolean possiveisAgencias() {
		System.out.println("\nObtendo possiveis agencias para o servico \""+agenteMovel.getServico()+"\"\n");
		ArrayList<AgenciaInterface> agencias=null;
		try{
			agencias=registradora.agenciasServico(agenteMovel.getServico());
		}
		catch(Exception e){
			System.out.println("\nNao foi possivel obter a lista de agencias a partir da registradora!\n");
			return false;
		}
		
		if(agencias==null || agencias.size()==0){
			System.out.println("\nNenhuma agencia retornada para o servico \""+agenteMovel.getServico()+"\"\n");
			return false;
		}
		
		
		
		for(int i=0;i<agencias.size();i++){
			try {
				agencias.get(i).getRegistro();
				filaAgencias.add(agencias.get(i));
			} catch (RemoteException e) {
				agencias.remove(i);
				i--;
			}
			
		}
			
		if(filaAgencias.size()==0){
			System.out.println("\nNenhuma agencia encontrada para o servico \""+agenteMovel.getServico()+"\"\n");
			return false;
		}
		
		System.out.println("\n* Possiveis agencias para o servico \""+agenteMovel.getServico()+"\":");
		
		for(AgenciaInterface agencia: agencias){
			try {
				System.out.println(agencia.getRegistro());
			} catch (RemoteException e) {
				System.out.println("Problema com a agencia: "+agencia.getClass());
			}
		}
		
		
		if(agenteMovel.voltarLancadora()){
			AgenteRetornarLancadoraInterface aux = (AgenteRetornarLancadoraInterface) agenteMovel;
			aux.setCaminhoLancadora(registro);
			agenteMovel=aux;
		}
		
		return true;
		
	}
	
	public boolean lancarAgente(){
		AgenciaInterface agenciaDestino=null;
		try{
			
			if(filaAgencias.size()==0)
				return false;
					
			agenciaDestino=filaAgencias.poll();
			
			if(agenciaDestino==null){
				System.out.println("\nProblema!\n");
				return false;
			}
			
			System.out.println("\nLancando agente para a agencia: "+agenciaDestino.getRegistro());
			
			if(!agenciaDestino.verificarCodigoAgente(nomeAgente)){
				System.out.println("A agencia: "+agenciaDestino.getRegistro()+" nao conseguiu obter o codigo do agente! Pulando...");
				lancarAgente();
				return false;
			}
			
			agenciaDestino.receberAgente(agenteMovel, filaAgencias);
			return true;
		}
		catch(Exception e){
			System.out.println("Nao foi possivel enviar o agente!");
			lancarAgente();
		}		
		return false;
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
			agenciaLancadora = new AgenciaLancadora();
			agenciaLancadora.registro="//"+Utilidades.getInstance().ip()+":"+porta+"/"+nome;
			Naming.rebind("//"+Utilidades.getInstance().ip()+"/"+nome, agenciaLancadora);
  		}
  		catch(Exception e){
  			System.out.println("Catch no registro");
  		}
		
		System.out.println("AgenciaLancadora Ligada!");
		
		while(!lerIpRegistradora()){
			System.out.println("\nNao foi possivel conectar a registradora!\n\nRepita o procedimento!");
		}
		
		System.out.println("\nConexao com a registradora realizada com sucesso!");
		
		senders.add(registradora);
		
		try {
			if(registradora.registrarAgenciaLancadora(agenciaLancadora)){
				System.out.println("\nAgencia Registrada!\n");
				
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		while(!lerNomeAgente()){
			System.out.println("\nNao foi possivel encontrar o agente desejado!\n\nRepita o procedimento!");
		}
		
		if(!agenciaLancadora.possiveisAgencias()){
			System.exit(0);
		}
		else{
			agenciaLancadora.lancarAgente();
		}
		
	}

	@Override
	public void receberAgenteRetorno(AgenteRetornarLancadoraInterface agente)
			throws RemoteException {
			
		agente.executarRetorno();
		System.exit(0);
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
				System.out.println("Excecao 1 "+ e.toString());
				return null;
			}
		}
		
		return null;

	}
	

	public boolean verificarCodigoAgente(String nomeAgente) throws RemoteException {		
		File diretorio = new File(".");
		
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
			sender.enviarArquivo(agenciaLancadora, nomeAgente);
			return true;
		} catch (Exception e) {
			System.out.println("\nProblema na transferencia do arquivo: "
					+ nomeAgente);
			System.exit(0);
			return false;
		}
		
	}
	
	

}
