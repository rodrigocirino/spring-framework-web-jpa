package com.farmio.api.usuario;


public record PharmacistDataList(
		Long id,
		String name, String license_work, Boolean active, Address address
) {

	// construtor compacto
	// validacoes like Lombok
	public PharmacistDataList {
		if (name == null || name.isBlank()) {
			throw new IllegalArgumentException("Name cannot be null or blank");
		}
		// ... other validations
	}

}
