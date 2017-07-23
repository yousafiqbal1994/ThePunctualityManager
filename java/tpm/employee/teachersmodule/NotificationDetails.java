package tpm.employee.teachersmodule;

/**
 * Created by YouCaf Iqbal on 3/22/2017.
 */

public class NotificationDetails {

    public String nID;
    public String nTitle;
    public String nMessage;
    public String nFrom; // Sender ID
    public String nTo; // to all people or ID if specific
    public String sendingTime;

    public NotificationDetails(String nID, String nTitle, String nMessage,String nFrom, String nTo, String sendingTime) {
        this.nID = nID;
        this.nTitle = nTitle;
        this.nMessage = nMessage;
        this.nFrom = nFrom;
        this.nTo = nTo;
        this.sendingTime = sendingTime;
    }

    public String getnID() {
        return nID;
    }

    public String getnTitle() {
        return nTitle;
    }

    public String getnMessage() {
        return nMessage;
    }

    public String getnTo() {
        return nTo;
    }

    public String getSendingTime() {
        return sendingTime;
    }

    public String getnFrom() {
        return nFrom;
    }
}
