package com.farmio.api.beans;

import com.farmio.api.dto.Customer;
import com.farmio.api.dto.PharmacistUpdate;
import com.farmio.api.dto.TypeMedication;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

// Jpa methods
@Table(name = "pharmacist")
@Entity(name = "Pharmacist")
// Lombok metods
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Pharmacist {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	String name;
	private String license_work;
	private Boolean active;
	
	@Enumerated(EnumType.STRING)
	private TypeMedication type_medication;
	
	@Embedded
	private Address address;
	
	public Pharmacist(Customer dto) {
		this.name = dto.name();
		this.license_work = dto.license_work();
		this.active = dto.active();
		this.type_medication = dto.type_medication();
		this.address = new Address(dto.address());
	}
	
	public void updateRec(PharmacistUpdate dto) {
		System.out.println(dto.name());
		if (dto.name() != null) {
			this.name = dto.name();
		}
		this.active = dto.active();
		this.address = new Address(dto.address());
	}

	public void deleteRec() {
		this.active = false;
	}

}
