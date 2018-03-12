package com.apress.solr.pa.chapter07.functionquery;

import org.apache.lucene.queries.function.ValueSource;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.search.FunctionQParser;
import org.apache.solr.search.SyntaxError;
import org.apache.solr.search.ValueSourceParser;

/**
 * This factory demonstrates a custom implementation of ValueSourceParser.
 * This example implements a simple gaussian formula as a Solr named function.
 * 
 * @author DikshantS
 *
 */
public class GaussianValueSourceParser extends ValueSourceParser {
	
	// configurable parameters
	private static final String STD_DEVIATION_PARAM = "stdDeviation";
 	private static final String MEAN_PARAM = "mean";
 	
 	private float stdDeviation;
 	private float mean;
	
	// get the values from the parser definition in solrconfig.xml
 	public void init(NamedList namedList) {
		this.stdDeviation = (Float) namedList.get(STD_DEVIATION_PARAM);
		this.mean = (Float) namedList.get(MEAN_PARAM);
	}

	/**
	 * This method returns the instance of actual class implementing
	 * the formula.
	 */
 	@Override
	public ValueSource parse(FunctionQParser fp) throws SyntaxError {
		ValueSource vs = fp.parseValueSource();
		
		return new GaussianValueSource(vs, stdDeviation, mean);
	}

}
