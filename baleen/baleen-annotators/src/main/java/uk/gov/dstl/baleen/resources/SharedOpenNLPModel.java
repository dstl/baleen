//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.resources;

import java.io.InputStream;

import opennlp.tools.util.model.BaseModel;
import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.uima.BaleenResource;

/**
 * Shared resource object for loading and accessing OpenNLP models.
 * loadModel(...) must be called once to initialize the resource, but any subsequent calls to the method will be ignored.
 * 
 * 
 */
public class SharedOpenNLPModel extends BaleenResource {
	private BaseModel model;
	
	/**
	 * Load model into the resource, if it hasn't already been loaded. If it has already been loaded, then the method returns.
	 * We don't initialize the model in doInitialize because there is no nice way of specifying the model parameters
	 * 
	 * @param modelClazz The model class to use when loading the file, e.g. TokenizerModel
	 * @param model The model to load
	 * @throws BaleenException
	 */
	public void loadModel(Class<? extends BaseModel> modelClazz, InputStream model) throws BaleenException{
		if(this.model != null){
			return;
		}
		
		try {
			this.model = modelClazz.getDeclaredConstructor(InputStream.class).newInstance(model);
		} catch (Exception e) {
			throw new BaleenException("Unable to construct model", e);
		}
	}
	
	@Override
	protected void doDestroy() {
		model = null;
	}
	
	/**
	 * Get the OpenNLP model held by this resource
	 */
	public BaseModel getModel(){
		return model;
	}
}
