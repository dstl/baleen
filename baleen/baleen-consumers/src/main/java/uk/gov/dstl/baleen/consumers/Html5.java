//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.consumers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import com.google.common.base.Strings;

import uk.gov.dstl.baleen.consumers.utils.ConsumerUtils;
import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.uima.BaleenConsumer;
import uk.gov.dstl.baleen.uima.utils.UimaTypesUtils;

/**
 * Creates HTML5 versions of the document, with entities annotated as spans. The original formatting
 * of the document is lost, and only the content is kept.
 *
 * Relationships are not currently supported.
 *
 *
 * @baleen.javadoc
 */
public class Html5 extends BaleenConsumer {

	/**
	 * The folder to output files to
	 *
	 * @baleen.config <i>Current directory</i>
	 */
	public static final String PARAM_OUTPUT_FOLDER = "outputFolder";
	@ConfigurationParameter(name = PARAM_OUTPUT_FOLDER, defaultValue = "")
	private String outputFolderString;
	private File outputFolder;

	/**
	 * Should the external ID be used for the file name? This option is useful if you have lots of
	 * files with duplicate names, or you are reading from a source that isn't file system based
	 * (e.g. a database).
	 *
	 * The external ID will be used by default if no Source URI is available, or it is badly formed.
	 *
	 * @baleen.config false
	 */
	public static final String PARAM_USE_EXTERNAL_ID = "useExternalId";
	@ConfigurationParameter(name = PARAM_USE_EXTERNAL_ID, defaultValue = "false")
	private Boolean useExternalId;

	/**
	 * Should a hash of the content be used to generate the ID? If false, then a hash of the Source
	 * URI is used instead.
	 *
	 * @baleen.config true
	 */
	public static final String PARAM_CONTENT_HASH_AS_ID = "contentHashAsId";
	@ConfigurationParameter(name = PARAM_CONTENT_HASH_AS_ID, defaultValue = "true")
	private Boolean contentHashAsId = true;

	/**
	 * Set the CSS file for the output to reference. The string, if provided, will be added as a
	 * <link ...> element in the document.
	 *
	 * @baleen.config
	 */
	public static final String PARAM_CSS = "css";
	@ConfigurationParameter(name = PARAM_CSS, defaultValue = "")
	private String css;

	private static final String FILE_EXTENSION = ".html";

	@Override
	public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
		if (Strings.isNullOrEmpty(outputFolderString)) {
			outputFolderString = System.getProperty("user.dir");
		}

		outputFolder = new File(outputFolderString);
		if (!outputFolder.exists()) {
			Boolean ret = outputFolder.mkdirs();
			if (!ret) {
				throw new ResourceInitializationException(new BaleenException("Unable to create output folder"));
			}
		}

