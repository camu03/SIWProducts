package esame.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import esame.model.Commento;

@Repository
public interface CommentoRepository extends JpaRepository<Commento, Long> {
	
	/**
	 * Trova tutti i commenti di un prodotto specifico
	 * @param prodottoId l'ID del prodotto
	 * @return lista di commenti
	 */
	@Query("SELECT c FROM Commento c WHERE c.prodotto.id = :prodottoId ORDER BY c.dataCreazione DESC")
	List<Commento> findByProdottoId(@Param("prodottoId") Long prodottoId);
	
	/**
	 * Trova tutti i commenti di un utente specifico
	 * @param userId l'ID dell'utente
	 * @return lista di commenti
	 */
	@Query("SELECT c FROM Commento c WHERE c.user.id = :userId ORDER BY c.dataCreazione DESC")
	List<Commento> findByUserId(@Param("userId") Long userId);
	
	/**
	 * Conta il numero di commenti per un prodotto
	 * @param prodottoId l'ID del prodotto
	 * @return numero di commenti
	 */
	@Query("SELECT COUNT(c) FROM Commento c WHERE c.prodotto.id = :prodottoId")
	Long countByProdottoId(@Param("prodottoId") Long prodottoId);
	
	/**
	 * Elimina commenti per prodotto e utente
	 * @param prodottoId ID del prodotto
	 * @param utenteId ID dell'utente
	 */
	@Modifying
	@Query("DELETE FROM Commento c WHERE c.prodotto.id = :prodottoId AND c.user.id = :utenteId")
	void deleteByProdottoAndUtente(@Param("prodottoId") Long prodottoId, @Param("utenteId") Long utenteId);
	
	/**
	 * Trova gli ultimi 10 commenti di un prodotto specifico
	 * @param prodottoId l'ID del prodotto
	 * @return lista dei ultimi 10 commenti
	 */
	List<Commento> findTop10ByProdottoIdOrderByDataCreazioneDesc(Long prodottoId);
	
	/**
	 * Carica commenti di un prodotto ordinati per data (più recenti prima)
	 * con limite per ottimizzazione
	 */
	@Query("SELECT c FROM Commento c " +
		   "LEFT JOIN FETCH c.user " +
		   "WHERE c.prodotto.id = :prodottoId " +
		   "ORDER BY c.dataCreazione DESC")
	List<Commento> findByProdottoIdOrderByDataCreazioneDesc(@Param("prodottoId") Long prodottoId);
}
