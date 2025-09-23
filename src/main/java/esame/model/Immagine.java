package esame.model;

import java.util.Arrays;
import java.util.Objects;

import org.hibernate.annotations.DynamicInsert;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@DynamicInsert(false)
@Table(name = "immagine")
public class Immagine {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "nome_file")
	private String nomeFile;
	
	@Column(name = "tipo_contenuto")
	private String tipoContenuto;

	//@Column(name="dati", columnDefinition="bytea")
	@Lob
	@Basic(fetch = FetchType.LAZY)        // << rende il byte[] non caricato subito
	private byte[] dati;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "Prodotto_id", nullable = true)
	private Prodotto prodotto;
	

	public Prodotto getProdotto() {
		return prodotto;
	}

	public void setProdotto(Prodotto prodotto) {
		this.prodotto = prodotto;
	}
	

	// costruttore
	public Immagine() {
	}

	public Immagine(String nomeFile, String tipoContenuto, byte[] dati) {
		this.nomeFile = nomeFile;
		this.tipoContenuto = tipoContenuto;
		this.dati = dati;
	}

	// getter e setter
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNomeFile() {
		return nomeFile;
	}

	public void setNomeFile(String nomeFile) {
		this.nomeFile = nomeFile;
	}

	public String getTipoContenuto() {
		return tipoContenuto;
	}

	public void setTipoContenuto(String tipoContenuto) {
		this.tipoContenuto = tipoContenuto;
	}

	public byte[] getDati() {
		return dati;
	}

	public void setDati(byte[] dati) {
		this.dati = dati;
	}

	// metodi hash e equals
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(dati);
		result = prime * result + Objects.hash(id, nomeFile, tipoContenuto);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Immagine other = (Immagine) obj;
		return Arrays.equals(dati, other.dati) && Objects.equals(id, other.id)
				&& Objects.equals(nomeFile, other.nomeFile) && Objects.equals(tipoContenuto, other.tipoContenuto);
	}

}
