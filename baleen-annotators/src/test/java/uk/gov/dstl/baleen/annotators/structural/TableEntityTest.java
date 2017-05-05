//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.structural;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static uk.gov.dstl.baleen.annotators.structural.TableEntity.PARAM_PATTERN;
import static uk.gov.dstl.baleen.annotators.structural.TableEntity.PARAM_SUB_TYPE;
import static uk.gov.dstl.baleen.annotators.structural.TableEntity.PARAM_TYPE;

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
import uk.gov.dstl.baleen.types.structure.Paragraph;
import uk.gov.dstl.baleen.types.structure.Table;
import uk.gov.dstl.baleen.types.structure.TableBody;
import uk.gov.dstl.baleen.types.structure.TableCell;
import uk.gov.dstl.baleen.types.structure.TableHeader;
import uk.gov.dstl.baleen.types.structure.TableRow;

public class TableEntityTest extends AbstractAnnotatorTest {

	private static final String TH1 = "Name";
	private static final String TH2 = "Id";
	private static final String R1C1 = "Stuart";
	private static final String R2C1 = "James";
	private static final String R3C1 = "Chris";
	private static final String R1C2 = "1";
	private static final String R2C2 = "2";
	private static final String R3C2 = "3";
	private static final String HEADING = TH1 + " " + TH2;
	private static final String ROW1 = R1C1 + " " + R1C2;
	private static final String ROW2 = R2C1 + " " + R2C2;
	private static final String ROW3 = R3C1 + " " + R3C2;

	private static final String TEXT = String.join("\n", "", HEADING, ROW1, ROW2, ROW3, " other");

	public TableEntityTest() {
		super(TableEntity.class);
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

		Table table = new Table(jCas);
		table.setBegin(cursor);
		table.setDepth(depth);

		TableHeader th = new TableHeader(jCas);
		th.setBegin(cursor);
		th.setDepth(++depth);

		cursor = addRow(depth, cursor, TH1, TH2);

		th.setEnd(cursor);
		th.addToIndexes();
		--depth;

		TableBody tableBody = new TableBody(jCas);
		tableBody.setBegin(cursor);
		tableBody.setDepth(++depth);

		cursor = addRow(depth, cursor, R1C1, R1C2);
		cursor = addRow(depth, cursor, R2C1, R2C2);
		cursor = addRow(depth, cursor, R3C1, R3C2);

		tableBody.setEnd(cursor);
		tableBody.addToIndexes();
		--depth;

		table.setEnd(cursor);
		table.addToIndexes();
		--depth;
	}

	private int addRow(int depth, int cursor, String cell1, String cell2) {
		TableRow tableRow = new TableRow(jCas);
		tableRow.setBegin(++cursor);
		tableRow.setDepth(++depth);

		TableCell c1 = new TableCell(jCas);
		c1.setBegin(cursor);
		c1.setDepth(++depth);

		Paragraph p1 = new Paragraph(jCas);
		p1.setBegin(cursor);
		p1.setDepth(++depth);
		cursor += cell1.length();
		p1.setEnd(cursor);
		p1.addToIndexes();

		--depth;
		c1.setEnd(cursor);
		c1.addToIndexes();

		TableCell c2 = new TableCell(jCas);
		c2.setBegin(++cursor);
		c2.setDepth(depth);

		Paragraph p2 = new Paragraph(jCas);
		p2.setBegin(cursor);
		p2.setDepth(++depth);
		cursor += cell2.length();
		p2.setEnd(cursor);
		p2.addToIndexes();
		--depth;

		c2.setEnd(cursor);
		c2.addToIndexes();

		--depth;

		tableRow.setEnd(cursor);
		tableRow.addToIndexes();

		--depth;

		return cursor;
	}

	@Test
	public void testProcess() throws Exception {

		processJCas(PARAM_PATTERN, "name", PARAM_TYPE, Person.class.getSimpleName(), PARAM_SUB_TYPE, "subType");

		Collection<Person> people = JCasUtil.select(jCas, Person.class);

		assertEquals(3, people.size());
		Set<String> names = people.stream().map(Person::getCoveredText).collect(Collectors.toSet());
		assertTrue(names.contains(R1C1));
		assertTrue(names.contains(R2C1));
		assertTrue(names.contains(R3C1));

		assertEquals("subType", people.iterator().next().getSubType());
	}

}
