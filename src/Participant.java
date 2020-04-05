import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Participant {

    /**
     * Writing this here so I don't forget as I'm likely off to bed soon and my grasp on this is tenuous currently
     *
     * The way I understand these sockets currently is that the coordinator will have a port which it listens on,
     * the same as all the participants do, and each process in this simulation will have a ServerSocket
     *
     * Participants will have only one connection, this should be to the coordinator, who gives the required information
     * to vote and have the algorithm carried out, the coorinator serverSocket will maintain connections to each
     * participant, if I am to follow the chat server example closely this will have each one on a separate thread.
     *
     * participant, once it has the details of all other processes, will then use the list to vote, it will do this by
     * ###Loop through all details and vote to each of those, opening connections to them one at a time and voting###
     * Concurrently there should probably be another thread listening to pick up each of the other participants' votes
     * Not sure whether sending confirmation is actually neccesary, will have to go over the consensus slides again
     * but last time they didnt feel like they made a lot of sense without the lecture to go with them.
     *
     */


//    private int port;


    public static void main(String[] args){
        if(args.length!=2){
            System.out.println("To run a participant, the usage is currently: java Participant <participant port> <coordinator port>");
        }
        //todo: not sure if the method should actually just be run in main yet, wont take long to put back here if needed,
        //using method for testing.
    }

    public void runWithThese(String thisPort, String coordPort){
        try{
            DetailToken details = null;
            VoteOptionsToken voteOptions =null;

            int port = Integer.parseInt(thisPort);
            int coordinator = Integer.parseInt(coordPort);

            ServerSocket listenSocket = new ServerSocket(port);
            Socket socket = new Socket(InetAddress.getLocalHost(), coordinator);
//            System.out.println("Part opened socket at "+ coordinator);
            //todo with PC, check to see if getlocalhost can create the sockets example connection between laptop and pc,
            //has to be done tomorrow as I dont want to wake anyone up with my pc rn.
            PrintWriter msgToCoord = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader  msgFromCoord = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            System.out.println("Sedning");
            msgToCoord.println(("JOIN "+port));
            msgToCoord.flush();
//            System.out.println("message sent");
//            System.out.println(info.readLine());
            boolean moreToRead = true;
            TokenHandler tokenHandler = new TokenHandler();
            String incoming;
            while(moreToRead){
                incoming = msgFromCoord.readLine();
                System.out.println(incoming);
                Token token = tokenHandler.getToken(incoming);
                if(token instanceof DetailToken){
                    details = (DetailToken) token;
//                    System.out.println("Made a detail!");
                }else if(token instanceof  VoteOptionsToken){
                    voteOptions = (VoteOptionsToken) token;
                }
                if (details!=null){
                    if(voteOptions!=null){
                    moreToRead=false;
                }}
            }
            System.out.println("Details: ");
            for (String det : details.getOptions()){
                System.out.println(det);
            }
            System.out.println("\n\nVote Options: ");
            for (String opt : voteOptions.getOptions()){
                System.out.println(opt);
            }

            //Now that the detail token has been recieved, I can go ahead and create threads for voting and listening yeah?

            //todo: Test to see that I can actually connect with a coordinator, send & recieve this information
            //todo: set up rounds, to use the threads to get and send votes
            //todo: open 2 threads, one for looping through other ports and voting, the other for recieving others votes.
            //todo: send coordinator back an outcome token!


        }catch(IOException e){
            e.printStackTrace();
        }
    }




//    boolean joinCoordinator(){
//
//    }



}
