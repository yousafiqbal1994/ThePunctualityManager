package tpm.employee.chatting;

/**
 * Created by YouCaf Iqbal on 3/22/2017.
 */

public class StudentNotificationClass {
    public String notID,title,nameofTeacher,TeacherID,Message,myReply;
    public StudentNotificationClass(String notID,String title,String nameofTeacher, String TeacherID, String Message, String myReply) {
        this.notID = notID;
        this.title = title;
        this.nameofTeacher = nameofTeacher;
        this.TeacherID = TeacherID;
        this.Message =Message;
        this.myReply =myReply;
    }

    public String getTitle() {
        return title;
    }

    public String getNameofTeacher() {
        return nameofTeacher;
    }

    public String getTeacherID() {
        return TeacherID;
    }

    public String getMessage() {
        return Message;
    }

    public String getMyReply() {
        return myReply;
    }

    public String getNotID() {
        return notID;
    }
}
