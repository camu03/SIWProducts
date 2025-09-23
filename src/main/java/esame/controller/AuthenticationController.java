package esame.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import esame.model.Credentials;
import esame.model.User;
import esame.service.CredentialsService;
import jakarta.validation.Valid;

@Controller
public class AuthenticationController {

	@Autowired
	private CredentialsService credentialsService;

	// @Autowired
	// private PasswordEncoder passwordEncoder;

	@GetMapping("/login") // praticamente gli sto dicendo cosa fare quando incotra lo /login
	public String showLogin(Model model) {
		return "login.html"; // la pagina html che deve ritornare
	}

	
	@GetMapping("/register") // imposto cosa deve fare quando incotro /register
	public String showRegister(Model model) {
		User user = new User();
		Credentials credentials = new Credentials();
		credentials.setUser(user);  // Initialize nested User for proper binding
		model.addAttribute("user", user);
		model.addAttribute("credentials", credentials);
		return "register.html"; // la pagina html che deve ritornare
	}
	
	@GetMapping("/default")
	public String defaultAfterLogin(Model model) {
        UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
        
        // Add null checks and use the constant values from Credentials class
        if (credentials != null && credentials.getRole() != null) {
            if (credentials.getRole().equals(Credentials.ADMIN_ROLE)) {
                return "admin/home";
            }
        }
        // Default to regular user home if role is null or not admin
        return "home";
    }

	@PostMapping("/register") 
	public String registerUser(@Valid @ModelAttribute("credentials") Credentials credentials,
			BindingResult bindingResult,
			Model model) {
		// Set a default role if none is selected
		if(credentials.getRole() == null) {
			credentials.setRole(Credentials.DEFAULT_ROLE);
		}
		
		// Validate and save credentials
		if (!bindingResult.hasErrors()) {
			try {
				credentialsService.saveCredentials(credentials);
				return "login";
			} catch (Exception e) {
				model.addAttribute("errorMessage", "Registration failed");
				return "register";
			}
		}
		return "register";
	}
	/*
	public String showRegister(Model model) {
		
		Credentials credentials = new Credentials();
		model.addAttribute("user", new User());
		model.addAttribute("credentials", credentials);

		return "register.html"; // la pagina html che deve ritornare
	}*/

	@GetMapping(value = "/registrazioneeffettuata")
	public String index(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication instanceof AnonymousAuthenticationToken) {
			return "index.html"; // mi ritorna l'homepage
		} else {
			UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());

			// se il ruolo dell'utente è quello di ADMIN vuol dire che ci dovriamo
			// davanti a una azienda
			if (credentials.getRole().equals(Credentials.ADMIN_ROLE)) {
				return "admin/indexAdmin.html"; // ritorno l'area riservata alle sole aziende
			}
		}

		return "index.html"; // torno alla homepage
	}

	@GetMapping(value = "/success") // il login ha avuto successo
	public String loginSuccessHandler(Model model) {
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
		if (credentials.getRole().equalsIgnoreCase(Credentials.ADMIN_ROLE)) {
			return "admin/indexAdmin.html"; // se ho permessi speciali allora posso accedere ad un'altra area
		}

		// implementazione ruolo di defoult
		if (credentials.getRole().equalsIgnoreCase(Credentials.DEFAULT_ROLE)) {
			return "redirect:/";
		}
		
		return "redirect:/"; //se mi sono autenticato e sono un utente normale torno alla homepage
	}

}