import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 *
 *  ChatServer
 *
 * @author Phillip
 *
 * This is the code to run the server part of the chatserver.
 * To run program, first run this ChatServer.java main method, then run the ChatClient.java.
 * Many ChatClient.java methods can be run to simulate multiple clients in the server
 *
 *
 * @version 4/26/2020
 */
final class ChatServer {
    private static int uniqueId = 0;
    private final List<ClientThread> clients = Collections.synchronizedList(new ArrayList<>());
    private final List<String> nameOfUsers = Collections.synchronizedList(new ArrayList<>());
    private final int port;
    private String fileName;
    private static final Object OBJECT = new Object();
    //private static final Object Usernameob = new Object();


    public ChatServer(int port) {
        this.port = port;
    }
    public ChatServer(int port, String fileName) {
        this.port = port;
        this.fileName = fileName;
    }


    /*
     * This is what starts the ChatServer.
     * Right now it just creates the socketServer and adds a new ClientThread to a list to be handled
     */
    private void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Connection Established");
                Runnable r = new ClientThread(socket, uniqueId++);
                Thread t = new Thread(r);
                synchronized (OBJECT) {
                    clients.add((ClientThread) r);
                }
                t.start();

                //ClientThread clientThread = new ClientThread(socket, uniqueId++);
                //ChatMessage chatMessage = new ChatMessage();
                //clientThread.writeMessage(chatMessage.getMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void broadcast(String message) {
        Object object = new Object();
        //synchronized (clients) {
        //synchronized (nameOfUsers) {

        String date = "";
        ChatFilter chatFilter = new ChatFilter(fileName);
        chatFilter.filter(message);
        for (ClientThread c : clients) {
            String pattern = "HH:mm:ss";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

            date = simpleDateFormat.format(new Date());

            try {
                c.sOutput.writeObject(date + " " + message + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }

            //c.writeMessage(date + " " + message);
        }
        System.out.println(date + " " + message);
        //}
        //}
    }



    private void remove(int id) {
        //Object object = new Object();
        //synchronized (clients) {
        //synchronized (nameOfUsers) {
        int where = 0;





        for (int i = 0; i < clients.size(); i++) {
            ClientThread c = clients.get(i);

            if (id == c.id) {
                clients.remove(c);
                nameOfUsers.remove(c.username);
                break;
            }
        }

            //synchronized (Usernameob) {

    }


    /*
     *  > java ChatServer
     *  > java ChatServer portNumber
     *  If the port number is not specified 1500 is used
     */
    public static void main(String[] args) {


        //while (true) {
        int port1 = 1500;
        String filename = "badwords.txt";

        // if (args.equals("/list"))

        if (args.length > 0) {
            port1 = Integer.parseInt(args[0]);
            if (args.length > 1)
                filename = args[1];
        }

        ChatFilter chatFilter = new ChatFilter(filename);
        System.out.println("List of bad words:");

        chatFilter.printFile(filename);

        ChatServer server = new ChatServer(port1);
        ChatServer server1 = new ChatServer(port1, filename);
        server1.start();

        //}
    }


    /**
     * This is a private class inside of the ChatServer
     * A new thread will be created to run this every time a new client connects.
     *
     * @author Phillip
     * @version 4/26/2020
     */
    private final class ClientThread implements Runnable {
        Socket socket;
        ObjectInputStream sInput;
        ObjectOutputStream sOutput;
        int id;
        String username;
        ChatMessage cm;
        private ClientThread(int id, String username) {
            this.id = id;
            this.username = username;
        }

