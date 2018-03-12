package com.apress.solr.pa.chapter06.component;

import java.io.IOException;

import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

/**
 * This class demonstrates a custom implementation of SearchComponent.
 * This example mandates that all response generated are JSON and 'wt' parameter
 * is ignored.
 * 
 * @author DikshantS
 *
 */
public class JsonMandatorComponent extends SearchComponent {
	
	public static final String COMPONENT_NAME = "jsonmandator";

	@Override
	public String getDescription() {
		return "jsonmandator: mandates JSON response.";
	}

	/**
	 * Prepare phase. It runs before the process phase.
	 * This method verifies the 'wt' parameter and if it not JSON sets it to JSON
	 */
	@Override
	public void prepare(ResponseBuilder rb) throws IOException {
		// get the request and response object
		SolrQueryRequest req = rb.req;
		SolrQueryResponse rsp = rb.rsp;
		
		// read the solrparams from request and create a modifiable solrparams
		SolrParams params = req.getParams();
		ModifiableSolrParams mParams = new ModifiableSolrParams(params);
		
		// read the value of 'wt' request parameter
		String wt = mParams.get(CommonParams.WT);
		
		// check the value. If not JSON, add an error element to the response,
		// which informs the user that invalid  value  has been provided.
		if(null != wt && !"json".equals(wt)) {
			NamedList nl = new SimpleOrderedMap<>();
			nl.add("error", "Only JSON response supported. Ignoring wt parameter!");
			
			rsp.add(COMPONENT_NAME, nl);
		}
		
		// set the 'wt' parameter to JSON irrespective of the old value
		mParams.set(CommonParams.WT, "json");
		
		// set the solrparams
		req.setParams(mParams);
	}

	/**
	 * Process phase. Write the custom processing logic here.
	 * This demonstration doesn't require any processing.
	 */
	@Override
	public void process(ResponseBuilder rb) throws IOException {
		
	}

}
