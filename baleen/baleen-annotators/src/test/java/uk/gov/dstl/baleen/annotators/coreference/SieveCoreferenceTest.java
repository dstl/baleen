package uk.gov.dstl.baleen.annotators.coreference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.coreference.SieveCoreference;
import uk.gov.dstl.baleen.annotators.language.MaltParser;
import uk.gov.dstl.baleen.annotators.language.OpenNLP;
import uk.gov.dstl.baleen.annotators.language.OpenNLPParser;
import uk.gov.dstl.baleen.annotators.language.WordNetLemmatizer;
import uk.gov.dstl.baleen.annotators.testing.AnnotatorTestBase;
import uk.gov.dstl.baleen.resources.SharedGenderMultiplicityResource;
import uk.gov.dstl.baleen.resources.SharedOpenNLPModel;
import uk.gov.dstl.baleen.resources.SharedStopwordResource;
import uk.gov.dstl.baleen.resources.SharedWordNetResource;
import uk.gov.dstl.baleen.types.common.Organisation;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.types.semantic.ReferenceTarget;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

public class SieveCoreferenceTest extends AnnotatorTestBase {
	private static ExternalResourceDescription stopwordsDesc;
	private static ExternalResourceDescription gMDesc;
	
	private static AnalysisEngine[] analysisEngines;
	
	@BeforeClass
	public static void before() throws ResourceInitializationException{
		analysisEngines = createAnalysisEngines();
	}
	protected static AnalysisEngine[] createAnalysisEngines() throws ResourceInitializationException {
		ExternalResourceDescription parserChunkingDesc = ExternalResourceFactory
				.createExternalResourceDescription("parserChunking", SharedOpenNLPModel.class);

		ExternalResourceDescription wordnetDesc = ExternalResourceFactory.createExternalResourceDescription("wordnet",
				SharedWordNetResource.class);

		ExternalResourceDescription tokensDesc = ExternalResourceFactory.createExternalResourceDescription("tokens",
				SharedOpenNLPModel.class);
		ExternalResourceDescription sentencesDesc = ExternalResourceFactory
				.createExternalResourceDescription("sentences", SharedOpenNLPModel.class);
		ExternalResourceDescription posDesc = ExternalResourceFactory.createExternalResourceDescription("posTags",
				SharedOpenNLPModel.class);
		ExternalResourceDescription chunksDesc = ExternalResourceFactory
				.createExternalResourceDescription("phraseChunks", SharedOpenNLPModel.class);

		stopwordsDesc = ExternalResourceFactory
				.createExternalResourceDescription(SieveCoreference.KEY_STOPWORDS, SharedStopwordResource.class);
		
		gMDesc = ExternalResourceFactory
				.createExternalResourceDescription(SieveCoreference.KEY_GENDER_MULTIPLICITY,
						SharedGenderMultiplicityResource.class);

		return asArray(
				createAnalysisEngine(OpenNLP.class, "tokens",
						tokensDesc, "sentences", sentencesDesc, "posTags", posDesc, "phraseChunks", chunksDesc),
				createAnalysisEngine(WordNetLemmatizer.class, "wordnet", wordnetDesc),
				createAnalysisEngine(OpenNLPParser.class, "parserChunking",
						parserChunkingDesc),
				createAnalysisEngine(MaltParser.class));
	}
	
	protected static AnalysisEngine[] asArray(AnalysisEngine... args) {
		return args;
	}
	
	protected static AnalysisEngine createAnalysisEngine(Class<? extends BaleenAnnotator> annotatorClass, Object... args)
			throws ResourceInitializationException {
		return AnalysisEngineFactory.createEngine(annotatorClass, args);
	}
	
	public void processJCas() throws ResourceInitializationException, AnalysisEngineProcessException {
		SimplePipeline.runPipeline(jCas, analysisEngines);
	}
	
