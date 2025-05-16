package model;

public class BookModel {
    private int bookId;
    private String title;
    private String author_first_name;
    private String author_last_name;
    private String description;
    private int page_nbr;
    private byte[] cover_picture;


    public BookModel(int bookId, String title, String author_first_name,   String author_last_name, String description,  int page_nbr, byte[] cover_picture) {
        this.bookId = bookId;
        this.title = title;
        this.author_first_name = author_first_name;
        this.author_last_name = author_last_name;
        this.description = description;
        this.page_nbr = page_nbr;
        this.cover_picture = cover_picture;


    }

    // Getters
    public int getBookId() { return bookId; }
    public String getTitle() { return title; }
    public String getAuthorLn() { return author_last_name; }
    public String getAuthorFn() { return author_first_name;}
    public byte[] getCover() {return cover_picture;}
}
