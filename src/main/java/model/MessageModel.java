package model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class MessageModel {
    private int userId;
    private String username;
    private String content;
    private Timestamp created_at;

    public MessageModel(int userId, String username, String content, Timestamp created_at) {
        this.userId = userId;
        this.username = username;
        this.content = content;
        this.created_at = created_at;
    }

    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getContent() { return content; }
    public Timestamp getcreated_at() { return created_at; }
    public String getFormattedTime() {
        if (created_at != null) {
            return new SimpleDateFormat("hh:mm a").format(created_at);
        }
        return "";
    }

}
