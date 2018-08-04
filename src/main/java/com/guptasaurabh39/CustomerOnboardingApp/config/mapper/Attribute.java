package com.guptasaurabh39.CustomerOnboardingApp.config.mapper;

public class Attribute {
	private String id;

	private String dataType;

	private String name;

	private String nullable;

	private String parentAttributeId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNullable() {
		return nullable;
	}

	public void setNullable(String nullable) {
		this.nullable = nullable;
	}

	public String getParentAttributeId() {
		return parentAttributeId;
	}

	public void setParentAttributeId(String parentAttributeId) {
		this.parentAttributeId = parentAttributeId;
	}

	@Override
	public String toString() {
		return "ClassPojo [id = " + id + ", dataType = " + dataType
				+ ", name = " + name + ", nullable = " + nullable
				+ ", parentAttributeId = " + parentAttributeId + "]";
	}
}