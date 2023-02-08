import java.util.ArrayList;
import java.util.Iterator;


public class HTTPRequest implements Request {

	//Fields	
	//Header parameters, Mozilla/5.0 etc..
	private ArrayList<String> paramnames = new ArrayList<String>();	
	//Version of HTTP
	private String protocol;	
	//Host   
	private String host;	
	//List after the request line, Host: User-Agent: etc
	private ArrayList<String> headernames = new ArrayList<String>();	
	//Follows ? mark
	private String query = "";	
	//Resource being requested
	private String reqURI;	
	//Method to be used (GET etc)
	private String method;
	
	//Constructor - takes in a complete request and parses it, adding to relevant fields
	public HTTPRequest(String completeReq){
				
		int count = 1;
		
		//Split the string into an array, separated by newlines (UNIX and Windows characters)
		String[] lines = completeReq.split("\\r?\\n");
				
		//First line will be the request line, extract method, URI and protocol
		//Split the first line by spaces to give method, uri and protocol
		String[] spaces = lines[0].split("\\s+");
		
		//First of this array will be method 
		method = spaces[0];
		//Then URI
		reqURI = spaces[1];
		//Lastly Protocol
		protocol = spaces[2];
		
		//Check URI for any query values
		String[] query = reqURI.split("\\?+");
		
		//If there is a query, add it
		if(query.length > 1){
			this.query = query[1];
		}
		
		//Next are headers, record up to colon in headernames and after colon in parameters
		//Take the array consisting of each line of the request
		for(String s: lines){
			
			//Check for host
			if(s.contains("Host:")){host = s;}
									
			//Now, split each of these lines at the first space (this separates name and params)
			String[] lastsplit = s.split("\\s", 2);
			
			for(String h: lastsplit){
				
				//skip first two entries (request lines)
				if(h.contains("HTTP/1.1") || h.contains("GET")){h = "";}
				
				//On odd count add to headers
				if(count%2 == 1){		
					headernames.add(h);
					count++;
					
				}
				//Else add to param
				else if(count%2 == 0){
					paramnames.add(h);
					count++;
				}			
			}				
		}	
	}
		
	//Interface Methods
	@Override
	public String getParameter(String name) {
		
		String pname = null;			
		
		//Find requested parameter
		Iterator<String> i = getParameterNames();		
		while(i.hasNext()){
			
				pname = i.next();
								
				if (pname.equals(name)){
					return pname;				
				}
		}		
		
		return pname;		
	}

	@Override
	public Iterator<String> getParameterNames() {		
		Iterator<String> pnames = paramnames.iterator();
		return pnames;
	}

	@Override
	public String getProtocol() {
		return protocol;
	}

	@Override
	public String getHost() {
		return host;
	}

	@Override
	public String getHeader(String name) {
		String hname = "";			
		
		//Find requested Header
		Iterator<String> i = getParameterNames();
		Iterator<String> h = getHeaderNames();

		while(h.hasNext() && i.hasNext()){
			
			String cheader = h.next();
			String cparam = i.next();
			
			if(cheader == name){
				
				return cparam;
			}			
		}	
		
		return hname;
	}
	

	@Override
	public Iterator<String> getHeaderNames() {		
		Iterator<String> hnames = headernames.iterator();
		return hnames;
	}

	@Override
	public String getQueryString() {
		return query;
	}

	@Override
	public String getRequestURI() {
		return reqURI;
	}

	@Override
	public String getMethod() {
		return method;
	}
}
