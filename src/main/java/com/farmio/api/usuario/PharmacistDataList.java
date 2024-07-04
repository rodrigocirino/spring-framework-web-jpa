package com.farmio.api.usuario;


public record PharmacistDataList(
		Long id,
		String name, String license_work, Boolean active, Address address
) {
}
