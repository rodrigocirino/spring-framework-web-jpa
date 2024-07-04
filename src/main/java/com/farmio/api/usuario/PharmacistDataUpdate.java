package com.farmio.api.usuario;

import jakarta.validation.constraints.NotNull;

public record PharmacistDataUpdate(
		@NotNull Long id, String name, String license_work, Boolean active, AddressData address)
{

}