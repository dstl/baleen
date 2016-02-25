//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.testing;

import org.apache.uima.jcas.JCas;

import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.common.Quantity;
import uk.gov.dstl.baleen.types.geo.Coordinate;
import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.types.semantic.ReferenceTarget;
import uk.gov.dstl.baleen.types.temporal.DateType;

public class Annotations {
	private static final String WEIGHT = "weight";

	private Annotations() {
		// Singleton
	}

	public static Location createLocation(JCas jCas, int begin, int end, String value, String geojson) {
		Location l = new Location(jCas);
		l.setValue(value);
		l.setBegin(begin);
		l.setEnd(end);
		if(geojson != null) {
			l.setGeoJson(geojson);
		}
		l.addToIndexes();
		return l;
	}
	
	public static Quantity createWeightQuantity(JCas jCas, int begin, int end, String value, double quantity,
			String unit, double normalizedQuantity) {
		Quantity q = new Quantity(jCas);
		q.setBegin(begin);
		q.setEnd(end);
		q.setConfidence(1.0);
		q.setValue(value);
		q.setQuantity(quantity);
		q.setUnit(unit);
		q.setNormalizedQuantity(normalizedQuantity);
		q.setNormalizedUnit("kg");
		q.setSubType(WEIGHT);
		q.addToIndexes();
		return q;
	}
	
	public static Quantity createDistanceQuantity(JCas jCas, int begin, int end, String value, int quantity,
			String unit, double normalizedQuantity) {
		Quantity q = new Quantity(jCas);
		q.setBegin(begin);
		q.setEnd(end);
		q.setConfidence(1.0);
		q.setValue(value);
		q.setQuantity(quantity);
		q.setUnit(unit);
		q.setNormalizedQuantity(normalizedQuantity);
		q.setNormalizedUnit("m");
		q.setSubType("length");
		q.addToIndexes();
		return q;
	}

	public static Coordinate createCoordinate(JCas jCas, int begin, int end, String value) {
		Coordinate c = new Coordinate(jCas);
		c.setBegin(begin);
		c.setEnd(end);
		c.setValue(value);
		c.addToIndexes();
		return c;

	}

	public static ReferenceTarget createReferenceTarget(JCas jCas) {
		ReferenceTarget rt = new ReferenceTarget(jCas);
		rt.addToIndexes();
		return rt;
	}

	public static Person createPerson(JCas jCas, int begin, int end, String value) {
		Person p = new Person(jCas);
		p.setValue(value);
		p.setBegin(begin);
		p.setEnd(end);
		p.addToIndexes();
		return p;		
	}

	public static DateType createDateType(JCas jCas, int begin, int end, String value) {
		DateType d2 = new DateType(jCas);
		d2.setValue(value);
		d2.setBegin(begin);
		d2.setEnd(end);
		d2.addToIndexes();
		return d2;
	}

	public static Metadata createMetadata(JCas jCas, String key, String value) {
		Metadata md2 = new Metadata(jCas);
		md2.setKey(key);
		md2.setValue(value);
		md2.addToIndexes();
		return md2;
	}

	public static Entity createEntity(JCas jCas, int begin, int end, String value) {
		Entity e = new Entity(jCas);
		e.setBegin(begin);
		e.setEnd(end);
		if(value != null) {
			e.setValue(e.getCoveredText());
		}
		e.addToIndexes();
		return e;
	}
}
