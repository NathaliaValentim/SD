import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnviarArquivos {

	static ArrayList<String> enderecos = new ArrayList<String>();
	static String caminho ="\\Users\\Public\\Documents\\Trabalho SD\\";

	public static void enviarArquivos() {
		try {
			File origem = new File(".");
			for (String ip : enderecos) {
				File destino = new File("\\\\" + ip
						+ caminho);
				try {
					if (!deleteDiretorio(destino)) {
						System.out.println("Problema ao deletar diretorio em: "
								+ ip);
						continue;
					}
					destino.mkdir();
				} catch (Exception e) {
					System.out.println("Catch - Problema ao deletar diretorio em: "
							+ ip);
					continue;
				}
				
				try {
					
					for (String arquivos : origem.list()) {
						File arquivoOrigem = new File(origem.getCanonicalPath()
								+ "\\" + arquivos);
						File arquivoDestino = new File("\\\\" + ip
								+ caminho
								+ arquivos);
						
						if (!copiarArquivos(arquivoOrigem,arquivoDestino)){
							System.out.println("Catch - Problema ao copiar arquivos em: "
									+ ip);
							break;
						}

					}

				} catch (Exception e) {
					System.out.println("Catch - Problema ao copiar arquivos em: "
							+ ip);
					System.out.println(e.toString());
					break;
				}

			}
		} catch (Exception e) {
			System.out.println(e.toString());
		}

	}

	public static boolean deleteDiretorio(File dir) {

		if (!dir.exists()) {
			return true;
		}

		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				
				boolean success = false;
				try {
					success = deleteDiretorio(new File(dir, children[i]));
				} catch (Exception e) {
					return false;
				}
				if (!success) {
					return false;
				}
			}
		}
		// Agora o diretório está vazio, restando apenas deletá-lo.
		return dir.delete();
	}

	public static boolean copiarArquivos(File origem, File destino)
			throws IOException {

		FileChannel origemCanal = null;
		FileChannel destinoCanal = null;
		
		if(origem.isDirectory())
			return false;

		try {
			origemCanal = new FileInputStream(origem).getChannel();
			destinoCanal = new FileOutputStream(destino).getChannel();
			origemCanal.transferTo(0, origemCanal.size(), destinoCanal);
			if (origemCanal != null && origemCanal.isOpen())
				origemCanal.close();
			if (destinoCanal != null && destinoCanal.isOpen())
				destinoCanal.close();

			return true;
		} catch (Exception e) {
			return false;
		}

	}

	public static void main(String[] args) {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String entrada = "";
		System.out
				.println("Digite os enderecos de IP: (aperte ponto final para encerrar)");

		try {
			while (!entrada.equals(".")) {
				System.out.print("=> ");
				entrada = in.readLine();
				if(entrada.equals("."))
					break;
				if (!Utilidades.getInstance().validarIP(entrada)) {
					System.out.println("*** IP Invalido ***\n");
				} else {
					if (enderecos.contains(entrada)) {
						System.out.println("*** IP ja inserido ***\n");
					} else if (Utilidades.getInstance().ip().equals(entrada)) {
						System.out.println("*** IP local ***\n");
					} else {
						enderecos.add(entrada);
					}
				}

			}
			
			enviarArquivos();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("\nExecucao encerrada!\n");
		
		
	}

}
