package pt.tecnico.groove.service;

import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pt.tecnico.JsonProtector;
import pt.tecnico.groove.domain.User;
import pt.tecnico.groove.domain.Song;
import pt.tecnico.groove.repository.SongRepository;
import pt.tecnico.groove.service.KeyService;

import java.security.Key;

@Service
public class SongService {

    @Autowired
    private SongRepository songRepository;

    public JsonObject getSongById(Integer id) throws Exception {
        Song song = songRepository.findById(id).orElseThrow();
        User user = song.getUser();
        Key key = KeyService.readSecretKey(user.getUserKeyFile());
        return JsonProtector.protect(song.toJson(), key);
    }
}
