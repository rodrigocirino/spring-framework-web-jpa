package com.farmio.api.dto;

import jakarta.validation.constraints.NotNull;

public record PharmacistUpdate(
		@NotNull Long id, String name, String license_work, Boolean active, AddressRules address)
{

}