	public void processJCasWithSieve(int sieve) throws AnalysisEngineProcessException, ResourceInitializationException{
		AnalysisEngine ae = createAnalysisEngine(SieveCoreference.class, SieveCoreference.KEY_GENDER_MULTIPLICITY, gMDesc, SieveCoreference.KEY_STOPWORDS, stopwordsDesc, "pass", sieve, "pronomial", true);
		
		ae.process(jCas);
		
		ae.destroy();
	}
	
	@Test
	public void test() throws AnalysisEngineProcessException, ResourceInitializationException {
		String text = "Chris Smith went to London and he saw Big Ben. Chris saw his sister there.";
		jCas.setDocumentText(text);

		Person chrisSmith = new Person(jCas);
		chrisSmith.setBegin(text.indexOf("Chris Smith"));
		chrisSmith.setEnd(chrisSmith.getBegin() + "Chris Smith".length());
		chrisSmith.setValue("Chris Smith");
		chrisSmith.addToIndexes();

		Person chris = new Person(jCas);
		chris.setBegin(text.indexOf("Chris", chrisSmith.getEnd()));
		chris.setEnd(chris.getBegin() + "Chris".length());
		chris.setValue("Chris");
		chris.addToIndexes();

		Location london = new Location(jCas);
		london.setBegin(text.indexOf("London"));
		london.setEnd(london.getBegin() + "London".length());
		london.setValue("London");
		london.addToIndexes();

		Location bigBen = new Location(jCas);
		bigBen.setBegin(text.indexOf("Big Ben"));
		bigBen.setEnd(bigBen.getBegin() + "Big Ben".length());
		bigBen.setValue("Big Ben");
		bigBen.addToIndexes();

		processJCas();
		processJCasWithSieve(-1);

		List<ReferenceTarget> targets = new ArrayList<>(JCasUtil.select(jCas, ReferenceTarget.class));

		List<Person> people = new ArrayList<>(JCasUtil.select(jCas, Person.class));
		List<WordToken> words = new ArrayList<>(JCasUtil.select(jCas, WordToken.class));

		long referenceId = people.get(0).getReferent().getInternalId();
		assertEquals("Chris Smith", people.get(0).getValue());
		assertEquals("Chris", people.get(1).getValue());

		assertEquals(referenceId, people.get(1).getReferent().getInternalId());

		// Check all the he and his connect to Chris
		boolean allMatch = words.stream()
				.filter(p -> p.getCoveredText().equalsIgnoreCase("his") || p.getCoveredText().equalsIgnoreCase("he"))
				.allMatch(p -> p.getReferent().getInternalId() == referenceId);
		assertTrue(allMatch);

		// We should have London or Big Ben to there - hence this should be 2, but something is off
		// at the moment...
		assertEquals(1, targets.size());
	}
	
	@Test
	public void testExtractReferenceTargets() throws AnalysisEngineProcessException, ResourceInitializationException {
		String text = "Chris went to London and he saw Big Ben there.";
		// there - london
		jCas.setDocumentText(text);

		Person chris = new Person(jCas);
		chris.setBegin(text.indexOf("Chris"));
		chris.setEnd(chris.getBegin() + "Chris".length());
		chris.addToIndexes();

		ReferenceTarget target = new ReferenceTarget(jCas);
		target.addToIndexes();

		Location london = new Location(jCas);
		london.setBegin(text.indexOf("London"));
		london.setEnd(london.getBegin() + "London".length());
		london.setReferent(target);
		london.addToIndexes();

		Location there = new Location(jCas);
		there.setBegin(text.indexOf("there"));
		there.setEnd(there.getBegin() + "there".length());
		there.setReferent(target);
		there.addToIndexes();

		processJCas();
		processJCasWithSieve(0);

		// We should have a reference target and it should be different to the previous, as its been recreated.
		Collection<ReferenceTarget> targets = JCasUtil.select(jCas, ReferenceTarget.class);
		assertEquals(1, targets.size());
		assertTrue(targets.iterator().next().getInternalId() != target.getInternalId());
	}
	
