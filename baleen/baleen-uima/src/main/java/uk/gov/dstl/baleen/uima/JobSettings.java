package uk.gov.dstl.baleen.uima;

import java.util.Optional;
import java.util.stream.Stream;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import uk.gov.dstl.baleen.types.metadata.Metadata;

/**
 * Settings for jobs.
 *
 * Job settings are held within a JCas, and that is passed between {@link BaleenTask}.
 *
 * Settings are string - string valued (to simplify serialisation of the jCas).
 *
 * Note that the settings are key - value, where key must be unique. If the key is set twice then,
 * like a map, the first value will be overwritten.
 */
public class JobSettings {

	private final JCas jCas;

	/**
	 * Instantiates a new job settings.
	 *
	 * @param jCas
	 *            the jcas
	 */
	public JobSettings(final JCas jCas) {
		this.jCas = jCas;
	}

	/**
	 * Gets the metadata value.
	 *
	 * @param key
	 *            the key
	 * @return the metadata
	 */
	private Optional<Metadata> getMetadata(final String key) {
		return JCasUtil.select(jCas, Metadata.class).stream()
				.filter(m -> m.getKey().equals(key))
				.findFirst();

	}

	/**
	 * Gets the value at a key.
	 *
	 * @param key
	 *            the key
	 * @param defaultValue
	 *            the default value (if the key does not exist)
	 * @return the string value
	 */
	public String get(final String key, final String defaultValue) {
		return get(key).orElse(defaultValue);
	}

	/**
	 * Gets the value of key
	 *
	 * @param key
	 *            the key
	 * @return the optional of the value
	 */
	public Optional<String> get(final String key) {
		return getMetadata(key)
				.map(Metadata::getValue);
	}

	/**
	 * Sets the value of a key.
	 *
	 * @param key
	 *            the key
	 * @param value
	 *            the value (if null the key will be deleted)
	 */
	public void set(final String key, final String value) {

		// Null = delete
		if (value == null) {
			remove(key);
			return;
		}

		// Do we have any existing metadata this this key?
		final Optional<Metadata> metadata = getMetadata(key);

		// If so, update or else create
		Metadata md;
		if (metadata.isPresent()) {
			md = metadata.get();
			md.setValue(value);
		} else {
			md = new Metadata(jCas);
			md.setBegin(0);
			md.setEnd(1);
			md.setKey(key);
			md.setValue(value);
		}
		md.addToIndexes();
	}

	/**
	 * Removes the data at the key
	 *
	 * @param key
	 *            the key
	 */
	public void remove(final String key) {
		final Optional<Metadata> metadata = getMetadata(key);
		if (metadata.isPresent()) {
			metadata.get().removeFromIndexes();
		}
	}

	/**
	 * Get all the keys
	 *
	 * @return the stream
	 */
	public Stream<String> keys() {
		return JCasUtil.select(jCas, Metadata.class).stream()
				.map(m -> m.getKey());
	}

}
