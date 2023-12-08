package pt.tecnico.groove.domain;

import javax.annotation.processing.Generated;
import javax.persistence.*;

import java.util.List;
import java.util.ArrayList;

import pt.tecnico.groove.domain.Owner;

@Entity
@Table(name = "song")
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToMany
    private List<Owner> owners = new ArrayList<Owner>();

    @Column(name = "title", nullable = false, columnDefinition = "VARCHAR(255)")
    private String title;

    @Column(name = "lyrics", nullable = false, columnDefinition = "TEXT")
    private String lyrics;

    @Column(name = "format", nullable = false, columnDefinition = "VARCHAR(255)")
    private String format;

    @Column(name = "artist", nullable = false, columnDefinition = "VARCHAR(255)")
    private String artist;

    @Column(name = "song_path", nullable = false, columnDefinition = "VARCHAR(255)")
    private String songPath;

    public Song() {
    }

    public Song(Integer id, String title, String lyrics, String format, String artist, String songPath) {
        this.id = id;
        this.title = title;
        this.lyrics = lyrics;
        this.format = format;
        this.artist = artist;
        this.songPath = songPath;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Owner> getOwners() {
        return owners;
    }

    public void addOwner(Owner owner) {
        this.owners.add(owner);
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

    public String getSongPath() {
        return songPath;
    }

    public void setSongPath(String songPath) {
        this.songPath = songPath;
    }

    @Override
    public String toString() {
        return "Song [id=" + id + ", title=" + title + ", lyrics=" + lyrics + ", format=" + format + ", artist="
                + artist + ", songPath=" + songPath + "]";
    }

}
