//Checks a URI and runs appropriate servlet
public class ServletCheck {
	
	
	public static void load(String URI, HTTPRequest req, ServletResponse rep) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		
		//Strip name from URI
		String name = URI.substring(9);
		
		//Does this servlet exist
		if(Class.forName(name) != null){
			
			Servlet serv = (Servlet) Class.forName(name).newInstance();			
			serv.service(req, rep);
		}			
	}
	
	public static boolean uricheck(String URI){
		
		if(URI.contains("/servlet/") == true){
			
			return true;
		}
		else{
			
			return false;
		}
		
	}
}
