package tpm.employee.service;

/**
 * Created by Infinal on 4/25/2017.
 */

public class LogsDetails {

    private String orderID,studentID,prevLoc,curLoc,checkinTime;
    public LogsDetails(String orderID, String studentID, String prevLoc, String curLoc, String checkinTime){
        this.orderID =orderID;
        this.studentID =studentID;
        this.prevLoc = prevLoc;
        this.curLoc = curLoc;
        this.checkinTime = checkinTime;
    }

    public String getOrderID() {
        return orderID;
    }

    public String getStudentID() {
        return studentID;
    }

    public String getPrevLoc() {
        return prevLoc;
    }

    public String getCurLoc() {
        return curLoc;
    }

    public String getCheckinTime() {
        return checkinTime;
    }
}
