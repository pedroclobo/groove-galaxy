package pt.tecnico.groove.service;

import java.security.Key;

import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pt.tecnico.groove.domain.User;
import pt.tecnico.groove.repository.UserRepository;

import pt.tecnico.AESKeyGenerator;
import pt.tecnico.groove.service.KeyService;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public JsonObject getAllUsers() throws Exception {
        JsonObject json = new JsonObject();
        JsonObject usersObject = new JsonObject();

        for (User user : userRepository.findAll()) {
            usersObject.addProperty(user.getId().toString(), user.getName());
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
}