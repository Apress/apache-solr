package com.apress.solr.pa.chapter11.enrichment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.didion.jwnl.dictionary.Dictionary;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;

import com.apress.solr.pa.chapter11.opennlp.PartOfSpeech;
import com.apress.solr.pa.chapter11.opennlp.PartOfSpeechTagger;

/**
 * This class implements custom token filter to generate synonyms for tokens by
 * using WordNet.
 * 
 * @author DikshantS
 *
 */
public class CVSynonymFilter extends TokenFilter {
	// provides the text of a token
	private final CharTermAttribute termAttr = addAttribute(CharTermAttribute.class);

	// provides the positon information of a token
	private final PositionIncrementAttribute posIncAttr = addAttribute(PositionIncrementAttribute.class);

	// provides the start and end offset of a token
	private final OffsetAttribute offsetAttr = addAttribute(OffsetAttribute.class);

	private WordnetVocabulary vocabulary;
	private PartOfSpeechTagger tagger;
	private int maxExpansion;

	private boolean finished;
	private List<String> pendingTokens;
	private int startOffset;
	private int endOffset;
	private int posIncr;

	/**
	 * constructor
	 * 
	 * @param input
	 * @param dictionary
	 * @param tagger
	 * @param maxExpansion
	 */
	protected CVSynonymFilter(TokenStream input, Dictionary dictionary,
			PartOfSpeechTagger tagger, int maxExpansion) {
		super(input);

		this.maxExpansion = maxExpansion;
		this.tagger = tagger;
		this.vocabulary = new WordnetVocabulary(dictionary);

		if (null == tagger || null == vocabulary) {
			throw new IllegalArgumentException(
					"Either POS Tagger or WordNet Vocabulary is not configured properly.");
		}

		pendingTokens = new ArrayList<String>();
		finished = false;
		startOffset = 0;
		endOffset = 0;
		posIncr = 1;

	}

	/**
	 * Gets invoked for each token in the stream
	 */
	@Override
	public boolean incrementToken() throws IOException {
		while (!finished) {
			// play back any pending tokens synonyms
			while (pendingTokens.size() > 0) {
				String nextToken = pendingTokens.remove(0);
				termAttr.copyBuffer(nextToken.toCharArray(), 0,
						nextToken.length());
				offsetAttr.setOffset(startOffset, endOffset);
				posIncAttr.setPositionIncrement(posIncr);

				posIncr = 0;
				return true;
			}

			// extract synonyms for each token
			if (input.incrementToken()) {
				// int tokenLength = termAtt.length();
				// String token = String.valueOf(termAtt.buffer(), 0,
				// tokenLength)
				// .trim();
				
				String token = termAttr.toString();
				startOffset = offsetAttr.startOffset();
				endOffset = offsetAttr.endOffset();

				addOutputSynonyms(token);
			} else {
				finished = true;
			}
		}

		// should always return false
		return false;
	}

	/**
	 * This method generates synonym for each input term and adds to the pending
	 * list.
	 * 
	 * @param token
	 * @throws IOException
	 */
	private void addOutputSynonyms(String token) throws IOException {

		// add the input term to list so that it doesn't gets missed out
		pendingTokens.add(token);

		// generate part of speech calling the POS Tagger
		// this is a naive approach. Accurate tagging happens when the complete
		// stream is provided.
		List<PartOfSpeech> posList = tagger.tag(token);

		// POS is required. If value not found, skip further processing
		if (null == posList || posList.size() < 1) {
			return;
		}

		// get the synonyms from the token
		Set<String> synonyms = vocabulary.getSynonyms(token, posList.get(0)
				.getPos(), maxExpansion);

		// return if no synonym is found for the term
		if (null == synonyms) {
			return;
		}

		// add all the generated synonyms to the pending list
		for (String syn : synonyms) {
			pendingTokens.add(syn);
		}
	}

	/**
	 * Resets the stream to a clean state. Invoked before incrementToken().
	 */
	@Override
	public void reset() throws IOException {
		super.reset();
		finished = false;
		pendingTokens.clear();
		startOffset = 0;
		endOffset = 0;
		posIncr = 1;
	}

}
