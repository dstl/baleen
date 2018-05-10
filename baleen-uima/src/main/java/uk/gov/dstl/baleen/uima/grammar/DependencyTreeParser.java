// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.uima.grammar;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * A class for the creation of {@link DependencyTree} form a text format.
 *
 * <p>The text format uses whitespace indentation to align governors with dependent nodes. The first
 * line declared the root node definition. Each following line first declares the type of the
 * dependency followed by a node definition. The node definition is an optional regular expression
 * content specification, a part of speech tag for the node and an optional id separatedlike the
 * following examples:
 *
 * <pre>
 * _NN
 * visit.*_VB
 * example_NN:myid
 * </pre>
 *
 * A full tree example:
 *
 * <pre>
 *
 * _NN:target
 *   nsubj _NN:attribute
 *     det [Tt]he_DT
 *     prep of_IN
 *     pobj _NN:source
 *   cop is_VB
 *
 * </pre>
 */
public class DependencyTreeParser {

  /**
   * Create a {@link DependencyTree} from the given string
   *
   * @param text string to parse
   * @return the constructed {@link DependencyTree}
   * @throws DependencyParseException
   */
  public static DependencyTree readFromString(String text) throws DependencyParseException {
    try (ByteArrayInputStream is = new ByteArrayInputStream(text.getBytes())) {
      return readFromStream(is);
    } catch (IOException e) {
      throw new RuntimeException("NO io to error, should not be thrown", e);
    }
  }

  /**
   * Create a {@link DependencyTree} from the given file
   *
   * @param file to read and parse
   * @return the constructed {@link DependencyTree}
   * @throws DependencyParseException
   */
  public static DependencyTree readFromFile(String file)
      throws IOException, DependencyParseException {
    try (FileInputStream is = new FileInputStream(file)) {
      return readFromStream(is);
    }
  }

  /**
   * Create a {@link DependencyTree} from the given input stream
   *
   * @param is input stream to parse
   * @return the constructed {@link DependencyTree}
   * @throws DependencyParseException
   */
  public static DependencyTree readFromStream(InputStream is)
      throws IOException, DependencyParseException {
    Deque<DependencyTree> stack = new ArrayDeque<>();
    Deque<Integer> depth = new ArrayDeque<>();
    int lineNumber = 0;
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
      for (String line; (line = reader.readLine()) != null; ) {
        int nextDepth = getDepth(line);
        if (lineNumber == 0) {
          stack.push(createTree(lineNumber, line.trim()));
          depth.push(nextDepth);
          lineNumber++;
          continue;
        }
        int currentDepth = depth.peek();
        DependencyEdge edge = createEdge(lineNumber, line.trim());
        if (nextDepth == currentDepth) {
          stack.pop();
          DependencyTree child = stack.peek().addDependency(edge);
          stack.push(child);
        } else if (nextDepth > currentDepth) {
          DependencyTree child = stack.peek().addDependency(edge);
          stack.push(child);
          depth.push(nextDepth);
        } else {
          do {
            stack.pop();
            depth.pop();
            if (depth.isEmpty()) {
              throw new DependencyParseException(lineNumber, "Depth error");
            }
          } while (nextDepth != depth.peek());
          stack.pop();
          DependencyTree child = stack.peek().addDependency(edge);
          stack.push(child);
        }
        lineNumber++;
      }
    }
    return stack.getLast();
  }

  private static DependencyEdge createEdge(int line, String edge) throws DependencyParseException {
    int splitPoint = edge.indexOf(' ');
    if (splitPoint < 0) {
      throw new DependencyParseException(line, "Cannot parse edge: " + edge);
    }
    String type = edge.substring(0, splitPoint).trim();
    String treeDef = edge.substring(splitPoint + 1).trim();
    return new DependencyEdge(type, createTree(line, treeDef));
  }

  private static DependencyTree createTree(int line, String treeDef)
      throws DependencyParseException {
    return new DependencyTree(createNode(line, treeDef));
  }

  private static DependencyNode createNode(int line, String nodeDef)
      throws DependencyParseException {
    try {
      return DependencyNode.create(nodeDef);
    } catch (Exception e) {
      throw new DependencyParseException(line, "Cannot parse node: " + nodeDef);
    }
  }

  private static int getDepth(String line) {
    int depth = 0;
    while (depth < line.length() && Character.isWhitespace(line.charAt(depth))) {
      depth++;
    }
    return depth;
  }
}
