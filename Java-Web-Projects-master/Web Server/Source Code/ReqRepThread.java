

import java.io.*;
import java.util.Iterator;


public class ReqRepThread extends Thread{	
	
	//Fields
	
	//Request being dealt with
	private HTTPRequest hreq;
	
	//Response to handle req
	private HTTPResponse hrep;
	
	//I/O Streams for client
	private InputStream input;
    private OutputStream output;	
    
    //Int for counting request parameters and bool for if modified since
    private int hcount = 0;    
    private boolean mod = false;
    
	public ReqRepThread(InputStream in, OutputStream out){
		
		input = in;
		output = out;						
	}
	
	public void run(){
		
		//Reading/Writing (for requests & responses)
		BufferedReader in = new BufferedReader(new InputStreamReader(input));
		PrintWriter out = new PrintWriter(output);

		//Read in request
		try {
			hreq = getRequest(in);					
		} 	
		
		//Catch exception
		catch (IOException e1) {e1.printStackTrace();}	
		
		//Check if an if-modified-since header was included
		Iterator<String> i = hreq.getHeaderNames();		
		Iterator<String> l = hreq.getParameterNames();
		String date = "";
		
		//Check for If-Modified
		while(i.hasNext()){
			
			String h = i.next();
			if(h == "If-Modified-Since"){
				mod = true;
				break;
			}
			else{
				
				hcount = hcount + 1;
			}			
		}
		
		//If this header is present
		if(mod == true){
			
			//Retrieve the parameter date
			for(int p = 0; p<=hcount; p++){
				
				date = l.next();				
			}			
		}
		
		//Check if we should be loading a server
		if(ServletCheck.uricheck(hreq.getRequestURI()) == true){
			
			//Create a response for the servlet
			ServletResponse srep = new ServletResponse(output, out);
			
			//Try loading the servlet
			try {
				ServletCheck.load(hreq.getRequestURI(), hreq, srep);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}			
		}
		
		//Else send a regular response
		else{
			
			//Create a response to deal with this new request 		
			try {
				hrep = new HTTPResponse(output, hreq.getRequestURI(), date, mod);
			//Catch exceptions
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}					
					
			//Send a response 
			hrep.sendResponseHeader();		
		
		}
		//Close out when finished
		out.close();		
	}
	
	//Reads in GET request and parses 
	public HTTPRequest getRequest(BufferedReader in) throws IOException{
		
		//Read in first line of request
		String currentline = in.readLine();
		
		//String to be returned, contains entire request
		String request = "";		
		
		//While there is still something to be read
		while(currentline.isEmpty() == false){			
			
			//Add to request
			request = request + currentline + "\n";		
			
			//Read it in
			currentline = in.readLine();			
		}
					
		//Done - create and return request
		HTTPRequest hreq = new HTTPRequest(request);
		return hreq;
	}	

	//Parses GET request and returns file path
	public String parseRequest(){
		
		String path = "";	
		return path;
	}
}
