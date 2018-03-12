package com.apress.solr.pa.chapter11.enrichment;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Set;

import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.Synset;
import net.didion.jwnl.data.Word;
import net.didion.jwnl.dictionary.Dictionary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class extracts synonyms from WordNet using JWNL library
 * 
 * @author DikshantS
 *
 */
public class WordnetVocabulary {
	private static final Logger logger = LoggerFactory
			.getLogger(WordnetVocabulary.class);
	private Dictionary dictionary = null;

	/**
	 * constructor
	 * 
	 * @param dictionary
	 */
	public WordnetVocabulary(Dictionary dictionary) {
		this.dictionary = dictionary;
	}

	/**
	 * This method provides the synonyms for the input term.
	 * This method only processes the synonyms for verbs, adjectives and
	 * adverbs. The nouns are ignored because it can lead to false positives
	 * due to polysemous words. It handle it you should have robust
	 * disambiguation mechanism.
	 * 
	 * @param term
	 *            specifies the term for which synonyms should be extracted
	 * @param posStr
	 *            specifies the Part of Speech of the term
	 * @param maxExpansion
	 *            specifies the desired number of synonyms
	 * @return
	 */
	public Set<String> getSynonyms(String term, String posStr, int maxExpansion) {
		Set<String> synonyms = new HashSet<>();
		// synonyms.add(term);

		// converts the Part of Speech to JWNL format
		POS pos = getMappedPOS(posStr);
		
		// validate
		if (null != dictionary && null != term && null != pos) {
			try {
				// get the matching words from wordnet
				IndexWord iWord = dictionary.getIndexWord(pos, term);

				// return, if no match is found
				if (null == iWord)
					return null;

				// extract the terms from each synset
				Synset[] synsets = iWord.getSenses();
				int count = 0;

				outerloop: for (Synset synset : synsets) {
					// if no match is available in this synset, check others
					if (null == synset.getWords()) {
						continue;
					}
					
					// extract all the synonyms for the synset
					Word[] words = synset.getWords();
					for (Word word : words) {
						// get the synonym and normalize
						String synonym = word.getLemma().toString()
								.replace("_", " ");
						synonyms.add(synonym);
						
						// if enough is extracted, return
						if (++count > maxExpansion) {
							break outerloop;
						}
					}
				}
			} catch (JWNLException ex) {
				logger.error(ex.getMessage());
				ex.printStackTrace();
			}
		}

		// ensures that input term is not extracted as synonym
		synonyms.remove(term);

		// return the synonymms
		return synonyms;
	}

	/**
	 * convert to JWNL POS.
	 * 
	 * 
	 * @param posStr
	 * @return
	 */
	private POS getMappedPOS(String posStr) {
		POS pos = null;
		switch (posStr) {
		case "VB":
		case "VBD":
		case "VBG":
		case "VBN":
		case "VBP":
		case "VBZ":
			pos = POS.VERB;
			break;
		case "RB":
		case "RBR":
		case "RBS":
			pos = POS.ADVERB;
			break;
		case "JJS":
		case "JJR":
		case "JJ":
			pos = POS.ADJECTIVE;
			break;
//		case "NN":
//		case "NNS":
//		case "NNP":
//		case "NNPS":
//			pos = POS.NOUN;
//			break;
		}
		return pos;
	}

	public static void main(String[] args) {
		Dictionary dictionary = null;
		try {
			JWNL.initialize(new FileInputStream(
					"properties.xml"));
			dictionary = Dictionary.getInstance();
		} catch (JWNLException | FileNotFoundException ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
		}

		WordnetVocabulary enrich = new WordnetVocabulary(dictionary);

		Set<String> synonyms = enrich.getSynonyms("meets", "VBZ", 3);
		
		System.out.println("Synonyms: " + synonyms.toString());

	}
}
