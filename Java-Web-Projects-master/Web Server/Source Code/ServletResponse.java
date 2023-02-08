import java.io.OutputStream;
import java.io.PrintWriter;

//Response class containing only response details, doesn't automatically rewrite paths or redirect
public class ServletResponse implements Response{

	//Fields
	OutputStream os;
	PrintWriter pw;
	String type;
	
	public ServletResponse(OutputStream os, PrintWriter pw){
		
		this.os = os;
		this.pw = pw;
				
	}	
	
	@Override
	public OutputStream getOutput() {
		return os;
	}

	@Override
	public PrintWriter getWriter() {
		return pw;
	}

	@Override
	public void setResponseType(String type) {
		this.type = "";
		
	}

	@Override
	public void sendResponseHeader() {
		
		//OK
		pw.write("HTTP/1.1 200 OK\n");		
		//Content type
		pw.write("Content-Type: " + type + "\n\n");		
		//Flush stream
		pw.flush();
		
	}

}
