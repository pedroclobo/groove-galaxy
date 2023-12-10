package pt.tecnico.groove.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pt.tecnico.groove.domain.Owner;
import pt.tecnico.groove.domain.Song;
import pt.tecnico.groove.dto.SongDto;
import pt.tecnico.groove.repository.SongRepository;

@Service
public class SongService {

    @Autowired
    private SongRepository songRepository;

    public SongDto getSongById(Integer id) {
        return songRepository.findById(id).map(SongDto::new).orElse(null);
    }

}
