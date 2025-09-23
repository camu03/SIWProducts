package esame.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import esame.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	
	/**
	 * Trova un utente per cognome
	 * @param surname cognome dell'utente
	 * @return utente se trovato
	 */
	Optional<User> findBySurname(String surname);
	
	/**
	 * Trova un utente per nome
	 * @param name nome dell'utente
	 * @return utente se trovato
	 */
	Optional<User> findByName(String name);
	
	/**
	 * Trova un utente per email
	 * @param email email dell'utente
	 * @return utente se trovato
	 */
	Optional<User> findByEmail(String email);
}
