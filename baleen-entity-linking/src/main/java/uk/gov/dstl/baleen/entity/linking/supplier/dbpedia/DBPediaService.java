// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.entity.linking.supplier.dbpedia;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.sparql.lang.sparql_11.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dstl.baleen.entity.linking.util.DefaultCandidate;
import uk.gov.dstl.baleen.entity.linking.util.StringTriple;

/**
 * Service for getting data from DBPedia using the DBPedia SPARQL endpoint See <a
 * href="http://uk.dbpedia.org/sparql">http://uk.dbpedia.org/sparql</a>
 */
public class DBPediaService {

  /**
   * Friend of a Friend prefix label See <a
   * href="http://xmlns.com/foaf/spec/">http://xmlns.com/foaf/spec/</a>
   */
  public static final String FOAF = "foaf";

  /**
   * DBPedia ontology prefix label See <a
   * href="http://mappings.dbpedia.org/server/ontology/classes/">
   * http://mappings.dbpedia.org/server/ontology/classes/</a>
   */
  public static final String DBPEDIA_OWL = "dbpedia-owl";

  /**
   * Friend of a Friend 'name' label See <a
   * href="http://xmlns.com/foaf/spec/">http://xmlns.com/foaf/spec/</a>
   */
  public static final String FOAF_NAME_LABEL = "name";

  /**
   * Person ontology label for DBPedia See <a
   * href="http://mappings.dbpedia.org/server/ontology/classes/">
   * http://mappings.dbpedia.org/server/ontology/classes/</a>
   */
  public static final String PERSON_ONTOLOGY_LABEL = "Person";

  /** Abstract ontology label for DBPedia */
  public static final String ABSTRACT_ONTOLOGY_LABEL = "abstract";

  /** Organisation ontology label for DBPedia */
  public static final String ORGANISATION_ONTOLOGY_LABEL = "Organisation";

  /** Location ontology label for DBPedia */
  public static final String LOCATION_ONTOLOGY_LABEL = "Place";

  /** The default language to filter specified fields by */
  public static final String DEFAULT_LANGUAGE = "en";

  private String language;

  private static final Logger LOGGER = LoggerFactory.getLogger(DBPediaService.class);

  private static final String FOAF_VALUE = "http://xmlns.com/foaf/0.1/";
  private static final String DBPEDIA_OWL_VALUE = "http://dbpedia.org/ontology/";

  private static final String DBPEDIA_SPARQL_ENDPOINT = "http://dbpedia.org/sparql";

  /** Constructor. Sets language to 'en' */
  public DBPediaService() {
    language = DEFAULT_LANGUAGE;
  }

  /**
   * Constructor
   *
   * @param language The language to filter certain parameters
   */
  public DBPediaService(String language) {
    this.language = language;
  }

  /**
   * Search DBPedia for candidates with no language restriction
   *
   * @param selectVars The placeholders for variables to find eg ?x ?y
   * @param whereClauses An array of StringTriple in Subject, Predicate, Object form
   * @param filterClause Clause String to filter results
   * @param limit The maximum number of Candidates to return
   * @param idField A uniquely identifying field contained in the returned Candidates
   * @param nameField The name field used to find the Candidates
   * @return a Set of DBPediaCandidates
   * @throws ParseException Exception thrown if the SPARQL query fails to parse
   */
  public Set<DefaultCandidate> searchForCandidates(
      String[] selectVars,
      StringTriple[] whereClauses,
      String filterClause,
      int limit,
      String idField,
      String nameField)
      throws ParseException {

    return searchForCandidates(
        selectVars, whereClauses, filterClause, limit, idField, nameField, new String[0]);
  }

  /**
   * Search DBPedia for candidates with a language restriction on certain variables
   *
   * @param selectVars The placeholders for variables to find eg ?x ?y
   * @param whereClauses An array of StringTriple in Subject, Predicate, Object form
   * @param filterClause Clause String to filter results
   * @param limit The maximum number of Candidates to return
   * @param idField A uniquely identifying field contained in the returned Candidates
   * @param nameField The name field used to find the Candidates
   * @param languageSpecificVars Variables to restrict to the specified language
   * @return a Set of DBPediaCandidates
   * @throws ParseException Exception thrown if the SPARQL query fails to parse
   */
  public Set<DefaultCandidate> searchForCandidates(
      String[] selectVars,
      StringTriple[] whereClauses,
      String filterClause,
      int limit,
      String idField,
      String nameField,
      String[] languageSpecificVars)
      throws ParseException {

    Query query = buildQuery(selectVars, whereClauses, filterClause, limit, languageSpecificVars);

    QueryExecution qe = QueryExecutionFactory.sparqlService(DBPEDIA_SPARQL_ENDPOINT, query);

    ResultSet rs = qe.execSelect();

    if (LOGGER.isDebugEnabled()) {
      ResultSet rsToPrint = qe.execSelect();
      LOGGER.debug(ResultSetFormatter.asText(rsToPrint));
    }

    return getCandidates(rs, idField, nameField);
  }

  private Query buildQuery(
      String[] selectVars,
      StringTriple[] whereTriples,
      String filterClause,
      int limit,
      String[] languageSpecificVars)
      throws ParseException {

    String selectClause = createSelectClause(selectVars);

    SelectBuilder selectBuilder =
        new SelectBuilder()
            .addPrefix(FOAF, FOAF_VALUE)
            .addPrefix(DBPEDIA_OWL, DBPEDIA_OWL_VALUE)
            .addVar(selectClause);
    for (StringTriple triple : whereTriples) {
      selectBuilder.addWhere(triple.getSubject(), triple.getPredicate(), triple.getObject());
    }

    selectBuilder.addFilter(filterClause).setLimit(limit);
    for (String selectVar : languageSpecificVars) {
      selectBuilder.addFilter("lang(" + selectVar + ") = " + "'" + language + "'");
    }

    String queryString = selectBuilder.toString();
    LOGGER.debug("Query to send to DBPedia: {}", queryString);
    return selectBuilder.build();
  }

  private String createSelectClause(String[] selectVars) {
    StringBuilder sb = new StringBuilder();

    for (String selectVar : selectVars) {
      sb.append(selectVar + " ");
    }

    return sb.toString();
  }

  private Set<DefaultCandidate> getCandidates(ResultSet rs, String idField, String nameField) {
    Set<DefaultCandidate> candidates = new HashSet<>();
    while (rs.hasNext()) {
      QuerySolution qs = rs.next();
      Map<String, String> fieldsMap = new HashMap<>();
      qs.varNames()
          .forEachRemaining(
              varName -> fieldsMap.put(varName, new DBPediaLanguageString(qs.get(varName)).raw()));
      DBPediaLanguageString id = new DBPediaLanguageString(qs.get(idField));
      DBPediaLanguageString name = new DBPediaLanguageString(qs.get(nameField));
      candidates.add(new DefaultCandidate(id.raw(), name.raw(), fieldsMap));
    }
    return candidates;
  }

  /**
   * Set the language to filter specified fields by
   *
   * @param language
   */
  public void setLanguage(String language) {
    this.language = language;
  }
}
