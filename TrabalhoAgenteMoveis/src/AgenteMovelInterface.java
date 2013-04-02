import java.io.Serializable;


public interface AgenteMovelInterface extends Serializable {
	public String getServico();
	public void executarAgente(ServicoInterface servico);
	public boolean voltarLancadora();
}
