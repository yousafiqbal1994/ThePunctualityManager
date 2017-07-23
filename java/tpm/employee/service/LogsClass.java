package tpm.employee.service;

/**
 * Created by Infinal on 4/27/2017.
 */

public class LogsClass {

    private String pdName;
    private String checkInTime;
    private String checkOutTime;
    private String duration;
    public LogsClass(String pdName, String checkInTime, String checkOutTime, String duration){
        this.pdName = pdName;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.duration = duration;
    }
    public LogsClass(String pdName, String checkInTime, String checkOutTime){
        this.pdName = pdName;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
    }
    public String getPdName() {
        return pdName;
    }

    public String getDuration() {
        return duration;
    }
    public String getCheckInTime() {
        return checkInTime;
    }

    public String getCheckOutTime() {
        return checkOutTime;
    }
}
