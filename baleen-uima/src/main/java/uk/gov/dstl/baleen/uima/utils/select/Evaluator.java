//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima.utils.select;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.Validate;


/**
 * Evaluates that an node matches the selector.
 */
public abstract class Evaluator<T> {
  protected Evaluator() {}

  /**
   * Test if the node meets the evaluator's requirements.
   *
   * @param root Root of the matching subtree
   * @param node tested node
   * @return Returns <tt>true</tt> if the requirements are met or <tt>false</tt> otherwise
   */
  public abstract boolean matches(Node<T> root, Node<T> node);

  /**
   * Evaluator for type name
   */
  public static final class TypeName<T> extends Evaluator<T> {
    private final String typeName;

    /**
     * Construct evaluator for given typeName
     * 
     * @param typeName the typeName to look for
     */
    public TypeName(String typeName) {
      this.typeName = typeName;
    }

    @Override
    public boolean matches(Node<T> root, Node<T> node) {
      return node.getTypeName().equalsIgnoreCase(typeName);
    }

    @Override
    public String toString() {
      return String.format("%s", typeName);
    }
  }

  /**
   * Evaluator for node id.
   */
  public static final class Id<T> extends Evaluator<T> {
    private final String id;

    /**
     * Construct an evaluator for the given id.
     * 
     * @param id the id to look for
     */
    public Id(String id) {
      this.id = id;
    }

    @Override
    public boolean matches(Node<T> root, Node<T> node) {
      return id.equals(node.id());
    }

    @Override
    public String toString() {
      return String.format("#%s", id);
    }

  }

  /**
   * Evaluator for node class
   */
  public static final class Class<T> extends Evaluator<T> {
    private final String className;

    /**
     * Construct an evaluator for the given className.
     * 
     * @param className the className to look for
     */
    public Class(String className) {
      this.className = className;
    }

    @Override
    public boolean matches(Node<T> root, Node<T> node) {
      return node.hasClass(className);
    }

    @Override
    public String toString() {
      return String.format(".%s", className);
    }

  }

  /**
   * Evaluator for attribute name matching (case insensitive)
   */
  public static final class Attribute<T> extends Evaluator<T> {
    private final String key;

    /**
     * Construct an evaluator for the given attribute.
     * 
     * @param key the attribute name to look for
     */
    public Attribute(String key) {
      this.key = key;
    }

    @Override
    public boolean matches(Node<T> root, Node<T> node) {
      return node.hasAttr(key);
    }

    @Override
    public String toString() {
      return String.format("[%s]", key);
    }

  }

  /**
   * Evaluator for attribute name prefix matching
   */
  public static final class AttributeStarting<T> extends Evaluator<T> {
    private final String keyPrefix;
    
    /**
     * Construct an evaluator for the given attribute prefix.
     * 
     * @param keyPrefix the prefix of the attribute name to look for
     */
    public AttributeStarting(String keyPrefix) {
      Validate.notEmpty(keyPrefix);
      this.keyPrefix = keyPrefix.toLowerCase();
    }

    @Override
    public boolean matches(Node<T> root, Node<T> node) {

      Map<String, String> attributes = node.attributes();
      for (String attribute : attributes.keySet()) {
        if (attribute.toLowerCase().startsWith(keyPrefix)) {
          return true;
        }
      }
      return false;
    }

    @Override
    public String toString() {
      return String.format("[^%s]", keyPrefix);
    }

  }

  /**
   * Evaluator for attribute name/value matching
   */
  public static final class AttributeWithValue<T> extends AttributeKeyPair<T> {
    
    /**
     * Construct an evaluator for the given attribute value.
     * 
     * @param key the attribute name to look for
     * @param value the attribute value to look for
     */
    public AttributeWithValue(String key, String value) {
      super(key, value);
    }

    @Override
    public boolean matches(Node<T> root, Node<T> node) {
      return node.hasAttr(key) && value.equalsIgnoreCase(node.attr(key).trim());
    }

    @Override
    public String toString() {
      return String.format("[%s=%s]", key, value);
    }

  }

