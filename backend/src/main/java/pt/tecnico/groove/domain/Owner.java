package pt.tecnico.groove.domain;

import javax.annotation.processing.Generated;
import javax.persistence.*;

import java.util.List;
import java.util.ArrayList;

import pt.tecnico.groove.domain.Song;

@Entity
@Table(name = "owner")
public class Owner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "owners")
    private List<Song> songs = new ArrayList<Song>();

    @Column(name = "name", nullable = false, columnDefinition = "VARCHAR(255)")
    private String name;

    @Column(name = "key_file", nullable = false, columnDefinition = "VARCHAR(255)")
    private String keyFile;

    public Owner(Integer id, String name, String keyFile) {
        this.id = id;
        this.name = name;
        this.keyFile = keyFile;
    }

    public Owner() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void addSong(Song song) {
        song.addOwner(this);
        this.songs.add(song);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKeyFile() {
        return keyFile;
    }

    public void setKeyFile(String keyFile) {
        this.keyFile = keyFile;
    }

    @Override
    public String toString() {
        return "Owner [id=" + id + ", name=" + name + ", keyFile=" + keyFile + "]";
    }

}
