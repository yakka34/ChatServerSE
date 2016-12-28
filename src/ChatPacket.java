import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author Jukka
 * ChatPacket consists of the following:
 * -Sender
 * -Target
 * -Type
 * -Message
 * Example JSON:
 * {
 *  "sender":   "yakka34",              //Who send this
 *  "target":   "server",               //server, room or user's name
 *  "type":     "message",              //message or response
 *  "message":  "/connect"
 * }
 * yakka34 is the sender
 * server is the target
 * All messages from the client are of the type "message"
 * The server uses "response" to reply for commands
 * /connect is the message
 */

public class ChatPacket {

    /*
    Copyright 2008 Google Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
    */
    
    /*
        Gson can transform Object into JSON String and JSON String into Object
    */
    public static final GsonBuilder BUILDER = new GsonBuilder();
    public static final Gson GSON = new Gson();

    private final String type;
    private final String sender;
    private final String target;
    private final String message;

    public ChatPacket(String sender, String target, String type, String message) throws IllegalArgumentException{

        if(sender.matches("[A-Za-z0-9]+") && target.matches("^#?[A-Za-z0-9]+")){
            this.sender = sender;
            this.target = target;
        }
        else{
            throw new IllegalArgumentException("Error: Only # in the begining, A-Z, a-z or 0-9 allowed in sender and target");
        }
        if(type.matches("[A-Za-z]+")){
            this.type = type;
        }
        else{
            throw new IllegalArgumentException("Error: Only A-Z or a-z allowed in type");
        }
        this.message = message;
    }

    public String sender(){
        return this.sender;
    }

    public String target(){
        return this.target;
    }

    public String type(){
        return this.type;
    }

    public String message(){
        return this.message;
    }

    @Override
    public String toString(){
        return GSON.toJson(this);
    }
}
