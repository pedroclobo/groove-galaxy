package pt.tecnico.groove.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pt.tecnico.groove.domain.Family;

@Repository
public interface FamilyRepository extends JpaRepository<Family, Integer> {

}
