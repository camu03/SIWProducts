package esame.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletResponse;

import esame.DTOmodel.CommentoDTO;
import esame.model.Commento;
import esame.model.Immagine;
import esame.model.Prodotto;

import esame.model.User;
import esame.repository.CommentoRepository;
import esame.repository.ImmagineRepository;
import esame.repository.ProdottoRepository;
import esame.service.CommentoService;
import esame.service.CredentialsService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@Controller
public class ProdottoController {

	@Autowired
	private CommentoService commentoService;

	@Autowired
	private CredentialsService credentialsService;

	@Autowired
	private ImmagineRepository immagineRepository;

	@Autowired
	private CommentoRepository commentoRepository;
	
	@Autowired
	private ProdottoRepository prodottoRepository;	
	/*mi serve per far ritornare tutti i commenti del dato prodotto*/
	public List<Commento> getCommentiDi(Long prodottoId) {
		return commentoRepository.findByProdottoId(prodottoId);
	}

	/**
	 * metodo che mi permette di vedere i dettagli del prodotto
	 * @return pagina con dettagli prodotto
	 **/
	@GetMapping("/prodotto/{id}")
	public String showDettagli(@PathVariable Long id, Model model) {

		// recupero il prodotto senza commenti per performance
		Prodotto prodotto = prodottoRepository.findById(id).orElse(null);
		if (prodotto == null) {
			return "redirect:/showAllProdotti";
		}
		
		// Carica commenti solo se esistono, con limite per performance
		List<Commento> commenti = commentoRepository.findByProdottoIdOrderByDataCreazioneDesc(id);
		
		// Limita a 15 commenti per evitare lag
		if (commenti.size() > 15) {
			commenti = commenti.subList(0, 15);
		}
		
		// Imposta i commenti limitati
		prodotto.setCommenti(commenti);
		
		model.addAttribute("prodotto", prodotto);
		model.addAttribute("totalCommenti", commentoRepository.countByProdottoId(id));

		// prepare un oggetto vuoto per il binding del form
		CommentoDTO commentoVuoto = new CommentoDTO();
		model.addAttribute("nuovoCommento", commentoVuoto);

		return "dettaglioProdotto"; // pagina web del prodotto
	}

	
	/**
	 * metodo per riprendere le immagini relative al prodotto (pubblico)
	 * @return immagine relativa al prodotto
	 * */
	@GetMapping("/prodotto/immagine/{iid}")
	public ResponseEntity<byte[]> serveImage(@PathVariable("iid") Long imageId) {

		Immagine img = immagineRepository.findById(imageId)
				.orElseThrow(() -> new EntityNotFoundException("Immagine non trovata"));

		return ResponseEntity.ok().contentType(MediaType.valueOf(img.getTipoContenuto())).body(img.getDati());
	}
	
	/**
	 * metodo per visualizzare l'homepage del sito
	 * 
	 * @return index.html (homepage del sito)
	 */
	@GetMapping("/showAllProdotti")
	public String showAllProdotti(Model model, HttpServletResponse response) {
		// Previeni caching
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Expires", "0");
		
		System.out.println("=== MOSTRA TUTTI I PRODOTTI ===");
		List<Prodotto> prodotti = prodottoRepository.findAll();
		System.out.println("Totale prodotti: " + prodotti.size());
		
		model.addAttribute("prodotti", prodotti);
		model.addAttribute("categoriaSelezionata", (String) null); // Reset categoria
		return "tuttiProdotti";
	}
	
