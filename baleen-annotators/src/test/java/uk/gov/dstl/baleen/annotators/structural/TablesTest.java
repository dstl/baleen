//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.structural;

import static org.junit.Assert.assertEquals;

import org.apache.uima.UIMAException;
import org.apache.uima.jcas.JCas;
import org.junit.Before;
import org.junit.Test;

import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.structure.Document;
import uk.gov.dstl.baleen.types.structure.Paragraph;
import uk.gov.dstl.baleen.types.structure.Table;
import uk.gov.dstl.baleen.types.structure.TableBody;
import uk.gov.dstl.baleen.types.structure.TableCell;
import uk.gov.dstl.baleen.types.structure.TableHeader;
import uk.gov.dstl.baleen.types.structure.TableRow;
import uk.gov.dstl.baleen.uima.testing.JCasSingleton;

public class TablesTest {

	private static final String TH1 = "Name";
	private static final String TH2 = "eMail";
	private static final String R1C1 = "Stuart";
	private static final String R2C1 = "James";
	private static final String R3C1 = "Chris";
	private static final String R1C2 = "a@b.com";
	private static final String R2C2 = "c@d.com";
	private static final String R3C2 = "d@e.com";
	private static final String HEADING = TH1 + " " + TH2;
	private static final String ROW1 = R1C1 + " " + R1C2;
	private static final String ROW2 = R2C1 + " " + R2C2;

	private static final String ROW3 = R3C1 + " " + R3C2;

	private static final String TEXT = String.join("\n", "", HEADING, ROW1, ROW2, ROW3, " other");

	private JCas jCas;
	private Tables tables;

	@Before
	public void setup() throws UIMAException {
		jCas = JCasSingleton.getJCasInstance();
		jCas.setDocumentText(TEXT);

		int cursor = 0;
		int depth = 0;
		Document document = new Document(jCas);
		document.setBegin(cursor);
		document.setDepth(depth);
		document.setEnd(TEXT.length());
		document.addToIndexes();

		Table table1 = new Table(jCas);
		table1.setBegin(cursor);
		table1.setDepth(depth);

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

		tableBody.setEnd(cursor);
		tableBody.addToIndexes();
		--depth;

		table1.setEnd(cursor);
		table1.addToIndexes();
		--depth;

		Table table2 = new Table(jCas);
		table2.setBegin(cursor);
		table2.setDepth(depth);

		TableBody tb = new TableBody(jCas);
		tb.setBegin(cursor);
		tb.setDepth(++depth);

		cursor = addRow(depth, cursor, R3C1, R3C2);

		tb.setEnd(cursor);
		tb.addToIndexes();
		--depth;

		table2.setEnd(cursor);
		table2.addToIndexes();
		--depth;

		Person chris = new Person(jCas);
		int begin = (HEADING + ROW1 + ROW2).length() + 4;
		chris.setBegin(begin);
		chris.setEnd(begin + R3C1.length());
		chris.addToIndexes();

		tables = new Tables(jCas);
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
	public void testGetTables() throws Exception {
		assertEquals(2, tables.getTables().count());
	}

	@Test
	public void testGetRows() throws Exception {
		assertEquals(3, tables.getRows().count());
	}

	public void testGetCells() throws Exception {
		assertEquals(6, tables.getCells().count());
	}

	@Test
	public void testGetFilteredTables() throws Exception {
		assertEquals(1, tables.withColumn("Name").getTables().count());
	}

	@Test
	public void testGetFilteredRows() throws Exception {
		assertEquals(2, tables.withColumn("Name").withColumn("(?i)email").getRows().count());
	}

	@Test
	public void testGetFilteredCells() throws Exception {
		assertEquals(0, tables.withColumn("Name").withColumn("(?i)email").withColumn("missing").getCells().count());
	}

}
