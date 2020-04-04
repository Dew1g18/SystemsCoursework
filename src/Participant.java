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
        try{
            int port = Integer.parseInt(args[0]);
            int coordinator = Integer.parseInt(args[1]);

            ServerSocket listenSocket = new ServerSocket(port);
            Socket socket = new Socket(InetAddress.getLocalHost(), coordinator);
            //todo with PC, check to see if getlocalhost can create the sockets example connection between laptop and pc,
            //has to be done tomorrow as I dont want to wake anyone up with my pc rn.

            //todo: Use this socket to send the join request and get the information the coordinator is meant to send.
            //todo: Once the previous is complete, open 2 threads, one for looping through other ports and voting, the other for recieving others votes.






        }catch(Exception e){
            e.printStackTrace();
        }
    }




//    boolean joinCoordinator(){
//
//    }



}
