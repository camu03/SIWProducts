package esame.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import esame.repository.ImmagineRepository;
import esame.repository.ProdottoRepository;
import esame.service.ImmagineService;
import esame.service.ProdottoService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import esame.DTOmodel.ProdottoDTO;
import esame.model.*;

/* classe controller che gestisce le interazioni 
 * 
 * 	ADMIN ----------> Prodotto
 * 
 * */

@Controller
public class AdminToProdottoController {

	@Autowired
	ProdottoRepository prodottoRepository;

	@Autowired
	ProdottoService prodottoService;


	@Autowired
	ImmagineRepository immagineRepository;

	@Autowired
	ImmagineService immagineService;

	/**
	 * metodo per visualizzare la dashboard admin principale
	 * 
	 * @param model per passare dati alla vista
	 * @return pagina principale admin
	 */
	@GetMapping("/admin/success")
	public String showAdminDashboard(Model model) {
		// Puoi aggiungere statistiche o dati utili per l'admin
		long totaleProdotti = prodottoRepository.count();
		model.addAttribute("totaleProdotti", totaleProdotti);
		return "admin/indexAdmin";
	}

	/**
	 * metodo per visualizzare la lista dei prodotti inseriti
	 * 
	 * @param modello come dati da passare alla pagina per visualizzare i prodotti
	 **/
	@GetMapping("/admin/prodotti")
	public String showListaProdotti(Model model) {
		List<Prodotto> prodotti = prodottoRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
		model.addAttribute("prodotti", prodotti);
		return "admin/showProdotto";
	}

	/**
	 * metodo per entrare nella pagina per aggiungere un prodotto
	 * 
	 * @param modello come dati da passare alla pagina di aggiunta del prodotto
	 **/
	@GetMapping("/admin/newProdotto")
	public String showCreatePageProdotto(Model model) {
		ProdottoDTO dto = new ProdottoDTO();
		model.addAttribute("prodottoDTO", dto);
		return "admin/newProdotto";
	}

	/**
	 * meotodo per gestire l'inizializzazione del prodotto da parte dell'admin
	 * 
	 * @param ProdottoDTO
	 * @return entità Prodotto salvata e ritorno alla visualizzazione di tutti i prodotti
	 * @throws IOException
	 **/
	// 3) Salva Prodotto + immagini dal DTO
	@PostMapping(path = "/admin/creaProdotto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String creaProdotto(@Valid @ModelAttribute("prodottoDTO") ProdottoDTO dto, BindingResult result,
			RedirectAttributes redirectAttrs) throws IOException {

		// controllo se ci sono errori
		if (result.hasErrors()) {
			return "admin/newProdotto";
		}

		// passo i valori da prodottoDTO a prodotto
		Prodotto prodotto = new Prodotto();
		prodotto.setNome(dto.getNome());
		prodotto.setPrezzo(dto.getPrezzo());
		prodotto.setTipologia(dto.getTipologia());
		prodotto.setDescrizione(dto.getDescrizione());


		// creo l'immagine
		// 2) per ogni file non vuoto, crea e associa un'immagine
		MultipartFile[] files = dto.getFiles();
		if (files != null) {
			for (MultipartFile file : files) {
				if (!file.isEmpty()) {
					Immagine img = new Immagine();
					img.setNomeFile(file.getOriginalFilename());
					img.setTipoContenuto(file.getContentType());
					img.setDati(file.getBytes()); // <-- byte[] corretto
					// immagineService.salva(img);
					prodotto.addImmagine(img);
				}
			}
		}

		// salvo l'entita
		prodottoService.save(prodotto);
		redirectAttrs.addFlashAttribute("success", "Prodotto creato con successo");

		return "redirect:/admin/prodotti";
	}

	/**
	 * metodo per gestire l'inizializzazione dell'immagine del prodotto
	 * 
	 * @param id       dell'immagine
	 * @param immagine da inserire
	 **/
	@PostMapping("/admin/prodotto/{id}/immagineProdotto")
	public String uploadImmagineProdotto(@PathVariable Long id, @RequestParam("file") MultipartFile file,
			RedirectAttributes redirectAttribute) {

		try {
			prodottoService.aggiungiImmagine(id, file);
			redirectAttribute.addFlashAttribute("message", "Immagine caricata con successo");
		} catch (Exception e) {
			redirectAttribute.addFlashAttribute("error", "Upload fallito: " + e.getMessage());
		}
		return "redirect:/admin/prodotto";
	}

