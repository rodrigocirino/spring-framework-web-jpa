package com.farmio.api.beans;


import com.farmio.api.dto.AddressRules;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Address {

	private String street;
	private String apt;
	private String city;
	private int num;
	
	public Address(AddressRules address) {
		this.street = address.street();
		this.apt = address.apt();
		this.num = address.num();
		this.city = address.city();		
	}
}
