package pt.tecnico.groove.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pt.tecnico.groove.domain.Owner;
import pt.tecnico.groove.domain.Song;

import pt.tecnico.groove.repository.OwnerRepository;
import pt.tecnico.groove.repository.SongRepository;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

@Service
public class DemoService {
    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private SongRepository songRepository;

    private final String lyricsPath = "/songs/lyrics/";
    private final String songPath = "/songs/base64/";
    private final String keyPath = "/keys/";

    @Transactional
    public void resetDemoOwners() {
        List<Owner> owners = new ArrayList<>();

        Owner owner1 = new Owner();
        owner1.setName("André");
        owner1.setKeyFile(keyPath + "aes-key-1.key");
        owners.add(owner1);

        Owner owner2 = new Owner();
        owner2.setName("Gonçalo");
        owner2.setKeyFile(keyPath + "aes-key-2.key");
        owners.add(owner2);

        Owner owner3 = new Owner();
        owner3.setName("Pedro");
        owner3.setKeyFile(keyPath + "aes-key-3.key");
        owners.add(owner3);

        Owner owner4 = new Owner();
        owner4.setName("Miguel");
        owner4.setKeyFile(keyPath + "aes-key-4.key");
        owners.add(owner4);

        Song song1 = songRepository.findById(1).orElseThrow();
        owner1.addSong(song1);

        Song song2 = songRepository.findById(2).orElseThrow();
        owner2.addSong(song2);

        Song song3 = songRepository.findById(3).orElseThrow();
        owner3.addSong(song3);

        Song song4 = songRepository.findById(4).orElseThrow();
        owner4.addSong(song4);

        ownerRepository.saveAll(owners);
    }

    @Transactional
    public void resetDemoSongs() {
        List<Song> songs = new ArrayList<>();

        Song song1 = createSong("Don't Stop Me Now", "dont_stop_me_now.txt", "MP3", "Queen",
                "dont_stop_me_now-mp3.base64");
        songs.add(song1);

        Song song2 = createSong("Hey Jude", "hey_jude.txt", "MP3", "The Beatles", "hey_jude-mp3.base64");
        songs.add(song2);

        Song song3 = createSong("Let It Be", "let_it_be.txt", "MP3", "The Beatles", "let_it_be-mp3.base64");
        songs.add(song3);
        Song song4 = createSong("Sweet Child O' Mine", "sweet_child_o_mine.txt", "WAV", "Guns N' Roses",
                "sweet_child_o_mine-wav.base64");
        songs.add(song4);
        songRepository.saveAll(songs);
    }

    private Song createSong(String title, String lyricsFile, String format, String artist, String songBase64File) {
        Song song = new Song();

        song.setTitle(title);
        try {
            song.setLyrics(DemoUtils.readResource(lyricsPath + lyricsFile));
        } catch (IOException e) {
            song.setLyrics("Lyrics");
        }
        song.setFormat(format);
        song.setArtist(artist);
        try {
            song.setSongBase64(DemoUtils.readResource(songPath + songBase64File));
        } catch (IOException e) {
            song.setSongBase64("SongPath");
        }

        return song;
    }

}
