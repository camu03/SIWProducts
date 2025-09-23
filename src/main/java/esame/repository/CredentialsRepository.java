package esame.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import esame.model.*;

public interface CredentialsRepository extends JpaRepository<Credentials, Long>{
	public Optional<Credentials> findByUsername(String username);

}
