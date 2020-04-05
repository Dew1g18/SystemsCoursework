import java.io.IOException;

/**
 * This is just here as a class with a main to quickly manually test to see if some of my things work, for instance here I
 * will check that my TokenHandler is able to compile a Details token nicely before I go through the hassle of implementing
 * it and trying to get info across sockets.
 *
 *
 * ###This should not be submitted###
 */

public class Test {

    public static void main(String[] args){

        System.out.println("Running detail token test:");
        String options = "DETAILS 0123 0234 4567 9876";
        TokenHandler tokenHandler = new TokenHandler();
        Token token = tokenHandler.getToken(options);
        System.out.println(options);
        if (token instanceof DetailToken){
            DetailToken tok = (DetailToken) token;
            System.out.println("Success, its a detail token, options follow:");
            for (String opt : tok.getOptions()){
                System.out.println(opt);
            }

        }else{
            System.out.println("Fucked up lmao");
        }
        System.out.println("Detail token test complete");

        System.out.println("Socket info test");
        Coordinator coordinator = new Coordinator();
        Participant participant = new Participant();
        try {
            System.out.println("listening starting");
            coordinator.startListening(4200);
            System.out.println("Participants part");
            participant.runWithThese("6969", "4200");

        }catch(IOException e){
            e.printStackTrace();
        }

    }
}

