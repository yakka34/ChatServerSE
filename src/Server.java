

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author Jukka
 */
public class Server implements Runnable {

    //Server status
    private boolean alive = true;
    private ServerSocket server;
    //Server's global user- and room list
    private ArrayList<User> userList;
    private ArrayList<Room> roomList;

    /*
        The commands available
     */
    public static final String JOIN = "JOIN";
    public static final String NICK = "NICK";
    public static final String PART = "PART";
    public static final String QUIT = "QUIT";
    public static final String CONNECT = "CONNECT";
    public static final String PARTALL = "PARTALL";

    public Server(int port) {
        try {
            this.server = new ServerSocket(port);
            this.userList = new ArrayList<>();
            this.roomList = new ArrayList<>();
        } catch (IOException e) {
            System.out.println(e);
            alive = false;
        }
    }

    /*
        Create new user and add it to server user list
     */
    public void addUser(String name, Connection connenction) {
        this.userList.add(new User(name, connenction));
    }

    /*
        Remove user from server's user list
     */
    public boolean removeUser(User user) {
        return this.userList.remove(user);
    }

    /*
        Create new room and add it to server room list
     */
    public void addRoom(String name) {
        this.roomList.add(new Room(name));
    }

    /*
        Find room by name
     */
    public Room findRoom(String name) {
        for (Room room : roomList) {
            if (name.equals(room.getName())) {
                return room;
            }
        }
        return null;
    }

    /*
        Find user by name
     */
    public User findUser(String name) {
        for (User user : userList) {
            if (name.equals(user.getName())) {
                return user;
            }
        }
        return null;
    }

    /*
        Remove user from one rooms
     */
    public void removeUserFromRoom(User user, String name) {
        Room room = findRoom(name);
        room.removeUser(user);
        if (room.size() == 0) {
            System.out.println(room.getName() + " removed: no users");
            roomList.remove(room);
        }
    }

    /*
        Remove user from all rooms
     */
    public void removeUserFromRooms(User user) {
        if (user != null && user.getRoomList().size() > 0) {
            ArrayList<Room> rooms = user.getRoomList();
            while (rooms.size() > 0) {
                Room room = rooms.get(0);
                room.removeUser(user);
                if (room.size() == 0) {
                    System.out.println(room.getName() + " removed: no users");
                    roomList.remove(room);
                }
            }
        }
    }

    /*
        Accepts new clients until server is closed.
     */
    @Override
    public void run() {
        try {
            while (alive) {
                Socket client = server.accept();
                Connection connection = new Connection(client, this);
                connection.run();
            }
            server.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
