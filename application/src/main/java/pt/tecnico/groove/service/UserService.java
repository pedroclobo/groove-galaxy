package pt.tecnico.groove.service;

import java.security.Key;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pt.tecnico.groove.domain.User;
import pt.tecnico.groove.repository.UserRepository;
import pt.tecnico.groove.domain.Family;
import pt.tecnico.groove.domain.Song;
import pt.tecnico.groove.repository.SongRepository;

import pt.tecnico.AESKeyGenerator;
import pt.tecnico.groove.service.KeyService;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SongRepository songRepository;

    public JsonObject getAllUsers() throws Exception {
        JsonObject json = new JsonObject();
        JsonObject usersObject = new JsonObject();

        for (User user : userRepository.findAll()) {
            JsonObject userJson = new JsonObject();
            userJson.addProperty("name", user.getName());

            if (user.getFamily() != null) {
                userJson.addProperty("family_id", user.getFamily().getId());
            } else {
                userJson.addProperty("family_id", "null");
            }

            usersObject.add(user.getId().toString(), userJson);
        }

        json.add("users", usersObject);
        return json;
    }

    public JsonObject createUserKey(Integer id) throws Exception {
        User user = userRepository.findById(id).orElseThrow();

        String userKeyFile = "user_" + user.getId() + "_key.key";

        Key key = AESKeyGenerator.genKey();

        KeyService.writeSecretKey(userKeyFile, key);

        user.setUserkeyFile(userKeyFile);
        userRepository.save(user);

        // Read master key
        Key masterKey = KeyService.readSecretKey(user.getMasterKeyFile());

        return KeyService.protectKey(key, masterKey);
    }

    public JsonObject getAllUserSongs(Integer id) throws Exception {
        Set<Song> songs = new HashSet<Song>();

        JsonObject json = new JsonObject();
        JsonObject songsObject = new JsonObject();

        User user = userRepository.findById(id).orElseThrow();

        Family family = user.getFamily();
        if (family != null) {
            for (User familyUser : family.getUsers()) {
                for (Song song : familyUser.getSongs()) {
                    songs.add(song);
                }
            }
            for (Song song : songs) {
                songsObject.addProperty(song.getId().toString(), song.getTitle());
            }
        } else {
            for (Song song : user.getSongs()) {
                songsObject.addProperty(song.getId().toString(), song.getTitle());
            }
        }

        json.add("songs", songsObject);
        return json;
    }

    public JsonObject getFamily(Integer user_id) throws Exception {
        User user = userRepository.findById(user_id).orElseThrow();

        if (user.getFamily() == null) {
            JsonObject json = new JsonObject();
            json.addProperty("error", "User " + user.getName() + " with id " + user.getId() + " does not have a family");
            return json;
        }

        return user.getFamily().toJson();
    }

    public JsonObject getFamilyKey(Integer user_id) throws Exception {
        User user = userRepository.findById(user_id).orElseThrow();

        if (user.getFamily() == null) {
            JsonObject json = new JsonObject();
            json.addProperty("error", "User " + user.getName() + " with id " + user.getId() + " does not have a family");
            return json;
        }

        Family family = user.getFamily();
        User owner = family.getOwner();

        Key key = KeyService.readSecretKey(owner.getUserKeyFile());
        Key masterKey = KeyService.readSecretKey(user.getMasterKeyFile());

        String userKeyFile = "user_" + user.getId() + "_key.key";
        KeyService.writeSecretKey(userKeyFile, key);

        return KeyService.protectKey(key, masterKey);
    }
}