  /**
   * Evaluator for attribute name != value matching
   */
  public static final class AttributeWithValueNot<T> extends AttributeKeyPair<T> {
    
    /**
     * Construct an evaluator for an attribute without the given value.
     * 
     * @param key the attribute name to look for
     * @param value the attribute value to lnot match
     */
    public AttributeWithValueNot(String key, String value) {
      super(key, value);
    }

    @Override
    public boolean matches(Node<T> root, Node<T> node) {
      return !value.equalsIgnoreCase(node.attr(key));
    }

    @Override
    public String toString() {
      return String.format("[%s!=%s]", key, value);
    }

  }

  /**
   * Evaluator for attribute name/value matching (value prefix)
   */
  public static final class AttributeWithValueStarting<T> extends AttributeKeyPair<T> {
    
    /**
     * Construct a starts with evaluator for the given attribute value.
     * 
     * @param key the attribute name to look for
     * @param value the attribute value should start with
     */
    public AttributeWithValueStarting(String key, String value) {
      super(key, value);
    }

    @Override
    public boolean matches(Node<T> root, Node<T> node) {
      // value is lower case already
      return node.hasAttr(key) && node.attr(key).toLowerCase().startsWith(value);
    }

    @Override
    public String toString() {
      return String.format("[%s^=%s]", key, value);
    }

  }

  /**
   * Evaluator for attribute name/value matching (value ending)
   */
  public static final class AttributeWithValueEnding<T> extends AttributeKeyPair<T> {
    
    /**
     * Construct a ends with evaluator for the given attribute value.
     * 
     * @param key the attribute name to look for
     * @param value the attribute value should end with
     */
    public AttributeWithValueEnding(String key, String value) {
      super(key, value);
    }

    @Override
    public boolean matches(Node<T> root, Node<T> node) {
      // value is lower case
      return node.hasAttr(key) && node.attr(key).toLowerCase().endsWith(value);
    }

    @Override
    public String toString() {
      return String.format("[%s$=%s]", key, value);
    }

  }

  /**
   * Evaluator for attribute name/value matching (value containing)
   */
  public static final class AttributeWithValueContaining<T> extends AttributeKeyPair<T> {
    
    /**
     * Construct a contains with evaluator for the given attribute value.
     * 
     * @param key the attribute name to look for
     * @param value the attribute value should contain
     */
    public AttributeWithValueContaining(String key, String value) {
      super(key, value);
    }

    @Override
    public boolean matches(Node<T> root, Node<T> node) {
      // value is lower case
      return node.hasAttr(key) && node.attr(key).toLowerCase().contains(value);
    }

    @Override
    public String toString() {
      return String.format("[%s*=%s]", key, value);
    }

  }

  /**
   * Evaluator for attribute name/value matching (value regex matching)
   */
  public static final class AttributeWithValueMatching<T> extends Evaluator<T> {
    final String key;
    final Pattern pattern;

    /**
     * Construct an matches evaluator for the given attribute value.
     * 
     * @param key the attribute name to look for
     * @param value the attribute value should match
     */
    public AttributeWithValueMatching(String key, Pattern pattern) {
      this.key = key.trim().toLowerCase();
      this.pattern = pattern;
    }

    @Override
    public boolean matches(Node<T> root, Node<T> node) {
      return node.hasAttr(key) && pattern.matcher(node.attr(key)).find();
    }

    @Override
    public String toString() {
      return String.format("[%s~=%s]", key, pattern.toString());
    }

  }

  /**
   * Abstract evaluator for attribute name/value matching
   */
  public abstract static class AttributeKeyPair<T> extends Evaluator<T> {
    final String key;
    final String value;

    /**
     * Construct the abstract attribute evaluator.
     * 
     * @param key the attribute name to look for
     * @param value the attribute value should use in the evaluation
     */
    public AttributeKeyPair(String key, String value) {
      Validate.notEmpty(key);
      Validate.notEmpty(value);

      this.key = key.trim().toLowerCase();
      if (value.startsWith("\"") && value.endsWith("\"")
          || value.startsWith("'") && value.endsWith("'")) {
        value = value.substring(1, value.length() - 1);
      }
      this.value = value.trim().toLowerCase();
    }
  }

