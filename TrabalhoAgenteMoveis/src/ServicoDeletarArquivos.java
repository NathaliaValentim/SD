import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class ServicoDeletarArquivos implements ServicoDeletarArquivosInterface{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String extensao="";
	

	public String getExtensao() {
		return extensao;
	}

	public void setExtensao(String extensao) {
		this.extensao = extensao;
	}

	@Override
	public String getDescricao() {
		return descricao;
	}

	@Override
	public void executarServicoDeletar(AgenteMovelDeletarArquivos agente) {
		extensao=agente.getExtensao();
		System.out.println("\n*** _______________________________________________________________ ***\n");
		System.out.println("Executando servico deletar arquivos na agencia...\n");
		
		long espacoLivreAnteriorLocal=0;
		long espacoLivrePosteriorLocal=0;
		long espacoTotal=0;
		
		File diretorioAtual=new File(".");
		String caminho="";
		try {
			caminho = Utilidades.getInstance().ip()+"\\"+diretorioAtual.getCanonicalPath();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if(!enderecosVisitados.contains(caminho)){
			enderecosVisitados.add(caminho);
		}
		else{
			System.out.println("Agente ja passou por essa maquina e tentou realizar o servico!");
			System.out.println("\n*** _______________________________________________________________ ***\n");
			return;
		}
		
		File disco=null;
		try {
			disco = new File(diretorioAtual.getCanonicalPath().substring(0,3));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		espacoTotal= disco.getTotalSpace()/(1024*1024);
		espacoLivreAnteriorLocal=disco.getFreeSpace()/(1024*1024);
		
	
		int i=0;
		for(File f:diretorioAtual.listFiles()){
			try {
				if (!f.isDirectory()) {
					if (f.getName().endsWith("." + extensao)) {
						f.delete();
						System.out.println("-> "+f.getName()+" deletado!");
						i++;
					}
				}
			} catch (Exception e) {

			}
		}
		
		espacoLivrePosteriorLocal= disco.getFreeSpace()/(1024*1024);
		
		agente.atualizarValores(espacoLivreAnteriorLocal,espacoLivrePosteriorLocal,espacoTotal,i);
		
		if(i==0){
			System.out.println("\nNenhum arquivo com a extensao requisitada foi encontrado!");
		}
		else if(i==1){
			System.out.println("\nServico executado!\n"+i+" arquivo deletado!");
			System.out.println((espacoLivrePosteriorLocal-espacoLivreAnteriorLocal)+" MB removidos!");
		}
		else{
			System.out.println("\nServico executado!\n"+i+" arquivos deletados!");
			System.out.println((espacoLivrePosteriorLocal-espacoLivreAnteriorLocal)+" MB removidos!");
		}
		
		System.out.println("\n*** _______________________________________________________________ ***\n");
		
	}
			
}
