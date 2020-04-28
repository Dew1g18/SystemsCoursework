import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;

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

//    public class UDPServerThread extends Thread{
//        private DatagramSocket socket;
//
//        UDPServerThread(DatagramSocket socket) throws IOException{
//            this.socket = socket;
//        }
//
//        public void run(){
//
//        }
//
//    }

    //This was only written to understand how datagrams worked tbh, but I'm sure it'll come in handy
    public void acknowledge(SocketAddress socketAddress) throws IOException{
        byte[] buf = "ACK".getBytes();
        DatagramSocket socket = new DatagramSocket(socketAddress);
        serverSocket.send(new DatagramPacket(buf, buf.length, socket.getInetAddress(), socket.getLocalPort()));
    }

    public boolean log(DatagramPacket packet){
        String in = new String(packet.getData(), 0, packet.getLength());
        if (!Arrays.asList(receivedMessages).contains(in)){
            //todo here is where I need to handle the message that came in and actually log it lmao
            receivedMessages.add(in);
                return true;
        }else{
            return false;
        }
    }

    /** Will run this entire logger on one thread, as packets should wait on the line and if the delivery would
     *  fail, it will be sent again as is the nature of the udp process.
     */
    private void startListening(int port) throws IOException {
        DatagramSocket server = new DatagramSocket(port);
        byte[] buff = new byte[256];
        DatagramPacket packet = new DatagramPacket(buff, buff.length);
        while(true){
            // Logs and acknowledges packet IFF its not been received before
            server.receive(packet);
            if(log(packet)){
                acknowledge(packet.getSocketAddress());
            }
        }

    }

}
