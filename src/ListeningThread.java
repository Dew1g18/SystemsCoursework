import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListeningThread extends Thread {

    private ServerSocket listeningPort;
    private int waitNo;
    private ArrayList<VoteToken> collectedVotes;
    private boolean finishedCollecting;
    private Map<String[], String[]> portToVote;


    public ListeningThread(ServerSocket socket, int waitNo){
        this.portToVote = new HashMap<>();
        this.listeningPort = socket;
        this.waitNo = waitNo;
    }

    public boolean isFinishedCollecting() {
        return finishedCollecting;
    }

    public Map<String[], String[]> getPortToVote() {
        return portToVote;
    }

    public void run(){
        //todo: This is going to be the class which gives the participant
        try {
            Socket voter;
            BufferedReader voterInput;
            TokenHandler tokenHandler = new TokenHandler();


            /**
             * This method now waits for the right number of votes from distinct ports to come in
             * it places them in 2 forms and has a boolean for the object that uses this to know when
             * the set of votes is complete.
             */
            while(!finishedCollecting){
                voter = listeningPort.accept();
                voterInput = new BufferedReader(new InputStreamReader(voter.getInputStream()));

                Token token = tokenHandler.getToken(voterInput.readLine());
                if (token instanceof VoteToken){
                    VoteToken voteToken = (VoteToken) token;
                    if (portToVote.get(voteToken.port)!=null ){
                        collectedVotes.add(voteToken);
                        portToVote.put(voteToken.port, voteToken.vote);
                    } }
                if(waitNo==collectedVotes.size()){
                    finishedCollecting = true;

                } }
        }catch(IOException e){
            e.printStackTrace();
        }
    }


    public ArrayList<VoteToken> getCollectedVotes() {
        return collectedVotes;
    }

    public void close(){

    }

}
