//NCA (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.regex;

import com.google.common.collect.ImmutableSet;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import uk.gov.dstl.baleen.annotators.regex.helpers.AbstractRegexAnnotator;
import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.semantic.Temporal;

import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extract UNIX Epoch timestamps from text and annotate them as Temporal entities
 *
 * @baleen.javadoc
 */
public class EpochTime extends AbstractRegexAnnotator<Temporal> {

    /**
     * What's the earliest timestamp that is acceptable?
     *
     * By default, must be after 1st January 2000.
     *
     * @baleen.config 946684800
     */
    public static final String PARAM_EARLIEST = "earliest";
    @ConfigurationParameter(name = PARAM_EARLIEST, defaultValue = "946684800")
    private long earliest;

    /**
     * What's the latest timestamp that is acceptable?
     * A value of -1 will indicate no maximum.
     *
     * @baleen.config -1
     */
    public static final String PARAM_LATEST = "latest";
    @ConfigurationParameter(name = PARAM_LATEST, defaultValue = "-1")
    private long latest;

    /**
     * Is the timestamp in milliseconds (rather than seconds).
     * Milliseconds will be converted into seconds (floored),
     * as Baleen does not support timestamps of millisecond resolution.
     *
     * @baleen.config false
     */
    public static final String PARAM_MILLIS = "millis";
    @ConfigurationParameter(name = PARAM_MILLIS, defaultValue = "false")
    private boolean millis;

    public EpochTime(){
        super(Pattern.compile("\\b\\d+\\b"), 1.0);
    }

    @Override
    protected Temporal create(JCas jCas, Matcher matcher) {
        Long l;
        try {
            l = Long.parseLong(matcher.group());
        }catch(NumberFormatException nfe){
            return null;
        }

        if(millis){
            l = l / 1000;
        }

        if(l < earliest)
            return null;

        if(latest >= 0 && l > latest)
            return null;

        Temporal t = new Temporal(jCas);

        t.setScope("SINGLE");
        t.setTemporalType("DATETIME");
        t.setPrecision("EXACT");

        t.setTimestampStart(l);
        t.setTimestampStop(l + 1);

        return t;
    }

    @Override
    public AnalysisEngineAction getAction() {
        return new AnalysisEngineAction(Collections.emptySet(), ImmutableSet.of(Temporal.class));
    }
}
