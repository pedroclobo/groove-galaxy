package pt.tecnico.groove.dto;

import pt.tecnico.groove.domain.Song;

import java.io.Serializable;

public class SongDto implements Serializable {

    private String title;
    private String lyrics;
    private String format;
    private String artist;
    private String songBase64;

    public SongDto() {
    }

    public SongDto(Song song) {
        this.title = song.getTitle();
        this.lyrics = song.getLyrics();
        this.format = song.getFormat();
        this.artist = song.getArtist();
        this.songBase64 = song.getSongBase64();
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
        this.songBase64 = songBase64;
    }

    @Override
    public String toString() {
        return "SongDto [title=" + title + ", lyrics=" + lyrics + ", format=" + format + ", artist="
                + artist + ", songBase64=" + songBase64 + "]";
    }

}
