package esame.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import esame.DTOmodel.ProdottoDTO;
import esame.model.Commento;
import esame.model.Immagine;
import esame.model.Prodotto;

import esame.model.User;
import esame.repository.CommentoRepository;
import esame.repository.ImmagineRepository;
import esame.repository.ProdottoRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class ProdottoService {

	@Autowired
	ProdottoRepository prodottoRepository;

	@Autowired
	ImmagineRepository immagineRepository;

	@Autowired
	private CommentoRepository commentoRepository;

	/**
	 * metodo per salvare l'entità Prodotto
	 * 
	 * @param l'oggetto Prodotto da salvare
	 **/
	public void save(Prodotto Prodotto) {
		prodottoRepository.save(Prodotto);
	}

	/**
	 * metodo per vedere tutti gli Prodotti
	 * 
	 * @param id degli Prodotti
	 * @throws Exception
	 **/
	public Prodotto get(Long id) throws Exception {
		Optional<Prodotto> result = prodottoRepository.findById(id);
		if (result.isPresent()) {
			return result.get();
		}
		throw new Exception("Non troviamo nessun prodotto con ID : " + id);
	}

	public Prodotto caricaConCommenti(Long prodId) {
		// Recupera l'Optional dal repository
		Optional<Prodotto> optProd = prodottoRepository.findById(prodId);

		// Se non presente, solleva l'eccezione
		if (!optProd.isPresent()) {
			throw new EntityNotFoundException("Prodotto non trovato");
		}

		// Estrae il Prodotto, forza il caricamento delle commenti e lo restituisce
		Prodotto prodotto = optProd.get();
		prodotto.getCommenti().size(); // forza fetch delle commenti
		return prodotto;
	}

	public Commento aggiungiCommento(Long prodId, User utente, String testo) {
		Prodotto prodotto = caricaConCommenti(prodId);
		Commento c = new Commento();
		c.setProdotto(prodotto);
		c.setUser(utente);
		c.setTesto(testo);
		return commentoRepository.save(c);
	}
	
	

	/**
	 * metodo per aggiungere l'immagine al prodotto
	 * 
	 * @param id         del prodotto al quale aggiungere l'immagine
	 * @param l'immagine da aggiungere
	 **/
	@Transactional
	public void aggiungiImmagine(Long prodId, MultipartFile file) throws IOException {

		// prendo l'oggetto Prodotto
		Prodotto prodotto = prodottoRepository.findById(prodId)
				.orElseThrow(() -> new EntityNotFoundException("Prodotto non trovato"));

		Immagine img = new Immagine();
		img.setNomeFile(file.getOriginalFilename());
		img.setTipoContenuto(file.getContentType());
		img.setDati(file.getBytes());

		// associa e salva
		prodotto.addImmagine(img);
		// grazie a CascadeType.ALL, salva anche l’immagine
		prodottoRepository.save(prodotto);
	}

	@Transactional
	public Immagine addImage(Long prodId, MultipartFile file) throws IOException {
		Prodotto prod = prodottoRepository.findById(prodId).orElseThrow(() -> new EntityNotFoundException("Prodotto non trovato"));
		Immagine img = new Immagine();
		img.setNomeFile(file.getOriginalFilename());
		img.setDati(file.getBytes());
		img.setProdotto(prod);
		return immagineRepository.save(img);
	}

	@Transactional
	public void creaProdottoConImmagini(ProdottoDTO dto) throws IOException {
		Prodotto prodotto = new Prodotto();
		prodotto.setNome(dto.getNome());
		prodotto.setPrezzo(dto.getPrezzo());

		if (dto.getFiles() != null) {
			for (MultipartFile file : dto.getFiles()) {
				if (!file.isEmpty()) {
					Immagine img = new Immagine();
					img.setNomeFile(file.getOriginalFilename());
					img.setTipoContenuto(file.getContentType());
					img.setDati(file.getBytes()); // ← byte[]
					img.setProdotto(prodotto);
					prodotto.getImmagini().add(img);
				}
			}
		}

		prodottoRepository.save(prodotto);
	}

	/**
	 * Recupera un Prodotto con tutte le commenti , per
	 * evitare problemi di lazy-loading in Thymeleaf.
	 *
	 * @param id l'id del Prodotto
	 * @return l'entità Prodotto popolata di commenti
	 * @throws EntityNotFoundException se non esiste un Prodotto con quell'id
	 */
	@Transactional
	public Prodotto findByIdWithCommentiAndAutori(Long id) {
		return prodottoRepository.findByIdWithCommentiAndAutori(id)
				.orElseThrow(() -> new EntityNotFoundException("Prodotto non trovato con id " + id));
	}
	
	
	/**
	 * metodo per creare la barra di ricerca e fare una ricerca in base a una keyword
	 * @param keyword sulla quale fare la ricerca
	 * */
	public List<Prodotto> findByTitolo(String keyword){
		return prodottoRepository.findByNomeContainingIgnoreCase(keyword);
	}

	public List<Prodotto> findAll(){
		return prodottoRepository.findAll();
	}

	public List<Prodotto> findAllOrderByPrezzoDesc(){
		return prodottoRepository.findAllOrderByPrezzoDesc();
	}

	public List<Prodotto> findAllOrderByCommentiDesc(){
		return prodottoRepository.findAllOrderByCommentiDesc();
	}
}
