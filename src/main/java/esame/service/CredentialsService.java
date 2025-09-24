package esame.service;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import esame.model.Credentials;
import esame.repository.*;

@Service
public class CredentialsService {
	
	@Autowired 
	protected PasswordEncoder passwordEncoder; 

	@Autowired 
	protected CredentialsRepository credentialsRepository;

	private static final Logger logger = Logger.getLogger(CredentialsService.class.getName());

	@Transactional 
	public Credentials getCredentials(Long id) {
		Optional<Credentials> result = this.credentialsRepository.findById(id);
		return result.orElse(null);
	}

	@Transactional
	public Credentials getCredentials(String username) {
		List<Credentials> results = this.credentialsRepository.findByUsername(username);
		if (results == null || results.isEmpty()) {
			return null;
		}

		if (results.size() > 1) {
			// Log a warning and pick the first one to avoid NonUniqueResultException
			logger.warning("Multiple credentials found for username '" + username + "'. Using the first match. Count=" + results.size());
		}

		Credentials credentials = results.get(0);
		// If role is null, set default
		if (credentials.getRole() == null) {
			credentials.setRole(Credentials.DEFAULT_ROLE);
			credentialsRepository.save(credentials);
		}
		return credentials;
	}


	@Transactional
	public Credentials saveCredentials(Credentials credentials) {
		// Ensure role is set
		if (credentials.getRole() == null) {
			credentials.setRole(Credentials.DEFAULT_ROLE);
		}

		// Check for existing username duplicates and prevent creating a new one
		List<Credentials> existing = this.credentialsRepository.findByUsername(credentials.getUsername());
		if (existing != null && !existing.isEmpty()) {
			// If we're updating an existing record (has id), allow if it's the same id
			if (credentials.getId() == null) {
				throw new IllegalArgumentException("Username already exists");
			} else {
				boolean conflict = existing.stream().anyMatch(c -> !c.getId().equals(credentials.getId()));
				if (conflict) {
					throw new IllegalArgumentException("Username already exists");
				}
			}
		}

		// Encode password before saving
		credentials.setPassword(passwordEncoder.encode(credentials.getPassword()));
		return credentialsRepository.save(credentials);
	}

}
