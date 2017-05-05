//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.contentmanipulators;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import uk.gov.dstl.baleen.contentmanipulators.helpers.ContentManipulator;

/**
 * Convert comment areas as asides.
 * 
 * This manipulator looks for "COMMENT:" through to "COMMENT ENDS" and then wraps all the tags
 * between with aside.
 * 
 * That approach might have issues if you have complex HTML between the tags... for example if you
 * had something like a merged rows in a table where the COMMENT spans multiple rows, but in
 * practise comments are usually single or multiple paragraph stretches.
 * 
 * To avoid some of these we look for COMMENT / COMMENT END within paragraph siblings and we wrap
 * each sibling individually. A later cleaner could merge adjacent comment annotations.
 */

public class CommentArea implements ContentManipulator {

	private static final String COMMENT_START = "COMMENT:";
	private static final String COMMENT_END = "COMMENT ENDS";
	
	private static final String ASIDE = "<aside />";

	@Override
	public void manipulate(Document document) {
		document.select("p:contains(" + COMMENT_END + ")").forEach(last -> {
			// We have the comment ends... but which sibling should we start from...
			// Cases are: 1. this element is also is the start block
			// 2. a previous sibling has the comment start
			// 3. Can't find in this group of siblings (so ignore)


			// Case 1: Single 'element comment'
			if (last.ownText().contains(COMMENT_START)) {
				last.wrap(ASIDE);
			} else {
				// Look for sibling before us..
				int index = last.elementSiblingIndex();
				Elements allSiblings = last.siblingElements();
				Elements pSiblings = allSiblings.select("p");
				Element startSibling = null;
				for (int i = index - 1; i >= 0; i--) {
					Element e = pSiblings.get(i);
					if (e.ownText().contains(COMMENT_START)) {
						startSibling = e;
						break;
					}
				}

				if (startSibling != null) {
					// NOTE: Difficult to know what to do here (wrap inner, wrap outter, create an
					// encompassing tag)
					// We'll wrap around the outer for the moment, which will generate multiple comments for
					// multiple paragraphs (but not break any HTML structure)
					for (int i = startSibling.elementSiblingIndex(); i < index; i++) {
						allSiblings.get(i).wrap(ASIDE);
					}
					last.wrap(ASIDE);
				}
			}
		});
	}
}