package pt.tecnico.groove.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pt.tecnico.groove.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

}
