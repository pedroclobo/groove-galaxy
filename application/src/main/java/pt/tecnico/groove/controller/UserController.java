package pt.tecnico.groove.controller;

import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import pt.tecnico.groove.service.UserService;


@RestController
public class UserController {
    @Autowired
    private UserService userService;

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
}