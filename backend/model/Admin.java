package model;

public class Admin extends User {

    private String adminLevel;

    public Admin(int id, String name, String email, String adminLevel) {
        super(id, name, email);
        this.adminLevel = adminLevel;
    }

    public String getAdminLevel() {
        return adminLevel;
    }

    @Override
    public void displayUser() {
        super.displayUser();
        System.out.println("Admin Level: " + adminLevel);
    }
}