/**
 * Contains annotators that are based on regular expressions (Regex).
 *
 * <p>Regular expressions are a technique for matching patterns within text, for example e-mail
 * addresses or MGRS coordinates. The annotators in this package generally have the regular
 * expressions built in, but you can also specify your own using the {@link Custom} annotator.
 *
 * <p>Regular expressions are good at extracting things that follow a well defined pattern, but can
 * struggle in cases where the entity does not follow that pattern. It is also possible for regular
 * expressions to pull out things that aren't correct, because they match the pattern. For example,
 * phone numbers are just a string of numbers and so a regular expression annotator might
 * incorrectly decide that any string of numbers (of the right length) is a phone number.
 *
 * <p>Annotators in this package inherit from abstract annotators in the {@link
 * uk.gov.dstl.baleen.annotators.regex.helpers} package, and in some cases may pass off to classes
 * in the {@link uk.gov.dstl.baleen.annotators.regex.internals} package to avoid naming conflicts.
 */
// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.regex;
