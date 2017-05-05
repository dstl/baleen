//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima.utils.select;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

/**
 * Parses a CSS selector into an Evaluator tree.
 */
public class QueryParser<T> {

  /** Combining tokens */
  private static final String[] combinators = {",", ">", "+", "~", " "};

  /** Attribute evaluation tokens */
  private static final String[] AttributeEvals = new String[] {"=", "!=", "^=", "$=", "*=", "~="};

  /** pattern for matching two arg pseudo selectors :first-child, :last-child, :nth-child, ... */
  private static final Pattern NTH_AB =
      Pattern.compile("((\\+|-)?(\\d+)?)n(\\s*(\\+|-)?\\s*\\d+)?", Pattern.CASE_INSENSITIVE);

  /** pattern for matching one arg pseudo selectors :first-child, :last-child, :nth-child, ... */
  private static final Pattern NTH_B = Pattern.compile("(\\+|-)?(\\d+)");

  /** the token queue */
  private TokenQueue tq;

  /** the query being parsed */
  private String query;

  /** the list of evaluators constructed on calling parse */
  private List<Evaluator<T>> evals = new ArrayList<>();

  /**
   * Create a new QueryParser.
   *
   * @param query CSS query
   */
  private QueryParser(String query) {
    this.query = query;
    tq = new TokenQueue(query);
  }

  /**
   * Parse a CSS query into an Evaluator.
   *
   * @param query CSS query
   * @return Evaluator
   */
  public static <T> Evaluator<T> parse(String query) {
    try {
      QueryParser<T> p = new QueryParser<>(query);
      return p.parse();
    } catch (IllegalArgumentException e) {
      throw new Selector.SelectorParseException(e.getMessage());
    }
  }

  /**
   * Parse the query
   *
   * @return Evaluator
   */
  Evaluator<T> parse() {
    tq.consumeWhitespace();

    if (tq.matchesAny(combinators)) { // if starts with a combinator, use root as elements
      evals.add(new StructuralEvaluator.Root<T>());
      combinator(tq.consume());
    } else {
      findElements();
    }

    while (!tq.isEmpty()) {
      // hierarchy and extras
      boolean seenWhite = tq.consumeWhitespace();

      if (tq.matchesAny(combinators)) {
        combinator(tq.consume());
      } else if (seenWhite) {
        combinator(' ');
      } else { // E.class, E#id, E[attr] etc. AND
        findElements(); // take next el, #. etc off queue
      }
    }

    if (evals.size() == 1) {
      return evals.get(0);
    }

    return new CombiningEvaluator.And<>(evals);
  }

  /**
   * Add a combinator to the evaluation and parse the sub query
   *
   * @param combinator defining character
   */
  private void combinator(char combinator) {
    tq.consumeWhitespace();
    String subQuery = consumeSubQuery(); // support multi > childs

    Evaluator<T> rootEval; // the new topmost evaluator
    Evaluator<T> currentEval; // the evaluator that the new evaluator will be combined to.
    // Could be root, or rightmost or.

    Evaluator<T> newEval = parse(subQuery); // the evaluator to add into target evaluator
    boolean replaceRightMost = false;

    if (evals.size() == 1) {
      rootEval = currentEval = evals.get(0);
      // make sure OR (,) has precedence:
      if (rootEval instanceof CombiningEvaluator.Or && combinator != ',') {
        currentEval = ((CombiningEvaluator.Or<T>) currentEval).rightMostEvaluator();
        replaceRightMost = true;
      }
    } else {
      rootEval = currentEval = new CombiningEvaluator.And<>(evals);
    }
    evals.clear();

    // for most combinators: change the current eval into an AND of the current eval and the new
    // eval
    if (combinator == '>') {
      currentEval = new CombiningEvaluator.And<>(newEval,
          new StructuralEvaluator.ImmediateParent<>(currentEval));
    } else if (combinator == ' ') {
      currentEval =
          new CombiningEvaluator.And<>(newEval, new StructuralEvaluator.Parent<>(currentEval));
    } else if (combinator == '+') {
      currentEval = new CombiningEvaluator.And<>(newEval,
          new StructuralEvaluator.ImmediatePreviousSibling<>(currentEval));
    } else if (combinator == '~') {
      currentEval = new CombiningEvaluator.And<>(newEval,
          new StructuralEvaluator.PreviousSibling<>(currentEval));
    } else if (combinator == ',') { // group or.
      CombiningEvaluator.Or<T> or;
      if (currentEval instanceof CombiningEvaluator.Or) {
        or = (CombiningEvaluator.Or<T>) currentEval;
        or.add(newEval);
      } else {
        or = new CombiningEvaluator.Or<>();
        or.add(currentEval);
        or.add(newEval);
      }
      currentEval = or;
    } else {
      throw new Selector.SelectorParseException("Unknown combinator: " + combinator);
    }

    if (replaceRightMost) {
      ((CombiningEvaluator.Or<T>) rootEval).replaceRightMostEvaluator(currentEval);
    } else {
      rootEval = currentEval;
    }
    evals.add(rootEval);
  }

