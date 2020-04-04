import java.util.StringTokenizer;

public class TokenHandler {

    /**
     * This class, similar to the chat server example, will enable me to create objects from any socket's incomming
     * byte streams and enable me to typecast them based on the 'supported vocabulary' word prefixing it.
     *
     * This class very closely follows the format of the ChatServer example. Hoping this doesn't violate any of the
     * plagiarism rules but I have absolutely no other idea of how to do this
     *
     * I'll be using java.util.StringTokenizer to pull apart the byte streams coming in and recognising them.
     *
     */
    Token getToken(String requirement){
        //Using java's string tokenizer, we will tokenize incomming transmissions and use this
        //token handler class to parse them into objects we can store and use.
        StringTokenizer tokenizer = new StringTokenizer(requirement);
        if (!(tokenizer.hasMoreTokens())){
            return null;
        }
        String token1 = tokenizer.nextToken();
        //gets the first string without space in the bytestream
        //Next we check all the forms it may take and from that, we create the neccesary token
        switch (token1) {
            case "JOIN":
                if (tokenizer.hasMoreTokens()) {
                    return new JoinToken(requirement, tokenizer.nextToken());
                }
        }
        return null;
    }




}

//Creating a model for tokens will help me typecast them later on.
abstract class Token{
    String requirement;
}


//Format: JOIN 'port'
class JoinToken extends Token{
    String port;
    public JoinToken(String req, String port){
        this.requirement = req;
        this.port = port;
    }

}