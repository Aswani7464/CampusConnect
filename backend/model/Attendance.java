package model;

public class Attendance {

    private int attendanceId;
    private int studentId;
    private int eventId;
    private String status;
    private String attendanceDate;

    public Attendance(
            int attendanceId,
            int studentId,
            int eventId,
            String status,
            String attendanceDate) {

        this.attendanceId = attendanceId;
        this.studentId = studentId;
        this.eventId = eventId;
        this.status = status;
        this.attendanceDate = attendanceDate;
    }

    public int getAttendanceId() {
        return attendanceId;
    }

    public int getStudentId() {
        return studentId;
    }

    public int getEventId() {
        return eventId;
    }

    public String getStatus() {
        return status;
    }

    public String getAttendanceDate() {
        return attendanceDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void displayAttendance() {

        System.out.println(
                "Attendance ID: "
                        + attendanceId
        );

        System.out.println(
                "Student ID: "
                        + studentId
        );

        System.out.println(
                "Event ID: "
                        + eventId
        );

        System.out.println(
                "Status: "
                        + status
        );

        System.out.println(
                "Attendance Date: "
                        + attendanceDate
        );
    }
}