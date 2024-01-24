package com.continuum.tenant.repos.entity;

import java.util.List;

public class EditsObject {
	private List<EditItem> Edits;
	private List<Object> RelativeDateEdits;

	// Getter and Setter methods

	public List<EditItem> getEdits() {
		return Edits;
	}

	public void setEdits(List<EditItem> edits) {
		Edits = edits;
	}

	public List<Object> getRelativeDateEdits() {
		return RelativeDateEdits;
	}

	public void setRelativeDateEdits(List<Object> relativeDateEdits) {
		RelativeDateEdits = relativeDateEdits;
	}

	// Inner class representing each item in the "Edits" array
	public static class EditItem {
		private String Name;
		private String Value;
		private boolean IgnoreIfEmpty;

		// Getter and Setter methods

		public String getName() {
			return Name;
		}

		public void setName(String name) {
			Name = name;
		}

		public String getValue() {
			return Value;
		}

		public void setValue(String value) {
			Value = value;
		}

		public boolean isIgnoreIfEmpty() {
			return IgnoreIfEmpty;
		}

		public void setIgnoreIfEmpty(boolean ignoreIfEmpty) {
			IgnoreIfEmpty = ignoreIfEmpty;
		}
	}

}
