import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

//Builds and sends status codes - redirect / not found / 304 not modified
public class StatusCodes {
	
	//Date format of if-modified-since requests
	private static SimpleDateFormat format = new SimpleDateFormat("EEE, dd, MMM, yyyy HH:mm:ss zzz");
	
	public StatusCodes(PrintWriter p) {}
	
	//Writes a redirect to given path
	public static void redirect(PrintWriter p1, String rpath){
		
		p1.write("HTTP/1.1 303 See Other\n");
		p1.write("Location: " + rpath + "\n\n");		
		p1.flush();					
	}
	
	//Writes a 404 response
	public static void notFound(PrintWriter p1){		

		p1.write("HTTP/1.1 404 Not Found\n");
		p1.write("Content-Type: text/html\n\n");		
		p1.write("<html><body>404 - Not Found</body></html>");
		p1.flush();						
	}
	
	//Writes a 304 not modifed, lists 
	public static void notMod(PrintWriter p1, Date d){
				
		p1.write("HTTP/1.1 304 Not Modified\n");
		p1.write("Date: "+ d +"\n\n");
		p1.flush();			
		
	}
	
	//Reads in date from request header and checks against current
	//Takes in string from request header and date from file.modified
	public static boolean checkDate(Date date1, String date2) throws ParseException{
		
		Date d = format.parse(date2);		
		
		//date 1 is when file was modified
		//date 2 is if modified since
		if(d.compareTo(date1) == -1 ){
			
			//If we need to send not modified
			return true;			
		}
			//Otherwise
			return false;		
	}
}
