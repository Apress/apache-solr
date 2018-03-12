package com.apress.solr.pa.chapter08.similarity;

import org.apache.lucene.search.similarities.Similarity;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.schema.SimilarityFactory;

public class CustomSimilarityFactory extends SimilarityFactory {
	
	private boolean discountOverlaps;
	private String x;
	private float y;
	
	@Override
	public void init(SolrParams params) {
		super.init(params);
		x = params.get("x", "def");
		y = params.getFloat("b", 1.0f);
		discountOverlaps = params.getBool("discountOverlaps", true);
	}

	@Override
	public Similarity getSimilarity() {
		return new CustomSimilarity(x, y, discountOverlaps);
	}

}
