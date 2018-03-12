package com.apress.solr.pa.chapter07.functionquery;

import java.io.IOException;
import java.util.Map;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.docvalues.FloatDocValues;

/**
 * This class demonstrates a custom implementation of ValueSourceParser.
 * This example implements a simple gaussian formula.
 * 
 * @author DikshantS
 *
 */
public class GaussianValueSource extends ValueSource {	
	
	// required variables
	protected final ValueSource source;
	protected float stdDeviation = 1.0f;
	protected float mean = 10.0f;
	protected float variance = 1.0f;
 	
	public GaussianValueSource(ValueSource source, float stdDeviation, float mean) {
		this.source = source;
		this.stdDeviation = stdDeviation;
		this.variance = this.stdDeviation * this.stdDeviation;
		this.mean = mean;
	}

	@Override
	public String description() {
		return "Gaussian function query (" + source.description() + ")";
	}

	@Override
	public boolean equals(Object arg0) {
		return false;
	}

	/**
	 * This method contains the custom formula
	 */
	@Override
	public FunctionValues getValues(Map context, LeafReaderContext readerContext)
			throws IOException {
		final FunctionValues aVals =  source.getValues(context, readerContext);
	    
	    // return a implementation of FloatDocValues to support retrieving
		// float values
		return new FloatDocValues(this) {
			
			@Override
			public float floatVal(int doc) {
				float val1 = aVals.floatVal(doc);
				
				// gaussian formula
				float score = (float) Math.pow(Math.exp(-(((val1 - mean) * (val1 - mean)) / ((2 * variance)))), 1 / (stdDeviation * Math.sqrt(2 * Math.PI)));
				
				// return the computed score
				return score;
			}
			
			@Override
		      public String toString(int doc) {
		        return "Gaussian function query (" + aVals.toString(doc) + ')';
		      }
	    };
	}

	@Override
	public int hashCode() {
		return source.hashCode() + "Gaussian function query".hashCode();
	}
	
}
