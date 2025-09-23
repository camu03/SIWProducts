package esame.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import esame.model.Immagine;
import esame.service.ImmagineService;

@RestController
public class ImmagineController {

	@Autowired
	ImmagineService immagineService;

	// costruttore
	public ImmagineController(ImmagineService service) {
		this.immagineService = service;
	}

	// controller per l'upload
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Long> upload(@RequestParam("file") MultipartFile file) throws IOException {
		Immagine salvata = immagineService.salva(file);
		return ResponseEntity.ok(salvata.getId());
	}
}