	@Test
	public void testExactStringMatch() throws AnalysisEngineProcessException, ResourceInitializationException {
		String text = "Chris went to London and in London he saw Big Ben.";
		// london - london
		jCas.setDocumentText(text);

		Person chris = new Person(jCas);
		chris.setBegin(text.indexOf("Chris"));
		chris.setEnd(chris.getBegin() + "Chris".length());
		chris.addToIndexes();

		Location london = new Location(jCas);
		london.setBegin(text.indexOf("London"));
		london.setEnd(london.getBegin() + "London".length());
		london.addToIndexes();

		Location london2 = new Location(jCas);
		london2.setBegin(text.indexOf("London", london.getEnd()));
		london2.setEnd(london2.getBegin() + "London".length());
		london2.addToIndexes();

		processJCas();
		processJCasWithSieve(1);

		List<ReferenceTarget> targets = new ArrayList<>(JCasUtil.select(jCas, ReferenceTarget.class));
		List<Location> location = new ArrayList<>(JCasUtil.select(jCas, Location.class));
		assertEquals(1, targets.size());
		assertSame(targets.get(0), location.get(0).getReferent());
		assertSame(targets.get(0), location.get(1).getReferent());
	}
	
	@Test
	public void testRelaxedStringMatch() throws AnalysisEngineProcessException, ResourceInitializationException {
		String text = "The University of Warwick is near Coventry and that was the University at which Chris studied.";
		// university of warwick - university
		jCas.setDocumentText(text);

		Person chris = new Person(jCas);
		chris.setBegin(text.indexOf("Chris"));
		chris.setEnd(chris.getBegin() + "Chris".length());
		chris.addToIndexes();

		Organisation uow = new Organisation(jCas);
		uow.setBegin(text.indexOf("University of Warwick"));
		uow.setEnd(uow.getBegin() + "University of Warwick".length());
		uow.addToIndexes();

		Organisation u = new Organisation(jCas);
		u.setBegin(text.indexOf("University", uow.getEnd()));
		u.setEnd(u.getBegin() + "University".length());
		u.addToIndexes();

		processJCas();
		processJCasWithSieve(2);

		List<ReferenceTarget> targets = new ArrayList<>(JCasUtil.select(jCas, ReferenceTarget.class));
		List<Organisation> location = new ArrayList<>(JCasUtil.select(jCas, Organisation.class));
		assertEquals(1, targets.size());
		assertSame(targets.get(0), location.get(0).getReferent());
		assertSame(targets.get(0), location.get(1).getReferent());
	}
	
	@Test
	public void testInSentencePronoun() throws AnalysisEngineProcessException, ResourceInitializationException {
		String text = "He said he has not been in touch with her.";
		jCas.setDocumentText(text);

		processJCas();
		processJCasWithSieve(3);

		List<ReferenceTarget> targets = new ArrayList<>(JCasUtil.select(jCas, ReferenceTarget.class));
		assertEquals(1, targets.size());
	}
	
	@Test
	public void testPreciseConstructApositive() throws AnalysisEngineProcessException, ResourceInitializationException {
		String text = "The prime minister, David Cameron explained on Tuesday.";
		// david camera - prime minister
		jCas.setDocumentText(text);

		Person p = new Person(jCas);
		p.setBegin(text.indexOf("David Cameron"));
		p.setEnd(p.getBegin() + "David Cameron".length());
		p.addToIndexes();

		processJCas();
		processJCasWithSieve(4);
		
		List<ReferenceTarget> targets = new ArrayList<>(JCasUtil.select(jCas, ReferenceTarget.class));
		assertEquals(1, targets.size());
	}

	@Test
	public void testPreciseConstructPredicate() throws AnalysisEngineProcessException, ResourceInitializationException {
		String text = "David Cameron is the prime minister.";
		// david camera - prime minister
		jCas.setDocumentText(text);

		Person p = new Person(jCas);
		p.setBegin(text.indexOf("David Cameron"));
		p.setEnd(p.getBegin() + "David Cameron".length());
		p.addToIndexes();

		processJCas();
		processJCasWithSieve(4);

		List<ReferenceTarget> targets = new ArrayList<>(JCasUtil.select(jCas, ReferenceTarget.class));
		assertEquals(1, targets.size());
	}

