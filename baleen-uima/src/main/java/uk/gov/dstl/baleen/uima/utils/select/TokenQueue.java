// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima.utils.select;

import org.apache.commons.lang3.Validate;

/** A character queue with parsing helpers. */
public class TokenQueue {

  /** Escape character for balanced chomp */
  private static final char ESC = '\\';

  /** the string queue */
  private String queue;

  /** the position */
  private int pos = 0;

  /**
   * Create a new TokenQueue.
   *
   * @param data string of data to back queue.
   */
  public TokenQueue(String data) {
    Validate.notNull(data);
    queue = data;
  }

  /**
   * Is the queue empty?
   *
   * @return true if no data left in queue.
   */
  public boolean isEmpty() {
    return remainingLength() == 0;
  }

  /**
   * What is the remaining length of the queue
   *
   * @return the remaining length
   */
  private int remainingLength() {
    return queue.length() - pos;
  }

  /**
   * Tests if the next characters on the queue match the sequence. Case insensitive.
   *
   * @param seq String to check queue for.
   * @return true if the next characters match.
   */
  public boolean matches(String seq) {
    return queue.regionMatches(true, pos, seq, 0, seq.length());
  }

  /**
   * Tests if the next characters match any of the sequences. Case insensitive.
   *
   * @param seq list of strings to case insensitively check for
   * @return true of any matched, false if none did
   */
  public boolean matchesAny(String... seq) {
    for (String s : seq) {
      if (matches(s)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Tests if the next characters match any of the sequences. Case sensitive.
   *
   * @param seq list of strings to case sensitively check for
   * @return true of any matched, false if none did
   */
  public boolean matchesAny(char... seq) {
    if (isEmpty()) {
      return false;
    }

    for (char c : seq) {
      if (queue.charAt(pos) == c) {
        return true;
      }
    }
    return false;
  }

  /**
   * Tests if the queue matches the sequence (as with match), and if they do, removes the matched
   * string from the queue.
   *
   * @param seq String to search for, and if found, remove from queue.
   * @return true if found and removed, false if not found.
   */
  public boolean matchChomp(String seq) {
    if (matches(seq)) {
      pos += seq.length();
      return true;
    } else {
      return false;
    }
  }

  /**
   * Tests if queue starts with a whitespace character.
   *
   * @return if starts with whitespace
   */
  public boolean matchesWhitespace() {
    return !isEmpty() && Character.isWhitespace(queue.charAt(pos));
  }

  /**
   * Test if the queue matches a word character (letter or digit).
   *
   * @return if matches a word character
   */
  public boolean matchesWord() {
    return !isEmpty() && Character.isLetterOrDigit(queue.charAt(pos));
  }

  /**
   * Consume one character off queue.
   *
   * @return first character on queue.
   */
  public char consume() {
    return queue.charAt(pos++);
  }

  /**
   * Consumes the supplied sequence of the queue. If the queue does not start with the supplied
   * sequence, will throw an illegal state exception -- but you should be running match() against
   * that condition.
   *
   * <p>Case insensitive.
   *
   * @param seq sequence to remove from head of queue.
   */
  public void consume(String seq) {
    if (!matches(seq)) {
      throw new IllegalStateException("Queue did not match expected sequence");
    }
    int len = seq.length();
    if (len > remainingLength()) {
      throw new IllegalStateException("Queue not long enough to consume sequence");
    }

    pos += len;
  }

  /**
   * Pulls a string off the queue, up to but exclusive of the match sequence, or to the queue
   * running out.
   *
   * @param seq String to end on (and not include in return, but leave on queue). <b>Case
   *     sensitive.</b>
   * @return The matched data consumed from queue.
   */
  public String consumeTo(String seq) {
    int offset = queue.indexOf(seq, pos);
    if (offset != -1) {
      String consumed = queue.substring(pos, offset);
      pos += consumed.length();
      return consumed;
    } else {
      return remainder();
    }
  }

  /**
   * Consumes to the first sequence provided, or to the end of the queue. Leaves the terminator on
   * the queue.
   *
   * @param seq any number of terminators to consume to. <b>Case insensitive.</b>
   * @return consumed string
   */
  public String consumeToAny(String... seq) {
    int start = pos;
    while (!isEmpty() && !matchesAny(seq)) {
      pos++;
    }

    return queue.substring(start, pos);
  }

  /**
   * Pulls a string off the queue (like consumeTo), and then pulls off the matched string (but does
   * not return it).
   *
   * <p>If the queue runs out of characters before finding the seq, will return as much as it can
   * (and queue will go isEmpty() == true).
   *
   * @param seq String to match up to, and not include in return, and to pull off queue. <b>Case
   *     sensitive.</b>
   * @return Data matched from queue.
   */
  public String chompTo(String seq) {
    String data = consumeTo(seq);
    matchChomp(seq);
    return data;
  }

  /**
   * Pulls a balanced string off the queue. E.g. if queue is "(one (two) three) four", (,) will
   * return "one (two) three", and leave " four" on the queue. Unbalanced openers and closers can be
   * quoted (with ' or ") or escaped (with \). Those escapes will be left in the returned string,
   * which is suitable for regexes (where we need to preserve the escape), but unsuitable for
   * contains text strings; use unescape for that.
   *
   * @param open opener
   * @param close closer
   * @return data matched from the queue
   */
  public String chompBalanced(char open, char close) {
    int start = -1;
    int end = -1;
    int depth = 0;
    char last = 0;
    boolean inQuote = false;

    do {
      if (isEmpty()) {
        break;
      }
      Character c = consume();
      if (last == 0 || last != ESC) {
        if ((c.equals('\'') || c.equals('"')) && c != open) {
          inQuote = !inQuote;
        }
        if (inQuote) {
          continue;
        }
        if (c.equals(open)) {
          depth++;
          if (start == -1) {
            start = pos;
          }
        } else if (c.equals(close)) {
          depth--;
        }
      }

      if (depth > 0 && last != 0) {
        end = pos; // don't include the outer match pair in the return
      }
      last = c;
    } while (depth > 0);
    final String out = end >= 0 ? queue.substring(start, end) : "";
    Validate.isTrue(depth == 0, "Did not find balanced maker at " + out);
    return out;
  }

  /**
   * Unescaped a \ escaped string.
   *
   * @param in backslash escaped string
   * @return unescaped string
   */
  public static String unescape(String in) {
    StringBuilder out = new StringBuilder();
    char last = 0;
    for (char c : in.toCharArray()) {
      if (c == ESC) {
        if (last != 0 && last == ESC) {
          out.append(c);
        }
      } else {
        out.append(c);
      }
      last = c;
    }
    return out.toString();
  }

  /**
   * Pulls the next run of whitespace characters of the queue.
   *
   * @return Whether consuming whitespace or not
   */
  public boolean consumeWhitespace() {
    boolean seen = false;
    while (matchesWhitespace()) {
      pos++;
      seen = true;
    }
    return seen;
  }

  /**
   * Consume a node selector (type name name)
   *
   * @return type name
   */
  public String consumeNodeSelector() {
    int start = pos;
    while (!isEmpty() && (matchesWord() || matchesAny("_", "-"))) {
      pos++;
    }

    return queue.substring(start, pos);
  }

  /**
   * Consume an identifier (ID or class) off the queue (letter, digit, -, _)
   * http://www.w3.org/TR/CSS2/syndata.html#value-def-identifier
   *
   * @return identifier
   */
  public String consumeIdentifier() {
    int start = pos;
    while (!isEmpty() && (matchesWord() || matchesAny('-', '_'))) {
      pos++;
    }

    return queue.substring(start, pos);
  }

  /**
   * Consume and return whatever is left on the queue.
   *
   * @return remained of queue.
   */
  public String remainder() {
    final String remainder = queue.substring(pos, queue.length());
    pos = queue.length();
    return remainder;
  }

  @Override
  public String toString() {
    return queue.substring(pos);
  }
}
