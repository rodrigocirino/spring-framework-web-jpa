package com.farmio.api.usuario;


public record PharmacistDetailsDTO(
		Long id, String name, String license_work, Boolean active, TypeMedication type_medication, Address address
) {
	public PharmacistDetailsDTO(Pharmacist ph) {
		this(ph.getId(), ph.getName(), ph.getLicense_work(), ph.getActive(), ph.getType_medication(), ph.getAddress());
	}
}

