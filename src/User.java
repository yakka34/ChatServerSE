

import java.util.ArrayList;

/**
 *
 * @author Jukka
 */
public class User {

    private String username;
    private final String identity;
    private ArrayList<Room> roomList;
    private final Connection connection;

    public User(String username, Connection connection) {
        this.username = username;
        this.connection = connection;
        this.identity = username + "@" + connection.InetAddress().toString().substring(1);
        roomList = new ArrayList<>();
    }

    public Connection connection() {
        return this.connection;
    }

    public String getIdentity() {
        return this.identity;
    }

    public String getName() {
        return this.username;
    }

    public void setName(String name) {
        this.username = name;
    }

    public ArrayList<Room> getRoomList() {
        return this.roomList;
    }

    public void addRoom(Room room) {
        this.roomList.add(room);
    }

    public boolean removeRoom(Room room) {
        return this.roomList.remove(room);
    }
}
