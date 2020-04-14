import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Participant {



    public static void main(String[] args){
        if(args.length!=3){
            System.out.println("To run a participant, the usage is currently: " +
                    "\njava Participant <participant port> <coordinator port> <vote>");

        }
        //todo: not sure if the method should actually just be run in main yet, wont take long to put back here if needed,
        else {
            Participant participant = new Participant();
            participant.runWithThese(args[0], args[1], args[2]);
        }


        //using method for testing.
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

    private OutcomeToken makeOutcome(Map<String, String> outMap){
        String outcomeReq = "OUTCOME";
        Map<String, Integer> voteFreq = new HashMap<>();
        for(String vote: outMap.values()){
            if(voteFreq.get(vote)!=null){
                voteFreq.put(vote, voteFreq.get(vote)+1);
            }else{
                voteFreq.put(vote, 1);
            }
        }
        String maxVote = "";
        int maxFreq = 0;
        for(String vote:voteFreq.keySet()){
            if(voteFreq.get(vote)>maxFreq){
                maxFreq=voteFreq.get(vote);
                maxVote=vote;
            }
        }

        outcomeReq += " "+maxVote;
        for (String port: outMap.keySet()){
            outcomeReq+=" "+port;
        }
        return new OutcomeToken(outcomeReq);
    }


    /**
     * substitute main for use in testing.
     * @param thisPort
     * @param coordPort
     * @param voteStringPassedInForTest
     */
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
//            System.out.println(outcomeMap.keySet()+ "\n"+ outcomeMap.values());
            OutcomeToken outcome = makeOutcome(outcomeMap);
            msgToCoord.println(outcome.requirement);
            msgToCoord.flush();

            //Now that the detail token has been recieved, I can go ahead and create threads for voting and listening yeah?


            //todo: send coordinator back an outcome token!


        }catch(IOException e){
            e.printStackTrace();
        }
    }




}
