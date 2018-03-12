package com.apress.solr.pa.chapter11.opennlp;

/**
 * Holds the token and corresponding Part of Speech
 * @author DikshantS
 *
 */
public class PartOfSpeech {
	private String token;
	private String pos;
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getPos() {
		return pos;
	}
	public void setPos(String pos) {
		this.pos = pos;
	}
	@Override
	public String toString() {
		return "PartOfSpeech [token=" + token + ", pos=" + pos + "]";
	}
}
