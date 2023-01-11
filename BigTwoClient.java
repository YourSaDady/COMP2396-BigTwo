import java.net.*;
import java.io.*;
import java.util.EventListener;
import javax.swing.JOptionPane;

public class BigTwoClient implements NetworkGame {
    /*
     a BigTwo object for the Big Two card game
     */
    private BigTwo game;
    /*
     a BigTwoGUI object for the Big Two card game
     */
    private BigTwoGUI gui;
    /*
    a socket connection to the game server
     */
    private Socket sock;
    /*
    an ObjectOutputStream for sending messages to the server
     */
    private ObjectOutputStream oos;
    /*
    an integer specifying the playerID (i.e., index) of the local player
     */
    private int playerID;
    /*
    a string specifying the name of the local player
     */
    private String playerName;
    /*
    a string specifying the IP address of the game server
     */
    private String serverIP;
    /*
    an integer specifying the TCP port of the game server
     */
    private int serverPort;


    /**
     * This is a constructor for creating a Big Two client.
     *
     * @param game a BigTwo object associated with this client
     * @param gui  a BigTwoGUI object associated the BigTwo object
     */
    public BigTwoClient(BigTwo game, BigTwoGUI gui) {
        this.game = game;
        this.gui = gui;

        this.gui.disable();
        playerName = (String) JOptionPane.showInputDialog("Please enter your name: ");

        if (playerName == null) {
            playerName = "Player " + game.getCurrentPlayerIdx();
        }

        serverIP = "127.0.0.1";
        serverPort = 2396;

    }

    /**
     * Gets the playerID of the local player.
     *
     * @return an index of playerID of the local player
     */
    public int getPlayerID() {
        return this.playerID;
    }

    /**
     * Sets the playerID of the local player.
     *
     * @param playerID an index of playerID to be set
     */
    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    /**
     * Gets the name of the local player.
     *
     * @return a string of the local player's name
     */
    public String getPlayerName() {
        return this.playerName;
    }

    /**
     * Sets the name of the local player.
     *
     * @param name a string of player name to be set
     */
    public void setPlayerName(String name) {
        if (name == null) {
            this.playerName = "Player " + this.game.getNumOfPlayers();
        }
        else {
            this.playerName = name;
        }
    }

    /**
     * Gets the  IP address of the game server.
     *
     * @return a string of the server's IP address
     */
    public String getServerIP() {
        return this.serverIP;
    }

    /**
     * Sets the  IP address of the game server.
     *
     * @param IP a string of server IP address to be set
     */
    public void setServerIP(String IP) {
        this.serverIP = IP;
    }

    /**
     * Gets the TCP port of the game server.
     *
     * @return an integer of the server's TCP port
     */
    public int getServerPort() {
        return this.serverPort;
    }

    /**
     * Sets the  TCP port of the game server.
     *
     * @param TCP an integer of TCP port to be set
     */
    public void setServerPort(int TCP) {
        this.serverPort = TCP;
    }

    /**
     * Makes a socket connection with the game server.
     */
    public synchronized void connect() {
        try {
            // Create socket to connect to server
            this.sock = new Socket(this.serverIP, this.serverPort);

            // Create output stream to send messages to server
            this.oos = new ObjectOutputStream(
                    sock.getOutputStream()
            );

//            this.gui.reset();                    //??
            this.gui.disable();
            this.gui.repaint();

            // Create a new thread to listen to messages from server
            Thread thread = new Thread(new ServerHandler());
            thread.start();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     *  This is an inner class that listens to messages from the server.
     */
    public class ServerHandler implements Runnable {
        private BufferedReader reader = null;
        private ObjectInputStream oistream;

        public ServerHandler() {
            try {
                // creates an ObjectInputStream and chains it to the InputStream
                // of the server socket
                oistream = new ObjectInputStream(sock.getInputStream());
            } catch (IOException ex) {
                gui.printMsg("Error in creating an ObjectInputStream for the server");
                ex.printStackTrace();
            }
        }

        /**
         * Prepares for a new game before the game is started.
         */
        public synchronized void run(){
            CardGameMessage message;
            try {
                // waits for messages from the server
                while ((message = (CardGameMessage) oistream.readObject()) != null) {
//                    println("Message received from "
//                            + clientSocket.getRemoteSocketAddress());
                    parseMessage(message);

                } // close while
            } catch (Exception ex) {
                gui.printMsg("Error in receiving messages from the server");
                ex.printStackTrace();
            }
        }
    }

    /**
     * Parses the messages received from the game server.
     * @param message
     *            the specified message received from the server
     */
    public synchronized void parseMessage(GameMessage message) {
        // Based on the message type,
        // different actions will be carried out
        if (message.getType() == CardGameMessage.PLAYER_LIST) {
            setPlayerID(message.getPlayerID());
            gui.setActivePlayer(getPlayerID());
            String[] nameList = (String[]) message.getData();
            game.setNames(nameList);
            this.gui.exist = this.game.exist;
            gui.updates(game);
            // set exist if name != null
            gui.repaint();
            CardGameMessage reply = new CardGameMessage(CardGameMessage.JOIN, -1, getPlayerName());
            sendMessage(reply);
        }

        else if (message.getType() == CardGameMessage.JOIN) {
            int new_pi = message.getPlayerID();
            String new_name = (String) message.getData();
            game.setPlayerName(new_pi, new_name);
            gui.updates(game);
            gui.repaint();

            if (playerID == message.getPlayerID()) {
                CardGameMessage reply = new CardGameMessage(CardGameMessage.READY, -1, null);
                sendMessage(reply);
            }
        }

        else if(message.getType() == CardGameMessage.FULL) {
            playerID = -1;
            gui.printMsg("The game is full now!\n");
            gui.repaint();
        }

        else if(message.getType() == CardGameMessage.QUIT) {
            for (int i=0; i<4;i++) {
                if (playerID == message.getPlayerID()) {
                    setPlayerName(null);
                    gui.setNotExist(message.getPlayerID());
                    if (!game.endOfGame()) {
                        gui.disable();
                        CardGameMessage reply = new CardGameMessage(CardGameMessage.READY, -1, null);
                        sendMessage(reply);
                    }
                }
            }
            gui.repaint();

        }

        else if(message.getType() == CardGameMessage.READY) {
            String readyPlayer = game.getPlayerList().get(message.getPlayerID()).getName();
            gui.printMsg(readyPlayer + " is ready for the game!\n");
            gui.repaint();
        }

        else if(message.getType() == CardGameMessage.START) {
            game.start((BigTwoDeck) message.getData());
            gui.printMsg("Game is started now!\n\n");
            gui.enable();
            gui.repaint();
        }

        else if(message.getType() == CardGameMessage.MOVE) {
            game.checkMove(message.getPlayerID(), (int[])message.getData());
            gui.repaint();
        }

        else if(message.getType() == CardGameMessage.MSG) {
            gui.printChat((String) message.getData());
            gui.repaint();
        }

    }


    /**
     * Sends  the specified message to the game server.
     * @param message
     *            the specified message to be sent the server
     */
    public synchronized void sendMessage(GameMessage message) {

        try {
            this.oos.writeObject(message);
        } catch (IOException ex) {
            this.gui.printMsg("Error: cannot write object to the server.");
            ex.printStackTrace();
        }
    }
}
