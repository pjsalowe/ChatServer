

import java.io.Serializable;
import java.util.Scanner;

/**
 *
 * ChatMessage
 *
 * @author Phillip
 * @version 4/26/2020
 */
final class ChatMessage implements Serializable {
    private static final long serialVersionUID = 6898543889087L;

    // Here is where you should implement the chat message object.
    // Variables, Constructors, Methods, etc.

    private String message;
    private int type;
    private String recipient;

    public ChatMessage(String message, int type) {
        this.message = message;
        this.type = type;

    }

    public ChatMessage(int type) {
        this.type = type;
    }

    public ChatMessage(String message, int type, String recipient) {
        this.message = message;
        this.type = type;
        this.recipient = recipient;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMessage() {
        /*Scanner scan = new Scanner(System.in);
        System.out.println("Servers message");
        message = scan.nextLine(); */
        return message;
    }

    public int getType() {
        return type;
    }
}
