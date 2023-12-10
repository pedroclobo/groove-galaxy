package pt.tecnico.groove.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pt.tecnico.groove.domain.Song;
import pt.tecnico.groove.repository.SongRepository;

@Service
public class SongService {
    @Autowired
    private SongRepository songRepository;

    // Get all songs
    public List<Song> getAllSongs() {
        return songRepository.findAll();
    }

    // Get song by ID
    public Optional<Song> getSongById(Integer id) {
        return songRepository.findById(id);
    }

}
