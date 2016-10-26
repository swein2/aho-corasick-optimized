package com.lahodiuk.ahocorasick;

class Found implements Comparable<Found> {

	public final String found;
	public final int startPosition;
	public final int endPosition;

	public Found(String found, int startPosition, int endPosition) {
		this.found = found;
		this.startPosition = startPosition;
		this.endPosition = endPosition;
	}

	@Override
	public int compareTo(Found o) {
		if (this.found.compareTo(o.found) != 0) {
			return this.found.compareTo(o.found);
		}
		if (Integer.compare(this.startPosition, o.startPosition) != 0) {
			return Integer.compare(this.startPosition, o.startPosition);
		}
		return Integer.compare(this.endPosition, o.endPosition);
	}

	@Override
	public String toString() {
		return "[" + this.found + ": " + this.startPosition + ".." + this.endPosition + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + this.endPosition;
		result = (prime * result) + ((this.found == null) ? 0 : this.found.hashCode());
		result = (prime * result) + this.startPosition;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		Found other = (Found) obj;
		if (this.endPosition != other.endPosition) {
			return false;
		}
		if (this.found == null) {
			if (other.found != null) {
				return false;
			}
		} else if (!this.found.equals(other.found)) {
			return false;
		}
		if (this.startPosition != other.startPosition) {
			return false;
		}
		return true;
	}
}