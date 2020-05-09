import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

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
    private PrintStream ps;

    public static void main(String[] args){
        int port = Integer.parseInt(args[0]);
        UDPLoggerServer loggerServer = new UDPLoggerServer(port);
        loggerServer.startListening();

    }

    public UDPLoggerServer(int listenPort){
        this.listenPort = listenPort;
        receivedMessages = new ArrayList<>();

        try {
            this.serverSocket = new DatagramSocket(this.listenPort);
            this.ps = new PrintStream("logger_server_"+ System.currentTimeMillis()+".log");
        }catch(IOException e){
            this.serverSocket = null;//Fix? not sure what to do here really.
            System.out.println("LOGGER SERVER INITIALISATION FAILED");
            e.printStackTrace();
        }
    }



    //This was only written to understand how datagrams worked tbh, but I'm sure it'll come in handy
    public void acknowledge(DatagramPacket packet) throws IOException{
        byte[] buf = "ACK".getBytes();
        serverSocket.send(new DatagramPacket(buf, buf.length, packet.getAddress(), packet.getPort()));
    }

    /**
     *
     * @param packet
     * @return true for new packets, false for duplicates
     */
    public boolean log(DatagramPacket packet){
        String in = new String(packet.getData(), 0, packet.getLength());

//        System.out.println("Logging packet!! "+in);

        if (!Arrays.asList(receivedMessages).contains(in)){
            receivedMessages.add(in);
            ps.println(formatLine(in));
            return true;
        }else{
            return false;
        }
    }

    public String formatLine(String message){
        StringTokenizer tokenizer = new StringTokenizer(message);
        String line = tokenizer.nextToken()+" "+System.currentTimeMillis();
        int left = tokenizer.countTokens();
        for(int i=0;i<left;i++){
            line+=" "+tokenizer.nextToken();
        }
        return line;
    }

    /** Will run this entire logger on one thread, as packets should wait on the line and if the delivery would
     *  fail, it will be sent again as is the nature of the udp process.
     */
    private void startListening(){
        byte[] buff = new byte[256];
        DatagramPacket packet = new DatagramPacket(buff, buff.length);
        while(true){
            // Logs and acknowledges packet IFF its not been received before
            try {
                this.serverSocket.receive(packet);
                if (log(packet)) {
                    acknowledge(packet);
                }
            }catch(IOException e){
//                e.printStackTrace();
            }
        }

    }

}
