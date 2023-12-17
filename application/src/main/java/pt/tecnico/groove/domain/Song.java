package pt.tecnico.groove.domain;

import javax.annotation.processing.Generated;
import javax.persistence.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import pt.tecnico.groove.domain.User;

@Entity
@Table(name = "song")
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private User user = null;

    @Column(name = "title", nullable = false, columnDefinition = "VARCHAR(255)")
    private String title;

    @Column(name = "lyrics", nullable = false, columnDefinition = "TEXT")
    private String lyrics;

    @Column(name = "format", nullable = false, columnDefinition = "VARCHAR(255)")
    private String format;

    @Column(name = "artist", nullable = false, columnDefinition = "VARCHAR(255)")
    private String artist;

    @Column(name = "song_base64", nullable = false, columnDefinition = "TEXT")
    private String songBase64;

    public Song() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void addUser(User user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getSongBase64() {
        return songBase64;
    }

    public void setSongBase64(String songBase64) {
        this.songBase64 = songBase64.trim();
    }

    @Override
    public String toString() {
        return "Song [id=" + id + ", user=" + user + ", title=" + title + ", lyrics=" + lyrics + ", format="
                + format + ", artist=" + artist + ", songBase64=" + songBase64 + "]";
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        JsonObject mediaObject = new JsonObject();

        JsonObject mediaInfoObject = new JsonObject();
        mediaInfoObject.addProperty("owner", user != null ? user.getName() : null);
        mediaInfoObject.addProperty("format", format);
        mediaInfoObject.addProperty("artist", artist);
        mediaInfoObject.addProperty("title", title);

        mediaObject.add("mediaInfo", mediaInfoObject);

        JsonObject mediaContentObject = new JsonObject();

        JsonArray lyricsArray = new JsonArray();
        try (Scanner scanner = new Scanner(getLyrics())) {
            while (scanner.hasNextLine()) {
                lyricsArray.add(scanner.nextLine());
            }
        }
        mediaContentObject.add("lyrics", lyricsArray);

        mediaContentObject.addProperty("audioBase64", songBase64);

        mediaObject.add("mediaContent", mediaContentObject);

        json.add("media", mediaObject);

        return json;
    }

}
