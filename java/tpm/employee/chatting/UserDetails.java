package tpm.employee.chatting;

/**
 * Created by YouCaf Iqbal on 3/7/2017.
 */

public class UserDetails {
    public String userID,userName;


    public UserDetails(String userID, String userName) {
        this.userID = userID;
        this.userName = userName;
    }

    public String getUserID() {
        return userID;
    }

    public String getUserName() {
        return userName;
    }
}
