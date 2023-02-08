import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;


//Provides static method for writing responses
public class ResponseWriter {

	public ResponseWriter() {}

	public static void writeResponse(File tosend, String type, PrintWriter p1, OutputStream output){
		
		try {
			
			DataInputStream is = new DataInputStream(new FileInputStream(tosend));
				
			//Get length of file
			int length = (int) tosend.length();
			//Create a byte array of this length
			byte[] buffer = new byte[length];
			//Read file into buffer
			is.readFully(buffer);
				
			//Write the response - Status OK
			p1.write("HTTP/1.1 200 OK\n");
			
			//Length of content
			p1.write("Content-Length: " + length + "\n");
			
			//Content type
			p1.write("Content-Type: " + type + "\n\n");
			
			//Flush stream
			p1.flush();
			
			//Write buffer
			output.write(buffer);
			
			//Flush
			output.flush();
			
			//Close					
			output.close();		
			is.close();
				
		} catch (IOException e) {					
			e.printStackTrace();
		}	
	}	
}
