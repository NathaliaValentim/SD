import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.Serializable;


public class UtilitarioArquivo implements Serializable {

	private String nome;
	private String caminho;
	private byte[] dados;

	
	public UtilitarioArquivo(String nome, String caminho) {
		this.nome = nome;
		this.caminho = caminho;
	}

	
	public String getName() {
		return nome;
	}

	public void readIn() {
		try {
			File file = new File(caminho + "\\" + nome);
			dados = new byte[(int) (file.length())];
			(new FileInputStream(file)).read(dados);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void writeTo(OutputStream out) {
		try {
			out.write(dados);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}