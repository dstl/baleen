//NCA (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.regex;

import org.apache.uima.fit.util.JCasUtil;
import org.junit.Test;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.types.common.Vulnerability;

import static org.junit.Assert.assertEquals;

public class CveTest extends AbstractAnnotatorTest {

    public CveTest(){
        super(Cve.class);
    }

    @Test
    public void test() throws Exception{
        jCas.setDocumentText("The second CVE to be issued, cve-1999-0002, describes a buffer overflow in NFS mountd.");
        processJCas();

        assertEquals(1, JCasUtil.select(jCas, Vulnerability.class).size());
        Vulnerability bw = JCasUtil.selectByIndex(jCas, Vulnerability.class, 0);
        assertEquals("cve-1999-0002", bw.getCoveredText());
    }
}
