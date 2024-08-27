package com.farmio.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.farmio.api.beans.Pharmacist;
import com.farmio.api.dto.Customer;
import com.farmio.api.dto.PharmacistDetails;
import com.farmio.api.dto.PharmacistList;
import com.farmio.api.dto.PharmacistUpdate;
import com.farmio.api.repository.PharmacistRepository;

import jakarta.validation.Valid;



@RestController
@RequestMapping("pharmacist")
public class PharmacistController {

	@Autowired // dependency injection
	private PharmacistRepository repository;

	// http://localhost:8080/pharmacist?sort=name,desc&size=2
	// app.properties can be edit size to tamanho or sort to filtro
	@GetMapping
	public Page<PharmacistList> listRec(@PageableDefault(size = 50) Pageable pages) {
		// Converte stream to Pharmacist Object
		return repository.findAll(pages)
				.map(ph -> new PharmacistList(ph.getId(), ph.getName(), ph.getLicense_work(), ph.getActive(),
						ph.getAddress()));
	}
	
	@GetMapping("/active")
	public ResponseEntity<Page<PharmacistList>> listTrueRec(@PageableDefault Pageable pages) {
		// Converte stream to Pharmacist Object
		var page = repository.findAllByActiveTrue(pages).map(ph -> new PharmacistList(ph.getId(), ph.getName(),
				ph.getLicense_work(), ph.getActive(), ph.getAddress()));
		return ResponseEntity.ok(page);
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getById(@PathVariable Long id) {
		Pharmacist entity = repository.getReferenceById(id);
		return ResponseEntity.ok(new PharmacistDetails(entity));

	}

	// save only one item
	@PostMapping
	@Transactional
	public ResponseEntity<?> recorder(@RequestBody @Valid Customer json, UriComponentsBuilder uriBuilder) {
		var ph = new Pharmacist(json);
		var rep = repository.save(ph);
		var uri = uriBuilder.path("/pharmacist/{id}").buildAndExpand(ph.getId()).toUri();
		return ResponseEntity.created(uri).body(rep);
	}

	// save multiple itens
	@PostMapping("/batch")
	@Transactional
	public void recorderBatch(@RequestBody @Valid List<Customer> jsonList) {
		for (Customer json : jsonList) {
			repository.save(new Pharmacist(json));
		}
	}

	@PutMapping
	@Transactional
	public ResponseEntity<?> updateRec(@RequestBody @Valid PharmacistUpdate json) {
		var entity = repository.getReferenceById(json.id());
		entity.updateRec(json);
		return ResponseEntity.ok(new PharmacistDetails(entity));
	}
	
	@DeleteMapping("/{id}")
	@Transactional
	public ResponseEntity<?> removeRec(@PathVariable Long id) {
		System.out.println("Deleting id=" + id);
		// repository.deleteById(id); // Hard Delete
		var entity = repository.getReferenceById(id);
		entity.deleteRec();// Soft Delete
		return ResponseEntity.noContent().build();

	}
	
}
