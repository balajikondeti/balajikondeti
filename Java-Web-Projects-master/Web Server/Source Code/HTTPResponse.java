
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Date;
import java.util.Properties;

//This class is responsible for building and sending http responses
public class HTTPResponse implements Response{

	//OutputStream
	private OutputStream output;	
	
	//PrintWriter for writing responses
	private PrintWriter print;
			
	//Properties for storing types/extensions - reverse of properties
	private Properties extensions;
	
	//Date Received
	private String date;
	//Do we need to check date (if modified)
	private boolean modified = false;
	//Path received 
	private String path;	
	
	//Server root
	private String root = "/space/doc/jdk/Tutorial/current"; 
		
	//Content-Type
	private String type;
	
	//Is a redirect needed
	private boolean redirect = false;	
	//Directory to list
	private boolean dir = false;
	
	//Sort directory
	private String sortdir = "Z";
	
	//Constructor	
	public HTTPResponse(OutputStream out, String reqpath, String date, boolean modified) throws FileNotFoundException, IOException{
		
		//Setup output stream
		output = out;
		
		//Setup PrintWriter and StatusCodes
		print = new PrintWriter(out);		
		
		//Date info
		this.modified = modified;
		this.date = date;
		
		//Rewrite path so it's compatible with root
		path = pathChange(reqpath);	
		
		//Setup properties - load in mime.types
		Properties prop = new Properties();
		FileInputStream fis = new FileInputStream("mime.types");
		
		prop.load(fis);
		fis.close();		
		
		//Now reverse properties so we can read content-types
		extensions = new Properties();		
		
		for (String s: prop.stringPropertyNames()){			
			extensions.setProperty(prop.getProperty(s), s);
		}		
	}			
	//Methods
	
	//Rewrites the path received from the request
	public String pathChange(String path){		
		
		String finalpath = path;
		
		//Check for Icon folder
		if(path.contains("icons/") == true){
		
			//Remove first slash and read from containing folder
			finalpath = finalpath.substring(1);
			return finalpath;
		}		
		
		//add root to the front
		finalpath = root + finalpath;
		
		//Clean up path
		if(finalpath.startsWith(root + root) == true){
			
			//Remove extra root (31 characters in length)
			finalpath = finalpath.substring(31);				
		}				
		
		//Check for an extension
		if(finalpath.contains(".") == false){
			
			//If path doesn't end with /
			if(finalpath.endsWith("/") == false){
				
				//Rewrite path for redirect
				//Ignore queries
				if(finalpath.contains("?") == true){}
				finalpath = finalpath + "/";				
				redirect = true;
			}	
		}		
	
		//Check paths ending with a /
		if(finalpath.endsWith("/") == true){			

			//File to check
			File f1 = new File(finalpath + "index.html");			
			
			//Check if index exists, if so add index.html
			if(f1.exists() == true){
			
				finalpath = finalpath + "index.html";							
			}
			
			//If there is no index, then leave as a directory
			else{				
				
				dir = true;	
			}			
		}						
		
		//Call for a redirect to be sent
		if(redirect == true){
			
			StatusCodes.redirect(print, finalpath);
		}
		
		//Check if the directory has to be sorted
		//Ending in ?N / ?M / ?S means dir is to be sorted
		if(finalpath.contains("?") && dir == true){
		
			//Read the final character
			sortdir = finalpath.substring(finalpath.length()-2, finalpath.length()-1);			
			
			//Remove query so path can be read
			finalpath = finalpath.substring(0, finalpath.length()-3);
		}				

		//Return final path
		return finalpath;		
	}		
	
	@Override
	public PrintWriter getWriter() {
		return print;
	}
		
	//Sets content Type
	public void getResponseType(String type) {		
		
		//Write own tables for directories, set content to html
		if(dir == true){
			
			setResponseType("text/html");
		}
		else{
		
			//First need to read file extension from path parameter		
			//Find index where "." is located
			int position = type.lastIndexOf(".");
			
			//Create substring from after this point (extension)
			String ext = type.substring(position+1);
			
			//Pass ext into Properties and set type as returned string	
			setResponseType((extensions.getProperty(ext)));		
		}
	}
	
	@Override
	//Sends response for a single file
	public void sendResponseHeader() {		
		
		//File to be sent, read in from path
		File tosend = new File(path);
		
		//To write response
		PrintWriter p1 = getWriter();	
		
		//First check if the file actually exists
		if(tosend.exists() == true){
			
			if(modified == true){
			
				//Check for if mod
				try {
					if(StatusCodes.checkDate(new Date(tosend.lastModified()), date) == true){
						
						StatusCodes.notMod(p1, new Date(tosend.lastModified()));
					}
				} catch (ParseException e1) {
					e1.printStackTrace();
				}			
			}
			//If it does but is marked as a directory
			if(dir == true){
								
				//Get response type
				getResponseType(path);
				
				//Create a list of these files
				File[] dirlist = tosend.listFiles();
				//Sort directory if needed
				dirlist = SortDir.sortDir(dirlist, sortdir);				
			
				try {
					
					//Pass to listDir which creates a html table of the directory
					DirectoryListWriter.listDir(dirlist, type, print);
						
				} catch (IOException e) {}								
			}							
			
			//If we're sending just a file
			if(dir == false){						
					
				//Set response type
				getResponseType(path);			
				//Send the response
				ResponseWriter.writeResponse(tosend, type, print, output);
			}		
		}		
		//Else the file doesn't exist - send a 404
		
		else{
			StatusCodes.notFound(p1);}
	}			
	
	public String getRoot(){
		return root;
	}
	
	//Returns path
	public String getPath(){		
		return this.path;		
	}
	
	//Sets response type (e.g. text/html)
	public void setResponseType(String val){
		this.type = val;
	}

	@Override
	public OutputStream getOutput() {
		return output;
	}
}
