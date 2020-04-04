import java.io.*;
import java.net.Socket;

public class Coordinator {

    /**
     * First I'll need some variables that the coordinator will need to keep track of, like the chat server
     *
     * These will include:
     *      -Max Connections (given in spec as N connections)
     *      -Current number of connections, used to keep track of whether we've reached the max number
     *      -A collection of all the connections active, will use a map of names to sockets.
     */

    private class ServerThread extends Thread{
        private Socket clientSocket;
        private String clientName;
        private BufferedReader clientIn;
        private PrintWriter clientOut;

        ServerThread(Socket client) throws IOException {
            clientSocket= client;

            clientIn = new BufferedReader(new InputStreamReader(client.getInputStream()));
            clientOut = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));

        }

        /**
         * This will be automatically called on the creation of a new serverThread, and so it will be used to
         * register new processes to the coordinator.
         */

        public void run(){

        }
    }

}