	/**
	 * metodo per filtrare prodotti per categoria
	 * @param categoria categoria da filtrare
	 * @param model per passare dati al template
	 * @return pagina con prodotti filtrati
	 */
	@GetMapping("/prodotti/categoria/{categoria}")
	public String showProdottiPerCategoria(@PathVariable String categoria, 
	                                      Model model, 
	                                      HttpServletResponse response) {
		
		// Previeni caching
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Expires", "0");
		
		System.out.println("=== FILTRO CATEGORIA ===");
		System.out.println("Categoria richiesta: '" + categoria + "'");
		
		List<Prodotto> prodotti = prodottoRepository.findByTipologiaIgnoreCase(categoria);
		System.out.println("Prodotti trovati per '" + categoria + "': " + prodotti.size());
		
		// Debug: mostra prime 3 tipologie nel database
		List<Prodotto> tuttiProdotti = prodottoRepository.findAll();
		System.out.println("Prime 3 tipologie nel database:");
		tuttiProdotti.stream().limit(3).forEach(p -> 
			System.out.println("  - Prodotto: '" + p.getNome() + "' - Tipologia: '" + p.getTipologia() + "'")
		);
		
		model.addAttribute("prodotti", prodotti);
		model.addAttribute("categoriaSelezionata", categoria);
		model.addAttribute("totaleProdotti", prodotti.size());
		
		return "tuttiProdotti";
	}
	
	/**
	 * metodo per la ricerca testuale
	 * @param keyword parola chiave da cercare
	 * @param model per passare dati al template
	 * @return pagina con risultati ricerca
	 */
	@PostMapping("/ricercaProdotti")
	public String ricercaProdotti(@RequestParam("keyword") String keyword, Model model) {
		List<Prodotto> prodotti;
		if (keyword == null || keyword.trim().isEmpty()) {
			prodotti = prodottoRepository.findAll();
		} else {
			prodotti = prodottoRepository.findByNomeContainingIgnoreCase(keyword.trim());
		}
		model.addAttribute("prodotti", prodotti);
		model.addAttribute("keyword", keyword);
		return "tuttiProdotti";
	}
	
	/**
	 * metodo per aggiungere un commento al prodotto
	 * @param id del prodotto
	 * @param commentoDTO contenente il testo del commento
	 * @param principal per ottenere l'utente loggato
	 * @return redirect alla pagina del prodotto
	 */
	@PostMapping("/prodotto/{id}/commento")
	public String aggiungiCommento(@PathVariable Long id, 
	                              @Valid @ModelAttribute("nuovoCommento") CommentoDTO commentoDTO,
	                              BindingResult result, 
	                              Principal principal,
	                              Model model) {
		
		if (result.hasErrors()) {
			// Ricarica la pagina del prodotto con gli errori
			Prodotto prodotto = prodottoRepository.findByIdWithCommenti(id).orElse(null);
			model.addAttribute("prodotto", prodotto);
			return "dettaglioProdotto";
		}
		
		try {
			// Trova l'utente loggato tramite credentials
			String username = principal.getName();
			User user = credentialsService.getCredentials(username).getUser();
			
			// Trova il prodotto
			Prodotto prodotto = prodottoRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Prodotto non trovato"));

			// Crea il commento
			Commento commento = new Commento();
			commento.setTesto(commentoDTO.getTesto());
			commento.setUser(user);
			commento.setProdotto(prodotto);
			
			// Salva il commento
			commentoRepository.save(commento);

		} catch (Exception e) {
			System.out.println("Errore durante l'aggiunta del commento: " + e.getMessage());
		}
		
		// Redirect alla pagina del prodotto
		return "redirect:/prodotto/" + id;
	}
	
	/**
	 * metodo per eliminare un commento del prodotto
	 * @param pid id del prodotto
	 * @param cid id del commento da eliminare
	 * @param principal per verificare che sia l'autore
	 * @return redirect alla pagina del prodotto
	 */
	@GetMapping("/prodotto/{pid}/commento/{cid}/elimina")
	public String eliminaCommento(@PathVariable Long pid, 
	                             @PathVariable Long cid,
	                             Principal principal) {
		
		try {
			// Trova l'utente loggato
			String username = principal.getName();
			User user = credentialsService.getCredentials(username).getUser();
			
			// Usa il service per eliminare il commento con controllo di proprietà
			commentoService.deleteCommentoIfOwner(cid, user.getSurname());
			
		} catch (Exception e) {
			System.out.println("Errore durante l'eliminazione del commento: " + e.getMessage());
		}
		
		// Redirect alla pagina del prodotto
		return "redirect:/prodotto/" + pid;
	}
	
