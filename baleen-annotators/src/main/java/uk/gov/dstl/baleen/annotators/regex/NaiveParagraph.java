package uk.gov.dstl.baleen.annotators.regex;

import com.google.common.collect.ImmutableSet;
import org.apache.uima.jcas.JCas;
import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.language.Paragraph;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Annotate paragraphs in a document by looking for multiple new lines
 *
 * @baleen.javadoc
 */
public class NaiveParagraph extends BaleenAnnotator {
    private static final Pattern PARAGRAPH_REGEX = Pattern.compile("[^\\r\\n]+((\\r|\\n|\\r\\n)[^\\r\\n]+)*");

    protected void doProcess(JCas jCas) {
        Matcher m = PARAGRAPH_REGEX.matcher(jCas.getDocumentText());

        while(m.find()) {
            Paragraph p = new Paragraph(jCas);
            p.setBegin(m.start());
            p.setEnd(m.end());
            addToJCasIndex(p);
        }

    }

    public AnalysisEngineAction getAction() {
        return new AnalysisEngineAction(Collections.emptySet(), ImmutableSet.of(Paragraph.class));
    }
}

