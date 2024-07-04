package com.farmio.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.farmio.api.usuario.CustomerData;
import com.farmio.api.usuario.Pharmacist;
import com.farmio.api.usuario.PharmacistDataList;
import com.farmio.api.usuario.PharmacistDataUpdate;
import com.farmio.api.usuario.PharmacistRepository;

import jakarta.validation.Valid;



@RestController
@RequestMapping("pharmacist")
public class PharmacistController {

	@Autowired // dependency injection
	private PharmacistRepository repository;

	// http://localhost:8080/pharmacist?sort=name,desc&size=2
	// app.properties can be edit size to tamanho or sort to filtro
	@GetMapping
	public Page<PharmacistDataList> listRec(@PageableDefault(size = 50) Pageable pages) {
		// Converte stream to Pharmacist Object
		return repository.findAll(pages)
				.map(ph -> new PharmacistDataList(ph.getId(), ph.getName(), ph.getLicense_work(), ph.getActive(),
						ph.getAddress()));
	}
	
	@GetMapping("/active")
	public Page<PharmacistDataList> listTrueRec(@PageableDefault Pageable pages) {
		// Converte stream to Pharmacist Object
		return repository.findAllByActiveTrue(pages).map(ph -> new PharmacistDataList(ph.getId(), ph.getName(),
				ph.getLicense_work(), ph.getActive(), ph.getAddress()));
	}

	// save only one item
	@PostMapping
	@Transactional
	public void recorder(@RequestBody @Valid CustomerData json) {
		repository.save(new Pharmacist(json));
	}

	// save multiple itens
	@PostMapping("/batch")
	@Transactional
	public void recorderBatch(@RequestBody @Valid List<CustomerData> jsonList) {
		for (CustomerData json : jsonList) {
			repository.save(new Pharmacist(json));
		}
	}

	@PutMapping
	@Transactional
	public void updateRec(@RequestBody @Valid PharmacistDataUpdate json) {
		var ref = repository.getReferenceById(json.id());
		ref.updateRec(json);
	}
	
	@DeleteMapping("/{id}")
	@Transactional
	public void removeRec(@PathVariable Long id) {
		System.out.println("Deleting id=" + id);
		// repository.deleteById(id); // Hard Delete
		var ref = repository.getReferenceById(id);
		ref.deleteRec();// Soft Delete

	}
	
}
