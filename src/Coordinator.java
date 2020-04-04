import java.io.*;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Coordinator {

    /**
     * First I'll need some variables that the coordinator will need to keep track of, like the chat server
     *
     * These will include:
     *      -Min connections (the spec wants the coordinator to wait for a certain number of processes to join.
     *      -Max Connections (given in spec as N connections)
     *      -Current number of connections, used to keep track of whether we've reached the max number
     *      -A collection of all the connections active, will use a map of names to sockets.
     */
    private int minPorts = 10;
    //todo check if there is to be a max ports implemented, the spec as far as I've read only has a lower bound.

    private int numberOfConnections = 0;

    private Map<String, PrintWriter> stored = Collections.synchronizedMap(new HashMap<String, PrintWriter>());
    //todo: Above in the final set of parenthesis should be the maxPorts variable if one is to be implemented.






    private class ServerThread extends Thread{
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

            }catch(IOException e){
                e.printStackTrace();

            }
        }
    }

    /**
     * The following method attempts to register a new port with the coordinator, and returns a boolean
     * @return success?
     */
    boolean register(String port, PrintWriter out){
        //todo ??May need to apply a maxPorts connected bound, but for now the spec doesn't seem to have such a requirement.
        if (stored.containsKey((port))){
            System.err.println("Port already registered, what are you trying to pull?");
            return false;
        }
        try{
            stored.put(port, out);
        }catch(NullPointerException e) {
            System.out.println("THE NULL POINTER YOU WERENT EXPECTING THAT WAS BROUGHT FROM THE CHAT SERVER WAS TRIPPED, PLEASE INSPECT!!!");
            return false;
        }
        this.numberOfConnections++;
        return true;
    }

    //Just to ensure that the map and number of connections stay updated in the event of a process becoming unregistered.
    void unregister(String port){
        stored.remove(port);
        this.numberOfConnections--;

    }



}