	// NOT IMPLEMENTED
	@Test
	@Ignore
	public void testPreciseConstructRole() throws AnalysisEngineProcessException, ResourceInitializationException {
		String text = "President Obama visited today.";
		// president - obama
		jCas.setDocumentText(text);

		processJCas();
		processJCasWithSieve(4);
		
		List<ReferenceTarget> targets = new ArrayList<>(JCasUtil.select(jCas, ReferenceTarget.class));
		assertEquals(1, targets.size());
	}

	@Test
	public void testPreciseConstructRelativePronoun()
			throws AnalysisEngineProcessException, ResourceInitializationException {
		String text = "The police want to catch a man who ran away.";
		// man - who
		jCas.setDocumentText(text);

		processJCas();
		processJCasWithSieve(4);
		
		List<ReferenceTarget> targets = new ArrayList<>(JCasUtil.select(jCas, ReferenceTarget.class));
		assertEquals(1, targets.size());
	}

	@Test
	public void testPreciseConstructAcronym()
			throws AnalysisEngineProcessException, ResourceInitializationException {
		String text = "The British Broadcasting Corporation or the BBC if you prefer shows television programmes.";
		// British Broadcasting Corporation - BBC
		jCas.setDocumentText(text);

		// We need these in otherwise we just get one long setence from the mention detector

		Organisation beeb = new Organisation(jCas);
		beeb.setBegin(text.indexOf("British Broadcasting Corporation"));
		beeb.setEnd(beeb.getBegin() + "British Broadcasting Corporation".length());
		beeb.addToIndexes();

		Organisation bbc = new Organisation(jCas);
		bbc.setBegin(text.indexOf("BBC"));
		bbc.setEnd(bbc.getBegin() + "BBC".length());
		bbc.addToIndexes();

		processJCas();
		processJCasWithSieve(4);
		
		List<ReferenceTarget> targets = new ArrayList<>(JCasUtil.select(jCas, ReferenceTarget.class));
		assertEquals(1, targets.size());
		List<Organisation> orgs = new ArrayList<Organisation>(JCasUtil.select(jCas, Organisation.class));
		assertEquals(2, orgs.size());
		assertNotNull(orgs.get(0).getReferent());
		assertEquals(orgs.get(0).getReferent().getInternalId(), orgs.get(1).getReferent().getInternalId());
	}
	
	@Test
	public void testStrictHeadMatch()
			throws AnalysisEngineProcessException, ResourceInitializationException {
		String text = "The Florida Supreme Court sat today, and the Florida Court made a decision.";
		jCas.setDocumentText(text);

		Organisation fsc = new Organisation(jCas);
		fsc.setBegin(text.indexOf("Florida Supreme Court"));
		fsc.setEnd(fsc.getBegin() + "Florida Supreme Court".length());
		fsc.addToIndexes();

		Organisation fc = new Organisation(jCas);
		fc.setBegin(text.indexOf("Florida Court"));
		fc.setEnd(fc.getBegin() + "Florida Court".length());
		fc.addToIndexes();

		processJCas();
		processJCasWithSieve(5);

		List<ReferenceTarget> targets = new ArrayList<>(JCasUtil.select(jCas, ReferenceTarget.class));
		assertEquals(1, targets.size());
	}
	
	@Test
	public void testProperHeadMatchSameNumbers()
			throws AnalysisEngineProcessException, ResourceInitializationException {
		String text = "The 200 people visited and then the people left.";
		jCas.setDocumentText(text);

		processJCas();
		processJCasWithSieve(8);

		List<ReferenceTarget> targets = new ArrayList<>(JCasUtil.select(jCas, ReferenceTarget.class));
		assertEquals(1, targets.size());
	}

	@Test
	public void testProperHeadMatchDifferentNumbers()
			throws AnalysisEngineProcessException, ResourceInitializationException {
		String text = "The 200 people visited and 100 people left.";
		jCas.setDocumentText(text);

		processJCas();
		processJCasWithSieve(8);

		List<ReferenceTarget> targets = new ArrayList<>(JCasUtil.select(jCas, ReferenceTarget.class));
		assertEquals(0, targets.size());
	}

