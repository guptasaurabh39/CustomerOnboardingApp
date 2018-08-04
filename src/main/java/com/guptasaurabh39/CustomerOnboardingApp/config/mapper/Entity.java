package com.guptasaurabh39.CustomerOnboardingApp.config.mapper;

public class Entity {
	private String name;

	private Attributes[] attributes;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Attributes[] getAttributes() {
		return attributes;
	}

	public void setAttributes(Attributes[] attributes) {
		this.attributes = attributes;
	}

	@Override
	public String toString() {
		return "ClassPojo [name = " + name + ", attributes = " + attributes
				+ "]";
	}
}