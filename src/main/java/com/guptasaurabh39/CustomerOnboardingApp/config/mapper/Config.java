package com.guptasaurabh39.CustomerOnboardingApp.config.mapper;

public class Config {
	private Entities[] entities;

	public Entities[] getEntities() {
		return entities;
	}

	public void setEntities(Entities[] entities) {
		this.entities = entities;
	}

	@Override
	public String toString() {
		return "ClassPojo [entities = " + entities + "]";
	}
}