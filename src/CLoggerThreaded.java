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
        service.execute(()->
                CoordinatorLogger.getLogger().startedListening(port)
        );    }



    public void joinReceived(int participantId) {
        service.execute(()->
                CoordinatorLogger.getLogger().joinReceived(participantId)
        );    }



    public void detailsSent(int destinationParticipantId, List<Integer> participantIds) {
        service.execute(()->
                CoordinatorLogger.getLogger().detailsSent(destinationParticipantId, participantIds)
        );    }



    public void voteOptionsSent(int destinationParticipantId, List<String> votingOptions) {
        service.execute(()->
                CoordinatorLogger.getLogger().voteOptionsSent(destinationParticipantId, votingOptions)
        );    }



    public void outcomeReceived(int participantId, String vote) {
        service.execute(()->
                CoordinatorLogger.getLogger().outcomeReceived(participantId, vote)
        );    }


    public void connectionAccepted(int otherPort) {
        service.execute(()->
                CoordinatorLogger.getLogger().connectionAccepted(otherPort)
        );    }


    public void messageSent(int destinationPort, String message) {
        service.execute(()->
                CoordinatorLogger.getLogger().messageSent(destinationPort, message)
        );    }


    public void messageReceived(int senderPort, String message) {
        service.execute(()->
                CoordinatorLogger.getLogger().messageReceived(senderPort, message)
        );    }


    public void participantCrashed(int crashedParticipantId) {
        service.execute(()->
                CoordinatorLogger.getLogger().participantCrashed(crashedParticipantId)
        );    }
}

