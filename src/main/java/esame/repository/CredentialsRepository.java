package esame.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import esame.model.*;

public interface CredentialsRepository extends JpaRepository<Credentials, Long>{
	public List<Credentials> findByUsername(String username);

}
