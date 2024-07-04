package com.farmio.api.usuario;

public enum TypeMedication {
	SIMPLE, // Need to be ask to pharmacist (sem receita médica)
	PRESCRIPTION, // Only with prescription medication/drug (retenção da receita)
	OVERTHECOUNTER // Could be sell in supermarket (remédio de prateleira)
}
