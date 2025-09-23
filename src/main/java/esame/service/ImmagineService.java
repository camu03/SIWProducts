package esame.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import esame.model.Immagine;
import esame.repository.ImmagineRepository;

@Service
public class ImmagineService {

	@Autowired
	ImmagineRepository immagineRepository; 
	
	
	//costruttore 
	public ImmagineService(ImmagineRepository repo) {
		this.immagineRepository = repo;
	}
	
	/**
	 * metodo per salvare l'immagine 
	 **/
	/*
	public Immagine salva(MultipartFile file) throws IOException{
		
		Immagine salvate = new Immagine(file.getOriginalFilename(), file.getContentType(),file.getBytes());
		return immagineRepository.save(salvate);
	}*/
	
	public Immagine salva(MultipartFile file) throws IOException {
        // Costruisci l'entità con i byte veri
        Immagine img = new Immagine(
            file.getOriginalFilename(),
            file.getContentType(),
            file.getBytes()            // ← byte[] corretto
        );
        // SALVA l'oggetto creato, non null!
        return immagineRepository.save(img);
    }
	/**
	 * metodo per caricare l'immagine 
	 **/
	public Immagine carica(Long id) {
		return immagineRepository.findById(id).orElseThrow(()-> new RuntimeException("Immagine non trovata"));
	}
	
}
