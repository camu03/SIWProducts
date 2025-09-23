package esame.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;

@Entity
public class Credentials {

	//costanti per definire i ruoli
	public static final String DEFAULT_ROLE = "DEFAULT";
	public static final String ADMIN_ROLE = "ADMIN";
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "credentials_seq")
	@SequenceGenerator(name = "credentials_seq", sequenceName = "credentials_sequence", allocationSize = 1)
	private Long id;
	
	private String username;
	private String password;
	private String role;
	
	@OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")    // ← specifica la FK nella tabella credentials
	private User user;

	public Credentials() {
        this.role = DEFAULT_ROLE; // Set default role in constructor
    }
	
	//metodi getter e setter
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User utente) {
		this.user = utente;
	}
}
