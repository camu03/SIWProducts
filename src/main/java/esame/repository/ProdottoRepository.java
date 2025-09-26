package esame.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import esame.model.Prodotto;

@Repository
public interface ProdottoRepository extends JpaRepository<Prodotto, Long> {
	public List<Prodotto> findByNome(String nome);

	public Long countById(Long id);
	public List<Prodotto> findByNomeContainingIgnoreCase(String nome);
	
	public List<Prodotto> findByTipologiaIgnoreCase(String tipologia);

	public Optional<Prodotto> findById(Long id);
	
	@Query("SELECT p FROM Prodotto p LEFT JOIN FETCH p.ProdottiSimili WHERE p.id = :id")
	public Optional<Prodotto> findByIdWithProdottiSimili(@Param("id") Long id);

	@Query("SELECT p FROM Prodotto p " + " LEFT JOIN FETCH p.commenti "
			+ " WHERE p.id = :id")
	Optional<Prodotto> findByIdWithCommentiAndAutori(@Param("id") Long id);

	/**
	 * Carica un prodotto con i suoi commenti
	 * @param id del prodotto
	 * @return prodotto con commenti
	 */
	@Query("SELECT p FROM Prodotto p LEFT JOIN FETCH p.commenti WHERE p.id = :id")
	Optional<Prodotto> findByIdWithCommenti(@Param("id") Long id);
	
	/**
	 * Carica un prodotto con solo gli ultimi 10 commenti per ottimizzazione
	 * @param id del prodotto
	 * @return prodotto con commenti limitati
	 */
	@Query("SELECT p FROM Prodotto p WHERE p.id = :id")
	Optional<Prodotto> findByIdOptimized(@Param("id") Long id);

	
	
	
	


	
	
	/*@Query("SELECT p FROM Prodotto p ORDER BY p.nome ASC")
	List<Prodotto> findAllOrderByNomeAsc();
	*/

	/*@Query("SELECT p FROM Prodotto p ORDER BY p.prezzo DESC")
	List<Prodotto> findAllOrderByPrezzoDesc();

	@Query("SELECT p FROM Prodotto p ORDER BY p.commenti.size() DESC")
	List<Prodotto> findAllOrderByCommentiDesc();
	*/
}