  /**
   * Evaluator for any / all node matching
   */
  public static final class AllNodes<T> extends Evaluator<T> {

    @Override
    public boolean matches(Node<T> root, Node<T> node) {
      return true;
    }

    @Override
    public String toString() {
      return "*";
    }
  }

  /**
   * Evaluator for matching by sibling index number (e {@literal <} idx)
   */
  public static final class IndexLessThan<T> extends IndexEvaluator<T> {
    
    /**
     * Constructor for the less than index evaluator
     * 
     * @param index the index to be less than
     */
    public IndexLessThan(int index) {
      super(index);
    }

    @Override
    public boolean matches(Node<T> root, Node<T> node) {
      return node.getSiblingIndex() < index;
    }

    @Override
    public String toString() {
      return String.format(":lt(%d)", index);
    }

  }

  /**
   * Evaluator for matching by sibling index number (e {@literal >} idx)
   */
  public static final class IndexGreaterThan<T> extends IndexEvaluator<T> {
    
    /**
     * Constructor for the greater than index evaluator
     * 
     * @param index the index to be greater than
     */
    public IndexGreaterThan(int index) {
      super(index);
    }

    @Override
    public boolean matches(Node<T> root, Node<T> node) {
      return node.getSiblingIndex() > index;
    }

    @Override
    public String toString() {
      return String.format(":gt(%d)", index);
    }

  }

  /**
   * Evaluator for matching by sibling index number (e = idx)
   */
  public static final class IndexEquals<T> extends IndexEvaluator<T> {
    
    /**
     * Constructor for the equals index evaluator
     * 
     * @param index the index to be equal to
     */
    public IndexEquals(int index) {
      super(index);
    }

    @Override
    public boolean matches(Node<T> root, Node<T> node) {
      return node.getSiblingIndex() == index;
    }

    @Override
    public String toString() {
      return String.format(":eq(%d)", index);
    }

  }

  /**
   * Evaluator for matching the last sibling (css :last-child)
   */
  public static final class IsLastChild<T> extends Evaluator<T> {
    @Override
    public boolean matches(Node<T> root, Node<T> node) {
      final Node<T> p = node.getParent();
      return p != null && node.getSiblingIndex() == p.getChildren().size() - 1;
    }

    @Override
    public String toString() {
      return ":last-child";
    }
  }

  /**
   * Evaluator for matching the first sibling of type (:first-of-type)
   *
   */
  public static final class IsFirstOfType<T> extends IsNthOfType<T> {
    
    /**
     * Constructor fo first of type evaluator.
     */
    public IsFirstOfType() {
      super(0, 1);
    }

    @Override
    public String toString() {
      return ":first-of-type";
    }
  }

  /**
   * Evaluator for matching the last sibling of type (:last-of-type)
   *
   */
  public static final class IsLastOfType<T> extends IsNthLastOfType<T> {
    
    /**
     * Constructor fo first of type evaluator.
     */
    public IsLastOfType() {
      super(0, 1);
    }

    @Override
    public String toString() {
      return ":last-of-type";
    }
  }


  /**
   * Base evaluator for 'nth of' matches.
   * Using (an + b)
   *
   */
  public abstract static class NthEvaluator<T> extends Evaluator<T> {
    
    final int a;
    final int b;

    /**
     * Constructor for the base nth evaluator defining both a and b.
     * 
     * @param a the a in (an+b)
     * @param b the b in (an+b)
     */
    public NthEvaluator(int a, int b) {
      this.a = a;
      this.b = b;
    }

    /**
     * Constructor for the base nth evaluator defining b and a=0.
     * 
     * @param b the b in (an+b)
     */
    public NthEvaluator(int b) {
      this(0, b);
    }

    @Override
    public boolean matches(Node<T> root, Node<T> node) {
      final Node<T> p = node.getParent();
      if (p == null) {
        return false;
      }

      final int pos = calculatePosition(root, node);
      if (a == 0) {
        return pos == b;
      }

      return (pos - b) * a >= 0 && (pos - b) % a == 0;
    }

