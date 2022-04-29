import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

/**
 *
 * ChatClient
 *
 * @author Phillip
 *
 * This is the ChatClient part of the chatserver. Run this AFTER running the ChatClient.
 * There are some commands to run special features of this server:
 * Type "/msg (username) (sentance)" to send another user a direct message. Replace (username) with the other users username
 *     and replace (sentance) with whatever you want to send the other user. For example,   /msg Phillip hello
 *
 * Type /list for a list of all users in the server
 *
 * Type /logout to logout of the server
 *
 * @version 4/26/2020
 */
final class ChatClient {
    private ObjectInputStream sInput;
    private ObjectOutputStream sOutput;
    private Socket socket;

    private final String server;
    private final String username;
    private final int port;

    //private static final Object OBJECT = new Object();



    private ChatClient(String username, int port, String server) {
        this.username = username;
        this.port = port;
        this.server = server;
    }

    /*
     * This starts the Chat Client
     */
    private boolean start() {
        // Create a socket
        try {
            socket = new Socket(server, port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create your input and output streams
        try {
            sInput = new ObjectInputStream(socket.getInputStream());
            sOutput = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // This thread will listen from the server for incoming messages
        Runnable r = new ListenFromServer();
        Thread t = new Thread(r);
        t.start();

        // After starting, send the clients username to the server.
        try {
            sOutput.writeObject(username);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }


    /*
     * This method is used to send a ChatMessage Objects to the server
     */
    private void sendMessage(ChatMessage msg) {
        try {

            sOutput.writeObject(msg);
            sOutput.flush();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /*
     * To start the Client use one of the following command
     * > java ChatClient
     * > java ChatClient username
     * > java ChatClient username portNumber
     * > java ChatClient username portNumber serverAddress
     *
     * If the portNumber is not specified 1500 should be used
     * If the serverAddress is not specified "localHost" should be used
     * If the username is not specified "Anonymous" should be used
     */
    public static void main(String[] args) {
        // Get proper arguments and override defaults
        Scanner scanner = new Scanner(System.in);

        ChatMessage msg;

        int portnumber = 1500;
        String username;
        String serverAddress = "localhost";


        if (args.length > 0) {
            username = args[0];
            if (args.length > 1) {
                portnumber = Integer.parseInt(args[1]);
            }
            if (args.length > 2) {
                serverAddress = args[2];
            }
        } else {
            System.out.println("Please Enter a Username, leave blank for Anonymous");
            username = scanner.nextLine();
            if (username == null || username.equals(""))
                username = "Anonymous";
        }


        ChatClient client = new ChatClient(username, portnumber, serverAddress);

        //String msg1 = (String) client.sInput.readObject();
        //System.out.println(msg1);

        try {
            client.start();
        } catch (Exception e) {
            System.out.println("Server is not currently running!");
            return;
        }
        String userInput;
        msg = new ChatMessage(username, 0);
        client.sendMessage(msg);

        //System.out.println("You may start entering messages under the username " + username);
        while (true) {
            userInput = scanner.nextLine();
            if (userInput.equals("/logout")) {            //needs to be edited
                msg = new ChatMessage(userInput, 1);
                client.sendMessage(msg);
                //client.sInput.close();
                //client.sOutput.close();
                //client.socket.close();
                break;
            } else {
                msg = new ChatMessage(userInput, 0);
                client.sendMessage(msg);
            }

        }
    }


    /**
     * This is a private class inside of the ChatClient
     * It will be responsible for listening for messages from the ChatServer.
     * ie: When other clients send messages, the server will relay it to the client.
     *
     * @author Phillip
     * @version 4/26/2020
     */
    private final class ListenFromServer implements Runnable {

        public ListenFromServer() {

        }

        public void run() {

            while (true) {

                //synchronized (object) {
                try {
                    String msg = "";
                    //ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                    //ChatMessage chatMessage = new ChatMessage();
                    msg = (String) sInput.readObject();

                    if (msg.equals(username + ": /logout")) {
                        System.out.print("You have logged out");
                        // sInput.read(msg.getBytes());
                        close();
                        break;
                    }
                    System.out.print(msg);
                    //}
                } catch (EOFException f) {
                    //f.printStackTrace();
                    System.out.println("You have logged out");
                    break;
                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                }
                //}
            }

        }
    }

    public void close() throws IOException {
        sInput.close();
        sOutput.close();
        socket.close();
    }
}

