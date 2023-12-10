package pt.tecnico.groove.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import pt.tecnico.groove.domain.Song;
import pt.tecnico.groove.services.SongService;


@RestController
@RequestMapping("/songs")
public class SongController {
    @Autowired
    private SongService songService;

    // Get all songs
    @GetMapping
    public List<Song> getAllSongs() {
        return songService.getAllSongs();
    }

    // Get song by ID
    @GetMapping("/{id}")
    public Optional<Song> getSongById(@PathVariable(value = "id") Integer id) {
        return songService.getSongById(id);
    }

}
