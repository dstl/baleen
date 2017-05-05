//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.structural;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static uk.gov.dstl.baleen.annotators.structural.StructuralEntity.PARAM_CONFIDENCE;
import static uk.gov.dstl.baleen.annotators.structural.StructuralEntity.PARAM_QUERY;
import static uk.gov.dstl.baleen.annotators.structural.StructuralEntity.PARAM_SUB_TYPE;
import static uk.gov.dstl.baleen.annotators.structural.StructuralEntity.PARAM_TYPE;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.uima.fit.util.JCasUtil;
import org.junit.Before;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.structure.Document;
import uk.gov.dstl.baleen.types.structure.Heading;
import uk.gov.dstl.baleen.types.structure.ListItem;
import uk.gov.dstl.baleen.types.structure.Ordered;

public class StructuralEntityTest extends AbstractAnnotatorTest {

	private static final String H1 = "List of names";
	private static final String H2 = "List of names";
	private static final String N1 = "Stuart";
	private static final String N2 = "James";
	private static final String N3 = "Chris";
	private static final String N4 = "Jon";

	private static final String TEXT = String.join("\n", "", H1, N1, N2, N3, "", H2, N4, " other");

	public StructuralEntityTest() {
		super(StructuralEntity.class);
	}

	@Before
	public void setup() throws IOException {
		jCas.setDocumentText(TEXT);

		int cursor = 0;
		int depth = 0;
		Document document = new Document(jCas);
		document.setBegin(cursor);
		document.setDepth(depth);
		document.setEnd(TEXT.length());
		document.addToIndexes();

		Heading h1 = new Heading(jCas);
		h1.setBegin(cursor);
		h1.setDepth(++depth);
		cursor += H1.length();
		h1.setEnd(cursor);
		h1.setLevel(1);
		h1.addToIndexes();

		Ordered ol1 = new Ordered(jCas);
		ol1.setBegin(++cursor);
		ol1.setDepth(depth);

		depth++;
		cursor = addItem(depth, cursor, N1);
		cursor = addItem(depth, cursor, N2);
		cursor = addItem(depth, cursor, N3);
		cursor = addItem(depth, cursor, "");
		depth--;

		ol1.setEnd(cursor);
		ol1.addToIndexes();

		Heading h2 = new Heading(jCas);
		h2.setBegin(cursor);
		h2.setDepth(++depth);
		cursor += H2.length();
		h2.setEnd(cursor);
		h2.setLevel(2);
		h2.addToIndexes();

		Ordered ol2 = new Ordered(jCas);
		ol2.setBegin(++cursor);
		ol2.setDepth(depth);

		depth++;
		cursor = addItem(depth, cursor, N4);
		depth--;

		ol2.setEnd(cursor);
		ol2.addToIndexes();
		--depth;
	}

	private int addItem(int depth, int cursor, String item) {
		ListItem li = new ListItem(jCas);
		li.setBegin(++cursor);
		li.setDepth(depth);
		cursor += item.length();
		li.setEnd(cursor);
		li.addToIndexes();
		return cursor;
	}

	@Test
	public void testProcess() throws Exception {

		processJCas(PARAM_QUERY, "heading[level=1] + Ordered ListItem", PARAM_CONFIDENCE, "0.5", PARAM_TYPE,
				Person.class.getSimpleName(), PARAM_SUB_TYPE, "sub");

		Collection<Person> people = JCasUtil.select(jCas, Person.class);

		assertEquals(3, people.size());
		Set<String> names = people.stream().map(Person::getCoveredText).collect(Collectors.toSet());
		assertTrue(names.contains(N1));
		assertTrue(names.contains(N2));
		assertTrue(names.contains(N3));

		assertEquals(0.5, people.iterator().next().getConfidence(), 0.0);
		assertEquals("sub", people.iterator().next().getSubType());
	}

}
