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
        String options = "DETAILS 0123 0234 4567 9876";
        TokenHandler tokenHandler = new TokenHandler();
        Token token = tokenHandler.getToken(options);
        if (token instanceof DetailToken){
            System.out.println(options);
            DetailToken tok = (DetailToken) token;
            System.out.println(tok.getOptions()[3]);
        }else{
            System.out.println("Fucked up lmao");
        }

    }
}

