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

    public boolean isFinishedVoting() {
        return finishedVoting;
    }

    private DetailToken details;

    public VotingThread(VoteToken voteToken, DetailToken detailToken){
//        this.mySocket = socket;
        this.voteToken = voteToken;
        this.details = detailToken;
        finishedVoting = false;
    }

    public void run(){
        Socket socket;
        try {
            for (String port : details.getOptions()) {
                while(true) {
                    try {
                        socket = new Socket(InetAddress.getLocalHost(), Integer.parseInt(port));
                        PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                        out.println(voteToken.requirement);
                        out.flush();
//                        System.out.println("sent vote!");
                        break;
                    }catch(ConnectException e){
//                        System.out.println("Failed connection");
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
