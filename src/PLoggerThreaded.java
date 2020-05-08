import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class PLoggerThreaded{

    /**
     * This class copies all of the participant logger methods and submits them as threads to
     * an excecutor pool of 1 to be queued for completion outside of priority processes.
     *
     * ***Problem with this may turn out that the logging no longer gets executed when it's meant to
     *    if the udpLogger takes more time than it should***
     */

    private ExecutorService service;

    public PLoggerThreaded( ) {
        this.service = Executors.newFixedThreadPool(3);
    }


    public void joinSent(int coordinatorId) {
        service.submit(()->
            ParticipantLogger.getLogger().joinSent(coordinatorId)
        );
    }


    public void detailsReceived(List<Integer> participantIds) {
        service.submit(()->
                ParticipantLogger.getLogger().detailsReceived(participantIds)
        );
    }



    public void voteOptionsReceived(List<String> votingOptions) {
        service.submit(()->
                ParticipantLogger.getLogger().voteOptionsReceived(votingOptions)
        );
    }



    public void beginRound(int round) {
        service.submit(()->
                ParticipantLogger.getLogger().beginRound(round)
        );
    }


    public void endRound(int round) {
        service.submit(()->
                ParticipantLogger.getLogger().endRound(round)
        );
    }


    public void votesSent(int destinationParticipantId, List<Vote> votes) {
        service.submit(()->
                ParticipantLogger.getLogger().votesSent(destinationParticipantId,votes)
        );
    }


    public void votesReceived(int senderParticipantId, List<Vote> votes) {
        service.submit(()->
                ParticipantLogger.getLogger().votesReceived(senderParticipantId, votes)
        );
    }


    public void outcomeDecided(String vote, List<Integer> participantIds) {
        service.submit(()->
                ParticipantLogger.getLogger().outcomeDecided(vote, participantIds)
        );
    }


    public void outcomeNotified(String vote, List<Integer> participantIds) {
        service.submit(()->
                ParticipantLogger.getLogger().outcomeNotified(vote, participantIds)
        );
    }


    public void participantCrashed(int crashedParticipantId) {
        service.submit(()->
                ParticipantLogger.getLogger().participantCrashed(crashedParticipantId)
        );
    }


    public void startedListening() {
        service.submit(()->
                ParticipantLogger.getLogger().startedListening()
        );
    }


    public void connectionAccepted(int otherPort) {
        service.submit(()->
                ParticipantLogger.getLogger().connectionAccepted(otherPort)
        );
    }


    public void connectionEstablished(int otherPort) {
        service.submit(()->
                ParticipantLogger.getLogger().connectionEstablished(otherPort)
        );
    }


    public void messageSent(int destinationPort, String message) {
        service.submit(()->
                ParticipantLogger.getLogger().messageSent(destinationPort, message)
        );
    }


    public void messageReceived(int senderPort, String message) {
        service.submit(()->
                ParticipantLogger.getLogger().messageReceived(senderPort, message)
        );
    }
}

