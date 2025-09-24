package esame.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.*;

import esame.model.*;
import esame.repository.ProdottoRepository;
import esame.service.ProdottoService;


@Controller
public class MainController {

	/*
	 * @GetMapping("/") public String showHomePage() { return "index.html"; }
	 */
	@Autowired
	private ProdottoService prodottoService;

	@Autowired
	private ProdottoRepository prodottoRepository;

	/**
	 * metodo per visualizzare l'homepage del sito
	 * 
	 * @return index.html (homepage del sito)
	 */
	@GetMapping("/")
	public String showHomePage(Model model) {
		List<Prodotto> Prodotti = prodottoRepository.findAll();
		model.addAttribute("Prodotti", Prodotti);
		return "index"; // Thymeleaf template: src/main/resources/templates/home.html
	}

	@PostMapping("/ricercaHome")
	public String ricercaHome(Model model,@RequestParam("keyword") String keyword) {

		List<Prodotto> risultati = prodottoService.findByTitolo(keyword);
		model.addAttribute("Prodotti", risultati);
		model.addAttribute("keyword", keyword);
		return "ricerca";
	}

}
