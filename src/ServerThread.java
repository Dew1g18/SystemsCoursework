import java.io.*;
import java.net.Socket;

class ServerThread extends Thread{
    private Socket portSocket;
    private String portName;
    private BufferedReader portIn;
    private PrintWriter portOut;

    ServerThread(Socket port) throws IOException {
        portSocket= port;

        portIn = new BufferedReader(new InputStreamReader(port.getInputStream()));
        portOut = new PrintWriter(new OutputStreamWriter(port.getOutputStream()));

    }

    /**
     * This will be automatically called on the creation of a new serverThread, and so it will be used to
     * register new processes to the coordinator.
     */

    public void run(){
        TokenHandler tokenHandler = new TokenHandler();
        try {
            Token token = tokenHandler.getToken(portIn.readLine());
            //todo: complete this method, its here to register a new socket and then run as a server thread
            //Not done yet because I want to go and have a look at how I plan on tackling the participants first

        }catch(IOException e){
            e.printStackTrace();

        }
    }
}