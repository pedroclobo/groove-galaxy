package pt.tecnico.groove.service;

import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import org.springframework.util.FileCopyUtils;
import pt.tecnico.JsonProtector;
import pt.tecnico.groove.domain.Owner;
import pt.tecnico.groove.domain.Song;
import pt.tecnico.groove.repository.SongRepository;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.Key;

@Service
public class SongService {

    @Autowired
    private SongRepository songRepository;

    public JsonObject getSongById(Integer id) throws Exception {
        Song song = songRepository.findById(id).orElseThrow();
        Owner owner = song.getOwner();
        Key key = readSecretKey(owner.getKeyFile());
        return JsonProtector.protect(song.toJson(), key);
    }

    public static Key readSecretKey(String secretKeyPath) throws Exception {
        byte[] encoded = readResource(secretKeyPath);
        return new SecretKeySpec(encoded, "AES");
    }

    private static byte[] readResource(String resourcePath) throws IOException {
        Resource resource = new ClassPathResource(resourcePath);
        return FileCopyUtils.copyToByteArray(resource.getInputStream());
    }
}
