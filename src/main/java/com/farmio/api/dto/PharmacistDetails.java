package com.farmio.api.dto;

import com.farmio.api.beans.Address;
import com.farmio.api.beans.Pharmacist;

public record PharmacistDetails(
		Long id, String name, String license_work, Boolean active, TypeMedication type_medication, Address address
) {
	public PharmacistDetails(Pharmacist ph) {
		this(ph.getId(), ph.getName(), ph.getLicense_work(), ph.getActive(), ph.getType_medication(), ph.getAddress());
	}
}

