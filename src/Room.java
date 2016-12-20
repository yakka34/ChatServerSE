

import java.util.ArrayList;

/**
 *
 * @author Jukka
 */
public class Room {

    private final String name;
    //Users in the room
    private ArrayList<User> userList;

    public Room(String name) {
        this.name = name;
        this.userList = new ArrayList<>();
    }

    public int size() {
        return this.userList.size();
    }

    public String getName() {
        return this.name;
    }

    public ArrayList<User> getUsers() {
        return this.userList;
    }

    public void addUser(User user) {
        this.userList.add(user);
        user.addRoom(this);
    }

    public void removeUser(User user) {
        this.userList.remove(user);
        user.removeRoom(this);
    }
}
