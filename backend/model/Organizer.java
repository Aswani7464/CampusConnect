package model;

public class Organizer extends User {

    private String organization;

    public Organizer(int id, String name, String email, String organization) {
        super(id, name, email);
        this.organization = organization;
    }

    public String getOrganization() {
        return organization;
    }

    @Override
    public void displayUser() {
        super.displayUser();
        System.out.println("Organization: " + organization);
    }
}