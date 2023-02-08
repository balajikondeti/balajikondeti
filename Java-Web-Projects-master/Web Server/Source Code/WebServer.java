import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by IntelliJ IDEA.
 * User: nrjs
 * Date: Oct 26, 2005
 * Time: 3:26:49 PM
 * To change this template use File | Settings | File Templates.
 */
@SuppressWarnings("unused")
public class WebServer
{
    public static void main(String[] args)
    {
        try
        {
        	//Creates a server on socket 8020
            ExecutorService scheduler = Executors.newCachedThreadPool();
            ServerSocket server = new ServerSocket(8020);
            while (true)
            {
            	
            	//Accepts connection and creates socket to client
                Socket client = server.accept();
                //Creates new thread and passes I/O streams
                Runnable r = new HTTPThread(client.getInputStream(), client.getOutputStream());
                scheduler.execute(r);               
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
