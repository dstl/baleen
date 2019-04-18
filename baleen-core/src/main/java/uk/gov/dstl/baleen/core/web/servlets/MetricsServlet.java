// Dstl (c) Crown Copyright 2017
// Modified by NCA (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.web.servlets;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.json.MetricsModule;

import uk.gov.dstl.baleen.core.web.security.WebPermission;

/**
 * Outputs all metrics in the registry as JSON.
 *
 * <p>Accepts an optional parameter <i>filter</i>, which will filter the output based on the metric
 * name. Single *'s should be used to replace one 'level', whereas double *'s can be used to replace
 * multiple levels (but not multiple 'sections'). For instance:
 *
 * <ul>
 *   <li><b>baleen:*:example</b> would match the metric baleen:foo:example, but not
 *       baleen:foo:bar:example
 *   <li><b>baleen:**:example</b> would match the metric baleen:foo:example and
 *       baleen:foo:bar:example
 *   <li><b>baleen:**:**</b> would match all metrics from the baleen pipeline, whereas
 *       <b>**:foo.bar:**</b> would match all metrics for foo.bar across any pipeline
 * </ul>
 *
 * In practice, this equates to a single * being replaced with the regular expression
 *
 * <pre>
 * [a-z0-9\\-]{0,}
 * </pre>
 *
 * and a double ** being replaced with the regular expression
 *
 * <pre>
 * [a-z0-9\\-\\.]{0,}
 * </pre>
 *
 * The comparison is done case insensitively.
 *
 * <p>If using authentication, the user will need the "metrics" role to access this resource.
 */
public class MetricsServlet extends AbstractApiServlet {
  public static final String PARAM_FILTER = "filter";

  private static final Logger LOGGER = LoggerFactory.getLogger(MetricsServlet.class);

  private static final long serialVersionUID = 1L;

  private static final String ROLES = "metrics";

  private final transient MetricRegistry registry;

  /**
   * New instance, which will report on the supplied metrics.
   *
   * @param registry the metrics registry to provide metrics from
   */
  public MetricsServlet(MetricRegistry registry) {
    super(LOGGER, MetricsServlet.class);
    this.registry = registry;

    getMapper().registerModule(new MetricsModule(TimeUnit.SECONDS, TimeUnit.SECONDS, false));
  }

  @Override
  public WebPermission[] getPermissions() {
    return new WebPermission[] {new WebPermission("Access Metrics", ROLES)};
  }

  @Override
  protected void get(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    String[] filters = req.getParameterValues(PARAM_FILTER);

    Map<String, Metric> metrics;
    if (filters == null || filters.length == 0) {
      metrics = registry.getMetrics();
    } else {
      metrics =
          registry.getMetrics().entrySet().stream()
              .filter(
                  e -> {
                    for (String s : filters) {
                      if (filterMetric(e.getKey(), s)) {
                        return true;
                      }
                    }
                    return false;
                  })
              .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }

    respondWithJson(resp, metrics);
  }

  /**
   * Test the metricName against the pattern and return true if it matches. We replace ** and * with
   * the appropriate regular expressions to do the package matching.
   *
   * @param metricName The name of the metric to test
   * @param pattern The pattern to test against
   * @return True or false
   */
  public static boolean filterMetric(String metricName, String pattern) {
    String regexPattern = pattern.replaceAll("\\*\\*", "[a-z0-9\\.\\-]{0,}");
    regexPattern = regexPattern.replaceAll("\\*", "[a-z0-9\\-]{0,}");

    Pattern p = Pattern.compile(regexPattern, Pattern.CASE_INSENSITIVE);
    Matcher m = p.matcher(metricName);

    return m.matches();
  }
}
