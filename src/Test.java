import java.io.IOException;
import java.util.Random;

/**
 * This is just here as a class with a main to quickly manually test to see if some of my things work, for instance here I
 * will check that my TokenHandler is able to compile a Details token nicely before I go through the hassle of implementing
 * it and trying to get info across sockets.
 *
 *  This is not a formal testing class, I do not have a bunch of preset tests with expected outcomes and pass fail
 *  It is only meant to run the methods and I manually check if the output is correct. If I can be bothered during
 *  the day time I will update this to include a test harness.
 *
 * ###This should not be submitted###
 *
 * Old tests will likely not work as I have refactored since.
 */

public class Test {

    public static void main(String[] args){

        /**
         * The following section is for testing TokenHandler's create details method
         */
//
//        System.out.println("Running detail token test:");
//        String options = "DETAILS 0123 0234 4567 9876";
//        TokenHandler tokenHandler = new TokenHandler();
//        Token token = tokenHandler.getToken(options);
//        System.out.println(options);
//        if (token instanceof DetailToken){
//            DetailToken tok = (DetailToken) token;
//            System.out.println("Success, its a detail token, options follow:");
//            for (String opt : tok.getOptions()){
//                System.out.println(opt);
//            }
//
//        }else{
//            System.out.println("Fucked up lmao");
//        }
//        System.out.println("Detail token test complete");


        /**
         * The following checks the sockets sending the initial data across, (currently relies on the coorinator having
         * some dummy data set up to send.)
         *
         * This will but updated to run a bunch of participants in separate threads to check that the whole sys works,
         * may migrate elsewhere.
         */
//        System.out.println("Socket info test");
//        Coordinator coordinator = new Coordinator(1);
//        Participant participant = new Participant();
//        Thread t1 = new Thread(new Runnable() {
//            @Override
//            public void run(){
//                try {
//                    System.out.println("listening starting");
//                    coordinator.startListening(4200);
//                }catch(Exception e){
//                    e.printStackTrace();
//                }
//            }
//        });
//        t1.start();
//
//        System.out.println("Participants running");
//        participant.runWithThese("6969", "4200", "unnecesary for this test..");

        /**
         * The following will test the participants rounds. each participant will be printing outcomes, so its gonna get loud
         * Need to set up a lot of information and give the coordinator a little more functionality before this test will work
         * will probably take a while...
         * sigh
         *
         */

        int numberOfParticipants = 10;
        Thread coordinatorThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Coordinator coord = new Coordinator(numberOfParticipants);
                    coord.startListening(6969);
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        });
        coordinatorThread.start();

        ThreadGroup participants = new ThreadGroup("Participants");
        Random randomInts = new Random();
        for(int i=0; i<numberOfParticipants; i++){
            int finalI = i;
            Thread pThred = new Thread(participants, new Runnable() {
                @Override
                public void run() {
                    Participant participant = new Participant();
                    String pport = Integer.toString(1070+ finalI);
                    String vote = Integer.toString(randomInts.nextInt(5));
                    participant.runWithThese(pport, "6969", vote);
                }
            });
            pThred.start();
        }



    }

}