  /**
   * Consume the sub query, up to the next combinator or the end of the query.
   *
   * @return the sub query
   */
  private String consumeSubQuery() {
    StringBuilder sq = new StringBuilder();
    while (!tq.isEmpty()) {
      if (tq.matches("(")) {
        sq.append("(").append(tq.chompBalanced('(', ')')).append(")");
      } else if (tq.matches("[")) {
        sq.append("[").append(tq.chompBalanced('[', ']')).append("]");
      } else if (tq.matchesAny(combinators)) {
        break;
      } else {
        sq.append(tq.consume());
      }
    }
    return sq.toString();
  }

  /**
   * Match the next section of the query
   */
  private void findElements() {
    if (tq.matchChomp("#")) {
      byId();
    } else if (tq.matchChomp(".")) {
      byClass();
    } else if (tq.matchesWord() || tq.matches("*|")) {
      byTypeName();
    } else if (tq.matches("[")) {
      byAttribute();
    } else if (tq.matchChomp("*")) {
      allNodes();
    } else if (tq.matchChomp(":lt(")) {
      indexLessThan();
    } else if (tq.matchChomp(":gt(")) {
      indexGreaterThan();
    } else if (tq.matchChomp(":eq(")) {
      indexEquals();
    } else if (tq.matches(":has(")) {
      has();
    } else if (tq.matches(":contains(")) {
      contains(false);
    } else if (tq.matches(":containsOwn(")) {
      contains(true);
    } else if (tq.matches(":matches(")) {
      matches(false);
    } else if (tq.matches(":matchesOwn(")) {
      matches(true);
    } else if (tq.matches(":not(")) {
      not();
    } else if (tq.matchChomp(":nth-child(")) {
      cssNthChild(false, false);
    } else if (tq.matchChomp(":nth-last-child(")) {
      cssNthChild(true, false);
    } else if (tq.matchChomp(":nth-of-type(")) {
      cssNthChild(false, true);
    } else if (tq.matchChomp(":nth-last-of-type(")) {
      cssNthChild(true, true);
    } else if (tq.matchChomp(":first-child")) {
      evals.add(new Evaluator.IsFirstChild<>());
    } else if (tq.matchChomp(":last-child")) {
      evals.add(new Evaluator.IsLastChild<>());
    } else if (tq.matchChomp(":first-of-type")) {
      evals.add(new Evaluator.IsFirstOfType<>());
    } else if (tq.matchChomp(":last-of-type")) {
      evals.add(new Evaluator.IsLastOfType<>());
    } else if (tq.matchChomp(":only-child")) {
      evals.add(new Evaluator.IsOnlyChild<>());
    } else if (tq.matchChomp(":only-of-type")) {
      evals.add(new Evaluator.IsOnlyOfType<>());
    } else if (tq.matchChomp(":empty")) {
      evals.add(new Evaluator.IsEmpty<>());
    } else if (tq.matchChomp(":root")) {
      evals.add(new Evaluator.IsRoot<>());
    } else {
      throw new Selector.SelectorParseException(
          "Could not parse query '%s': unexpected token at '%s'", query, tq.remainder());
    }

  }

  /**
   * add the Id evaluator
   */
  private void byId() {
    String id = tq.consumeIdentifier();
    Validate.notEmpty(id);
    evals.add(new Evaluator.Id<>(id));
  }

  /**
   * add the Class evaluator
   */
  private void byClass() {
    String className = tq.consumeIdentifier();
    Validate.notEmpty(className);
    evals.add(new Evaluator.Class<>(className.trim()));
  }

  /**
   * add the Type Name evaluator
   */
  private void byTypeName() {
    String typeName = tq.consumeNodeSelector();
    Validate.notEmpty(typeName);
    evals.add(new Evaluator.TypeName<>(typeName.trim()));
  }

  /**
   * add an Attribute evaluator, selecting the correct one as required
   */
  private void byAttribute() {
    TokenQueue cq = new TokenQueue(tq.chompBalanced('[', ']')); // content queue
    String key = cq.consumeToAny(AttributeEvals); // eq, not, start, end, contain, match, (no val)
    Validate.notEmpty(key);
    cq.consumeWhitespace();

    if (cq.isEmpty()) {
      if (key.startsWith("^")) {
        evals.add(new Evaluator.AttributeStarting<>(key.substring(1)));
      } else {
        evals.add(new Evaluator.Attribute<>(key));
      }
    } else {
      if (cq.matchChomp("=")) {
        evals.add(new Evaluator.AttributeWithValue<>(key, cq.remainder()));
      } else if (cq.matchChomp("!=")) {
        evals.add(new Evaluator.AttributeWithValueNot<>(key, cq.remainder()));
      } else if (cq.matchChomp("^=")) {
        evals.add(new Evaluator.AttributeWithValueStarting<>(key, cq.remainder()));
      } else if (cq.matchChomp("$=")) {
        evals.add(new Evaluator.AttributeWithValueEnding<>(key, cq.remainder()));
      } else if (cq.matchChomp("*=")) {
        evals.add(new Evaluator.AttributeWithValueContaining<>(key, cq.remainder()));
      } else if (cq.matchChomp("~=")) {
        evals.add(new Evaluator.AttributeWithValueMatching<>(key, Pattern.compile(cq.remainder())));
      } else {
        throw new Selector.SelectorParseException(
            "Could not parse attribute query '%s': unexpected token at '%s'", query,
            cq.remainder());
      }
    }
  }

