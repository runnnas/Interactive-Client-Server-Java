```markdown
# Interactive-Client-Server-Java

## Project Overview
This is a Java client-server application where a **terminal-based server** manages a list of integers (`inputValues`) and a **GUI-based client** sends structured commands to interact with it. The client displays server responses in a readable format, allowing users to perform operations on the list dynamically.

The application demonstrates **socket programming**, **interactive communication**, and **basic GUI design** in Java.

---

## Supported Commands

| Command           | Description                                                                 |
|------------------|-----------------------------------------------------------------------------|
| `Add [x]`        | Add integer `x` to the list                                                 |
| `Remove [x]`     | Remove all occurrences of integer `x` from the list                         |
| `Clear`          | Clear all values from the list                                              |
| `Get_Summation`  | Calculate and return the sum of all numbers in the list                     |
| `Get_Minimum`    | Return the minimum number in the list                                       |
| `Get_Maximum`    | Return the maximum number in the list                                       |
| `Display_Content`| Show all numbers currently stored in the list                               |
| `Exit`           | Close the client application (server continues running)                     |

**Message Format**:  
Sender: <Sender_ID>; Receiver: <Receiver_ID>; Payload: <Command>

Example:  
Sender: User_A; Receiver: Server_A; Payload: Add 54

---

## Project Structure
Interactive-Client-Server-Java/
├─ Server/ → Terminal-based server
│ └─ Server_A.java
├─ Client/ → GUI-based client
│ └─ ClientGUI_.java
└─ README.md → Project overview and instructions

---

## How to Run

1. **Compile the server and client**

```bash
javac Server/Server_A.java
javac Client/ClientGUI_.java
```

2. **Start the server**

```bash
java Server.Server_A
```

3. **Start the client**

```bash
java Client.ClientGUI_
```

Use the GUI to select or type commands. The server will respond according to the operations performed.

## Requirements
- Java JDK 11 or higher
- GUI library (Swing or JavaFX, depending on your implementation)
- Basic terminal/command-line access

---
```
