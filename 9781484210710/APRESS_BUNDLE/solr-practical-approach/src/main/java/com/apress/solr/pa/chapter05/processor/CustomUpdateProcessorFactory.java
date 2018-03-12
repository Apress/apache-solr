package com.apress.solr.pa.chapter05.processor;

import java.io.IOException;

import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;
import org.apache.solr.update.AddUpdateCommand;
import org.apache.solr.update.processor.UpdateRequestProcessor;
import org.apache.solr.update.processor.UpdateRequestProcessorFactory;

/**
 * This class demonstrates a custom implementation of
 * UpdateRequestProcessorFactory.
 * 
 * @author DikshantS
 *
 */
public class CustomUpdateProcessorFactory extends UpdateRequestProcessorFactory {

	/**
	 * Initialize your factory. This method is not mandatory.
	 */
	public void init(NamedList args) {
		super.init(args);
	}

	/**
	 * This method instantiates the custom UpdateRequestProcessor.
	 */
	@Override
	public UpdateRequestProcessor getInstance(SolrQueryRequest req,
			SolrQueryResponse rsp, UpdateRequestProcessor nxt) {
		return new CustomUpdateProcessor(nxt);
	}
}

/**
 * This class demonstrates a custom implementation of UpdateRequestProcessor.
 * 
 * @author DikshantS
 *
 */
class CustomUpdateProcessor extends UpdateRequestProcessor {
	public CustomUpdateProcessor(UpdateRequestProcessor nxt) {
		super(nxt);
	}

	/**
	 * Your custom logic for processing the document being indexed goes here.
	 */
	@Override
	public void processAdd(AddUpdateCommand cmd) throws IOException {
		// get the document being indexed
		SolrInputDocument doc = cmd.getSolrInputDocument();

		// get the value of the desired field
		Object obj = doc.getFieldValue("downloadcount");
		if (obj != null) {
			int dc = Integer.parseInt(String.valueOf(obj));
			// if the download count is more than 1 lac, specify that the 
			// document is popular
			if (dc > 100000) {
				// add a new field with desired value before the document 
				// is indexed
				doc.setField("popular", true);
			}
		}

		// you must make this call
		super.processAdd(cmd);
	}
}
