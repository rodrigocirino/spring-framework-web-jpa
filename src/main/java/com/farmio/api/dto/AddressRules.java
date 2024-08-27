package com.farmio.api.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddressRules(
		
		@NotBlank
		String street,
		
		// Could be NULL
		String apt,
		
		@NotNull
		@Digits(integer=6, fraction=0, message="Invalid Number Validation")
		int num,
		
		@NotBlank
		String city
	) {
}

