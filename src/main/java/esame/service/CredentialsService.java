package esame.service;

import java.util.Optional;

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

	@Transactional 
	public Credentials getCredentials(Long id) {
		Optional<Credentials> result = this.credentialsRepository.findById(id);
		return result.orElse(null);
	}

	@Transactional
	public Credentials getCredentials(String username) {
		Optional<Credentials> result = this.credentialsRepository.findByUsername(username);
		if (result.isPresent()) {
			Credentials credentials = result.get();
			// Se il ruolo è null, impostiamo quello di default
			if (credentials.getRole() == null) {
				credentials.setRole(Credentials.DEFAULT_ROLE);
				credentialsRepository.save(credentials);
			}
			return credentials;
		}
		return null;
	}


	@Transactional
	public Credentials saveCredentials(Credentials credentials) {
		// Ensure role is set
		if (credentials.getRole() == null) {
			credentials.setRole(Credentials.DEFAULT_ROLE);
		}
		
		// Encode password before saving
		credentials.setPassword(passwordEncoder.encode(credentials.getPassword()));
		return credentialsRepository.save(credentials);
	}

}
