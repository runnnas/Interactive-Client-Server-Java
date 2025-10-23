/**
 * Server_A_.java
 * 
 * A terminal-based server that communicates with a GUI client.
 * It will process pay load commands like ADD, Remove, Clear, and Get-summation but may include more
 * Will use socket communication over TCP
 * 
 * */

 import java.io.*;
 import java.net.*;
 import java.util.*;
 import java.util.regex.*;
 
public class Server_A {

	//Message Identifiers
	public static final String SERVER_ID = "Server_A";
	public static final String USER_ID = "User_A";
	
	private ServerSocket serverSocket;
	private ArrayList<Integer> inputValues = new ArrayList<>(); // stores the added numbers
	
	
	/**
	 * Constructor - starts the server on the specified port
	 */
	
	public Server_A(int port) throws IOException {
		serverSocket = new ServerSocket(port);
		System.out.println("Startup the server side over port " + port);
	}
	
	/** 
	 * Wait for a client connection and handle messages 
	 */
	
	public void start() {
		try {
			while(true) {
				System.out.println("Waiting for client connection...");
				Socket client = serverSocket.accept();
				System.out.println("Connection with client established: " + client.getRemoteSocketAddress());
				handleClient(client);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try { serverSocket.close(); } catch (IOException ignored) {}
		}
	}
	/** 
	 * Handles one communication for one client session 
	 * */
	
	private void handleClient(Socket client) {
		try (
				BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
			) {
			
			String line;
            while ((line = in.readLine()) != null) {
                System.out.println("RAW received: " + line);

                // Extract the pay load (the actual command)
                String payload = extractPayload(line);
                if (payload == null) {
                    sendResponse(out, "received an unsupported command");
                    continue;
                }

                // Process commands and send results back
                String response = processPayload(payload.trim());
                sendResponse(out, response);

                // Exit command ends the client connection
                if (payload.equalsIgnoreCase("Exit")) {
                    System.out.println("Exit command received. Closing client connection.");
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Client disconnected: " + e.getMessage());
        } finally {
            try { client.close(); } catch (IOException ignored) {}
        }
    }

    /**
     * Extracts the part after "Pay load:" from the message
     * Example: "Sender: User_A; Receiver: Server_A; Pay load: Add 54"
     */
    private String extractPayload(String message) {
        String[] parts = message.split("(?i)payload:"); // case-insensitive split
        if (parts.length < 2) return null;
        return parts[1].trim();
    }

    /**
     * Sends a formatted response message to the client
     */
    private void sendResponse(BufferedWriter out, String payloadText) throws IOException {
        String response = String.format("Sender: %s; Receiver: %s; Payload: %s",
                SERVER_ID, USER_ID, payloadText);
        out.write(response);
        out.newLine();
        out.flush();
        System.out.println("Sent -> " + response);
    }

    /**
     * Handles all valid client commands and returns appropriate responses
     */
    private String processPayload(String payload) {
        try {
            // Normalize command format (remove extra spaces and handle colons)
            payload = payload.replaceAll("\\s*:\\s*", ":").trim();

            // Clear command
            if (payload.equalsIgnoreCase("Clear")) {
                inputValues.clear();
                return "cleared successfully";
            }

            // Display content
            if (payload.equalsIgnoreCase("Display_Content")) {
                return inputValues.isEmpty() ? "[]" : inputValues.toString();
            }

            // Get summation
            if (payload.equalsIgnoreCase("Get_Summation")) {
                if (inputValues.isEmpty()) return "The summation is null";
                int sum = inputValues.stream().mapToInt(Integer::intValue).sum();
                boolean allZeros = inputValues.stream().allMatch(v -> v == 0);
                return allZeros ? "The summation is null" : "The summation is " + sum;
            }

            // Get minimum
            if (payload.equalsIgnoreCase("Get_Minimum")) {
                if (inputValues.isEmpty()) return "The minimum is null";
                boolean allZeros = inputValues.stream().allMatch(v -> v == 0);
                return allZeros ? "The minimum is null"
                        : "The minimum is " + Collections.min(inputValues);
            }

            // Get maximum
            if (payload.equalsIgnoreCase("Get_Maximum")) {
                if (inputValues.isEmpty()) return "The maximum is null";
                boolean allZeros = inputValues.stream().allMatch(v -> v == 0);
                return allZeros ? "The maximum is null"
                        : "The maximum is " + Collections.max(inputValues);
            }

            // Exit command
            if (payload.equalsIgnoreCase("Exit")) {
                return "Exit acknowledged";
            }

            // Add command (e.g., Add 10 or Add: 10)
            Matcher addMatch = Pattern.compile("(?i)^Add:?\\s*(-?\\d+)$").matcher(payload);
            if (addMatch.find()) {
                int number = Integer.parseInt(addMatch.group(1));
                inputValues.add(number);
                return "added successfully";
            }

            // Remove command (e.g., Remove 5 or Remove: 5)
            Matcher removeMatch = Pattern.compile("(?i)^Remove:?\\s*(-?\\d+)$").matcher(payload);
            if (removeMatch.find()) {
                int number = Integer.parseInt(removeMatch.group(1));
                inputValues.removeIf(v -> v == number);
                return "removed successfully";
            }

            // Unsupported command
            return "received an unsupported command";

        } catch (Exception e) {
            return "received an unsupported command";
        }
    }

    /**
     * Main method - starts server on port 5050 by default because 5000 is in use
     */
    public static void main(String[] args) throws IOException {
        int port = 5050;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        new Server_A(port).start();
    }
}