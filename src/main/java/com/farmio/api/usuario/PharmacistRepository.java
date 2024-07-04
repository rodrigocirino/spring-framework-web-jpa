package com.farmio.api.usuario;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PharmacistRepository extends JpaRepository<Pharmacist, Long> {

	// findAllBy <my field is> <expected value>
	Page<Pharmacist> findAllByActiveTrue(Pageable pages);
	// <Bean of repository, type of identifiers>  <Pharmacist, Long>
}