        private ClientThread(Socket socket, int id) {
            this.id = id;
            this.socket = socket;
            //synchronized (this) {
            try {
                sOutput = new ObjectOutputStream(socket.getOutputStream());
                sInput = new ObjectInputStream(socket.getInputStream());

                username = (String) sInput.readObject();
                synchronized (OBJECT) {
                    if (!username.equals("Anonymous")) {
                        while (true) {
                            if (nameOfUsers.contains(username)) {
                                Object object = sInput.readObject();
                                username = ((ChatMessage) object).getMessage();
                                if (nameOfUsers.contains(username))
                                    sOutput.writeObject("Username Taken, Try again:\n");
                                //username = (String) sInput.readObject();
                            } else {
                                if (username.equals(""))
                                    username = "Anonymous";
                                break;
                            }
                        }
                    }
                }
                try {
                    sOutput.writeObject("Pong\nYou may start entering messages under the username " + username + "\n");
                    System.out.println(username + ": Ping");
                } catch (IOException e) {
                    e.printStackTrace();
                }



                nameOfUsers.add(username);


                //ClientThread clientThread = new ClientThread(id, username);
                //clients.add(clientThread);

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            //}
        }
        private ClientThread() {

        }

        /*
         * This is what the client thread actually runs.
         */
        @Override
        public void run() {
            // Read the username sent to you by client

            //synchronized (this) {
            try {
                cm = (ChatMessage) sInput.readObject();

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }



            // Send message back to the client
            //try {
            //  sOutput.writeObject("Pong\n");
            //} catch (IOException e) {
            //  e.printStackTrace();
            //}
            int flag = 0;

            while (true) {
                //Object object = new Object();


                try {
                    cm = (ChatMessage) sInput.readObject();
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println(username + " has logged out");
                    synchronized (OBJECT) {
                        remove(id);
                    }
                    break;
                }

                if (cm.getType() == 0) {
                    String msg = "";
                    String message = "";
                    ChatFilter chatFilter = new ChatFilter(fileName);

                    //ClientThread c = new ClientThread();

                    message = cm.getMessage();


                    String[] split = message.split(" ");
                    if (message.equals("/list")) {
                        for (int i = 0; i < nameOfUsers.size(); i++) {
                            if (!nameOfUsers.get(i).equals(username)) {
                                msg = msg + nameOfUsers.get(i) + "\n";
                            }

                        }
                        synchronized (OBJECT) {
                            directMessage(msg, username, 1);
                        }

                    } else if (split[0].equals("/msg")) {
                        String reciever = split[1];
                        if (!nameOfUsers.contains(reciever)) {
                            directMessage("That user does not exist!", username, 0);
                        } else if (!reciever.equals(username)) {
                            String send = "DM from " + username + ": ";
                            for (int i = 2; i < split.length; i++) {
                                send = send + split[i] + " ";
                            }
                            synchronized (OBJECT) {
                                directMessage(send, reciever, 0);
                            }
                        } else {
                            synchronized (OBJECT) {
                                directMessage("You cannot DM yourself!", username, 0);
                            }
                        }

                    } else {
                        msg = username + ": " + chatFilter.filter(message);
                        synchronized (OBJECT) {
                            broadcast(msg);
                        }
                    }

                } else {
                    synchronized (OBJECT) {
                        broadcast(username + " has logged out");
                    }
                    close();
                    synchronized (OBJECT) {
                        remove(id);
                    }

                    //close();
                    break;
                }
            }
            // }
        }

        private void close() {
            try {

                sOutput.flush();
                sOutput.close();
                sInput.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public boolean writeMessage(String message) {
            if (socket.isConnected()) {
                try {
                    ObjectOutputStream oos = new ObjectOutputStream(sOutput);
                    oos.writeObject(message);
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }
        private void directMessage(String message, String username1, int type1) {

            //synchronized (clients) {
            //synchronized (nameOfUsers) {
            ChatFilter chatFilter = new ChatFilter(fileName);
            String filteredMess = chatFilter.filter(message);

            int where = 0;

            for (int i = 0; i < nameOfUsers.size(); i++) {
                if (username1.equals(nameOfUsers.get(i))) {
                    where = i;
                    break;
                }
            }

            int newid = clients.get(where).id;

            if (type1 == 1) {
                for (ClientThread c : clients) {
                    Object object = new Object();
                    //synchronized (object) {

                    if (newid == c.id) {
                        try {
                            //OutputStreamWriter sOutput1 = new ObjectOutputStream(c.socket.getOutputStream());

                            //ObjectOutputStream oos = new ObjectOutputStream(c.sOutput);
                            //oos.writeObject(filteredMess);
                            //sOutput.flush();
                            sOutput.writeObject(filteredMess);
                            //c.writeMessage(filteredMess)
                            //oos.flush();
                            break;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
            else {
                for (ClientThread c: clients) {
                    if (newid == c.id) {
                        try {

                            c.sOutput.writeObject(filteredMess + "\n");

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        }
    }
}