package pt.tecnico.groove.domain;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.google.gson.JsonObject;

@Entity
@Table(name = "family")
public class Family {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    private User owner;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "family")
    private List<User> users = new ArrayList<User>();

    public Family() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getOwner(){
        return owner;
    }

    public void setOwner(User owner) throws Exception{
        if (owner.getFamily() != null) {
            throw new Exception("User already has a family");
        }

        this.owner = owner;
    }

    public List<User> getUsers(){
        return users;
    }

    public void addUser(User user) throws Exception {
        if(this.owner.equals(user)) {
            throw new Exception("User is already the owner of the family");
        }

        if(user.getFamily() != null) {
            throw new Exception("User already has a family");
        }

        this.users.add(user);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("id", this.id);
        json.addProperty("owner_id", this.owner.getId());
        
        JsonObject usersObject = new JsonObject();
        
        for (User user : this.users) {
            usersObject.addProperty(user.getId().toString(), user.getName());
        }

        json.add("users", usersObject);

        return json;
    }
}