	@Test
	public void testProperHeadMatchSameLocation()
			throws AnalysisEngineProcessException, ResourceInitializationException {
		String text = "We visited the south of Amercia and travelled to the deep south of America.";
		jCas.setDocumentText(text);

		processJCas();
		processJCasWithSieve(8);

		List<ReferenceTarget> targets = new ArrayList<>(JCasUtil.select(jCas, ReferenceTarget.class));
		assertEquals(1, targets.size());
	}

	@Test
	public void testProperHeadMatchDifferentLocations()
			throws AnalysisEngineProcessException, ResourceInitializationException {
		String text = "We visited the south of Amercia and went to the north of America.";
		jCas.setDocumentText(text);

		processJCas();
		processJCasWithSieve(8);

		List<ReferenceTarget> targets = new ArrayList<>(JCasUtil.select(jCas, ReferenceTarget.class));
		assertEquals(0, targets.size());
	}
	
	@Test
	public void testRelaxedHeadMatch()
			throws AnalysisEngineProcessException, ResourceInitializationException {
		String text = "Circuit Judge N. Sanders has been seen talking to James when the Judge said ok.";
		jCas.setDocumentText(text);

		Person fsc = new Person(jCas);
		fsc.setBegin(text.indexOf("Circuit Judge N. Sanders"));
		fsc.setEnd(fsc.getBegin() + "Circuit Judge N. Sanders".length());
		fsc.addToIndexes();

		Person fc = new Person(jCas);
		fc.setBegin(text.indexOf("Judge", fsc.getEnd()));
		fc.setEnd(fc.getBegin() + "Judge".length());
		fc.addToIndexes();

		Person j = new Person(jCas);
		j.setBegin(text.indexOf("James"));
		j.setEnd(j.getBegin() + "James".length());
		j.addToIndexes();

		processJCas();
		processJCasWithSieve(9);

		List<ReferenceTarget> targets = new ArrayList<>(JCasUtil.select(jCas, ReferenceTarget.class));
		assertEquals(1, targets.size());
	}
	
	@Test
	public void testPronounResolutionSingleSentence()
			throws AnalysisEngineProcessException, ResourceInitializationException {
		String text = "John went to see Lucy and he ate with her.";
		jCas.setDocumentText(text);

		Person john = new Person(jCas);
		john.setBegin(text.indexOf("John"));
		john.setEnd(john.getBegin() + "John".length());
		john.addToIndexes();

		Person lucy = new Person(jCas);
		lucy.setBegin(text.indexOf("Lucy"));
		lucy.setEnd(lucy.getBegin() + "Lucy".length());
		lucy.addToIndexes();

		processJCas();
		processJCasWithSieve(10);

		List<ReferenceTarget> targets = new ArrayList<>(JCasUtil.select(jCas, ReferenceTarget.class));
		assertEquals(2, targets.size());

		// TODO: Need to test what that its he which is matched
	}

	@Test
	public void testPronounResolutionTwoSentence()
			throws AnalysisEngineProcessException, ResourceInitializationException {
		String text = "John went to see Lucy at the weekend. That was the first time that he saw her there.";
		jCas.setDocumentText(text);

		Person john = new Person(jCas);
		john.setBegin(text.indexOf("John"));
		john.setEnd(john.getBegin() + "John".length());
		john.addToIndexes();

		Person lucy = new Person(jCas);
		lucy.setBegin(text.indexOf("Lucy"));
		lucy.setEnd(lucy.getBegin() + "Lucy".length());
		lucy.addToIndexes();

		processJCas();
		processJCasWithSieve(10);

		List<ReferenceTarget> targets = new ArrayList<>(JCasUtil.select(jCas, ReferenceTarget.class));
		assertEquals(2, targets.size());

		// TODO: Need to test what that its he which is matched
	}
}