    @Override
    public String toString() {
      if (a == 0) {
        return String.format(":%s(%d)", getPseudoClass(), b);
      }
      if (b == 0) {
        return String.format(":%s(%dn)", getPseudoClass(), a);
      }
      return String.format(":%s(%dn%+d)", getPseudoClass(), a, b);
    }

    protected abstract String getPseudoClass();

    protected abstract int calculatePosition(Node<T> root, Node<T> node);
  }


  /**
   * Evaluator for (:nth-child)
   *
   * @see IndexEquals
   */
  public static final class IsNthChild<T> extends NthEvaluator<T> {

    /**
     * Constructor for is nth child evaluator defining both a and b.
     * 
     * @param a the a in (an+b)
     * @param b the b in (an+b)
     */
    public IsNthChild(int a, int b) {
      super(a, b);
    }

    @Override
    protected int calculatePosition(Node<T> root, Node<T> node) {
      return node.getSiblingIndex() + 1;
    }


    @Override
    protected String getPseudoClass() {
      return "nth-child";
    }
  }

  /**
   * Evaluator for pseudo class :nth-last-child
   *
   * @see IndexEquals
   */
  public static final class IsNthLastChild<T> extends NthEvaluator<T> {
    
    /**
     * Constructor for is nth last child evaluator defining both a and b.
     * 
     * @param a the a in (an+b)
     * @param b the b in (an+b)
     */
    public IsNthLastChild(int a, int b) {
      super(a, b);
    }

    @Override
    protected int calculatePosition(Node<T> root, Node<T> node) {
      return node.getParent().getChildren().size() - node.getSiblingIndex();
    }

    @Override
    protected String getPseudoClass() {
      return "nth-last-child";
    }
  }

  /**
   * Evaluator for pseudo class nth-of-type
   *
   */
  public static class IsNthOfType<T> extends NthEvaluator<T> {
    
    /**
     * Constructor for is nth of type evaluator defining both a and b.
     * 
     * @param a the a in (an+b)
     * @param b the b in (an+b)
     */
    public IsNthOfType(int a, int b) {
      super(a, b);
    }

    @Override
    protected int calculatePosition(Node<T> root, Node<T> node) {
      int pos = 0;

      Nodes<T> family = new Nodes<>(node.getParent().getChildren());
      for (Node<T> el : family) {
        if (el.getTypeName().equals(node.getTypeName())) {
          pos++;
        }
        if (el == node) {
          break;
        }
      }
      return pos;
    }

    @Override
    protected String getPseudoClass() {
      return "nth-of-type";
    }
  }

  /**
   * Evaluator for matching nth last of type
   */
  public static class IsNthLastOfType<T> extends NthEvaluator<T> {

    /**
     * Constructor for is nth list child of type evaluator defining both a and b.
     * 
     * @param a the a in (an+b)
     * @param b the b in (an+b)
     */
    public IsNthLastOfType(int a, int b) {
      super(a, b);
    }

    @Override
    protected int calculatePosition(Node<T> root, Node<T> node) {
      Nodes<?> family = new Nodes<>(node.getParent().getChildren(node.getType()));
      return family.size() - node.getTypeIndex();
    }

    @Override
    protected String getPseudoClass() {
      return "nth-last-of-type";
    }
  }

  /**
   * Evaluator for matching the first sibling (css :first-child)
   */
  public static final class IsFirstChild<T> extends Evaluator<T> {
    @Override
    public boolean matches(Node<T> root, Node<T> node) {
      final Node<T> p = node.getParent();
      return p != null && node.getSiblingIndex() == 0;
    }

    @Override
    public String toString() {
      return ":first-child";
    }
  }

  /**
   * Evaluator to match if node is the root
   *
   * @see <a href="http://www.w3.org/TR/selectors/#root-pseudo">:root selector</a>
   *
   */
  public static final class IsRoot<T> extends Evaluator<T> {
    @Override
    public boolean matches(Node<T> root, Node<T> node) {
      return node == root;
    }

