package pt.tecnico.groove.domain;

import javax.annotation.processing.Generated;
import javax.persistence.*;

import com.google.gson.JsonObject;

import java.util.List;
import java.util.ArrayList;

import pt.tecnico.groove.domain.Song;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<Song> songs = new ArrayList<Song>();

    @Column(name = "name", nullable = false, columnDefinition = "VARCHAR(255)")
    private String name;

    @Column(name = "master_key_file", nullable = false, columnDefinition = "VARCHAR(255)")
    private String masterkeyFile;

    @Column(name = "user_key_file", nullable = true, columnDefinition = "VARCHAR(255)")
    private String userkeyFile;

    public User() {
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
        song.addUser(this);
        this.songs.add(song);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMasterKeyFile() {
        return masterkeyFile;
    }

    public void setMasterkeyFile(String masterkeyFile) {
        this.masterkeyFile = masterkeyFile;
    }

    public String getUserKeyFile() {
        return userkeyFile;
    }

    public void setUserkeyFile(String userkeyFile) {
        this.userkeyFile = userkeyFile;
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", name=" + name + ", masterKeyFile=" + masterkeyFile + ", userKeyFile="
                + userkeyFile + "]";
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("id", getId());
        json.addProperty("name", getName());
        return json;
    }

}
