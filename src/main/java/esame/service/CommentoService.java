package esame.service;

import java.nio.file.AccessDeniedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import esame.DTOmodel.CommentoDTO;
import esame.model.Commento;
import esame.model.Prodotto;

import esame.model.User;
import esame.repository.CommentoRepository;
import esame.repository.ProdottoRepository;

import esame.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class CommentoService {

	@Autowired
	private ProdottoRepository prodRepo;

	@Autowired
	private CommentoRepository commentoRepo;

	// Se usi login:
	@Autowired
	private UserRepository utenteRepo;

	@Transactional
	public void aggiungiCommento(Long prodId, CommentoDTO dto, String surname) {
		// 1) carica il Prodotto o fallisci
		Prodotto prodotto = prodRepo.findById(prodId)
				.orElseThrow(() -> new EntityNotFoundException("Prodotto non trovato: " + prodId));

		// 2) carica l'utente (usa findBySurname)
		User utente = utenteRepo.findBySurname(surname)
				.orElseThrow(() -> new EntityNotFoundException("Utente non trovato: " + surname));

		// Costruisci il commento
		Commento c = new Commento();
		c.setTesto(dto.getTesto());
		// dataCreazione viene impostata automaticamente nel costruttore
		c.setProdotto(prodotto);
		c.setUser(utente);

		// Salva
		commentoRepo.save(c);
	}

	@Transactional
	public void deleteCommento(Long prodId, Long utenteId) {
		commentoRepo.deleteByProdottoAndUtente(prodId, utenteId);
	}

	public void deleteCommento(Long commentoId) {
        Commento c = commentoRepo.findById(commentoId)
            .orElseThrow(() -> new EntityNotFoundException("Commento non trovato"));
        commentoRepo.delete(c);
    }

	/*
	 * posso fare anche tramite il service senza utilizzare la query nella
	 * repository
	 */
	@Transactional
	public void removeCommentiByUtente(Long prodId, Long utenteId) {
		Prodotto prodotto = prodRepo.findById(prodId)
				.orElseThrow(() -> new EntityNotFoundException("Prodotto non trovato: " + prodId));

		prodotto.getCommenti().removeIf(r -> r.getUser().getId().equals(utenteId));

		// il save non è strettamente necessario se sei in Persistence Context,
		// ma lo mettiamo per chiarezza
		// prodRepo.save(prodotto);
	}
	/*
	 * Con orphanRemoval, basta rimuovere dall’ArrayList; JPA si occupa del DELETE.
	 */

	@Transactional
	public void deleteById(Long commentoId) {
		commentoRepo.deleteById(commentoId);
	}

	/**
	 * Elimina una commento solo se l'username corrisponde all'autore.
	 *
	 * @param commentoId id della commento da cancellare
	 * @param userSurname  cognome/username del chiamante
	 * @throws EntityNotFoundException se la commento non esiste
	 * @throws AccessDeniedException   se l'utente non è l'autore
	 */
	@Transactional
	public void deleteCommentoIfOwner(Long commentoId, String userSurname) throws AccessDeniedException {
		Commento c = commentoRepo.findById(commentoId)
				.orElseThrow(() -> new EntityNotFoundException("Commento non trovato: " + commentoId));

		// controllo che il cognome dell'utente del commento corrisponda
		if (!c.getUser().getSurname().equals(userSurname)) {
			throw new AccessDeniedException("Non sei autorizzato a cancellare questo commento");
		}

		commentoRepo.delete(c);
	}
	
	/**
	 * Trova un commento per ID
	 * @param commentoId ID del commento
	 * @return commento se trovato
	 */
	public Commento trovaCommento(Long commentoId) {
		return commentoRepo.findById(commentoId)
				.orElseThrow(() -> new EntityNotFoundException("Commento non trovato: " + commentoId));
	}
	
	/**
	 * Modifica un commento solo se l'utente è l'autore
	 * @param commentoId ID del commento da modificare
	 * @param nuovoTesto nuovo testo del commento
	 * @param userSurname cognome dell'utente che vuole modificare
	 * @throws AccessDeniedException se l'utente non è l'autore
	 */
	@Transactional
	public void modificaCommentoIfOwner(Long commentoId, String nuovoTesto, String userSurname) throws AccessDeniedException {
		Commento c = commentoRepo.findById(commentoId)
				.orElseThrow(() -> new EntityNotFoundException("Commento non trovato: " + commentoId));

		// controllo che il cognome dell'utente del commento corrisponda
		if (!c.getUser().getSurname().equals(userSurname)) {
			throw new AccessDeniedException("Non sei autorizzato a modificare questo commento");
		}

		// aggiorna il testo
		c.setTesto(nuovoTesto);
		commentoRepo.save(c);
	}

}
