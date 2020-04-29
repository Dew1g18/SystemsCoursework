import com.sun.xml.internal.ws.wsdl.writer.document.Part;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Participant {
    private ParticipantLogger participantLogger;
    private int timeout;

    public static void main(String[] args){
        if(args.length!=4){
            System.out.println("To run a participant, the usage is currently:" +
                    "\n java Participant <participant port> <coordinator port> <timeout>");
        }
        else {
            try {
//                int coordinatorPort = Integer.parseInt(args[0]);
                int loggerPort = Integer.parseInt(args[1]);
                int partPort = Integer.parseInt(args[2]);
                int timeout = Integer.parseInt(args[3]);
                
                ParticipantLogger.initLogger(loggerPort, partPort, timeout);
                ParticipantLogger pl = ParticipantLogger.getLogger();
                Participant participant = new Participant(pl);

                participant.runParticipant(args[2], args[0],timeout);
                
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public Participant(ParticipantLogger pl){
            this.participantLogger = pl;
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
    ){ ;
        ListeningThread listeningThread = new ListeningThread(thisPortSocket, details.getOptions().length, participantLogger, this.timeout);
        VoteToken myvoteToken = voteTokenFromMap(sendInfo);
        VotingThread votingThread = new VotingThread(myvoteToken, details, participantLogger);
        votingThread.start();
        listeningThread.start();
        participantLogger.startedListening();
        boolean canProceed = false;
        int count = 0; //todo: This is here to stop the round after a certain about of time, not sure if this is required

        while(!canProceed){
            try {

                if(listeningThread.isFinishedCollecting()&&votingThread.isFinishedVoting()){
//                    System.out.println("finished collecting");
//                    System.out.println("Voting finished: "+votingThread.isFinishedVoting()+"    All collected: "+listeningThread.isFinishedCollecting());

                    canProceed=true;
                    listeningThread.setFinishedCollecting(true);
                }
                count++;
                if (count>4){
//                    System.out.println("Timeout Elapsed");

//                    System.out.println("Voting finished: "+votingThread.isFinishedVoting()+"    All collected: "+listeningThread.isFinishedCollecting());
                    canProceed = true;
//                    System.out.println(listeningThread.getCollectedVotes());
                    listeningThread.setFinishedCollecting(true);
                    votingThread.setFinishedVoting(true);
                }

                Thread.sleep(this.timeout/4);

            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }

        Map<String, String> newInfo = new HashMap<>();
        for(VoteToken voteToken : listeningThread.getCollectedVotes()){
//            System.out.println(voteToken.requirement);
            for(Vote vote: voteToken.voteArray){
                if(storedP2V.get(Integer.toString(vote.getParticipantPort()))==null){
                    newInfo.put(Integer.toString(vote.getParticipantPort()), vote.getVote());
//                    System.out.println(vote.toString());
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
        int minRuns = (details.options.length/3)+1;
//        while (storedP2V.keySet().size()<=details.getOptions().length){
        while(true){
            participantLogger.beginRound(i);
            sendInfo = round(thisPortSocket, details, sendInfo, storedP2V);
            storedP2V.putAll(sendInfo);
//            System.out.println("Round: "+i);
            participantLogger.endRound(i);
//            System.out.println("cont");
            i++;
//            System.out.println("Round "+i);
            if (i>minRuns){
//                System.out.println(minRuns+" rounds complete");
                return storedP2V;
            }
        }
//        return storedP2V;
    }

        //TODO: Work out the correct places to implement the ParticipantCrashed method..

    /**
     * substitute main for use in testing.
     * @param thisPort
     * @param coordPort
     * @param timeout
     */
    public void runParticipant(String thisPort, String coordPort, int timeout){
        try{
            this.timeout = timeout;
            DetailToken details = null;
            VoteOptionsToken voteOptions =null;

            int port = Integer.parseInt(thisPort);
            int coordinator = Integer.parseInt(coordPort);

            Socket socket = new Socket(InetAddress.getLocalHost(), coordinator);
//            System.out.println("Part opened socket at "+ coordinator);
            PrintWriter msgToCoord = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader  msgFromCoord = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            msgToCoord.println(("JOIN "+port));
            msgToCoord.flush();
            ParticipantLogger.getLogger().joinSent(Integer.parseInt(coordPort));
            ParticipantLogger.getLogger().messageSent(coordinator, "JOIN "+port);
            //todo: Find out if logger.messageSent is to be called for every kind of message.

            boolean moreToRead = true;
            TokenHandler tokenHandler = new TokenHandler();
            String incoming;

            /**
             * This gets the initial information from the coordinator
             */
            while(moreToRead){
                incoming = msgFromCoord.readLine();
                ParticipantLogger.getLogger().messageReceived(coordinator ,incoming);
//                System.out.println(incoming);

                Token token = tokenHandler.getToken(incoming);
                if(token instanceof DetailToken){
                    details = (DetailToken) token;
                    ParticipantLogger.getLogger().detailsReceived(details.detailsArray());
//                    System.out.println("Made a detail!");

                }else if(token instanceof  VoteOptionsToken){
                    voteOptions = (VoteOptionsToken) token;
                    ParticipantLogger.getLogger().voteOptionsReceived(voteOptions.voteOptionArray());
                }

                if (details!=null){
                    if(voteOptions!=null){
                    moreToRead=false;
                }}
            }

            ServerSocket thisPortSocket = new ServerSocket(Integer.parseInt(thisPort));
            Map<String, String> initialVote = new HashMap<>();


            //todo: do we know if votes are truely supposed to be random yet?
            Random randomInts = new Random();
            int voteNo = randomInts.nextInt(voteOptions.options.length);
            System.out.println(voteNo);
            String vote = voteOptions.options[voteNo];
            initialVote.put(thisPort, vote);
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

            Map<String, String> outcomeMap = roundRunner(initialVote, thisPortSocket, details);
//            System.out.println(outcomeMap.keySet()+ "\n"+ outcomeMap.values());
            OutcomeToken outcome = makeOutcome(outcomeMap);
            ParticipantLogger.getLogger().outcomeDecided(outcome.vote, outcome.portIDs());
            msgToCoord.println(outcome.requirement);
            msgToCoord.flush();
            ParticipantLogger.getLogger().outcomeNotified(outcome.vote, outcome.portIDs());
            ParticipantLogger.getLogger().messageSent(coordinator, outcome.requirement);

        }catch(IOException e){
            e.printStackTrace();
        }
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

    public VoteToken voteTokenFromMap(Map<String, String> tokenInfo){
        String newVote = "VOTE";
        for(String port : tokenInfo.keySet()){
            newVote+=" "+port+" "+tokenInfo.get(port);
        }
        TokenHandler tokenHandler = new TokenHandler();
        Token token = tokenHandler.getToken(newVote);
//        System.out.println("just the token" + token.requirement);
        VoteToken voteToken = null;
        if(token instanceof VoteToken){
            voteToken = (VoteToken) token;
        }
        return voteToken;
    }


}
