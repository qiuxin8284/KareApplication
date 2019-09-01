package com.kaer.more.entitiy;

public class OperateData {
	private String operate;
	private boolean isOK;

	public String getOperate() {
		return operate;
	}

	public void setOperate(String operate) {
		this.operate = operate;
	}

	public boolean isOK() {
		return isOK;
	}
	public void setOK(boolean isOK) {
		this.isOK = isOK;
	}

	@Override
	public String toString() {
		return "OperateData{" +
				"operate='" + operate + '\'' +
				", isOK=" + isOK +
				'}';
	}
}
