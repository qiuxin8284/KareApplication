package com.kaer.more.entitiy;

public class PropellingMovementData {
	private String function;
	private String state;
	private String value;
	private boolean isOK;

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isOK() {
		return isOK;
	}

	public void setOK(boolean OK) {
		isOK = OK;
	}

	@Override
	public String toString() {
		return "PropellingMovementData{" +
				"function='" + function + '\'' +
				", state='" + state + '\'' +
				", value='" + value + '\'' +
				", isOK=" + isOK +
				'}';
	}
}
