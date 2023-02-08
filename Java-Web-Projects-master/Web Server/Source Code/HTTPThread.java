import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: nrjs
 * Date: Oct 26, 2005
 * Time: 3:33:01 PM
 * To change this template use File | Settings | File Templates.
 */

public class HTTPThread implements Runnable
{
    InputStream input;
    OutputStream output;

    HTTPThread(InputStream in, OutputStream out)
    {
        this.input = in;
        this.output = out;
    }

    
    public void run()
    {
        ReqRepThread s1 = new ReqRepThread(input, output);
        s1.run();
    }
}
