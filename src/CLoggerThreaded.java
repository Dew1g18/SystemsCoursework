import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class CLoggerThreaded {

    private ExecutorService service;

    public CLoggerThreaded(){
        this.service = Executors.newFixedThreadPool(3);
    }

    public void startedListening(int port) {
        service.submit(()->
                CoordinatorLogger.getLogger().startedListening(port)
        );    }



    public void joinReceived(int participantId) {
        service.submit(()->
                CoordinatorLogger.getLogger().joinReceived(participantId)
        );    }



    public void detailsSent(int destinationParticipantId, List<Integer> participantIds) {
        service.submit(()->
                CoordinatorLogger.getLogger().detailsSent(destinationParticipantId, participantIds)
        );    }



    public void voteOptionsSent(int destinationParticipantId, List<String> votingOptions) {
        service.submit(()->
                CoordinatorLogger.getLogger().voteOptionsSent(destinationParticipantId, votingOptions)
        );    }



    public void outcomeReceived(int participantId, String vote) {
        service.submit(()->
                CoordinatorLogger.getLogger().outcomeReceived(participantId, vote)
        );    }


    public void connectionAccepted(int otherPort) {
        service.submit(()->
                CoordinatorLogger.getLogger().connectionAccepted(otherPort)
        );    }


    public void messageSent(int destinationPort, String message) {
        service.submit(()->
                CoordinatorLogger.getLogger().messageSent(destinationPort, message)
        );    }


    public void messageReceived(int senderPort, String message) {
        service.submit(()->
                CoordinatorLogger.getLogger().messageReceived(senderPort, message)
        );    }


    public void participantCrashed(int crashedParticipantId) {
        service.submit(()->
                CoordinatorLogger.getLogger().participantCrashed(crashedParticipantId)
        );    }
}

