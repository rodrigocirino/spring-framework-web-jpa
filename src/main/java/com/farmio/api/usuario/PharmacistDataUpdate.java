package com.farmio.api.usuario;

import jakarta.validation.constraints.NotNull;

public record PharmacistDataUpdate(
		@NotNull Long id, String name, String license_work, Boolean active, AddressData address)
{

}

/*
 * public class PharmacistDataUpdate { private Long id; private String name;
 * private String email; private String licenseWork; private String
 * typeMedication; private Address address;
 * 
 * // getters and setters
 * 
 * public static class Address { private String street; private String apt;
 * private int num; private String city;
 * 
 * // getters and setters } }
 */