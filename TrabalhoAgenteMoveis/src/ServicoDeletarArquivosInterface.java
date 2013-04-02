import java.util.ArrayList;


public interface ServicoDeletarArquivosInterface extends ServicoInterface {
	String descricao="Deletar Arquivos";
	public void executarServicoDeletar(AgenteMovelDeletarArquivos agente);
	public void setExtensao(String extensao);
	public String getExtensao();
	ArrayList<String> enderecosVisitados = new ArrayList<String>();
}
