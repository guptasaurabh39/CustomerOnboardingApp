package com.guptasaurabh39.CustomerOnboardingApp.config.mapper;

public class Entities {
	private Entity entity;

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	@Override
	public String toString() {
		return "ClassPojo [entity = " + entity + "]";
	}
}