	/**
	 * metodo per mostrare la pagina di modifica commento
	 * @param pid id del prodotto
	 * @param cid id del commento da modificare
	 * @param principal per verificare che sia l'autore
	 * @param model per passare dati al template
	 * @return pagina di modifica commento
	 */
	@GetMapping("/prodotto/{pid}/commento/{cid}/modifica")
	public String mostraModificaCommento(@PathVariable Long pid, 
	                                   @PathVariable Long cid,
	                                   Principal principal,
	                                   Model model) {
		
		try {
			// Trova l'utente loggato
			String username = principal.getName();
			User user = credentialsService.getCredentials(username).getUser();
			
			// Trova il commento
			Commento commento = commentoService.trovaCommento(cid);
			
			// Verifica che l'utente sia l'autore
			if (!commento.getUser().getSurname().equals(user.getSurname())) {
				return "redirect:/prodotto/" + pid;
			}
			
			// Trova il prodotto
			Prodotto prodotto = prodottoRepository.findById(pid).orElse(null);
			if (prodotto == null) {
				return "redirect:/showAllProdotti";
			}
			
			// Prepara il DTO con il testo attuale
			CommentoDTO commentoDTO = new CommentoDTO();
			commentoDTO.setTesto(commento.getTesto());
			
			model.addAttribute("prodotto", prodotto);
			model.addAttribute("commento", commento);
			model.addAttribute("commentoDTO", commentoDTO);
			
			return "modificaCommento";
			
		} catch (Exception e) {
			System.out.println("Errore durante il caricamento della modifica: " + e.getMessage());
			return "redirect:/prodotto/" + pid;
		}
	}
	
	/**
	 * metodo per salvare le modifiche al commento
	 * @param pid id del prodotto
	 * @param cid id del commento da modificare
	 * @param commentoDTO dati del commento modificato
	 * @param result risultato della validazione
	 * @param principal per verificare che sia l'autore
	 * @param model per passare dati al template
	 * @return redirect alla pagina del prodotto
	 */
	@PostMapping("/prodotto/{pid}/commento/{cid}/modifica")
	public String salvaModificaCommento(@PathVariable Long pid, 
	                                  @PathVariable Long cid,
	                                  @Valid @ModelAttribute("commentoDTO") CommentoDTO commentoDTO,
	                                  BindingResult result,
	                                  Principal principal,
	                                  Model model) {
		
		if (result.hasErrors()) {
			// Ricarica la pagina con gli errori
			try {
				Prodotto prodotto = prodottoRepository.findById(pid).orElse(null);
				Commento commento = commentoService.trovaCommento(cid);
				model.addAttribute("prodotto", prodotto);
				model.addAttribute("commento", commento);
				return "modificaCommento";
			} catch (Exception e) {
				return "redirect:/prodotto/" + pid;
			}
		}
		
		try {
			// Trova l'utente loggato
			String username = principal.getName();
			User user = credentialsService.getCredentials(username).getUser();
			
			// Usa il service per modificare il commento con controllo di proprietà
			commentoService.modificaCommentoIfOwner(cid, commentoDTO.getTesto(), user.getSurname());
			
		} catch (Exception e) {
			System.out.println("Errore durante la modifica del commento: " + e.getMessage());
		}
		
		// Redirect alla pagina del prodotto
		return "redirect:/prodotto/" + pid;
	}
	
	/**
	 * Endpoint per mostrare i prodotti simili nella vista pubblica
	 */
	@GetMapping("/prodotto/{id}/simili")
	public String showProdottiSimiliPubblici(@PathVariable Long id, Model model) {
		try {
			Prodotto prodotto = prodottoRepository.findByIdWithProdottiSimili(id)
				.orElseThrow(() -> new RuntimeException("Prodotto non trovato"));
			
			model.addAttribute("prodotto", prodotto);
			model.addAttribute("prodottiSimili", prodotto.getProdottiSimili());
			
			return "prodottiSimili";
			
		} catch (Exception e) {
			System.out.println("Errore nel caricamento prodotti simili: " + e.getMessage());
			return "redirect:/showAllProdotti";
		}
	}

}
