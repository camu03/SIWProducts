package esame.DTOmodel;

import java.time.LocalDateTime;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CommentoDTO {

	@Id 
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private Integer valutazione;
	private LocalDateTime dataCreazione = LocalDateTime.now();
	
	@NotBlank(message = "Il testo del commento non può essere vuoto")
	@Size(min = 5, max = 500, message = "Il commento deve essere tra 5 e 500 caratteri")
	private String testo;

	
	//metodi getter e setter
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getValutazione() {
		return valutazione;
	}
	public void setValutazione(Integer valutazione) {
		this.valutazione = valutazione;
	}
	public LocalDateTime getDataCreazione() {
		return dataCreazione;
	}
	public void setDataCreazione(LocalDateTime dataCreazione) {
		this.dataCreazione = dataCreazione;
	}
	public String getTesto() {
		return testo;
	}
	public void setTesto(String testo) {
		this.testo = testo;
	}
}
