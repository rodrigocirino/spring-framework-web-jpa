package com.farmio.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/*
 * Class record!! 
 * introduzida no Java 14 como um preview e estabilizada no Java 16
 *  Ela simplifica a criação de classes cuja principal finalidade é armazenar dados.
 *  Um record define um conjunto imutável de campos e fornece automaticamente implementações para os métodos 
 *  	equals, hashCode e toString.
 *  Java automaticamente gera um construtor, métodos de acesso (se tiver parametros), e implementações de equals, hashCode, e toString para esta classe.
 */

/* Não há getters ou setters
	Eles fornecem métodos de acesso, mas esses métodos não seguem a convenção de nomenclatura "get"
	e não há setters porque os campos de um record são implicitamente finais e imutáveis.
	Se precisar alterar o valor crie uma cópia ou outro objeto.
*/

// Padrão DTO - Data Transfer Object

public record Customer(
		@NotBlank // notblank only for string
		String name,

		@Email
		String email,
		
		@NotNull Boolean active,
		
		@NotNull
		@Pattern(regexp = "\\d{2,4}\\p{Alpha}{2,4}") // ou "^\\d{2,4}\\p{Alpha}{2,4}$"
		String license_work, // LOWERCASE!! 12ab, 1234a,...
		
		@Valid
		TypeMedication type_medication,
		
		@NotNull
		@Valid
		AddressRules address
	) {

}
