package com.apress.solr.pa.chapter11.enrichment;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.dictionary.Dictionary;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenFilterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apress.solr.pa.chapter11.opennlp.PartOfSpeechTagger;

/**
 * This is a factory for instantiating the WordNet based synonym filter factory
 * 
 * @author DikshantS
 *
 */
public class CVSynonymFilterFactory extends TokenFilterFactory implements ResourceLoaderAware {
	private static final Logger logger = LoggerFactory.getLogger(CVSynonymFilterFactory.class);

	/**
	 * attributes provided in field definition in schema.xml
	 */

	// specifies the maximum number of synonyms to generate
	private int maxExpansion;

	// specifies the path of properties file required by JWNL
	private String propFile;

	// specifies the path of OpenNLP POS model
	private String modelFile;

	private Dictionary dictionary = null;
	private PartOfSpeechTagger tagger = null;

	/**
	 * constructor
	 * 
	 * @param args
	 */
	public CVSynonymFilterFactory(Map<String, String> args) {
		super(args);
		maxExpansion = getInt(args, "maxExpansion", 3); // default 3 expansions
		propFile = require(args, "wordnetFile");
		modelFile = require(args, "posModel");
	}

	@Override
	public TokenStream create(TokenStream input) {
		// return an instance of custom token filter
		return new CVSynonymFilter(input, dictionary, tagger, maxExpansion);
	}

	/**
	 * Load the required resources
	 */
	@Override
	public void inform(ResourceLoader loader) throws IOException {

		// initialize for wordnet
		try {
			JWNL.initialize(new FileInputStream(propFile));
			dictionary = Dictionary.getInstance();
		} catch (JWNLException ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
			throw new IOException(ex.getMessage());
		}

		// initialize for part of speech tagging
		tagger = new PartOfSpeechTagger();
		tagger.setup(modelFile);
	}

}
