/**
 *
 * @author Jukka
 */
public class ChatServerSE {

    public static void main(String[] args) {
        //Runs on port 8888
        Server server = new Server(8888);
        server.run();
    }

}