  /**
   * add the all nodes evaluator
   */
  private void allNodes() {
    evals.add(new Evaluator.AllNodes<>());
  }

  /**
   * add index less than evaluator
   */
  private void indexLessThan() {
    evals.add(new Evaluator.IndexLessThan<>(consumeIndex()));
  }

  /**
   * add index greater than evaluator
   */
  private void indexGreaterThan() {
    evals.add(new Evaluator.IndexGreaterThan<>(consumeIndex()));
  }

  /**
   * add index equals evaluator
   */
  private void indexEquals() {
    evals.add(new Evaluator.IndexEquals<>(consumeIndex()));
  }



  /**
   * Add nth child evaluator
   *
   * @param backwards true if matching from last
   * @param ofType true if matching type
   */
  private void cssNthChild(boolean backwards, boolean ofType) {
    String argS = tq.chompTo(")").trim().toLowerCase();
    Matcher mAB = NTH_AB.matcher(argS);
    Matcher mB = NTH_B.matcher(argS);
    final int a;
    final int b;
    if ("odd".equals(argS)) {
      a = 2;
      b = 1;
    } else if ("even".equals(argS)) {
      a = 2;
      b = 0;
    } else if (mAB.matches()) {
      a = mAB.group(3) != null ? Integer.parseInt(mAB.group(1).replaceFirst("^\\+", "")) : 1;
      b = mAB.group(4) != null ? Integer.parseInt(mAB.group(4).replaceFirst("^\\+", "")) : 0;
    } else if (mB.matches()) {
      a = 0;
      b = Integer.parseInt(mB.group().replaceFirst("^\\+", ""));
    } else {
      throw new Selector.SelectorParseException("Could not parse nth-index '%s': unexpected format",
          argS);
    }
    if (ofType) {
      if (backwards) {
        evals.add(new Evaluator.IsNthLastOfType<>(a, b));
      } else {
        evals.add(new Evaluator.IsNthOfType<>(a, b));
      }
    } else {
      if (backwards) {
        evals.add(new Evaluator.IsNthLastChild<>(a, b));
      } else {
        evals.add(new Evaluator.IsNthChild<>(a, b));
      }
    }
  }

  /**
   * Consume the index off the queue
   *
   * @return the index
   */
  private int consumeIndex() {
    String indexS = tq.chompTo(")").trim();
    Validate.isTrue(StringUtils.isNumeric(indexS), "Index must be numeric");
    return Integer.parseInt(indexS);
  }

  /**
   * add Has evaluator
   */
  private void has() {
    tq.consume(":has");
    String subQuery = tq.chompBalanced('(', ')');
    Validate.notEmpty(subQuery, ":has(el) subselect must not be empty");
    evals.add(new StructuralEvaluator.Has<>(parse(subQuery)));
  }

  /**
   * Add contains (or containsOwn) evaluator
   *
   * @param own true if own text
   */
  private void contains(boolean own) {
    tq.consume(own ? ":containsOwn" : ":contains");
    String searchText = TokenQueue.unescape(tq.chompBalanced('(', ')'));
    Validate.notEmpty(searchText, ":contains(text) query must not be empty");
    if (own) {
      evals.add(new Evaluator.ContainsOwnText<>(searchText));
    } else {
      evals.add(new Evaluator.ContainsText<>(searchText));
    }
  }

  /**
   * Add matches (or matchesOwn) evaluator
   *
   * @param own true if own text
   */
  private void matches(boolean own) {
    tq.consume(own ? ":matchesOwn" : ":matches");
    String regex = tq.chompBalanced('(', ')'); // don't unescape, as regex bits will be escaped
    Validate.notEmpty(regex, ":matches(regex) query must not be empty");

    if (own) {
      evals.add(new Evaluator.MatchesOwn<>(Pattern.compile(regex)));
    } else {
      evals.add(new Evaluator.Matches<>(Pattern.compile(regex)));
    }
  }

  /**
   * add Not evaluator
   */
  private void not() {
    tq.consume(":not");
    String subQuery = tq.chompBalanced('(', ')');
    Validate.notEmpty(subQuery, ":not(selector) subselect must not be empty");

    evals.add(new StructuralEvaluator.Not<>(parse(subQuery)));
  }
}
