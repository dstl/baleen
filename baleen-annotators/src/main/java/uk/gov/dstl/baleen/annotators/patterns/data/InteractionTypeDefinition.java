//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.patterns.data;

/**
 * Holds information about a particular interaction type
 */
public class InteractionTypeDefinition {

	private final String type;
	private final String subType;
	private final String pos;

	/**
	 * Instantiates a new interaction type definition.
	 *
	 * @param type
	 *            the type
	 * @param subType
	 *            the sub type
	 * @param pos
	 *            the pos
	 */
	public InteractionTypeDefinition(String type, String subType, String pos) {
		this.type = type;
		this.subType = subType;
		this.pos = pos;
	}

	/**
	 * Gets the pos.
	 *
	 * @return the pos
	 */
	public String getPos() {
		return pos;
	}

	/**
	 * Gets the sub type.
	 *
	 * @return the sub type
	 */
	public String getSubType() {
		return subType;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (pos == null ? 0 : pos.hashCode());
		result = prime * result + (subType == null ? 0 : subType.hashCode());
		result = prime * result + (type == null ? 0 : type.hashCode());
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		InteractionTypeDefinition other = (InteractionTypeDefinition) obj;
		if (pos == null) {
			if (other.pos != null) {
				return false;
			}
		} else if (!pos.equals(other.pos)) {
			return false;
		}
		if (subType == null) {
			if (other.subType != null) {
				return false;
			}
		} else if (!subType.equals(other.subType)) {
			return false;
		}
		if (type == null) {
			if (other.type != null) {
				return false;
			}
		} else if (!type.equals(other.type)) {
			return false;
		}
		return true;
	}

}