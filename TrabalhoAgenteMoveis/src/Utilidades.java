import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Utilidades {

	private static Utilidades instance = new Utilidades();
	
	public static Utilidades getInstance(){
		return instance;
	}
	
	
	private Utilidades(){
		
	}
	
	public boolean validarIP (String ip){
		String IPADDRESS_PATTERN = 
				"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
				"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
				"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
				"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
		Pattern pattern;
	    Matcher matcher;
	    pattern = Pattern.compile(IPADDRESS_PATTERN);
	    matcher = pattern.matcher(ip);
		return matcher.matches();
		
	}
	
	public String ip(){
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			return null;
		}
	}
	
	

}
