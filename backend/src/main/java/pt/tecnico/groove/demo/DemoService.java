package pt.tecnico.groove.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pt.tecnico.groove.domain.Owner;
import pt.tecnico.groove.domain.Song;

import pt.tecnico.groove.repository.OwnerRepository;
import pt.tecnico.groove.repository.SongRepository;

import java.util.List;

@Service
public class DemoService {
    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private SongRepository songRepository;

    @Transactional
    public void resetDemoOwners() {
        Owner owner1 = new Owner(1, "André", "key1");
        Owner owner2 = new Owner(2, "Gonçalo", "key2");
        Owner owner3 = new Owner(3, "Pedro", "key3");
        Owner owner4 = new Owner(4, "Miguel", "key4");

        Song song1 = songRepository.findById(1).orElseThrow();
        owner1.addSong(song1);

        ownerRepository.saveAll(List.of(owner1, owner2, owner3, owner4));
    }

    @Transactional
    public void resetDemoSongs() {
        Song song1 = new Song(1, "Song1", "Lyrics1", "Format1", "Artist1", "SongPath1");
        Song song2 = new Song(2, "Song2", "Lyrics2", "Format2", "Artist2", "SongPath2");

        songRepository.saveAll(List.of(song1, song2));
    }

}
