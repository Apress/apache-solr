package com.apress.solr.pa.chapter11.opennlp;

import java.io.IOException;
import java.util.List;

import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;
import org.apache.solr.update.AddUpdateCommand;
import org.apache.solr.update.processor.UpdateRequestProcessor;
import org.apache.solr.update.processor.UpdateRequestProcessorFactory;

/**
 * This class is a custom update request processor for adding the 
 * extracted entities to the specified destination field
 * 
 * @author DikshantS
 *
 */
public class NERUpdateProcessorFactory extends UpdateRequestProcessorFactory {
	private String modelFile;
	private String src;
	private String dest;
	private String entity;
	private float boost;
	
	private NamedEntityExtractor tagger;
	/**
	 * Initializing
	 */
	public void init(NamedList args) {
		super.init(args);
		
		// read the required parameters from the config 
		SolrParams param = SolrParams.toSolrParams(args);

		modelFile = param.get("modelFile");
		src = param.get("src");
		dest = param.get("dest");
		entity = param.get("entity","person");
		boost = param.getFloat("boost", 1.0f);
		
		tagger = new NamedEntityExtractor();
		tagger.setup(modelFile, entity);
	};

	/**
	 * This method instantiates the custom UpdateRequestProcessor.
	 */
	@Override
	public UpdateRequestProcessor getInstance(SolrQueryRequest req,
			SolrQueryResponse rsp, UpdateRequestProcessor nxt) {
		return new NERUpdateProcessor(nxt);
	}

	/**
	 * This update processor adds the extracted entity to the SolrInputDocument
	 * 
	 * @author DikshantS
	 *
	 */
	class NERUpdateProcessor extends UpdateRequestProcessor {

		public NERUpdateProcessor(UpdateRequestProcessor next) {
			super(next);
		}
		
		@Override
		public void processAdd(AddUpdateCommand cmd) throws IOException {
			SolrInputDocument doc = cmd.getSolrInputDocument();

		    Object obj = doc.getFieldValue(src);
		    if (null != obj && obj instanceof String) {
		    	List<NamedEntity> neList = tagger.tag((String) obj);

		    	// adds the extracted entity to the destination field
		    	// it assumes that the field is multi-valued
		    	for(NamedEntity ne : neList) {
		    		doc.addField(dest, ne.getToken(), boost);
		    	}
		    }
		 // pass it up the chain
	    super.processAdd(cmd);
		}
	}
}

