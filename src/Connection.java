
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

    //Is null until client sends /connect request
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
    public void processInput(ChatPacket packet) {
        //If the message contains a '/' it is a command
        if (packet.message().startsWith("/")) {
            //Command without the "/"
            String command = packet.message().substring(1);
            String parameters[] = command.split(" ");
            //Create new user
            if (command.equalsIgnoreCase(Server.CONNECT)) {
                server.addUser(packet.sender(), this);
                user = server.findUser(packet.sender());
                System.out.println("New User " + user.getIdentity());
            } //Join existing room or create one
            else if (user != null && command.toUpperCase().contains(Server.JOIN)) {
                String name = parameters[1];
                Room room = server.findRoom(name);
                //Already existing room
                if (room != null) {
                    room.addUser(user);
                    ChatPacket info = new ChatPacket("server", room.getName(), 1, user.getName() + " joined room");
                    room.getUsers().forEach(roomUser ->{
                        roomUser.connection().sendMessage(info.toString());
                    });
                } //Create new room
                else {
                    server.addRoom(name);
                    room = server.findRoom(name);
                    room.addUser(user);
                }
                System.out.println(user.getIdentity() + " joined " + room.getName());
                //Sends packet to client that has room id and name.
                ChatPacket response = new ChatPacket("server", packet.sender(), 1, room.getName());
                sendMessage(response.toString());
            } //Change the name of the user if the name is free
            else if (user != null && command.toUpperCase().contains(Server.NICK)) {
                String name = packet.message().substring(packet.message().indexOf(' ') + 1);
                if (server.findUser(name) == null) {
                    user.setName(name);
                    System.out.println(packet.sender() + " is now known as " + user.getName());
                }
                //Sends information to the client stating it's current username.
                //May be the same as the old one or new if the name was free.
                ChatPacket response = new ChatPacket("server", packet.sender(), 1, user.getName());
                sendMessage(response.toString());
            } //Leave from one channell
            else if (user != null && command.equalsIgnoreCase(Server.PART)) {
                System.out.println(user.getName() + " has left room " + server.findRoom(packet.target()).getName());
                server.removeUserFromRoom(user, packet.target());
            } //Leave from all channells but don't disconnect
            else if (user != null && command.equalsIgnoreCase(Server.PARTALL)) {
                server.removeUserFromRooms(user);
                System.out.println(user.getName() + " has left from all rooms");
            } //Disconnect
            else if (command.equalsIgnoreCase(Server.QUIT)) {
                if (user != null && user.getRoomList().size() > 0) {
                    server.removeUserFromRooms(user);
                    System.out.println(user.getName() + " has left from all rooms");
                }
                System.out.println(((user != null) ? user.getIdentity() : InetAddress()) + " has disconnected");
                this.alive = false;
                server.removeUser(user);
            } //Invalid command
            else {
                System.out.println("Invalid command: " + packet.toString());
                sendMessage("Invalid:" + packet.toString());
            }
        } //Message to room
        else {
            Room room = server.findRoom(packet.target());
            if (user != null && room != null) {
                System.out.println(packet.target() + ": <" + packet.sender() + "> " + packet.message());
                room.getUsers().forEach(roomUser -> {
                    roomUser.connection().sendMessage(packet.toString());
                });
            } else {
                System.out.println("Invalid command: " + packet.toString());
                sendMessage("Invalid:" + packet.toString());
            }
        }
    }

    public InetAddress InetAddress() {
        return socket.getInetAddress();
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
            System.out.println("New connection");
            String received;
            while (alive && (received = input.readLine()) != null) {
                try {
                    ChatPacket packet = new ChatPacket(received);
                    processInput(packet);
                } /*
                    If the received message is invalid It's mirrored back to the client
                 */ catch (Exception e) {
                    sendMessage("Invalid:" + received);
                }
            }
            ChatPacket response = new ChatPacket("server", user.getName(), 0, "Connection closed by the server");
            sendMessage(response.toString());
            socket.close();
        } /*
            In case of an networking error connection is terminated by the server.
         */ catch (IOException e) {
            System.out.println(e);
            alive = false;
        }
    }
}
