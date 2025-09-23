package esame.controller;

import java.nio.file.AccessDeniedException;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import esame.DTOmodel.CommentoDTO;
import esame.model.Credentials;
import esame.model.Prodotto;

import esame.service.CommentoService;
import esame.service.CredentialsService;
import esame.service.ProdottoService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

/* classe che gestisce le interazioni tre 
 * 	
 * 	UTENTE -----> COMMENTO	
 *
 **/
@Controller
public class UtenteToCommentoController {

	@Autowired
	private ProdottoService prodottoService;

	@Autowired
	private CommentoService commentoService;

	@Autowired
	private CredentialsService credentialsService;

	/**
	 * metodo che gestisce il POST del form di recensione
	 **/
	@PostMapping("/Prodotto/{id}/commento")
	public String saveCommento(@PathVariable Long id, @ModelAttribute("nuovoCommento") @Valid CommentoDTO dto,
			BindingResult bindingResult, Principal principal, RedirectAttributes redirectAttrs, Model model) {

		// verifico se ci sono errori di validazione
		if (bindingResult.hasErrors()) {
			Prodotto Prodotto = prodottoService.caricaConCommenti(id);
			model.addAttribute("Prodotto", Prodotto);
			return "Prodotto/showProdotto.html";
		}

		// funzione che gestisce l'inserimento della commento nel Prodotto
		commentoService.aggiungiCommento(id, dto, principal.getName());

		redirectAttrs.addFlashAttribute("success", "Grazie per il tuo commento!");

		// ricarico la pagina per mostrare anche il nuovo commento
		return "redirect:/Prodotto/" + id;
	}

	/**
	 * metodo che gestisce la cancellazione da parte dell'utente della commento
	 **/
	@GetMapping("/Prodotto/{id}/commento/delete")
	public String deleteCommento(@RequestParam Long id) {

		try {

			// prendo il commento corrente
			// Commento commento = commentoRepository.findById(id);

		} catch (Exception ex) {
			System.out.println("Exception: " + ex.getMessage());
		}
		return "redirect:/";
	}

	/**
	 * metodo per gestire la rimozione della commento dell'utente
	 * 
	 * @return la pagina del Prodotto senza la commento
	 */
	/*
	 * @PostMapping("/Prodotto/{ProdottoId}/commento/{commentoId}/delete") public
	 * String deleteUserCommento(@PathVariable Long ProdottoId, @PathVariable Long
	 * commentoId, Principal principal, RedirectAttributes redirectAttrs) {
	 * 
	 * try { // principal.getName() deve restituire il cognome con cui hai fatto il
	 * lookup commentoService.deleteCommentoIfOwner(commentoId,
	 * principal.getName());
	 * 
	 * redirectAttrs.addFlashAttribute("success",
	 * "Commento eliminato con successo");
	 * 
	 * } catch (EntityNotFoundException e) {
	 * redirectAttrs.addFlashAttribute("error", "Commento non trovato");
	 * 
	 * } catch (AccessDeniedException e) { redirectAttrs.addFlashAttribute("error",
	 * "Non puoi cancellare commenti di altri utenti"); }
	 * 
	 * return "redirect:/Prodotto/" + ProdottoId; }
	 */

	@PostMapping("/Prodotto/{ProdottoId}/commento/{commentoId}/delete")
	public String deleteUserCommento(
	        @PathVariable Long ProdottoId,
	        @PathVariable Long commentoId,
	        Authentication authentication,
	        RedirectAttributes redirectAttrs) {

	    // estraggo lo username dal principal
	    Object principalObj = authentication.getPrincipal();
	    String username = (principalObj instanceof UserDetails)
	        ? ((UserDetails) principalObj).getUsername()
	        : principalObj.toString();

	    // recupero le credenziali dal DB
	    Credentials creds = credentialsService.getCredentials(username);
	    if (creds == null || creds.getUser() == null) {
	        return "redirect:/login?error";
	    }

	    // confronto diretto sul campo role
	    boolean isAdmin = Credentials.ADMIN_ROLE.equals(creds.getRole());

	    try {
	        if (isAdmin) {
	            // Admin: cancella senza controlli di ownership
	            commentoService.deleteCommento(commentoId);
	        } else {
	            // Utente normale: può solo cancellare il proprio commento
	            commentoService.deleteCommentoIfOwner(commentoId, username);
	        }
	        redirectAttrs.addFlashAttribute("success", "Commento eliminato con successo");
	    } catch (EntityNotFoundException e) {
	        redirectAttrs.addFlashAttribute("error", "Commento non trovato");
	    } catch (AccessDeniedException e) {
	        redirectAttrs.addFlashAttribute("error", "Non puoi cancellare commenti di altri utenti");
	    }

	    return "redirect:/Prodotto/" + ProdottoId;
	}

}
