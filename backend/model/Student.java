package model;

public class Student extends User {

    private String department;

    public Student(int id, String name, String email, String department) {
        super(id, name, email);
        this.department = department;
    }

    public String getDepartment() {
        return department;
    }

    @Override
    public void displayUser() {
        super.displayUser();
        System.out.println("Department: " + department);
    }
}