package com.guptasaurabh39.CustomerOnboardingApp.config.mapper;

public class Attributes {
	private Attribute attribute;

	public Attribute getAttribute() {
		return attribute;
	}

	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}

	@Override
	public String toString() {
		return "ClassPojo [attribute = " + attribute + "]";
	}
}