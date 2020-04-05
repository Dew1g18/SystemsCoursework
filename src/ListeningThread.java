import java.io.IOException;
import java.net.Socket;

public class ListeningThread extends Thread {

    private Socket listeningPort;

    public ListeningThread(Socket socket){
        this.listeningPort = socket;
    }

    public void run(){

        //todo: This is going to be the class which gives the participant
//        try {
//
//        }catch(IOException e){
//            e.printStackTrace();
//        }
    }

    public void close(){

    }

}
