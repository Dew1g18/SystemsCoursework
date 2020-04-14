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
    private ParticipantLogger pl;


    public ListeningThread(ServerSocket socket, int waitNo, ParticipantLogger pl){
        this.portToVote = new HashMap<>();
        this.collectedVotes = new ArrayList<>();
        this.listeningPort = socket;
        this.waitNo = waitNo;
        this.pl = pl;
    }

    public void setFinishedCollecting(boolean finishedCollecting) {
        this.finishedCollecting = finishedCollecting;
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
            while(!finishedCollecting) {
                voter = listeningPort.accept();
                try {
                voterInput = new BufferedReader(new InputStreamReader(voter.getInputStream()));
                String req= voterInput.readLine();
                Token token = tokenHandler.getToken(req);
//                System.out.println(req);
                if (token instanceof VoteToken){
                    VoteToken voteToken = (VoteToken) token;
                    System.out.println("/////////"+voter.getPort());
                    //todo: continue from implementation of votesRecieved

                    collectedVotes.add(voteToken);
                    System.out.println(voteToken.requirement+" added");
                    portToVote.put(voteToken.ports, voteToken.votes);
                    }
                    if (waitNo == collectedVotes.size()) {
                        finishedCollecting = true;
                    }
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public ArrayList<VoteToken> getCollectedVotes() {
        return collectedVotes;
    }


}
