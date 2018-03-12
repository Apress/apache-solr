package com.apress.solr.pa.chapter08.similarity;

import java.io.IOException;

import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.CollectionStatistics;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.search.similarities.Similarity;

public class CustomSimilarity extends Similarity {
	private boolean discountOverlaps;
	private String x;
	private float y;
	
	public CustomSimilarity() {
		
	}
	
	public CustomSimilarity(String x, float y, boolean discountOverlaps) {
		this.x = x;
		this.y = y;
		this.discountOverlaps = discountOverlaps;
	}

	@Override
	public long computeNorm(FieldInvertState arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public SimWeight computeWeight(float arg0, CollectionStatistics arg1,
			TermStatistics... arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SimScorer simScorer(SimWeight arg0, LeafReaderContext arg1)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
