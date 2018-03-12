package com.apress.solr.pa.chapter08.similarity;

import org.apache.lucene.search.similarities.DefaultSimilarity;

/**
 * This class demonstrates a custom implementation of
 * UpdateRequestProcessorFactory.
 * 
 * @author DikshantS
 *
 */
public class NoIDFSimilarity extends DefaultSimilarity {
	  
	/**
	 * Override the method to provide custom calculation for IDF factor
	 */
	@Override
	public float idf(long docFreq, long numDocs) {
		// set the value as 1.0 to disable role of IDF in score calculation
		return 1.0f;
	}
}
