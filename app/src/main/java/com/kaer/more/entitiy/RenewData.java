package com.kaer.more.entitiy;

public class RenewData {
    private String desp;
    private String version;
    private String link;
	private boolean isOK;

	public String getDesp() {
		return desp;
	}

	public void setDesp(String desp) {
		this.desp = desp;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public boolean isOK() {
		return isOK;
	}

	public void setOK(boolean OK) {
		isOK = OK;
	}

	@Override
	public String toString() {
		return "RenewData{" +
				"desp='" + desp + '\'' +
				", version='" + version + '\'' +
				", link='" + link + '\'' +
				", isOK=" + isOK +
				'}';
	}
}
