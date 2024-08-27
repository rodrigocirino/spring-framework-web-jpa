package com.farmio.api.dto;

import com.farmio.api.beans.Address;

public record PharmacistList(
		Long id,
		String name, String license_work, Boolean active, Address address
) {

	// construtor compacto
	// validacoes like Lombok
	public PharmacistList {
		if (name == null || name.isBlank()) {
			throw new IllegalArgumentException("Name cannot be null or blank");
		}
		// ... other validations
	}

}
