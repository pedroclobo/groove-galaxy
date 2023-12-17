package pt.tecnico.groove.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pt.tecnico.groove.domain.User;
import pt.tecnico.groove.domain.Song;

import pt.tecnico.groove.repository.UserRepository;
import pt.tecnico.groove.repository.SongRepository;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

@Service
public class DemoService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SongRepository songRepository;

    private final String lyricsPath = "/songs/lyrics/";
    private final String songPath = "/songs/base64/";

    @Transactional
    public void resetDemoUsers() {
        List<User> users = new ArrayList<>();

        User user1 = new User();
        user1.setName("André");
        user1.setMasterkeyFile("aes-key-1.key");
        users.add(user1);

        User user2 = new User();
        user2.setName("Gonçalo");
        user2.setMasterkeyFile("aes-key-2.key");
        users.add(user2);

        User user3 = new User();
        user3.setName("Pedro");
        user3.setMasterkeyFile("aes-key-3.key");
        users.add(user3);

        User user4 = new User();
        user4.setName("Miguel");
        user4.setMasterkeyFile("aes-key-4.key");
        users.add(user4);

        Song song1 = songRepository.findById(1).orElseThrow();
        user1.addSong(song1);

        Song song2 = songRepository.findById(2).orElseThrow();
        user2.addSong(song2);

        Song song3 = songRepository.findById(3).orElseThrow();
        user3.addSong(song3);

        Song song4 = songRepository.findById(4).orElseThrow();
        user4.addSong(song4);

        userRepository.saveAll(users);
    }

    @Transactional
    public void resetDemoSongs() {
        List<Song> songs = new ArrayList<>();

        Song song1 = createSong("Don't Stop Me Now", "dont_stop_me_now.txt", "MP3", "Queen", "dont_stop_me_now-mp3.base64");
        songs.add(song1);

        Song song2 = createSong("Hey Jude", "hey_jude.txt", "MP3", "The Beatles", "hey_jude-mp3.base64");
        songs.add(song2);

        Song song3 = createSong("Let It Be", "let_it_be.txt", "MP3", "The Beatles", "let_it_be-mp3.base64");
        songs.add(song3);

        Song song4 = createSong("Sweet Child O' Mine", "sweet_child_o_mine.txt", "WAV", "Guns N' Roses", "sweet_child_o_mine-wav.base64");
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
