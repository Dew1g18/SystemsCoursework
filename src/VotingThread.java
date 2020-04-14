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

    public void run(){
        Socket socket;
        try{
            this.sleep(1000);
            }catch(InterruptedException e){
            e.printStackTrace();
        }
        try {
            for (String port : details.getOptions()) {
                while(true) {
                    try {
                        int portInt = Integer.parseInt(port);
                        socket = new Socket(InetAddress.getLocalHost(), portInt);
                        PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                        out.println(voteToken.requirement);
                        out.flush();
                        pl.votesSent(portInt, voteToken.voteArrayList());
//                        System.out.println(voteToken.ports + "VoteToken");
                        break;
                    }catch(ConnectException e){
                        try{
                            this.sleep(50);
                        }catch(InterruptedException f){
                            f.printStackTrace();
                        }
                        System.out.println("Failed connection");
                        continue;
                    }
                }
            }
            finishedVoting=true;
        }catch (IOException e){
            e.printStackTrace();
        }

    }


}
