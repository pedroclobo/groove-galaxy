package pt.tecnico.groove.service;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.gson.JsonObject;

import java.security.Key;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pt.tecnico.AESKeyGenerator;
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
            if (owner.getFamily() != null && owner.getFamily().getOwner() != owner) {
                json.addProperty("error", "User " + owner.getName() + " with id " + owner.getId() + " is not the owner of the family");
                return json;
            }
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
            
            Key key = KeyService.readSecretKey(owner.getUserKeyFile());
            String userKeyFile = "user_" + user.getId() + "_key.key";

            user.setUserkeyFile(userKeyFile);
            userRepository.save(user);

            KeyService.writeSecretKey(userKeyFile, key);

            json.addProperty("family_id", family.getId());

            return json;
            
        } catch (Exception e) {
            json.addProperty("error", e.getMessage());
            return json;
        }
    }

    public JsonObject removeUserFromFamily(Integer owner_id, Integer user_id) throws Exception {
        JsonObject json = new JsonObject();

        try {
            User owner = userRepository.findById(owner_id).orElseThrow();
            if (owner.getFamily() == null) {
                json.addProperty("error", "User " + owner.getName() + " with id " + owner.getId() + " has no family");
                return json;
            }
            if (owner.getFamily().getOwner() != owner) {
                json.addProperty("error", "User " + owner.getName() + " with id " + owner.getId() + " is not the owner of the family");
                return json;
            }
            if (owner_id == user_id) {
                json.addProperty("error", "An owner cannot remove himself from the family");
                return json;
            }

            User user = userRepository.findById(user_id).orElseThrow();

            Family family = owner.getFamily();
            if (!family.hasUser(user)) {
                json.addProperty("error", "User " + user.getName() + " with id " + user.getId() + " is not in this family");
                return json;
            }

            // Remove user from family
            family.removeUser(user);
            familyRepository.save(family);

            // Remove family from user
            user.setFamily(null);

            // Create new key for user
            String userKeyFile = "user_" + user.getId() + "_key.key";
            Key userNewKey = AESKeyGenerator.genKey();
            KeyService.writeSecretKey(userKeyFile, userNewKey);
            user.setUserkeyFile(userKeyFile);

            userRepository.save(user);

            // Create new key for owner's family
            String ownerKeyFile = "user_" + owner.getId() + "_key.key";
            Key ownerNewKey = AESKeyGenerator.genKey();
            KeyService.writeSecretKey(ownerKeyFile, ownerNewKey);
            owner.setUserkeyFile(ownerKeyFile);

            userRepository.save(owner);

            // Update family key
            for(User familyUser : family.getUsers()) {
                String familyUserKeyFile = "user_" + familyUser.getId() + "_key.key";
                KeyService.writeSecretKey(familyUserKeyFile, ownerNewKey);
                familyUser.setUserkeyFile(familyUserKeyFile);
                userRepository.save(familyUser);
            }

            // Return new owner key protected with master key
            Key ownerMasterKey = KeyService.readSecretKey(owner.getMasterKeyFile());
            json = KeyService.protectKey(ownerNewKey, ownerMasterKey);
            return json;

        } catch (Exception e) {
            json.addProperty("error", e.getMessage());
            return json;
        }
    }
}
