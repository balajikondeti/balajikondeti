
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: nrjs
 * Date: Oct 26, 2005
 * Time: 9:13:11 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Response
{
	OutputStream getOutput();
    PrintWriter getWriter();
    void setResponseType(String type);
    void sendResponseHeader();
}