	@GetMapping("/admin/prodotto/{lid}/immagine/{iid}")
	public ResponseEntity<byte[]> serveImage(@PathVariable("iid") Long imageId) {

		Immagine img = immagineRepository.findById(imageId)
				.orElseThrow(() -> new EntityNotFoundException("Immagine non trovata"));

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(img.getTipoContenuto())).body(img.getDati());
	}

	@GetMapping("/admin/prodotto/{lid}")
	public String showDettaglio(@PathVariable Long lid, Model model) {
		Prodotto prodotto = prodottoService.caricaConCommenti(lid);
		model.addAttribute("prodotto", prodotto);
		return "admin/editProdotto";
	}

	/**
	 * metodo che permette di modificare le informazioni sui prodotti dato l'id
	 * 
	 * @param modello come dati da passare alla pagina di modifica del prodotto
	 * @param id      del prodotto da modificare
	 * @return la pagina dove posso modificare il prodotto
	 **/
	@GetMapping("/admin/editProdotto")
	public String showProdottoEditPage(Model model, @RequestParam Long id) {

		try {

			// mi prendo il Prodotto corrispondente all'id
			Prodotto prodotto = prodottoRepository.findById(id).get();
			model.addAttribute("prodotto", prodotto);

			// creo il nuovo prodotto transiente
			ProdottoDTO dto = new ProdottoDTO();
			dto.setNome(prodotto.getNome());
			dto.setPrezzo(prodotto.getPrezzo());
			dto.setId(prodotto.getId());
			dto.setNome(prodotto.getNome());
			dto.setPrezzo(prodotto.getPrezzo());
			dto.setDescrizione(prodotto.getDescrizione());
			dto.setTipologia(prodotto.getTipologia());
			model.addAttribute("prodottoDTO", dto);
			model.addAttribute("prodotto", prodotto);

		} catch (Exception ex) {
			System.out.println("Exception: " + ex.getMessage());
			return "redirect:/admin/prodotti";
		}

		return "admin/editProdotto";
	}

	/**
	 * metodo che gestisce cosa fare una volta modificato il prodotto dato il suo id
	 * 
	 * @param modello coem dati da passare per modificare i dati del prodotto
	 * @param id      del prodotto da modificare
	 * @param oggetto transiente da modificare
	 **/

	@PostMapping("/admin/editProdotto")
	public String updateProdotto(Model model, @ModelAttribute("prodottoDTO") ProdottoDTO dto,
			BindingResult result) {

		System.out.println("Ricevuto DTO con ID: " + dto.getId());
		System.out.println("Errori di validazione: " + result.hasErrors());
		
		if (result.hasErrors()) {
			System.out.println("Errori trovati: " + result.getAllErrors());
			// Ricarica il prodotto per la visualizzazione delle immagini
			Prodotto prodotto = prodottoRepository.findById(dto.getId()).orElse(null);
			model.addAttribute("prodotto", prodotto);
			return "admin/editProdotto";
		}

		try {
			// recupera l'entità, aggiorna ed esegui save()
			Prodotto prodotto = prodottoRepository.findById(dto.getId()).orElse(null);
			if (prodotto == null) {
				System.out.println("Prodotto non trovato con ID: " + dto.getId());
				return "redirect:/admin/prodotti";
			}
			
			System.out.println("Aggiornamento prodotto ID: " + prodotto.getId());
			prodotto.setNome(dto.getNome());
			prodotto.setPrezzo(dto.getPrezzo());
			prodotto.setDescrizione(dto.getDescrizione());
			prodotto.setTipologia(dto.getTipologia());
			
			prodottoService.save(prodotto);
			System.out.println("Prodotto salvato con successo");
			
		} catch (Exception e) {
			System.out.println("Errore durante il salvataggio: " + e.getMessage());
			e.printStackTrace();
		}

		return "redirect:/admin/prodotti";
	}

	/*
	 * @PostMapping("/admin/editProdotto") public String updateProdotto(Model
	 * model, @RequestParam Long id, @Valid @ModelAttribute ProdottoDTO dto,
	 * BindingResult result) {
	 * 
	 * // mi accerto di essere connesso al database try {
	 * 
	 * // prendo il Prodotto a partire dal suo id Prodotto prodotto =
	 *  prodottoRepository.findById(id).get(); model.addAttribute(" prodotto",  prodotto);
	 * 
	 * // verifico se i parametri del form sono validi if (result.hasErrors()) {
	 * return "admin/indexAdmin.html"; }
	 * 
	 * // se non ci sono errori aggiorno i valori del  prodotto
	 *  prodotto.setNome(dto.getNome());
	 *  prodotto.setPrezzo(dto.getPrezzo());
	 * 
	 * // salvo le modifiche  prodottoRepository.save( prodotto); } catch (Exception ex) {
	 * System.out.println("Exception: " + ex.getMessage()); } return
	 * "redirect:/admin/ prodotto"; }
	 */

	/**
	 * metodo per cancellare un  prodotto dato il suo id
	 * 
	 * @param id del  prodotto da cancellare
	 * @return  prodotto cancellato e mostra la pagina con la tabella dei  prodotti
	 **/
	@GetMapping("/admin/deleteProdotto")
	public String deleteProdotto(@RequestParam Long id) {

		try {

			// prendo il prodotto corrente (quello corrispondente all'id passato)
			Prodotto prodotto = prodottoRepository.findById(id).get();

			// elimino l'oggetto prodotto vero e proprio
			prodottoRepository.delete(prodotto);

		} catch (Exception ex) {
			System.out.println("Exception : " + ex.getMessage());
		}
		return "redirect:/admin/prodotti";
	}

	/**
	 * Pagina per gestire i prodotti simili di un prodotto specifico
	 */
	@GetMapping("/admin/prodotto/{id}/prodottiSimili")
	public String showProdottiSimili(@PathVariable Long id, Model model) {
		try {
			Prodotto prodotto = prodottoRepository.findByIdWithProdottiSimili(id)
				.orElseThrow(() -> new EntityNotFoundException("Prodotto non trovato"));
			
			// Tutti i prodotti tranne quello corrente
			List<Prodotto> tuttiProdotti = prodottoRepository.findAll()
				.stream()
				.filter(p -> !p.getId().equals(id))
				.collect(java.util.stream.Collectors.toList());
			
			model.addAttribute("prodotto", prodotto);
			model.addAttribute("tuttiProdotti", tuttiProdotti);
			
			return "admin/prodottiSimili";
			
		} catch (Exception e) {
			System.out.println("Errore nel caricamento prodotti simili: " + e.getMessage());
			return "redirect:/admin/prodotti";
		}
	}
	
	/**
	 * Aggiungi un prodotto simile
	 */
	@PostMapping("/admin/prodotto/{id}/addProdottoSimile")
	public String addProdottoSimile(@PathVariable Long id, 
	                               @RequestParam Long prodottoSimileId,
	                               RedirectAttributes redirectAttrs) {
		try {
			Prodotto prodotto = prodottoRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Prodotto non trovato"));
			
			Prodotto prodottoSimile = prodottoRepository.findById(prodottoSimileId)
				.orElseThrow(() -> new EntityNotFoundException("Prodotto simile non trovato"));
			
			prodotto.addProdottoSimile(prodottoSimile);
			prodottoRepository.save(prodotto);
			
			redirectAttrs.addFlashAttribute("success", "Prodotto simile aggiunto con successo");
			
		} catch (Exception e) {
			redirectAttrs.addFlashAttribute("error", "Errore nell'aggiunta del prodotto simile");
		}
		
		return "redirect:/admin/prodotto/" + id + "/prodottiSimili";
	}
	
	/**
	 * Rimuovi un prodotto simile
	 */
	@GetMapping("/admin/prodotto/{id}/removeProdottoSimile/{simileId}")
	public String removeProdottoSimile(@PathVariable Long id, 
	                                  @PathVariable Long simileId,
	                                  RedirectAttributes redirectAttrs) {
		try {
			Prodotto prodotto = prodottoRepository.findByIdWithProdottiSimili(id)
				.orElseThrow(() -> new EntityNotFoundException("Prodotto non trovato"));
			
			Prodotto prodottoSimile = prodottoRepository.findById(simileId)
				.orElseThrow(() -> new EntityNotFoundException("Prodotto simile non trovato"));
			
			prodotto.removeProdottoSimile(prodottoSimile);
			prodottoRepository.save(prodotto);
			
			redirectAttrs.addFlashAttribute("success", "Prodotto simile rimosso con successo");
			
		} catch (Exception e) {
			redirectAttrs.addFlashAttribute("error", "Errore nella rimozione del prodotto simile");
		}
		
		return "redirect:/admin/prodotto/" + id + "/prodottiSimili";
	}
}
