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
