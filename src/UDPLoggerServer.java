import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

/**
 * Writing this to get my head straight about how this is going to work.
 *
 * What does this server have to do:
 *      -Get messages from places and log them all somewhere
 *      -Send acknowledgement reciepts
 *      -bin duplicates
 */

public class UDPLoggerServer {
    private int listenPort;
    private DatagramSocket serverSocket;
    private ArrayList<String> receivedMessages;

    public static void main(String[] args){
        int port = Integer.parseInt(args[0]);
        UDPLoggerServer loggerServer = new UDPLoggerServer(port);

    }

    public UDPLoggerServer(int listenPort){
        this.listenPort = listenPort;
        receivedMessages = new ArrayList<>();
        try {
            this.serverSocket = new DatagramSocket(listenPort);
        }catch(IOException e){
            this.serverSocket = null;//Fix? not sure what to do here really.
            e.printStackTrace();
        }
    }

    public class UDPServerThread extends Thread{
        private DatagramSocket socket;

        UDPServerThread(DatagramSocket socket) throws IOException{
            this.socket = socket;
        }

        public void run(){

        }

    }

    //This was only written to understand how datagrams worked tbh, but I'm sure it'll come in handy
    public void acknowledge(DatagramSocket socket) throws IOException{
        byte[] buf = "ACK".getBytes();
        serverSocket.send(new DatagramPacket(buf, buf.length, socket.getInetAddress(), socket.getLocalPort()));
    }

    private void startListening(int port) throws IOException {
        /**
         * This nees
         */
        DatagramSocket server = new DatagramSocket(port);

        while(true){
//            server.receive();
        }

    }

}
