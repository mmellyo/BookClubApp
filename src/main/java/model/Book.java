package model;

public class Book {
    private String title;
    private String author;
    private String description;
    private byte[] coverBytes;
    private int book_id;

    public Book(String title, String author, byte[] coverBytes, String description) {
        this.title = title;
        this.author = author;
        this.coverBytes = coverBytes;
        this.description = description;
    }
    public Book(String title, byte[] coverBytes) {
        this.title = title;
        this.coverBytes = coverBytes;
    }

    public Book(String title, byte[] coverBytes, int book_id) {
        this.title = title;
        this.coverBytes = coverBytes;
        this.book_id = book_id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public byte[] getCoverBytes() {
        return coverBytes;
    }

    public int getBook_id() {
        return book_id;
    }


}
