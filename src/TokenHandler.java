import java.util.*;

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
                VoteToken voteToken = new VoteToken(requirement);
//                System.out.println(requirement+" makes "+ voteToken.requirement);
                return voteToken;

            case "OUTCOME":
                return new OutcomeToken(requirement);

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

    public ArrayList<Integer> detailsArray(){
        ArrayList<Integer> dets = new ArrayList<>();
        for(String det: getOptions()){
            dets.add(Integer.parseInt(det));
        }
        return dets;
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

    public ArrayList<String> voteOptionArray(){
        ArrayList<String> votes = new ArrayList<>();
        for(String vote: getOptions()){
            votes.add(vote);
        }
        return votes;
    }

    public String[] getOptions() {
        return options;
    }
}

class VoteToken extends Token{
    String[] ports, votes;
    Vote[] voteArray;

    public VoteToken(String requirement, String[] port, String[] vote){
        this.requirement = requirement;
        this.ports = port;
        this.votes = vote;
        voteArray= makeVoteArray(port.length);
    }

    public VoteToken(String requirement){
        this.requirement = requirement;
        StringTokenizer tokenizer = new StringTokenizer(requirement);
        tokenizer.nextToken();
        int howManyVotes = tokenizer.countTokens()/2;
        this.ports = new String[howManyVotes];
        this.votes = new String[howManyVotes];
        for(int i=0; i<howManyVotes; i++){
            this.ports[i]=tokenizer.nextToken();
            this.votes[i]=tokenizer.nextToken();
        }
        this.voteArray = makeVoteArray(howManyVotes);
//        System.out.println("this "+this.requirement);
    }

    public Vote[] makeVoteArray(int length){
        Vote[] make = new Vote[length];
        for(int i=0; i<length; i++){
            make[i]=new Vote(Integer.parseInt(ports[i]), votes[i]);
        }
        return make;
    }

    public ArrayList<Vote> voteArrayList(){
        ArrayList<Vote> votes = new ArrayList<>();
        for(Vote vote: makeVoteArray(this.voteArray.length)){
            votes.add(vote);
        }
        return votes;
    }


    public Map<String, String> mapPortsToVotes(){
        Map<String, String> portsToVotes = new HashMap<>();
        for(int i=0; i<ports.length; i++){
            portsToVotes.put(ports[i], votes[i]);
        }
        return portsToVotes;
    }
}

class OutcomeToken extends Token{
    String vote;
    String[] portsConsidered;

    public OutcomeToken(String requirement){
        this.requirement = requirement;
        StringTokenizer tokenizer = new StringTokenizer(requirement);
        tokenizer.nextToken();
        this.vote = tokenizer.nextToken();
        this.portsConsidered = new String[tokenizer.countTokens()];
        for(int i=0; i<tokenizer.countTokens(); i++){
            this.portsConsidered[i]=tokenizer.nextToken();
        }
    }

    public ArrayList<Integer> portIDs(){
        ArrayList<Integer> arr = new ArrayList<>();
        for(String port : portsConsidered){
            arr.add(Integer.parseInt(port));
        }
        return arr;
    }
}