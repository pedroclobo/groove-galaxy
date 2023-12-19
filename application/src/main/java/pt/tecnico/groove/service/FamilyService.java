package pt.tecnico.groove.service;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.gson.JsonObject;

import java.security.Key;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pt.tecnico.JsonProtector;
import pt.tecnico.groove.domain.Family;
import pt.tecnico.groove.domain.User;
import pt.tecnico.groove.domain.Song;
import pt.tecnico.groove.repository.FamilyRepository;
import pt.tecnico.groove.repository.UserRepository;

@Service
public class FamilyService {

    @Autowired
    private FamilyRepository familyRepository;

    @Autowired
    private UserRepository userRepository;

    public JsonObject addUserToFamily(Integer owner_id, Integer user_id) throws Exception {
        JsonObject json = new JsonObject();

        try {    
            User owner = userRepository.findById(owner_id).orElseThrow();
            if (owner.getUserKeyFile() == null) {
                json.addProperty("error", "User " + owner.getName() + " with id " + owner.getId() + " does not have a key");
                return json;
            }

            User user = userRepository.findById(user_id).orElseThrow();
            if (user.getFamily() != null) {
                json.addProperty("error", "User " + user.getName() + " with id " + user.getId() + " already has a family");
                return json;
            }

            Family family = owner.getFamily();
            if (family == null) {
                family = new Family();
                family.setOwner(owner);
                familyRepository.save(family);
                owner.setFamily(family);
                userRepository.save(owner);
            }

            family.addUser(user);
            familyRepository.save(family);
            
            user.setFamily(family);
            userRepository.save(user);

            Key key = KeyService.readSecretKey(owner.getUserKeyFile());
            String userKeyFile = "user_" + user.getId() + "_key.key";
            KeyService.writeSecretKey(userKeyFile, key);

            json.addProperty("family_id", family.getId());

            return json;
            
        } catch (Exception e) {
            json.addProperty("error", e.getMessage());
            return json;
        }
    }
}
