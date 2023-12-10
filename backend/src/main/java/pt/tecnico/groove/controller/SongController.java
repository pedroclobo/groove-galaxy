package pt.tecnico.groove.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import pt.tecnico.groove.dto.SongDto;
import pt.tecnico.groove.service.SongService;


@RestController
public class SongController {
    @Autowired
    private SongService songService;

    @GetMapping(value="/songs/{id}")
    public SongDto getSongById(@PathVariable(value = "id") Integer id) {
        return songService.getSongById(id);
    }

}
