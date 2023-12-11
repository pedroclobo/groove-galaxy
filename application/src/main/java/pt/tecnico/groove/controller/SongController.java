package pt.tecnico.groove.controller;

import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import pt.tecnico.groove.service.SongService;


@RestController
public class SongController {
    @Autowired
    private SongService songService;

    @GetMapping(value="/songs/{id}")
    public JsonObject getSongById(@PathVariable(value = "id") Integer id) throws Exception {
        return songService.getSongById(id);
    }

}
