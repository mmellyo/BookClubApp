package model;

public class Club {
    private String name;
    private String description;
    private byte[] coverImage;
    private String admin_id;

    public Club(String name, byte[] coverImage) {
        this.name = name;
        this.coverImage = coverImage;
    }

    public String getName() {
        return name;
    }

    public byte[] getCoverImage() {
        return coverImage;
    }

    public String getAdmin_id() {
        return admin_id;
    }

    public String getDescription() {
        return description;
    }
}
