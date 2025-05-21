package model;

public class Club {
    private String name;
    private String description;
    private byte[] coverImage;
    private int admin_id;

    public Club(String name, byte[] coverImage,int admin_id) {
        this.name = name;
        this.coverImage = coverImage;

        this.admin_id =  admin_id;
    }

    public String getName() {
        return name;
    }

    public byte[] getCoverImage() {
        return coverImage;
    }

    public int getAdmin_id() {
        return admin_id;
    }

    public String getDescription() {
        return description;
    }
}
