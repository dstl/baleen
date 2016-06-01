package uk.gov.dstl.baleen.jobs.interactions.io;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import uk.gov.dstl.baleen.jobs.interactions.data.InteractionDefinition;

/**
 * A writer for Interactions.
 * <p>
 * Implementations can save to database, file or print to console.
 */
@FunctionalInterface
public interface InteractionWriter extends AutoCloseable {

	/**
	 * Initialise the writer - this is called before any calls to write().
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	default void initialise() throws IOException {
		// Do nothing
	}

	/**
	 * Write an interaction together with its alternative words (words which indicate the same
	 * interaction).
	 *
	 * @param interaction
	 *            the interaction
	 * @param alternatives
	 *            the alternatives
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	void write(InteractionDefinition interaction, Collection<String> alternatives) throws IOException;

	/**
	 * Write an interaction, without alternatives.
	 *
	 * The only alternative which is output is the interactions own lemma.
	 *
	 * @param interaction
	 *            the interaction
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	default void write(InteractionDefinition interaction) throws IOException {
		write(interaction, Collections.singletonList(interaction.getWord().getLemma()));
	}

	/**
	 * Destroy the writer - this is called after all write() calls.
	 */
	default void destroy() {
		// Do nothing
	}

	@Override
	default void close() {
		destroy();
	}
}
