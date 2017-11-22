package uk.gov.dstl.baleen.consumers;

import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.junit.Before;
import org.junit.Test;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.types.common.CommsIdentifier;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.ReferenceTarget;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GremlinConsumerTest extends AbstractAnnotatorTest {
    File tmpConfig;

    public GremlinConsumerTest(){
        super(GremlinConsumer.class);
    }

    @Before
    public void before() throws IOException{
        tmpConfig = File.createTempFile("GremlinConsumer", ".properties");

        BufferedWriter bw = new BufferedWriter(new FileWriter(tmpConfig));
        bw.write("gremlin.graph=org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph");
        bw.close();
    }

    @Test
    public void test() throws Exception{
        jCas.setDocumentText("Hello James! Is your e-mail address foo@bar.com? 'No', said James.");

        DocumentAnnotation da = getDocumentAnnotation(jCas);
        da.setDocType("test");
        da.setSourceUri("http://www.example.com/hello.txt");

        StringArray sa = new StringArray(jCas, 2);
        sa.set(0, "UK");
        sa.set(1, "US");
        da.setDocumentCaveats(sa);

        ReferenceTarget rt = new ReferenceTarget(jCas);
        rt.addToIndexes();

        Entity e1 = new Person(jCas, 6, 11);
        e1.setValue("James");
        e1.setReferent(rt);
        e1.addToIndexes();

        Entity e1a = new Person(jCas, 60, 65);
        e1a.setValue("James");
        e1a.setReferent(rt);
        e1a.addToIndexes();

        Entity e2 = new CommsIdentifier(jCas, 36, 47);
        e2.setValue("foo@bar.com");
        e2.addToIndexes();

        processJCas(GremlinConsumer.PARAM_GRAPH_CONFIG, tmpConfig.getPath());

        //TODO: Write some proper tests that actually check something
    }
}
