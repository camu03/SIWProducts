package esame.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.*;

@Entity
public class Prodotto {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String nome;
	private Double prezzo;
	private String descrizione;
	private String tipologia;

	@OneToMany(mappedBy = "prodotto", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Commento> commenti = new ArrayList<>();

	@OneToMany(mappedBy = "prodotto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Immagine> immagini = new ArrayList<>();

	// Prodotti simili - relazione Many-to-Many
	@ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
	@JoinTable(name = "Prodotti_simili", joinColumns = @JoinColumn(name = "prodotto_id"), inverseJoinColumns = @JoinColumn(name = "prodotto_simile_id"))
	private List<Prodotto> ProdottiSimili = new ArrayList<>();

	// getter e setter
	public List<Immagine> getImmagini() {
		return immagini;
	}

	public void setImmagini(List<Immagine> immagini) {
		this.immagini = immagini;
	}

	// utilità per aggiungere/rimuovere
	public void addImmagine(Immagine img) {
		immagini.add(img);
		img.setProdotto(this);
	}

	public void removeImmagine(Immagine img) {
		immagini.remove(img);
		img.setProdotto(null);
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
		this.nome = paroleMaiuscole(nome);
	}

	public String getTipologia() {
		return tipologia;
	}

	public void setTipologia(String tipologia) {
		this.tipologia = tipologia;
	}

	/**
	 * metodo che serve per rendere maiuscola il primo carattere della parola
	 * 
	 * @param Stringa di testo
	 * @return la stringa con il primo carattere maiuscolo
	 */
	private String paroleMaiuscole(String testo) {

		if (testo == null || testo.isBlank()) {
			return testo;
		}

		String[] frase = testo.trim().split("\\s+");
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < frase.length; i++) {

			String w = frase[i];
			sb.append(w.substring(0, 1).toUpperCase() + w.substring(1).toLowerCase());

			if (i < frase.length - 1) {
				sb.append(" ");
			}
		}

		return sb.toString();
	}

	public double getPrezzo() {
		return prezzo;
	}

	public void setPrezzo(Double prezzo) {
		this.prezzo = prezzo;
	}

	public List<Commento> getCommenti() {
		return this.commenti;
	}

	public void setCommenti(List<Commento> commenti) {
		this.commenti = commenti;
	}

	public void addCommento(Commento commento) {
		this.commenti.add(commento);
		commento.setProdotto(this);
	}

	public void removeCommento(Commento commento) {
		this.commenti.remove(commento);
		commento.setProdotto(null);
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	// Getter e Setter per Prodotti simili
	public List<Prodotto> getProdottiSimili() {
		return ProdottiSimili;
	}

	public void setProdottiSimili(List<Prodotto> ProdottiSimili) {
		this.ProdottiSimili = ProdottiSimili;
	}

	// Metodi di utilità per gestire Prodotti simili
	public void addProdottoSimile(Prodotto prodotto) {
		if (!this.ProdottiSimili.contains(prodotto)) {
			this.ProdottiSimili.add(prodotto);
		}
	}

	public void removeProdottoSimile(Prodotto prodotto) {
		this.ProdottiSimili.remove(prodotto);
	}

	// metodi hash e equals
	@Override
	public int hashCode() {
		return Objects.hash(nome, id, tipologia);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Prodotto other = (Prodotto) obj;
		return Objects.equals(tipologia, other.tipologia) && Objects.equals(id, other.id)
				&& Objects.equals(nome, other.nome);
	}
}
