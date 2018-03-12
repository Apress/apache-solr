package com.apress.solr.pa.chapter11.opennlp;

/**
 * Holds the token and tagged entity
 * @author DikshantS
 *
 */
public class NamedEntity {
	private String token;
	private String entity;
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getEntity() {
		return entity;
	}
	public void setEntity(String entity) {
		this.entity = entity;
	}
	
	@Override
	public String toString() {
		return "NamedEntity [token=" + token + ", entity=" + entity + "]";
	}
	
	
}
