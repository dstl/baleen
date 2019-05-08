/* Apache UIMA v3 - First created by JCasGen Wed Apr 17 13:42:22 BST 2019 */

package uk.gov.dstl.baleen.types;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.cas.Feature;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.impl.TypeSystemImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.tcas.Annotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

import uk.gov.dstl.baleen.core.utils.IdentityUtils;
import uk.gov.dstl.baleen.exceptions.BaleenException;

/**
 * A base class for annotations used by Baleen. Includes things like an internal ID and a function
 * to generate an external ID. Updated by JCasGen Wed Apr 17 13:42:22 BST 2019 XML source:
 * types/template_type_system.xml
 *
 * @generated
 */
public class BaleenAnnotation extends Annotation {

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final String _TypeName = "uk.gov.dstl.baleen.types.BaleenAnnotation";

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = JCasRegistry.register(BaleenAnnotation.class);
  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int type = typeIndexID;
  /**
   * @generated
   * @return index of the type
   */
  @Override
  public int getTypeIndexID() {
    return typeIndexID;
  }

  /* *******************
   *   Feature Offsets *
   * *******************/

  public static final String _FeatName_internalId = "internalId";

  /* Feature Adjusted Offsets */
  private static final CallSite _FC_internalId =
      TypeSystemImpl.createCallSite(BaleenAnnotation.class, "internalId");
  private static final MethodHandle _FH_internalId = _FC_internalId.dynamicInvoker();

  /**
   * Never called. Disable default constructor
   *
   * @generated
   */
  protected BaleenAnnotation() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @generated
   * @param casImpl the CAS this Feature Structure belongs to
   * @param type the type of this Feature Structure
   */
  public BaleenAnnotation(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   */
  public BaleenAnnotation(JCas jcas) {
    super(jcas);
    readObject();
  }

  /**
   * @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA
   */
  public BaleenAnnotation(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }

  /**
   *
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable
   */
  private void readObject() {
    if (getInternalId() == 0L) {
      IdentityUtils iu = IdentityUtils.getInstance();
      setInternalId(iu.getNewId());
    }
  }

  // *--------------*
  // * Feature: internalId

  /**
   * getter for internalId - gets An ID that is used internally to refer to the entity
   *
   * @generated
   * @return value of the feature
   */
  public long getInternalId() {
    return _getLongValueNc(wrapGetIntCatchException(_FH_internalId));
  }

  /**
   * setter for internalId - sets An ID that is used internally to refer to the entity
   *
   * @generated
   * @param v value to set into the feature
   */
  public void setInternalId(long v) {
    _setLongValueNfc(wrapGetIntCatchException(_FH_internalId), v);
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(BaleenAnnotation.class);

  /**
   * Produces an ID that is based on the properties of the current annotation. This ID is repeatable
   * (i.e. if you call the method twice without changing the object you will get the same ID), and
   * should be used whenever an entity is being persisted.
   *
   * <p>This ID should be unique within the document, but is not guaranteed to be unique across
   * documents.
   */
  public String getExternalId() {
    List<String> properties = new ArrayList<>();

    properties.add(this.getType().getName());
    properties.add(this.getCoveredText());

    for (Feature f : this.getType().getFeatures()) {
      if (!f.getRange().isPrimitive()
          || "uk.gov.dstl.baleen.types.BaleenAnnotation:internalId".equals(f.getName())) {
        continue;
      }

      try {
        String s = this.getFeatureValueAsString(f);
        if (!Strings.isNullOrEmpty(s)) {
          properties.add(s);
        }
      } catch (Exception e) {
        LOGGER.debug(
            "Couldn't read feature value for feature {} - property will be ignored in hash generation",
            f.getName(),
            e);
      }
    }

    try {
      return IdentityUtils.hashStrings(properties.toArray(new String[0]));
    } catch (BaleenException e) {
      LOGGER.error(
          "Unable to generate external ID for entity with internal ID {}", getInternalId(), e);
    }

    return null;
  }
}
