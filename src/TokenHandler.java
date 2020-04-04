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