    @Override
    public String toString() {
      return ":root";
    }
  }

  /**
   * Evaluator to match if only child
   *
   */
  public static final class IsOnlyChild<T> extends Evaluator<T> {
    @Override
    public boolean matches(Node<T> root, Node<T> node) {
      final Node<T> p = node.getParent();
      return p != null && p.getChildren().size() == 1;
    }

    @Override
    public String toString() {
      return ":only-child";
    }
  }

  /**
   * Evaluator to match if only of type
   *
   */
  public static final class IsOnlyOfType<T> extends Evaluator<T> {
    @Override
    public boolean matches(Node<T> root, Node<T> node) {
      final Node<T> p = node.getParent();
      if (p == null) {
        return false;
      }
      return p.getChildren(node.getType()).size() == 1;
    }

    @Override
    public String toString() {
      return ":only-of-type";
    }
  }

  /**
   * Evaluator to match if the node is empty (of text and other nodes)
   *
   */
  public static final class IsEmpty<T> extends Evaluator<T> {
    @Override
    public boolean matches(Node<T> root, Node<T> node) {
      Nodes<T> family = node.getChildren();
      return family.isEmpty() && node.text().isEmpty();
    }

    @Override
    public String toString() {
      return ":empty";
    }
  }

  /**
   * Abstract evaluator for sibling index matching
   */
  public abstract static class IndexEvaluator<T> extends Evaluator<T> {
    final int index;

    /**
     * Construcotr for base index evaluator.
     * 
     * @param index the index
     */
    public IndexEvaluator(int index) {
      this.index = index;
    }
  }

  /**
   * Evaluator for matching Node<T> (and its descendants) text
   */
  public static final class ContainsText<T> extends Evaluator<T> {
    private String searchText;

    /**
     * Constructor for the contains text evaluator.
     * 
     * @param searchText the text to search for
     */
    public ContainsText(String searchText) {
      this.searchText = searchText.toLowerCase();
    }

    @Override
    public boolean matches(Node<T> root, Node<T> node) {
      return node.text().toLowerCase().contains(searchText);
    }

    @Override
    public String toString() {
      return String.format(":contains(%s)", searchText);
    }
  }

  /**
   * Evaluator for matching Node's own text (this excludes the text contained in any children).
   */
  public static final class ContainsOwnText<T> extends Evaluator<T> {
    private final String searchText;

    /**
     * Constructor for the contains own text evaluator.
     * 
     * @param searchText the text to search for
     */
    public ContainsOwnText(String searchText) {
      this.searchText = searchText.toLowerCase();
    }

    @Override
    public boolean matches(Node<T> root, Node<T> node) {
      return node.ownText().toLowerCase().contains(searchText);
    }

    @Override
    public String toString() {
      return String.format(":containsOwn(%s)", searchText);
    }
  }

  /**
   * Evaluator for matching Node<T> (and its descendants) text with regex
   */
  public static final class Matches<T> extends Evaluator<T> {
    private final Pattern pattern;

    /**
     * Constructor for the matches text evaluator.
     * 
     * @param pattern the regular expression to match
     */
    public Matches(Pattern pattern) {
      this.pattern = pattern;
    }

    @Override
    public boolean matches(Node<T> root, Node<T> node) {
      Matcher m = pattern.matcher(node.text());
      return m.find();
    }

    @Override
    public String toString() {
      return String.format(":matches(%s)", pattern);
    }
  }

  /**
   * Evaluator for matching Node's own text with regex (excluding text in children).
   */
  public static final class MatchesOwn<T> extends Evaluator<T> {
    private final Pattern pattern;

    /**
     * Constructor for the matches own text evaluator.
     * 
     * @param pattern the regular expression to match
     */
    public MatchesOwn(Pattern pattern) {
      this.pattern = pattern;
    }

    @Override
    public boolean matches(Node<T> root, Node<T> node) {
      Matcher m = pattern.matcher(node.ownText());
      return m.find();
    }

    @Override
    public String toString() {
      return String.format(":matchesOwn(%s)", pattern);
    }
  }
}
