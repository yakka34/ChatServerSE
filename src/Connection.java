import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 *
 * @author Jukka
 */
public class Connection implements Runnable {

    //User is null until client sends /connect request
    private User user;
    private Socket socket;
    //The reference to the server object to which this connection is attached
    private Server server;
    private PrintWriter output;
    private BufferedReader input;
    private boolean alive = true;

    public Connection(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
        try {
            this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.output = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println(e);
            alive = false;
        }
    }

    /*
        Handles valid packets received from the client
     */
    public void processInput(ChatPacket packet) throws IllegalArgumentException{
        //If the message contains a '/' it is a command
        if (packet.message().startsWith("/")) {
            //Command without the "/"
            String command = packet.message().substring(1);
            String parameters[] = command.split(" ");
            //Create new user
            if (command.equalsIgnoreCase(Server.CONNECT)) {
                this.user = server.addUser(packet.sender(), this);
                if(user != null){
                    System.out.println("New User " + user.getIdentity());
                    sendResponse(user.getName());
                    //sendMessage(new ChatPacket("server", packet.sender(), "response", user.getName()).toString());
                }
                else{
                    throw new IllegalArgumentException("Error: name already in use");
                }
            }
            //Join existing room or create one
            else if (user != null && command.toUpperCase().contains(Server.JOIN)) {
                String name = parameters[1];
                Room room = server.findRoom(name);
                //Already existing room
                if (room != null) {
                    room.addUser(user);
                    ChatPacket info = new ChatPacket("server", room.getName(), "message", user.getName() + " joined room");
                    room.getUsers().forEach(roomUser ->{
                        roomUser.connection().sendMessage(info.toString());
                    });
                }
                //Create new room
                else {
                    server.addRoom(name);
                    room = server.findRoom(name);
                    room.addUser(user);
                }
                System.out.println(user.getIdentity() + " joined " + room.getName());
                //Sends packet to client that has room id and name.
                sendResponse(room.getName());
            } 
            //Change the name of the user if the name is free
            else if (user != null && command.toUpperCase().contains(Server.NICK)) {
                if (server.findUser(parameters[1]) == null) {
                    user.setName(parameters[1]);
                    System.out.println(packet.sender() + " is now known as " + user.getName());
                }
                else{
                    throw new IllegalArgumentException("Error: name already in use");
                }
                //Send new username to the client
                sendResponse(user.getName());
            }
            //Leave from one channel
            else if (user != null && command.equalsIgnoreCase(Server.PART)) {
                System.out.println(user.getName() + " has left room " + server.findRoom(packet.target()).getName());
                server.removeUserFromRoom(user, packet.target());
                sendResponse("part");
            }
            //Leave from all channels but don't disconnect
            else if (user != null && command.equalsIgnoreCase(Server.PARTALL)) {
                server.removeUserFromRooms(user);
                System.out.println(user.getName() + " has left from all rooms");
                sendResponse("partall");
            }
            //Disconnect
            else if (command.equalsIgnoreCase(Server.QUIT)) {
                if (user != null && user.getRoomList().size() > 0) {
                    server.removeUserFromRooms(user);
                    System.out.println(user.getName() + " has left from all rooms");
                }
                System.out.println(((user != null) ? user.getIdentity() : InetAddress()) + " has disconnected");
                this.alive = false;
                server.removeUser(user);
            }
            //Invalid command
            else {
                System.out.println("Invalid command: " + packet.toString());
                throw new IllegalArgumentException("Error: Invalid command");
            }
        }
        //Message to room
        else {
            Room room = server.findRoom(packet.target());
            if (user != null && room != null) {
                System.out.println(packet.target() + ": <" + packet.sender() + "> " + packet.message());
                room.getUsers().forEach(roomUser -> {
                    roomUser.connection().sendMessage(packet.toString());
                });
            } else {
                throw new IllegalArgumentException("Error: Invalid target");
            }
        }
    }

    public InetAddress InetAddress() {
        return socket.getInetAddress();
    }
    
    public void sendResponse(String msg){
        sendMessage(new ChatPacket("server","client","response",msg).toString());
    }

    public void sendMessage(String msg) {
        output.println(msg);
    }

    /*
        Receives messages from the client until connection is closed by the server or the client
     */
    @Override
    public void run() {
        try {
            String received;
            System.out.println("New connection thread");
            while (alive && (received = input.readLine()) != null) {
                try {
                    ChatPacket packet = ChatPacket.GSON.fromJson(received, ChatPacket.class);
                    if(user != null && !user.getName().equals(packet.sender())){
                        throw new IllegalArgumentException("Error: sender should be "+user.getName());
                    }
                    processInput(packet);
                }
                //Error messages are sent to the client
                catch (IllegalArgumentException | JsonSyntaxException e) {
                    if(e instanceof JsonSyntaxException){
                        sendResponse("Error: Invalid JSON "+received);
                    }
                    else{
                        sendResponse(e.getMessage());
                    }
                }
            }
            sendResponse("Connection closed by the server");
            socket.close();
        } /*
            In case of an networking error connection is terminated by the server.
         */ catch (IOException e) {
            System.out.println(e);
            alive = false;
        }
    }
}
