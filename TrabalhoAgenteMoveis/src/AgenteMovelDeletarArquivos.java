import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class AgenteMovelDeletarArquivos implements AgenteRetornarLancadoraInterface{
	
	private String caminhoLancadora="";
	private String servicoDescricao="Deletar Arquivos";
	private String extensao="";
	private long espacoLivreAnterior=0;
	private long espacoLivrePosterior=0;
	private long espacoTotal=0;
	private int arquivosDeletados=0;
	
	public AgenteMovelDeletarArquivos(){
		while(!lerExtensao()){
			System.out.println("\nNao foi possivel ler a extensao!\n\nRepita o procedimento!");
		}
	}

	public void executarAgente(ServicoInterface servico) {
		ServicoDeletarArquivosInterface servicoDeletar = (ServicoDeletarArquivosInterface) servico; 
		servicoDeletar.executarServicoDeletar(this);		
	}

	@Override
	public String getServico() {
		return servicoDescricao;
	}

	@Override
	public void executarRetorno() {
		System.out.println("\n*** _______________________________________________________________ ***\n");
		System.out.println("Resumo do servico: \n");
		System.out.println("Quantidade de arquivos deletados: "+arquivosDeletados);
		System.out.println("Espaco total em disco (somando todas as maquinas): "+espacoTotal+" MB");
		System.out.println("Espaco livre (somando todas as maquinas - antes da exclusao): "+espacoLivreAnterior+" MB");
		System.out.println("Espaco livre (somando todas as maquinas - apos a exclusao): "+espacoLivrePosterior+" MB");
		System.out.println("Total deletado: "+(espacoLivrePosterior-espacoLivreAnterior)+" MB");
		System.out.println("\n*** _______________________________________________________________ ***\n");
			
	}
	
	public String getCaminhoLancadora() {
		return caminhoLancadora;
	}

	public void setCaminhoLancadora(String caminhoLancadora) {
		this.caminhoLancadora = caminhoLancadora;
	}

	@Override
	public boolean voltarLancadora() {
		return true;
	}
	
	public void atualizarValores(long espacoLivreAnterior,long espacoLivrePosterior,long espacoTotal,int arquivosDeletados){
		this.espacoLivreAnterior+=espacoLivreAnterior;
		this.espacoLivrePosterior+=espacoLivrePosterior;
		this.espacoTotal+=espacoTotal;
		this.arquivosDeletados+=arquivosDeletados;
	}
	
	public boolean lerExtensao(){
		String entrada = "";
		System.out.println("\nDigite a extensao dos arquivos a serem removidos: ");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("=> ");
		try {
			entrada=in.readLine();
		} catch (IOException e) {
			return false;
		}
		if(entrada==null || entrada.length()==0)
			return false;
		
		this.extensao=entrada;
		return true;
	}
	
	public String getExtensao(){
		return extensao;
	}

}
