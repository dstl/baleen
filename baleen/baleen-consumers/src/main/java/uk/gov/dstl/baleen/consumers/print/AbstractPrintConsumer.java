/*
 *
 */
package uk.gov.dstl.baleen.consumers.print;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;

import uk.gov.dstl.baleen.types.Base;
import uk.gov.dstl.baleen.uima.BaleenConsumer;
import uk.gov.dstl.baleen.uima.utils.UimaTypesUtils;

/**
 * Base class for consumers that print annotations to the log.
 *
 * Implementors should use the writeLine functions to output text.
 *
 * @param <T>
 *            the generic type
 *            
 * @baleen.javadoc
 */
public abstract class AbstractPrintConsumer<T extends Base> extends BaleenConsumer {

	private final Class<T> clazz;

	/**
	 * Instantiates a new annotator.
	 *
	 * @param clazz
	 *            the clazz
	 */
	protected AbstractPrintConsumer(Class<T> clazz) {
		this.clazz = clazz;
	}

	@Override
	protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {
		JCasUtil.select(jCas, clazz).stream()
				.map(this::print)
				.filter(Objects::nonNull)
				.forEach(s -> getMonitor().info("{}:\n{}", clazz.getName(), s));
	}

	/**
	 * Write a line
	 *
	 * @param sb
	 *            the sb
	 * @param value
	 *            the value
	 */
	protected void writeLine(StringBuilder sb, String value) {
		if (value != null && !value.isEmpty()) {
			sb.append("\t");
			sb.append(value);
			sb.append("\n");
		}
	}

	/**
	 * Write an annotation
	 *
	 * @param sb
	 *            the sb
	 * @param annotation
	 *            the annotation
	 */
	protected void writeLine(StringBuilder sb, Base annotation) {
		writeLine(sb, annotation.getCoveredText() + "[" + annotation.getType().getName() + "]");
	}

	/**
	 * Write a string array.
	 *
	 * @param sb
	 *            the sb
	 * @param array
	 *            the array
	 */
	protected void writeLine(StringBuilder sb, StringArray array) {
		writeLine(sb, asString(array, ";"));
	}

	/**
	 * Write line.
	 *
	 * @param sb
	 *            the sb
	 * @param array
	 *            the array
	 */
	protected void writeLine(StringBuilder sb, FSArray array) {
		writeLine(sb, asString(array, Annotation.class, fs -> fs.getCoveredText(), ";"));
	}

	/**
	 * Write a FSArray.
	 *
	 * @param <S>
	 *            the generic type
	 * @param sb
	 *            the sb
	 * @param array
	 *            the array
	 * @param clazz
	 *            the clazz
	 * @param toString
	 *            the to string
	 */
	protected <S extends Base> void writeLine(StringBuilder sb, FSArray array, Class<S> clazz,
			Function<S, String> toString) {
		writeLine(sb, asString(array, clazz, toString, ";"));
	}

	/**
	 * Write a line
	 *
	 * @param sb
	 *            the sb
	 * @param value
	 *            the value
	 */
	protected void writeLine(StringBuilder sb, String prefix, String value) {
		if (value != null && !value.isEmpty()) {
			sb.append("\t");
			sb.append(prefix);
			sb.append(": ");
			sb.append(value);
			sb.append("\n");
		}
	}

	/**
	 * Write an annotation
	 *
	 * @param sb
	 *            the sb
	 * @param annotation
	 *            the annotation
	 */
	protected void writeLine(StringBuilder sb, String prefix, Base annotation) {
		writeLine(sb, prefix, annotation.getCoveredText() + "[" + annotation.getType().getName() + "]");
	}

	/**
	 * Write a string array.
	 *
	 * @param sb
	 *            the sb
	 * @param array
	 *            the array
	 */
	protected void writeLine(StringBuilder sb, String prefix, StringArray array) {
		writeLine(sb, prefix, asString(array, ";"));
	}

	/**
	 * Write line.
	 *
	 * @param sb
	 *            the sb
	 * @param array
	 *            the array
	 */
	protected void writeLine(StringBuilder sb, String prefix, FSArray array) {
		writeLine(sb, prefix, asString(array, Annotation.class, fs -> fs.getCoveredText(), ";"));
	}

	/**
	 * Write a FSArray.
	 *
	 * @param <S>
	 *            the generic type
	 * @param sb
	 *            the sb
	 * @param array
	 *            the array
	 * @param clazz
	 *            the clazz
	 * @param toString
	 *            the to string
	 */
	protected <S extends Base> void writeLine(StringBuilder sb, String prefix, FSArray array, Class<S> clazz,
			Function<S, String> toString) {
		writeLine(sb, prefix, asString(array, clazz, toString, ";"));
	}

	/**
	 * Convert an SFArray to a string.
	 *
	 * @param <S>
	 *            the generic type
	 * @param array
	 *            the array
	 * @param clazz
	 *            the clazz
	 * @param toString
	 *            the to string
	 * @param separator
	 *            the separator
	 * @return the string
	 */
	// NOTE This is checked by the filter
	@SuppressWarnings("unchecked")
	protected <S> String asString(FSArray array, Class<S> clazz, Function<S, String> toString, String separator) {
		if (array == null) {
			return "";
		}

		final FeatureStructure[] fses = array.toArray();

		if (fses == null) {
			return "";
		}

		return Arrays.stream(fses)
				.filter(Objects::nonNull)
				.filter(fs -> clazz.isAssignableFrom(fs.getClass()))
				.map(fs -> toString.apply((S) fs))
				.collect(Collectors.joining(separator));
	}

	/**
	 * Convert a StringArray to a string.
	 *
	 * @param array
	 *            the array
	 * @param separator
	 *            the separator
	 * @return the string
	 */
	protected String asString(StringArray array, String separator) {
		return Arrays.stream(UimaTypesUtils.toArray(array))
				.collect(Collectors.joining(separator));
	}

	/**
	 * Override to write the text.
	 *
	 * Implemetations can use the Write* functions to help.
	 *
	 * @param t
	 *            the t
	 * @return the string
	 */
	protected abstract String print(T t);
}
