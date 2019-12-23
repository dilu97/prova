package com.yourbooking.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.yourbooking.model.Categoria;
import com.yourbooking.model.Cliente;
import com.yourbooking.model.Negozio;
import com.yourbooking.model.Prenotazione;
import com.yourbooking.repo.CategoriaRepository;
import com.yourbooking.repo.NegozioRepository;
import com.yourbooking.repo.PrenotazioneRepository;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
//@RequestMapping("/api")
public class NegozioController {

	@Autowired
	NegozioRepository repository;

	@Autowired
	CategoriaRepository categoriaRepository;

	@Autowired
	PrenotazioneRepository prenotazioneRepository;


	@GetMapping("/negozi")
	public List<Negozio> getAllNegozi() {
		List<Negozio> negozi = new ArrayList<>();

		repository.findAll().forEach(negozi::add);

		return negozi;
	}

	//https://www.baeldung.com/spring-request-param

	@GetMapping(value = "negozi/{id}")
	public List<Negozio> findById(@PathVariable long id) {
		return repository.findById(id);
	}

	@GetMapping(value = "negozi/categoria")
	public List<Negozio> findByCategoria(@RequestParam("categoria") long categoria) {
		Categoria c = categoriaRepository.findById(categoria);
		return repository.findAllByCategoria(c);
	}

	@GetMapping(value = "negozi/citta")
	public List<Negozio> findAllByCategoriaAndCitta(@RequestParam("categoria") long categoria, @RequestParam("citta") long citta) {
		return repository.findAllByCategoriaAndCitta(categoria, citta);
	}

	@PostMapping(value = "/negozi/create")
	public Negozio postNegozio(@RequestBody Negozio negozio) {
		Negozio _negozio = new Negozio();
		_negozio.setDescrizione(negozio.getDescrizione());
		_negozio.setEmail(negozio.getEmail());
		_negozio.setFacebook(negozio.getFacebook());
		_negozio.setInstagram(negozio.getInstagram());
		_negozio.setNome(negozio.getNome());
		_negozio.setOperatori(negozio.getOperatori());
		_negozio.setSito(negozio.getSito());
		_negozio.setTelefono(negozio.getTelefono());

		return repository.save(_negozio);
	}

	@PostMapping(value = "/api/negozio/login")
	public JSONObject login(@RequestBody JsonNode credenziali) {
		String email = credenziali.get("email").asText();
		String pwd = credenziali.get("pwd").asText();
		Negozio n = repository.findByEmailAndPwd(email, pwd);
		String res = "nok";
		if(n != null) res = "ok";
		JSONObject ret = new JSONObject();
		ret.put("response", res);

		return ret;
	}

	//numero : prenotazione attive, passate, clienti, operatori
	@PostMapping(value = "/api/negozio/stats")
	public JSONObject getStats(@RequestBody JsonNode data) {
		long id = Long.valueOf(data.get("id").asText());

		LocalDate now = LocalDate.now();
		List<Prenotazione> listPrenotazioni = prenotazioneRepository.findAllPrenotazioniAttiveByNegozio(repository.findById(id).get(0));
		int attive = 0, passate = 0;
		for(Prenotazione p : listPrenotazioni){
			if(p.getData().compareTo(now) > 0) attive++;
			else passate++;
		}

		JSONObject ret = new JSONObject();
		ret.put("attive", attive);
		ret.put("passate", passate);
		ret.put("clienti", repository.getNumeroClientiPreferiti(id));
		ret.put("operatori", repository.getNumeroOperatori(id));

		return ret;
	}


	/*
	@PutMapping("/customers/{id}")
	public ResponseEntity<Customer> updateCustomer(@PathVariable("id") long id, @RequestBody Customer customer) {
		System.out.println("Update Customer with ID = " + id + "...");

		Optional<Customer> customerData = repository.findById(id);

		if (customerData.isPresent()) {
			Customer _customer = customerData.get();
			_customer.setName(customer.getName());
			_customer.setAge(customer.getAge());
			_customer.setActive(customer.isActive());
			return new ResponseEntity<>(repository.save(_customer), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	 */
}
