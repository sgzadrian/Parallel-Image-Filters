import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class ClientGUI {

    private JFrame mainFrame;
    private JPanel paneRegister;
    private JPanel paneMessageLog;
    private JPanel paneSendMessage;
    private JScrollPane scrollPane;

    private JButton sendBtn;
    private JButton connectBtn;

    private JTextField serverInput;
    private JTextField usernameInput;
    private JTextField msgInput;
    private JTextField destinyUserInput;
    private JTextArea chatArea;

    private ChatInterface server = null;
    private Chat client = null;

    public ClientGUI() {
        this( "Chat" );
    }

    public ClientGUI( String name ) {
        initializeGUIComponents( name );
    }

    private void initializeGUIComponents( String frameName ) {
        /* Initialize the components */
        mainFrame = new JFrame( frameName );
        sendBtn = new JButton( "Send" );
        sendBtn.addActionListener( this::sendMessage );
        connectBtn = new JButton( "Connect" );
        connectBtn.addActionListener( this::connectServer );
        serverInput = new JTextField( "192.168.84.115" );
        usernameInput = new JTextField( "User 1" );
        msgInput = new JTextField( "Send a message..." );
        destinyUserInput = new JTextField( "To..." );
        chatArea = new JTextArea( 5, 20 );
        chatArea.setEditable( false );
        scrollPane = new JScrollPane( chatArea );
        /* Panel - registration initialization */
        paneRegister = new JPanel( new GridLayout( 2, 3 ) );
        paneRegister.setPreferredSize( new Dimension( 640, 64 ) );
        paneRegister.setBorder( new EmptyBorder( 8, 8, 8, 8 ) );
        paneRegister.add( new JLabel( "Server IP" ) );
        paneRegister.add( new JLabel( "Username" ) );
        paneRegister.add( new JLabel( "" ) );
        paneRegister.add( serverInput );
        paneRegister.add( usernameInput );
        paneRegister.add( connectBtn );
        /* Panel - messages log initialization */
        paneMessageLog = new JPanel( new BorderLayout() );
        paneMessageLog.setPreferredSize( new Dimension( 640, 294 ) );
        paneMessageLog.setBorder( new EmptyBorder( 8, 8, 8, 8 ) );
        paneMessageLog.add( new JLabel( "Chat" ), BorderLayout.PAGE_START );
        paneMessageLog.add( scrollPane, BorderLayout.CENTER );
        /* Panel - send message initialization */
        paneSendMessage = new JPanel( new GridLayout( 0, 3 ) );
        paneSendMessage.setPreferredSize( new Dimension( 640, 42 ) );
        paneSendMessage.setBorder( new EmptyBorder( 8, 8, 8, 8 ) );
        paneSendMessage.add( destinyUserInput );
        paneSendMessage.add( msgInput );
        paneSendMessage.add( sendBtn );
        /* Add the panels to frame */
        mainFrame.add( paneRegister, BorderLayout.NORTH );
        mainFrame.add( paneMessageLog, BorderLayout.CENTER );
        mainFrame.add( paneSendMessage, BorderLayout.SOUTH );
        /* Finally, show the frame */
        mainFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        mainFrame.setSize( 640, 400 );
        mainFrame.setResizable( false );
        mainFrame.setVisible( true );
    }

    public void saveMessage( String msg ) {
        chatArea.append( msg + "\n" );
    }

    public String getServerAddress() {
        return serverInput.getText().trim();
    }

    public String getSendMessage() {
        return "[" + usernameInput.getText().trim() + "] "+ msgInput.getText().trim() + " \n";
    }

    public String getUsername() {
        return usernameInput.getText().trim();
    }

    public void connect() throws MalformedURLException, RemoteException, NotBoundException {
        String address = getServerAddress();
        if ( address.length() < 7 ) {
            address = "localhost";
        }
        server = ( ChatInterface ) Naming.lookup( "//"+ address +"/Chat" );
        client = new Chat( getUsername() );
        client.setOutput( chatArea );
        server.setClient( client );
        saveMessage( "Client connected \n" );
    }

    public void connectServer( ActionEvent evt ) {
        try {
            connect();
        } catch ( Exception e ) {
            addToChat( "!!! Can't connect to the server \n" );
            return;
        }
    }

    public void addToChat( String msg ) {
        chatArea.append( msg );
    }

    public void sendMessage( ActionEvent evt ) {
        if ( server == null ) {
            try {
                connect();
            } catch ( Exception e ) {
                addToChat( "!!! Can't connect to the server \n" );
                return;
            }
        }
        String target = destinyUserInput.getText().trim();
        if ( target.length() < 1 ) {
            return;
        }
        try {
            String msg = getSendMessage();
            server.send( msg , target );
            saveMessage( msg );
            msgInput.setText( "Message..." );
        } catch ( Exception e ) {
            addToChat( "!! Message can't be sent\n" );
        }
    }

    public JFrame getFrame() { return mainFrame; }

    public int onDestroy() {
        try {
            server.removeClient( client.getName() );
        } catch (Exception e) {
            System.out.println( "F*" );
        }
        return JFrame.EXIT_ON_CLOSE;
    }

}
