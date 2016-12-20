
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Jukka
 * ChatPacket consists of the following:
 * -Sender
 * -Target
 * -Type
 * -Message
 * Example:
 * "yakka34","server","2","/connect"
 * yakka34 is the sender
 * server is the target
 * 2 is the type. All messages from the client are of the type 2
 * The server uses different types to distinguish between the types of messages
 * Type 0: Connection is closed by the server
 * Type 1: Message holds information regarding client request eg. new username, room name...
 * Type 2: Message from client to server.
 * /connect is the message and it may contain any character except new line
 */

public class ChatPacket {
    
    private final int type;
    private final String target;
    private final String sender;
    private final String message;
   
    private final Pattern pattern = Pattern.compile("\\\"(.+)\\\",\\\"(.+)\\\",\\\"(\\d+)\\\",\\\"(.+)?\\\"");
    
    public ChatPacket(String sender, String target, int type, String message){
        this.sender = sender;
        this.target = target;
        this.type = type;
        this.message = message;
    }
    
    public ChatPacket (String encoded) throws IllegalArgumentException{
        Matcher matcher = pattern.matcher(encoded);
        if(matcher.matches()){
            this.sender = matcher.group(1);
            this.target = matcher.group(2);
            this.type = Integer.parseInt(matcher.group(3));
            this.message = matcher.group(4);
        }
        else{
            throw new IllegalArgumentException();
        }
    }
    
    public String sender(){
        return this.sender;
    }
    
    public String target(){
        return this.target;
    }
    
    public int type(){
        return this.type;
    }
    
    public String message(){
        return this.message;
    }

    @Override
    public String toString(){
        return "\""+sender+"\",\""+target+"\",\""+type+"\",\""+message+"\"";
    }
}
