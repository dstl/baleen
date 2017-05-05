//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers.csv;

import uk.gov.dstl.baleen.consumers.csv.internals.CsvEvent;

/**
 * For consistency and clarity of code, this class is a wrapper for {@link uk.gov.dstl.baleen.consumers.csv.internals.CsvEvent}.
 * Including CsvEvent directly in this package and refactoring it to Event would require us to fully qualify the type Event in the class. 
 */
public class Event extends CsvEvent {

}