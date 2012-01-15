package com.alien_roger.android.court_deadlines.entities;

public class CourtObj {
	private int depthLevel;
	private int parentLevel;
	private int currentLevel;

	private boolean haveChilds;
	private String value = "";

	public int getDepthLevel() {
		return depthLevel;
	}
	public void setDepthLevel(int level) {
		this.depthLevel = level;
	}
	public int getParentLevel() {
		return parentLevel;
	}
	public void setParentLevel(int parent) {
		this.parentLevel = parent;
	}
	public boolean isHaveChilds() {
		return haveChilds;
	}
	public void setHaveChilds(boolean haveChilds) {
		this.haveChilds = haveChilds;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public void setCurrentLevel(int currentLevel) {
		this.currentLevel = currentLevel;
	}
	public int getCurrentLevel() {
		return currentLevel;
	}
}