//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.contentmanipulators;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import uk.gov.dstl.baleen.contentmanipulators.helpers.ContentManipulator;

/**
 * Recursively remove empty HTML tags to clean the document.
 * 
 * This will not remove the body tag, but everything either will be remove it is empty (or only
 * holds empty elements).
 *
 */
public class RemoveEmptyText implements ContentManipulator {

	@Override
	public void manipulate(Document document) {
		Element body = document.body();

		while (!removeEmpty(body)) {
			// Repeat as needed.... work done in the while
		}
	}

	private boolean removeEmpty(Element document) {
		Elements emptyNodes = document.select(":empty").not("body");
		if (emptyNodes.isEmpty()) {
			return true;
		}
		emptyNodes.remove();
		return false;
	}

}