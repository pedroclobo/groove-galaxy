package pt.tecnico.groove.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pt.tecnico.groove.domain.Song;

@Repository
public interface SongRepository extends JpaRepository<Song, Integer> {

}
