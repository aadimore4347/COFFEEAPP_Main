package com.coffee.coffeeApp.dto;

import lombok.Data;

@Data
public class CoffeeMachineDataDto {
	private Integer id;
	private String status;
	private float temperature;
	private float waterLevel;
	private float milkLevel;
	private float beansLevel;
	private String BrewType;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public float getTemperature() {
		return temperature;
	}
	public void setTemperature(float temperature) {
		this.temperature = temperature;
	}
	public float getWaterLevel() {
		return waterLevel;
	}
	public void setWaterLevel(float waterLevel) {
		this.waterLevel = waterLevel;
	}
	public float getMilkLevel() {
		return milkLevel;
	}
	public void setMilkLevel(float milkLevel) {
		this.milkLevel = milkLevel;
	}
	public float getBeansLevel() {
		return beansLevel;
	}
	public void setBeansLevel(float beansLevel) {
		this.beansLevel = beansLevel;
	}
	public String getBrewType() {
		return BrewType;
	}
	public void setBrewType(String brewType) {
		BrewType = brewType;
	}
	
	public CoffeeMachineDataDto(Integer id, String status, float temperature, float waterLevel, float milkLevel,
			float beansLevel, String brewType) {
		super();
		this.id = id;
		this.status = status;
		this.temperature = temperature;
		this.waterLevel = waterLevel;
		this.milkLevel = milkLevel;
		this.beansLevel = beansLevel;
		BrewType = brewType;
	}
	
	public CoffeeMachineDataDto() {

	}
	
	@Override
	public String toString() {
		return "CoffeeMachineDataDto [id=" + id + ", status=" + status + ", temperature=" + temperature
				+ ", waterLevel=" + waterLevel + ", milkLevel=" + milkLevel + ", beansLevel=" + beansLevel
				+ ", BrewType=" + BrewType + "]";
	}
}