		if (!outputFolder.isDirectory() || !outputFolder.canWrite()) {
			throw new ResourceInitializationException(new BaleenException("Unable to write to folder"));
		}
	}

	@Override
	protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {
		File f = getFileName(jCas);
		DocumentAnnotation da = getDocumentAnnotation(jCas);

		Document doc = Jsoup.parse("<!DOCTYPE html>\n<html lang=\"" + da.getLanguage() + "\"></html>");
		Element head = doc.head();

		if (!Strings.isNullOrEmpty(css)) {
			Element cssLink = head.appendElement("link");
			cssLink.attr("rel", "stylesheet");
			cssLink.attr("href", css);
		}

		Element charset = head.appendElement("meta");
		charset.attr("charset", "utf-8");

		appendMeta(head, "document.type", da.getDocType());
		appendMeta(head, "document.sourceUri", da.getSourceUri());
		appendMeta(head, "externalId", da.getHash());

		appendMeta(head, "document.classification", da.getDocumentClassification());
		appendMeta(head, "document.caveats", String.join(",", UimaTypesUtils.toArray(da.getDocumentCaveats())));
		appendMeta(head, "document.releasability",
				String.join(",", UimaTypesUtils.toArray(da.getDocumentReleasability())));

		String title = null;
		for (Metadata md : JCasUtil.select(jCas, Metadata.class)) {
			appendMeta(head, md.getKey(), md.getValue());
			if ("documentTitle".equalsIgnoreCase(md.getKey())) {
				title = md.getValue();
			}
		}

		if (!Strings.isNullOrEmpty(title)) {
			doc.title(title);
		}

		Element body = doc.body();

		// Entities
		Map<Integer, String> insertPositions = getEntityInsertPositions(jCas);

		String text = jCas.getDocumentText();
		Integer offset = 0;
		for (Entry<Integer, String> pos : insertPositions.entrySet()) {
			String insert = pos.getValue();
			text = text.substring(0, pos.getKey() + offset) + insert + text.substring(pos.getKey() + offset);
			offset += insert.length();
		}

		for (String para : text.split("[\n]+")) {
			Document docFragment = Jsoup.parseBodyFragment(para);
			Element p = body.appendElement("p");
			for (Node n : docFragment.body().childNodes()) {
				p.appendChild(n.clone());
			}
		}

		try {
			FileUtils.writeStringToFile(f, doc.html());
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		}
	}

	private Map<Integer, String> getEntityInsertPositions(JCas jCas) {
		Map<Integer, String> insertPositions = new TreeMap<>();
		Map<Integer, List<Entity>> entityStartPositions = new HashMap<>();
		for (Entity e : JCasUtil.select(jCas, Entity.class)) {
			if (insertPositions.containsKey(e.getBegin())) {
				List<Entity> entities = entityStartPositions.getOrDefault(e.getBegin(), new ArrayList<>());

				long eCount = entities.stream().filter(e2 -> e2.getEnd() > e.getEnd()).count();

				String[] spans = insertPositions.get(e.getBegin()).split("(?<=>)");
				insertPositions.put(e.getBegin(), joinSpans(eCount, e, spans));
			} else {
				insertPositions.put(e.getBegin(), generateSpanStart(e));
			}

			List<Entity> entities = entityStartPositions.getOrDefault(e.getBegin(), new ArrayList<>());
			entities.add(e);
			entityStartPositions.put(e.getBegin(), entities);

			String end = insertPositions.getOrDefault(e.getEnd(), "");
			end = "</span>" + end;
			insertPositions.put(e.getEnd(), end);
		}

		return insertPositions;
	}

	/**
	 * @param eCount
	 *            The number of entities starting in the same position as e, but finishing
	 *            afterwards
	 * @param e
	 *            The entity of interest
	 * @param spans
	 *            The array of spans that we already have
	 * @return
	 */
	private String joinSpans(long eCount, Entity e, String[] spans) {
		StringBuilder joinedSpans = new StringBuilder(eCount == 0 ? generateSpanStart(e) : "");

		Integer i = 0;
		for (String span : spans) {
			joinedSpans.append(span);
			i++;

			if (i == eCount) {
				joinedSpans.append(generateSpanStart(e));
			}
		}

		return joinedSpans.toString();
	}

	private File getFileName(JCas jCas) {
		File f = null;
		DocumentAnnotation da = getDocumentAnnotation(jCas);
		String source = da.getSourceUri();

		if (useExternalId || Strings.isNullOrEmpty(source)) {
			String id = ConsumerUtils.getExternalId(da, contentHashAsId);
			f = new File(outputFolder, id + FILE_EXTENSION);
		} else {
			try {
				String name = source.substring(source.lastIndexOf(File.separator) + 1);

				f = new File(outputFolder, name + FILE_EXTENSION);

				int append = 0;
				while (f.exists()) {
					append++;
					f = new File(outputFolder, name + "." + append + FILE_EXTENSION);
				}
				if (append != 0) {
					getMonitor().info("File with the same name already exists in {} - source file will be saved as {}",
							outputFolder.getName(), f.getName());
				}
			} catch (Exception e) {
				getMonitor().warn(
						"An error occurred trying to use the source URI {} as a file name - the external ID will be used instead",
						source, e);

				String id = ConsumerUtils.getExternalId(da, contentHashAsId);
				f = new File(outputFolder, id + FILE_EXTENSION);
			}
		}

		return f;
	}

	private Element appendMeta(Element el, String name, String content) {
		if (Strings.isNullOrEmpty(name) || Strings.isNullOrEmpty(content)) {
			return null;
		}

		Element meta = el.appendElement("meta");
		meta.attr("name", name);
		meta.attr("content", content);

		return meta;
	}

	private String generateSpanStart(Entity e) {
		String value = e.getValue() == null ? "" : e.getValue().replaceAll("\"", "'");
		String referent = e.getReferent() == null ? "" : Long.toString(e.getReferent().getInternalId());

		return String.format("<span class=\"baleen %s\" id=\"%s\" value=\"%s\" data-referent=\"%s\" >",
				e.getClass().getSimpleName(),
				e.getExternalId(), value, referent);
	}
}
