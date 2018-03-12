package com.apress.solr.pa.chapter11.opennlp;

import java.io.IOException;
import java.util.Arrays;
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
 * Factory class for instantiating the POSUpdateProcessor
 * @author DikshantS
 *
 */
public class POSUpdateProcessorFactory extends UpdateRequestProcessorFactory {
	private String modelFile;
	private String src;
	private String dest;
	private float boost;
	private List<String> allowedPOS;
	
	private PartOfSpeechTagger tagger;
	
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
		boost = param.getFloat("boost", 1.0f);
		
		String posStr = param.get("pos","nnp,nn,nns");
		if (null != posStr) {
			allowedPOS = Arrays.asList(posStr.split(","));
		}
		
		tagger = new PartOfSpeechTagger();
		tagger.setup(modelFile);
	};

	/**
	 * This method instantiates the custom UpdateRequestProcessor.
	 */
	@Override
	public UpdateRequestProcessor getInstance(SolrQueryRequest req,
			SolrQueryResponse rsp, UpdateRequestProcessor nxt) {
		return new POSUpdateProcessor(nxt);
	}

	/**
	 * UpdateRequestProcessor for extracting the determining the part of speeches
	 * from the input field and adding to a separate field as defined in the config.
	 * 
	 * @author DikshantS
	 *
	 */
	class POSUpdateProcessor extends UpdateRequestProcessor {

		public POSUpdateProcessor(UpdateRequestProcessor next) {
			super(next);
		}
		
		@Override
		public void processAdd(AddUpdateCommand cmd) throws IOException {
			SolrInputDocument doc = cmd.getSolrInputDocument();

		    Object obj = doc.getFieldValue(src);
		    StringBuilder tokens = new StringBuilder();
		    if (null != obj && obj instanceof String) {
		    	List<PartOfSpeech> posList = tagger.tag((String) obj);
		    	
		    	// adds the tokens with allowed Part of Speech to the destination field
		    	// it assumes that the field is multi-valued
		    	for(PartOfSpeech pos : posList) {
		    		if (allowedPOS.contains(pos.getPos().toLowerCase())) {
		    			tokens.append(pos.getToken()).append(" ");
		    		}
		    	}
		    	doc.addField(dest, tokens.toString(), boost);
		    }
		    
		    // pass it up the chain
		    super.processAdd(cmd);
		}
		
	}
}
