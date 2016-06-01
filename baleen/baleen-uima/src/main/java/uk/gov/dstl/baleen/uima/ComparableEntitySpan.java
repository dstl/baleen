package uk.gov.dstl.baleen.uima;

import com.google.common.base.Strings;

import uk.gov.dstl.baleen.types.semantic.Entity;

/**
 * A span of text (begin/end) which can be associated with an entity.
 * <p>
 * NOTE: Entity is specifically excluded from the equals / hashcode so that we get uniqueness based
 * on span and type alone
 */
public class ComparableEntitySpan implements Comparable<ComparableEntitySpan> {

	private final int begin;
	private final int end;

	private final Class<? extends Entity> clazz;
	private final Entity entity;

	/**
	 * Instantiates a new span.
	 *
	 * @param entity
	 *            the entity
	 * @param begin
	 *            the begin
	 * @param end
	 *            the end
	 */
	public ComparableEntitySpan(Entity entity, int begin, int end) {
		this.entity = entity;
		this.clazz = entity.getClass();
		this.begin = begin;
		this.end = end;
	}

	/**
	 * Gets the begin.
	 *
	 * @return the begin
	 */
	public int getBegin() {
		return begin;
	}

	/**
	 * Gets the end.
	 *
	 * @return the end
	 */
	public int getEnd() {
		return end;
	}

	/**
	 * Gets the class of the entity
	 *
	 * @return the class
	 */
	public Class<?> getClazz() {
		return clazz;
	}

	/**
	 * Gets the entity.
	 *
	 * @return the entity
	 */
	public Entity getEntity() {
		return entity;
	}
	
	/**
	 * Gets the entity value, or if the value has not been set then gets the covered text
	 * 
	 * @return the entity value
	 */
	public String getValue() {
		if(!Strings.isNullOrEmpty(entity.getValue()))
			return entity.getValue();
		
		return entity.getCoveredText();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + begin;
		result = prime * result + (clazz == null ? 0 : clazz.hashCode());
		result = prime * result + end;
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
		final ComparableEntitySpan other = (ComparableEntitySpan) obj;
		if (begin != other.begin) {
			return false;
		}
		if (!clazz.equals(other.clazz)) {
			return false;
		}
		if (end != other.end) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return String.format("%s[%d,%d]", getClass().getSimpleName(), begin, end);
	}

	@Override
	public int compareTo(ComparableEntitySpan s) {
		if (s.getBegin() > this.getBegin()) {
			return -1;
		} else if (s.getBegin() < this.getBegin()) {
			return 1;
		} else if (s.getEnd() > this.getEnd()) {
			return -1;
		} else if (s.getEnd() < this.getEnd()) {
			return 1;
		} else if(this.getValue() == null && s.getValue() == null) {
			return 0;
		} else if(this.getValue() != null && s.getValue() == null) {
			return 1;
		} else if(this.getValue() == null && s.getValue() != null) {
			return -1;	
		} else {
			return this.getValue().compareTo(s.getValue());
		}		
	}

}
