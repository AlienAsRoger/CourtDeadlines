package com.alien_roger.android.court_deadlines.entities;

public class CourtObj {
	private int depthLevel;
	private long parentLevel;
	private long currentLevel;

	private boolean haveChild;
	private String value = "";

	public int getDepthLevel() {
		return depthLevel;
	}
	public void setDepthLevel(int level) {
		this.depthLevel = level;
	}
	public long getParentLevel() {
		return parentLevel;
	}
	public void setParentLevel(long parent) {
		this.parentLevel = parent;
	}
	public boolean isHaveChild() {
		return haveChild;
	}
	public int doesHaveChild() {
		return haveChild ?1:0;
	}
	public void setHaveChild(boolean haveChild) {
		this.haveChild = haveChild;
	}
	public void setHaveChild(int haveChild) {
		this.haveChild = haveChild>0;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public void setCurrentLevel(long currentLevel) {
		this.currentLevel = currentLevel;
	}
	public long getCurrentLevel() {
		return currentLevel;
	}
}
