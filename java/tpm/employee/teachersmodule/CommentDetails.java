package tpm.employee.teachersmodule;

/**
 * Created by YouCaf Iqbal on 3/22/2017.
 */

public class CommentDetails {
    // time , senderName , text, idFrom, idTo

    public String cID; // ID of notification
    public String cFrom;  // ID of student who send it
    public String cText; // Actual reply text
    public String cSendingTime; // Sending time
    public String cSenderName; // Sender name


    public CommentDetails(String cID,String cFrom, String cText, String cSendingTime,String cSenderName) {
        this.cID = cID;
        this.cFrom = cFrom;
        this.cText = cText;
        this.cSendingTime =cSendingTime;
        this.cSenderName =cSenderName;
    }

    public String getcID() {
        return cID;
    }

    public String getcText() {
        return cText;
    }

    public String getcFrom() {
        return cFrom;
    }

    public String getcSendingTime() {
        return cSendingTime;
    }

    public String getcSenderName() {
        return cSenderName;
    }
}
