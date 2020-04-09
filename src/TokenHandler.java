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

            case "DETAILS":
                int howManyOps = tokenizer.countTokens();
                String[] options = new String[howManyOps];
                for(int i = 0; i<howManyOps; i++){
                    options[i]=tokenizer.nextToken();
                }
                return new DetailToken(requirement, options);

            case "VOTE_OPTIONS":
                int howManyVoteOps = tokenizer.countTokens();
                String[] voteOptions = new String[howManyVoteOps];
                for(int i = 0; i<howManyVoteOps; i++){
                    voteOptions[i]=tokenizer.nextToken();
                }
                return new VoteOptionsToken(requirement, voteOptions);

            case "VOTE":
                int howManyVotes = tokenizer.countTokens();
                String[] ports = new String[howManyVotes/2];
                String[] votes = new String[howManyVotes/2];
                for(int i=0; i<howManyVotes/2; i++){
                    ports[i]=tokenizer.nextToken();
                    votes[i]=tokenizer.nextToken();
                }
                return new VoteToken(requirement, ports, votes);

        }
        return null;
    }

    public VoteToken makeVote(String port, String vote){
        return (VoteToken) getToken("VOTE "+port+" "+vote);
    }




}

//Creating a model for tokens will help me typecast them later on.
abstract class Token{
    String requirement;

    public String getRequirement() {
        return requirement;
    }
}


//Format: JOIN 'port'
class JoinToken extends Token{
    String port;
    public JoinToken(String req, String port){
        this.requirement = req;
        this.port = port;
    }
}

//Format: Details '[ports]'
class DetailToken extends Token{
    String[] options;
    public DetailToken(String req, String[] options){
        this.options= options;
        this.requirement = req;
    }

    public String[] getOptions() {
        return options;
    }
}

class VoteOptionsToken extends Token{
    String[] options;
    public VoteOptionsToken(String requirement, String[] options){
        this.requirement= requirement;
        this.options=options;
    }
    public String[] getOptions() {
        return options;
    }
}

class VoteToken extends Token{
    String[] port, vote;
    public VoteToken(String requirement, String[] port, String[] vote){
        this.requirement = requirement;
    }
}