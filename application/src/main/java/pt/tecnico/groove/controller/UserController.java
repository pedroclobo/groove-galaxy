package pt.tecnico.groove.controller;

import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import pt.tecnico.groove.service.FamilyService;
import pt.tecnico.groove.service.UserService;


@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private FamilyService familyService;

    @GetMapping(value="/users")
    public JsonObject getAllUsers() throws Exception {
        return userService.getAllUsers();
    }

    @PostMapping(value="/create_user_key/{id}")
    public JsonObject createUserKey(@PathVariable(value = "id") Integer id) throws Exception {
        return userService.createUserKey(id);
    }

    @GetMapping(value="/user/{id}/songs")
    public JsonObject getAllUserSongs(@PathVariable(value = "id") Integer id) throws Exception {
        return userService.getAllUserSongs(id);
    }

    @PostMapping(value="/user/{id}/add_to_family/{user_id}")
    public JsonObject addUserToFamily(@PathVariable(value = "id") Integer id, @PathVariable(value = "user_id") Integer user_id) throws Exception {
        return familyService.addUserToFamily(id, user_id);
    }

    @GetMapping(value="/user/{id}/family")
    public JsonObject getUserFamily(@PathVariable(value = "id") Integer id) throws Exception {
        return userService.getFamily(id);
    }

    @GetMapping(value="/user/{id}/get_family_key")
    public JsonObject getFamilyKey(@PathVariable(value = "id") Integer id) throws Exception {
        return userService.getFamilyKey(id);
    }

    @PostMapping(value="/user/{id}/remove_from_family/{user_id}")
    public JsonObject removeUserFromFamily(@PathVariable(value = "id") Integer id, @PathVariable(value = "user_id") Integer user_id) throws Exception {
        return familyService.removeUserFromFamily(id, user_id);
    }
}