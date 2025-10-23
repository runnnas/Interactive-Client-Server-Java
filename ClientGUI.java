
/**
 * ClientGUI.java
 * 
 * This is the CLients GUI that connects to our Server_A via sockets.
 * It sends and receives messages in the format:
 * "Sender: User_A; Receiver: Server_A; Pay load: <command>"
 * 
 * 
 * */
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.DefaultCaret;

public class ClientGUI extends JFrame {

    // GUI components
    private JTextArea messageArea;
    private JTextField inputField;
    private JButton sendButton, connectButton;

    // Socket communication
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;

    // Identifiers for the message format
    private final String USER_ID = "User_A";
    private final String SERVER_ID = "Server_A";

    /**
     * Constructor - setting up the  GUI
     */
    public ClientGUI() {
        super("J.A.V.A. Console - Client GUI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 380); 
        getRootPane().setBorder(new LineBorder(new Color(0,255,255,50), 1, true));
        setLocationRelativeTo(null);
        setResizable(false);
        initUI();
    }

    /**
     * The GUI layout and event handlers
     */
    private void initUI() {
        // Background gradient panel
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(10, 20, 30),
                        0, getHeight(), new Color(0, 0, 0));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Header label
        JLabel header = new JLabel("J.A.V.A. CLIENT INTERFACE");
        header.setForeground(new Color(0, 255, 255));
        header.setFont(new Font("Consolas", Font.BOLD, 20));
        header.setHorizontalAlignment(SwingConstants.CENTER);
        header.setBorder(new EmptyBorder(10, 0, 20, 0));
        mainPanel.add(header, BorderLayout.NORTH);

        // Message display area
        messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setBackground(new Color(15, 25, 35));
        messageArea.setForeground(new Color(0, 255, 255));
        messageArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        messageArea.setCaretColor(Color.CYAN);
        messageArea.setBorder(new LineBorder(new Color(0, 255, 255, 90), 2, true));

        // Auto-scroll setup
        DefaultCaret caret = (DefaultCaret) messageArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        JScrollPane scroll = new JScrollPane(messageArea);
        scroll.setBorder(new LineBorder(new Color(0, 255, 255, 50), 2, true));
        mainPanel.add(scroll, BorderLayout.CENTER);

        // Bottom input area
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        inputField = new JTextField();
        inputField.setBackground(new Color(10, 20, 30));
        inputField.setForeground(new Color(0, 255, 255));
        inputField.setCaretColor(Color.CYAN);
        inputField.setFont(new Font("Consolas", Font.PLAIN, 14));
        inputField.setBorder(new LineBorder(new Color(0, 255, 255, 80), 2, true));
        bottomPanel.add(inputField, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setOpaque(false);

        connectButton = createGlowingButton("CONNECT");
        sendButton = createGlowingButton("SEND");
        sendButton.setEnabled(false);

        buttonPanel.add(connectButton);
        buttonPanel.add(sendButton);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        add(mainPanel);

        // Event handlers
        connectButton.addActionListener(e -> handleConnect());
        sendButton.addActionListener(e -> sendCommand());
        inputField.addActionListener(e -> sendCommand());
    }

    /**
     * Creates a glowing, futuristic-style button
     */
    private JButton createGlowingButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Consolas", Font.BOLD, 14));
        button.setForeground(Color.CYAN);
        button.setBackground(new Color(10, 20, 30));
        button.setFocusPainted(false);
        button.setBorder(new LineBorder(new Color(0, 255, 255, 90), 2, true));

        // Hover glow effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setForeground(Color.WHITE);
                button.setBorder(new LineBorder(new Color(0, 255, 255), 3, true));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setForeground(Color.CYAN);
                button.setBorder(new LineBorder(new Color(0, 255, 255, 90), 2, true));
            }
        });
        return button;
    }

    /**
     * Handles Connect / Disconnect logic
     */
    private void handleConnect() {
        if (socket == null || socket.isClosed()) {
            connectToServer();
        } else {
            disconnect();
        }
    }

    /**
     * Connects to the server
     */
    private void connectToServer() {
        String host = JOptionPane.showInputDialog(this, "Enter Server Host:", "localhost");
        if (host == null) return;
        String portStr = JOptionPane.showInputDialog(this, "Enter Server Port:", "5050");
        if (portStr == null) return;

        try {
            int port = Integer.parseInt(portStr);
            socket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            appendMessage(">>> CONNECTED TO " + host + ":" + port + "\n");
            sendButton.setEnabled(true);
            connectButton.setText("DISCONNECT");

            new Thread(this::listenForResponses).start();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Connection failed: " + e.getMessage());
        }
    }

    /**
     * Continuously listens for server messages
     */
    private void listenForResponses() {
        try {
            String response;
            while ((response = in.readLine()) != null) {
                final String resp = response;
                // Update the GUI safely from the Swing UI thread
                SwingUtilities.invokeLater(() -> {
                    appendMessage("SERVER ➜ " + resp + "\n");
                    messageArea.paintImmediately(messageArea.getBounds());
                });
            }
        } catch (IOException e) {
            SwingUtilities.invokeLater(() -> appendMessage(">>> CONNECTION CLOSED.\n"));
        } finally {
            disconnect();
        }
    }

    /**
     * Sends the command entered by the user
     */
    private void sendCommand() {
        String command = inputField.getText().trim();
        if (command.isEmpty()) return;

        String message = String.format("Sender: %s; Receiver: %s; Payload: %s",
                USER_ID, SERVER_ID, command);
        try {
            out.write(message);
            out.newLine();
            out.flush();
            appendMessage("YOU ➜ " + message + "\n");
            inputField.setText("");

            if (command.equalsIgnoreCase("Exit")) {
                new Thread(() -> {
                    try { Thread.sleep(300); } catch (InterruptedException ignored) {}
                    disconnect();
                }).start();
            }
        } catch (IOException e) {
            appendMessage(">>> ERROR SENDING MESSAGE.\n");
        }
    }

    /**
     * Disconnects from the server
     */
    private void disconnect() {
        try {
            // Close all I/O streams first
            if (in != null) in.close();
            if (out != null) out.close();
            // Then close the socket itself
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException ignored) {}

        // Reset GUI state
        sendButton.setEnabled(false);
        connectButton.setText("CONNECT");

        // Show message instantly
        SwingUtilities.invokeLater(() -> {
            appendMessage(">>> DISCONNECTED.\n");
            messageArea.paintImmediately(messageArea.getBounds());
        });
    }
    /**
     * Appends text to the console area
     */
    private void appendMessage(String text) {
        messageArea.append(text);
    }

    /**
     * Launches the GUI
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ClientGUI gui = new ClientGUI();
            gui.setVisible(true);
        });
    }
}