package com.alien_roger.court_deadlines.entities;

/**
 * PriorityObject class
 *
 * @author alien_roger
 * @created at: 29.01.12 20:56
 */
public class PriorityObject {
	private int value;
	private String text;
	private boolean checked;

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}
}
