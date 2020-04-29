import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;

public class VotingThread extends Thread {

//    private Socket mySocket;
    private VoteToken voteToken;
    private boolean finishedVoting;
    private ParticipantLogger pl;

    public boolean isFinishedVoting() {
        return finishedVoting;
    }

    private DetailToken details;

    public VotingThread(VoteToken voteToken, DetailToken detailToken, ParticipantLogger pl){
//        this.mySocket = socket;
        this.voteToken = voteToken;
//        System.out.println(voteToken.requirement);
        this.details = detailToken;
        finishedVoting = false;
        this.pl = pl;
    }

    public void setFinishedVoting(boolean finishedVoting) {
        this.finishedVoting = finishedVoting;
    }

    public void run(){
//        System.out.println(voteToken.requirement);
//        try{
//            this.sleep(1000);
//            }catch(InterruptedException e){
//            e.printStackTrace();
//        }
//        try {
            for (String port : details.getOptions()) {

//                while(true) {
                    int portInt = Integer.parseInt(port);

                    new Thread(()->voteOnPort(portInt)).start();

//                    try {
//                        socket = new Socket(InetAddress.getLocalHost(), portInt);
//                        PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
//                        pl.connectionEstablished(portInt);
//                        out.println(voteToken.requirement);
//                        out.flush();
//                        pl.votesSent(portInt, voteToken.voteArrayList());
//                        pl.messageSent(portInt, voteToken.requirement);
////                        System.out.println(voteToken.ports + "VoteToken");
//                        break;
//                    }catch(Exception e){
//                        pl.participantCrashed(portInt);
//                        if(finishedVoting){
//                            break;
//                        }
//                        System.out.println("Failed connection");
//                        continue;
//                    }
//                }
            }
            finishedVoting=true;
//        }catch (IOException e){
//            e.printStackTrace();
//        }

    }

    public boolean voteOnPort(int portInt) {
        boolean notConnected = true;
        while (notConnected) {
            try {
                Socket socket = new Socket(InetAddress.getLocalHost(), portInt);
                PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                notConnected = false;
                pl.connectionEstablished(portInt);
                out.println(voteToken.requirement);
                out.flush();
                pl.votesSent(portInt, voteToken.voteArrayList());
                pl.messageSent(portInt, voteToken.requirement);
//                        System.out.println(voteToken.ports + "VoteToken");
                return false;
            } catch (Exception e) {
//                e.printStackTrace();
                pl.participantCrashed(portInt);
                if (finishedVoting) {
                    return false;
                }
                System.out.println("Failed connection");
                return true;
            }
        }
        return true;
    }


}
