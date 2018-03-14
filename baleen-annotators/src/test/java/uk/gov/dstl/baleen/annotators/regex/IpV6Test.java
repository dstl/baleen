// NCA (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.regex;

import static org.junit.Assert.assertEquals;

import org.apache.uima.fit.util.JCasUtil;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.types.common.CommsIdentifier;

public class IpV6Test extends AbstractAnnotatorTest {

  public IpV6Test() {
    super(IpV6.class);
  }

  @Test
  public void testFull() throws Exception {
    jCas.setDocumentText(
        "Here's a full IPv6 address fe80:0000:0000:0000:0204:61ff:fe9d:f156 and some text after it");
    processJCas();

    assertEquals(1, JCasUtil.select(jCas, CommsIdentifier.class).size());
    CommsIdentifier ip = JCasUtil.selectByIndex(jCas, CommsIdentifier.class, 0);
    assertEquals("fe80:0000:0000:0000:0204:61ff:fe9d:f156", ip.getCoveredText());
    assertEquals("ipv6address", ip.getSubType());
  }

  @Test
  public void testDropLeadingZeroes() throws Exception {
    jCas.setDocumentText(
        "Here's an IPv6 address with leading zeroes dropped: fe80:0:0:0:204:61ff:fe9d:f156.");
    processJCas();

    assertEquals(1, JCasUtil.select(jCas, CommsIdentifier.class).size());
    CommsIdentifier ip = JCasUtil.selectByIndex(jCas, CommsIdentifier.class, 0);
    assertEquals("fe80:0:0:0:204:61ff:fe9d:f156", ip.getCoveredText());
    assertEquals("ipv6address", ip.getSubType());
  }

  @Test
  public void testCollapseLeadingZeroes() throws Exception {
    jCas.setDocumentText(
        "Here's an IPv6 address with collapsed leading zeroes: (fe80::204:61ff:fe9d:f156)");
    processJCas();

    assertEquals(1, JCasUtil.select(jCas, CommsIdentifier.class).size());
    CommsIdentifier ip = JCasUtil.selectByIndex(jCas, CommsIdentifier.class, 0);
    assertEquals("fe80::204:61ff:fe9d:f156", ip.getCoveredText());
    assertEquals("ipv6address", ip.getSubType());
  }

  @Test
  public void testLocalhost() throws Exception {
    jCas.setDocumentText("Here's the localhost IPv6 address: ::1");
    processJCas();

    assertEquals(1, JCasUtil.select(jCas, CommsIdentifier.class).size());
    CommsIdentifier ip = JCasUtil.selectByIndex(jCas, CommsIdentifier.class, 0);
    assertEquals("::1", ip.getCoveredText());
    assertEquals("ipv6address", ip.getSubType());
  }
}
