

/* First created by JCasGen Thu Feb 05 10:12:58 GMT 2015 */
//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.types;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;
import org.apache.uima.jcas.tcas.Annotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dstl.baleen.core.utils.IdentityUtils;
import uk.gov.dstl.baleen.exceptions.BaleenException;

import com.google.common.base.Strings;


/** A base class for annotations used by Baleen. Includes things like an internal ID and a function to generate an external ID.
 * Updated by JCasGen Fri Feb 05 14:54:30 GMT 2016
 * XML source: C:/co/git/CCD-DE/RMR/baleen/baleen/baleen-uima/src/main/resources/types/semantic_type_system.xml
 * @generated */
public class BaleenAnnotation extends Annotation {
	/** @generated
	 * @ordered 
	 */
	@SuppressWarnings ("hiding")
	public final static int typeIndexID = JCasRegistry.register(BaleenAnnotation.class);
	/** @generated
	 * @ordered 
	 */
	@SuppressWarnings ("hiding")
	public final static int type = typeIndexID;
	/** @generated
	 * @return index of the type  
	 */
	@Override
	public              int getTypeIndexID() {return typeIndexID;}
 
	/** Never called.  Disable default constructor
	 * @generated */
	protected BaleenAnnotation() {/* intentionally empty block */}
    
	/** Internal - constructor used by generator 
	 * @generated
	 * @param addr low level Feature Structure reference
	 * @param type the type of this Feature Structure 
	 */
	public BaleenAnnotation(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
	/** @generated
	 * @param jcas JCas to which this Feature Structure belongs 
	 */
	public BaleenAnnotation(JCas jcas) {
    super(jcas);
    readObject();   
  } 

	/** @generated
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
   * <!-- begin-user-doc -->
	 * Write your own initialization here
	 * <!-- end-user-doc -->
	 *
   * @generated modifiable 
   */
	private void readObject() {
		if(getInternalId() == 0L){
			IdentityUtils iu = IdentityUtils.getInstance();
			setInternalId(iu.getNewId());
		}
	}

  //*--------------*
  //* Feature: internalId

  /** getter for internalId - gets An ID that is used internally to refer to the entity
   * @generated
   * @return value of the feature 
   */
	public long getInternalId() {
    if (BaleenAnnotation_Type.featOkTst && ((BaleenAnnotation_Type)jcasType).casFeat_internalId == null)
      jcasType.jcas.throwFeatMissing("internalId", "uk.gov.dstl.baleen.types.BaleenAnnotation");
    return jcasType.ll_cas.ll_getLongValue(addr, ((BaleenAnnotation_Type)jcasType).casFeatCode_internalId);}
    
  /** setter for internalId - sets An ID that is used internally to refer to the entity 
   * @generated
   * @param v value to set into the feature 
   */
	public void setInternalId(long v) {
    if (BaleenAnnotation_Type.featOkTst && ((BaleenAnnotation_Type)jcasType).casFeat_internalId == null)
      jcasType.jcas.throwFeatMissing("internalId", "uk.gov.dstl.baleen.types.BaleenAnnotation");
    jcasType.ll_cas.ll_setLongValue(addr, ((BaleenAnnotation_Type)jcasType).casFeatCode_internalId, v);}    
                	private static final Logger LOGGER = LoggerFactory.getLogger(BaleenAnnotation.class);

	/**
	 * Produces an ID that is based on the properties of the current annotation.
	 * This ID is repeatable (i.e. if you call the method twice without changing the object you will get the same ID),
	 * and should be used whenever an entity is being persisted.
	 * 
	 * This ID should be unique within the document, but is not guaranteed to be unique across documents.
	 */
	public String getExternalId(){
		List<String> properties = new ArrayList<>();

		properties.add(this.getType().getName());
		properties.add(this.getCoveredText());

		for(Feature f : this.getType().getFeatures()){
			if(!f.getRange().isPrimitive() || "uk.gov.dstl.baleen.types.BaleenAnnotation:internalId".equals(f.getName())){
				continue;
			}

			try{
				String s = this.getFeatureValueAsString(f);
				if(!Strings.isNullOrEmpty(s)){
					properties.add(s);
				}
			}catch(Exception e){
				LOGGER.debug("Couldn't read feature value for feature {} - property will be ignored in hash generation",f.getName(),e);
			}
		}

		try {
			return IdentityUtils.hashStrings(properties.toArray(new String[0]));
		} catch (BaleenException e) {
			LOGGER.error("Unable to generate external ID for entity with internal ID {}",getInternalId(), e);
		}

		return null;
	}
}

