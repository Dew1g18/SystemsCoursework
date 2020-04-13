import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

    //This method is for finding the most common vote in a round.
//    private VoteToken winningVote(ArrayList<VoteToken> votes, String myPort){
//        TokenHandler tokenHandler = new TokenHandler();
//        Map<String, Integer> optionToHits = new HashMap<>();
//        for (VoteToken vote : votes){
//            if (optionToHits.get(vote.vote)!=null){
//                optionToHits.put(vote.vote,optionToHits.get(vote.vote)+1);
//            }else{
//                optionToHits.put(vote.vote,1);
//            }
//        }
//        VoteToken chosen = votes.get(0);
//        chosen.port=myPort;
//        for (String vote : optionToHits.keySet()){
//            if (optionToHits.get(vote)>optionToHits.get(chosen)){
//                chosen=tokenHandler.makeVote(myPort, vote);
//            }
//        }
//        return chosen;
//    }
    VoteToken stored;

    public Participant(){
        stored = new VoteToken("VOTE");
    }

    /**
     * following method runs a round. Should be able to handle any round, and comparing the previous
     * vVoteToken to the one returned should tell us whether we need to continue with rounds. .
     */
    public VoteToken roundDepricated(ServerSocket thisPortSocket, VoteToken myvoteToken, DetailToken details, Map<String, String> storedP2V){
        ListeningThread listeningThread = new ListeningThread(thisPortSocket, details.getOptions().length);
        VotingThread votingThread = new VotingThread(myvoteToken, details);
        listeningThread.start();
        votingThread.start();
        boolean canProceed = false;
        while(!canProceed){
            try {
                Thread.sleep(200);
                if(listeningThread.isFinishedCollecting()&&votingThread.isFinishedVoting()){
                    canProceed=true;
                }
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }

        for(Vote vote: myvoteToken.voteArray){
            storedP2V.put(Integer.toString(vote.getParticipantPort()), vote.getVote());
        }
        //Creating a requirement of all NEW vote information.
        String newReq = "VOTE";
        for(VoteToken voteToken: listeningThread.getCollectedVotes()){
            for (Vote vote : voteToken.voteArray){
                if(storedP2V.get(Integer.toString(vote.getParticipantPort()))==null){
                    newReq = newReq + vote.getParticipantPort() + vote.getVote();
                }else if(storedP2V.get(Integer.toString(vote.getParticipantPort()))!=vote.getVote() ){
                    newReq = newReq + vote.getParticipantPort() + vote.getVote();
                }else{
                    System.out.println("Already had it.");
                }
//                for (Vote existing : myvoteToken.voteArray){
//                    if (!vote.equals(existing)){
////                        newVote = new VoteToken(newVote.getRequirement() + vote.)
//                        newReq = newReq + vote.getParticipantPort() + vote.getVote();
//                    }
//                }
            }
        }
        //turn the newly constructed req into a voteToken to be returned!
        return new VoteToken(newReq);
    }

    public VoteToken voteTokenFromMap(Map<String, String> tokenInfo){
        String newVote = "VOTE";
        for(String port : tokenInfo.keySet()){
            newVote+=" "+port+" "+tokenInfo.get(port);
        }
//        System.out.println(newVote);
        TokenHandler tokenHandler = new TokenHandler();
        Token token = tokenHandler.getToken(newVote);
//        System.out.println("just the token" + token.requirement);
        VoteToken voteToken = null;
        if(token instanceof VoteToken){
            voteToken = (VoteToken) token;
        }
//        System.out.println(voteToken.requirement);
        return voteToken;
    }

    /**
     * @param thisPortSocket
     * @param details
     * @param sendInfo This is the previous round's returned newInfo, or on the initial run, its a map
     *                 containing the initial vote
     * @param storedP2V This is all the information stored so far, to check for new.
     * @return          This will return any new info, based on the total info supplied
     */
    public Map<String, String> round(
            ServerSocket thisPortSocket,
            DetailToken details,
            Map<String, String> sendInfo,
            Map<String,String> storedP2V
    ){
        ListeningThread listeningThread = new ListeningThread(thisPortSocket, details.getOptions().length);
        VoteToken myvoteToken = voteTokenFromMap(sendInfo);
        VotingThread votingThread = new VotingThread(myvoteToken, details);
        listeningThread.start();
        votingThread.start();
        boolean canProceed = false;
        int count = 0; //todo: This is here to stop the round after a certain about of time, not sure if this is required
        while(!canProceed){
            try {
                Thread.sleep(20);
                count++;
                if(listeningThread.isFinishedCollecting()&&votingThread.isFinishedVoting()||count>4){
                    canProceed=true;
                    listeningThread.setFinishedCollecting(true);
                }
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }

        Map<String, String> newInfo = new HashMap<>();
        for(VoteToken voteToken : listeningThread.getCollectedVotes()){
            for(Vote vote: voteToken.voteArray){
                if(storedP2V.get(Integer.toString(vote.getParticipantPort()))==null){
                    newInfo.put(Integer.toString(vote.getParticipantPort()), vote.getVote());
                }
            }
        }
        return newInfo;
    }

    /**
     *
     * @param sendInfo Initial vote
     * @param thisPortSocket
     * @param details
     * @return  All the info gathered over the process of running rounds however many times,
     * to be turned into an outcome and sent back to the coordinator.
     */
    public Map<String, String> roundRunner(Map<String, String> sendInfo, ServerSocket thisPortSocket, DetailToken details){
        Map<String, String> storedP2V = sendInfo;
        int j = 100;//Upper bound for number of bounds, hardcoded for now.
        int i = 0;
//        Map<String, String> newInfo = new HashMap<>();
        while (storedP2V.keySet().size()<details.getOptions().length){

            sendInfo = round(thisPortSocket, details, sendInfo, storedP2V);
            storedP2V.putAll(sendInfo);

            i++;
            if (i>j){
                return storedP2V;
            }
        }
        return storedP2V;
    }



    public static void main(String[] args){
        if(args.length!=2){
            System.out.println("To run a participant, the usage is currently: java Participant <participant port> <coordinator port>");
        }
        //todo: not sure if the method should actually just be run in main yet, wont take long to put back here if needed,
        //using method for testing.
    }

    public void runWithThese(String thisPort, String coordPort, String voteStringPassedInForTest){
        try{
            DetailToken details = null;
            VoteOptionsToken voteOptions =null;

            int port = Integer.parseInt(thisPort);
            int coordinator = Integer.parseInt(coordPort);

//            ServerSocket listenSocket = new ServerSocket(port);
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

            /**
             * This gets the initial information from the coordinator
             */
            while(moreToRead){
                incoming = msgFromCoord.readLine();
//                System.out.println(incoming);
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
//            System.out.println("Details: ");
//            for (String det : details.getOptions()){
//                System.out.println(det);
//            }
//            System.out.println("\n\nVote Options: ");
//            for (String opt : voteOptions.getOptions()){
//                System.out.println(opt);
//            }

//            Map<String, String> storedPortsToVotes = new HashMap<>();


            ServerSocket thisPortSocket = new ServerSocket(Integer.parseInt(thisPort));
            Map<String, String> initialVote = new HashMap<>();
            initialVote.put(thisPort, voteStringPassedInForTest);
//            System.out.println(thisPort+" "+voteStringPassedInForTest);

//            VoteToken round2 = round(thisPortSocket,round1vote, details, storedPortsToVotes);
            /**
             * Rounds plan
             * Take in stored info, everyone new info needs to be sent to, and ports..
             *
             * Send old info map.
             * get new info map
             *
             * Outside the rounds, roundRunner will send in new info every time, while storing a total list.
             * initial run: sends map with only vote, recieves newInfo
             * inbetween runs: adds newInfo to storedInfo
             * next run: sends newInfo, recieves (updated) newInfo. Repeat.
             *
             * This is how it works, leaving the comment here in case I ever need to go over it again.
             */

//            System.out.println(thisPort+"  "+ details.requirement);

            Map<String, String> outcomeMap = roundRunner(initialVote, thisPortSocket, details);
            System.out.println(outcomeMap.keySet()+ "\n"+ outcomeMap.values());

            //Now that the detail token has been recieved, I can go ahead and create threads for voting and listening yeah?


            //todo: send coordinator back an outcome token!


        }catch(IOException e){
            e.printStackTrace();
        }
    }




//    boolean joinCoordinator(){
//
//    }



}
