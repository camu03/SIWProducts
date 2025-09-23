package esame.DTOmodel;

import org.springframework.web.multipart.MultipartFile;

import esame.model.Prodotto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ProdottoDTO {

	// variabili di istanza
	private Long id;

	@NotBlank(message = "Il campo Nome non può essere vuoto")
	private String nome;

	@NotNull(message = "Il prezzo è obbligatorio")
	private Double prezzo;

	@NotBlank(message = "Il campo Descrizione non può essere vuoto")
	@Size(max = 5000, message = "La descrizione non può superare i 500 caratteri")
	private String descrizione;

	@NotBlank(message = "La Tipologia è obbligatoria")
	private String tipologia;

	// qui aggiungiamo il file (puoi anche usare MultipartFile[] se servono più
	// immagini)
	@NotNull(message = "Devi caricare almeno un’immagine")
	private MultipartFile[] files;


	// costruttori
	public ProdottoDTO() {
	}

	public ProdottoDTO(Long id, String nome, Double prezzo, String descrizione, String tipologia) {
		this.id = id;
		this.nome = nome;
		this.prezzo = prezzo;
		this.descrizione = descrizione;
		this.tipologia = tipologia;
	}

	// metodi getter e setter
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome =nome;
	}

	public Double getPrezzo() {
		return prezzo;
	}

	public void setPrezzo(Double prezzo) {
		this.prezzo = prezzo;
	}

	public MultipartFile[] getFiles() {
		return files;
	}

	public void setFiles(MultipartFile[] file) {
		this.files = file;
	}
	public String getDescrizione() {
		return descrizione;
	}
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}


	public String getTipologia() {
		return tipologia;
	}

	public void setTipologia(String tipologia) {
		this.tipologia = tipologia;
	}

	/**
	 * metodo che crea un DTO a partire da un Prodotto
	 * 
	 * @param oggetto Prodotto
	 * @retun oggetto ProdottoDTO ( oggetto transiente )
	 **/
	public static ProdottoDTO fromEntity(Prodotto l) {
		// da modificare quando aggiungo l'immagine e la recensione
		return new ProdottoDTO(l.getId(), l.getNome(), l.getPrezzo(), l.getDescrizione(), l.getTipologia());
	}

	/**
	 * metodo che converte questa DTO in una entità Prodotto
	 * 
	 * @return entità Prodotto persistente
	 **/
	public Prodotto toEntity() {
		Prodotto l = new Prodotto();
		l.setNome(this.nome);
		l.setPrezzo(this.prezzo);
		l.setDescrizione(this.descrizione);
		l.setTipologia(this.tipologia);
		return l;
	}
}
