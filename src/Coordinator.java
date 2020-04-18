import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Coordinator {

    public void setMinPorts(int minPorts) {
        this.minPorts = minPorts;
    }

    /**
     * First I'll need some variables that the coordinator will need to keep track of, like the chat server
     *
     * These will include:
     *      -Min connections (the spec wants the coordinator to wait for a certain number of processes to join.
     *      -Max Connections (given in spec as N connections)
     *      -Current number of connections, used to keep track of whether we've reached the max number
     *      -A collection of all the connections active, will use a map of names to sockets.
     */
    private int minPorts;

    private int numberOfConnections = 0;

    private Map<String, PrintWriter> stored = Collections.synchronizedMap(new HashMap<String, PrintWriter>());

    private String voteOptionsSave;

    public static void main(String[] args){
        int argMinPorts = Integer.parseInt(args[2]);
        int coordPort = Integer.parseInt(args[0]);

        //todo; work out how they expect us to implement the coordinators timeouts
        int timeout = Integer.parseInt(args[3]);

        int loggerPort = Integer.parseInt(args[1]);

        String voteOptions = "VOTE_OPTIONS";
        for (int i = 4; i<args.length;i++){
            voteOptions+=args[i];
        }
        System.out.println(voteOptions);
        try{
            Coordinator coordinator = new Coordinator(argMinPorts, voteOptions);
            coordinator.startListening(coordPort);
            CoordinatorLogger.initLogger(loggerPort, coordPort,timeout);

        }catch(IOException e){
            System.out.println("Coordinator IOException?");
            e.printStackTrace();
        }


    }

    public Coordinator(int minPorts, String voteOptions) {
        this.minPorts = minPorts;
        this.voteOptionsSave = voteOptions;
    }

    public void startListening(int port) throws IOException {
        ServerSocket listener = new ServerSocket(port);
        CoordinatorLogger.getLogger().startedListening(port);
//        System.out.println("server socket open");
        while (true) {
            Socket client = listener.accept();

            int senderParticipant = Integer.parseInt(client.getLocalSocketAddress().toString().split(":")[1]);
            CoordinatorLogger.getLogger().connectionAccepted(senderParticipant);
//            System.out.println("Socket accepted");
            //todo: use timeout on the join request?
            new ServerThread(client).start();
        }
    }


    private class ServerThread extends Thread{
        private Socket portSocket;
        private String portName;
        private BufferedReader portIn;
        private PrintWriter portOut;

        ServerThread(Socket port) throws IOException {
            portSocket= port;

            portIn = new BufferedReader(new InputStreamReader(port.getInputStream()));
            portOut = new PrintWriter(new OutputStreamWriter(port.getOutputStream()));
//            System.out.println("ServerThread started");

        }

        /**
         * This will be automatically called on the creation of a new serverThread, and so it will be used to
         * register new processes to the coordinator.
         */

        public void run(){
//            System.out.println("Runnin");
            TokenHandler tokenHandler = new TokenHandler();
            try {
                String in = portIn.readLine();
//                System.out.println(in);
                Token token = tokenHandler.getToken(in);
                System.out.println(token.requirement);
//                String thisPort = "eh";
                //todo: complete this method, its here to register a new socket and then run as a server thread
                //In its current state it will connect to a single participant and send some dummy data (for testing)
                if (!(token instanceof JoinToken)) {
                    portSocket.close();
                    return;
                }
                String thisPort = ((JoinToken) token).port;
                // Check the client's registration request.
                if (!(register(portName = ((JoinToken) token).port, portOut))) {
                    portSocket.close();
                    return;
                }
                while(true){
                    if(numberOfConnections>=minPorts){
//                        System.out.println("Saturated");
                        break;
                    }else{
                        try {
                            this.sleep(1000);
                        }catch(InterruptedException e){
                            e.printStackTrace();
                        }
                    }
                }
                String details = "DETAILS";

                for(String port: stored.keySet() ){ ;
                    if(!thisPort.equals(port)) {
                        details += " " + port;
                    }
                }
                //This uses an integer vote from 0 to 5

                portOut.println(details);

                String[] detailList = details.split(" ");
                List<Integer> detInts = new ArrayList<>();
                for(int i=1;i<detailList.length;i++){
                    detInts.add(Integer.parseInt(detailList[i]));
                }
                int intPort = Integer.parseInt(thisPort);
                CoordinatorLogger.getLogger().detailsSent(intPort, detInts);

                portOut.println(voteOptionsSave);

                voteOptionsSave.replaceFirst("VOTE_OPTIONS ", "");
                List<String> votesSplit = new ArrayList<>();
                for(String v:  voteOptionsSave.split(" ")){
                    votesSplit.add(v);
                }
                CoordinatorLogger.getLogger().voteOptionsSent(intPort, votesSplit);

                portOut.flush();

                String hopeOutcome= portIn.readLine();
                System.out.println(hopeOutcome);
                Token newToken = tokenHandler.getToken(hopeOutcome);
                if(newToken instanceof OutcomeToken){
                    OutcomeToken outcome = (OutcomeToken) newToken;
                    CoordinatorLogger.getLogger().outcomeReceived(intPort,outcome.vote);
                }


                portSocket.close();

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
        if (stored.containsKey((port))){
            System.err.println("Port already registered, what are you trying to pull?");
            return false;
        }
        try{
            stored.put(port, out);
        }catch(NullPointerException e) {
            System.out.println("THE NULL POINTER YOU WERENT EXPECTING THAT WAS BROUGHT FROM THE CHAT SERVER WAS TRIPPED, PLEASE INSPECT!!!");
            System.out.println("Coordinator, register method");
            return false;
        }
        CoordinatorLogger.getLogger().joinReceived(Integer.parseInt(port));
        this.numberOfConnections++;
        return true;
    }

    //Just to ensure that the map and number of connections stay updated in the event of a process becoming unregistered.
    void unregister(String port){
        stored.remove(port);
        this.numberOfConnections--;

    }



}
