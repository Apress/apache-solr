package com.apress.solr.pa.chapter11.opennlp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class tags the Part of Speech using OpenNLP.
 * 
 * @author DikshantS
 *
 */
public class PartOfSpeechTagger {
	private static final Logger logger = LoggerFactory.getLogger(PartOfSpeechTagger.class);

	private POSModel model = null;

	/**
	 * This method loads the model, which will be used for tagging
	 * 
	 * @param fileName
	 */
	public void setup(String fileName) {
		InputStream modelIn = null;
		try {
			modelIn = new FileInputStream(fileName);
			model = new POSModel(modelIn);

		} catch (IOException e) {
			// Model loading failed, handle the error
			logger.error(e.getMessage());
		} finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * Method to tag the Part of Speech
	 * @param query
	 * @return 
	 */
	public List<PartOfSpeech> tag(String query) {
		List<PartOfSpeech> posList = new ArrayList<>();
		
		// A quick way to generate tokens from the stream of text.
		// As an alternative appropriate tokenizer such as WhitespaceTokenizer
		// can be used.
		String [] tokens = query.split(" ");
		
		// Instantiate the tagger from the model
		POSTaggerME tagger = new POSTaggerME(model);
		
		// generate the tags
		String [] tags = tagger.tag(tokens);
		
		// get the tags and add it to the list
		int i = 0;
		for(String token : tokens) {
			PartOfSpeech pos = new PartOfSpeech();
			pos.setToken(token);
			pos.setPos(tags[i]);
			posList.add(pos);
			i++;
		}

		// return the tags
		return posList;
	}
	
	
	public static void main(String[] args) {
		PartOfSpeechTagger tagger = new PartOfSpeechTagger();
		tagger.setup("D:\\Projects\\semantic_home\\opennlp\\en-pos-maxent.bin");
		System.out.println(tagger.tag("Barack Obama is the president of America. He lives in pune"));
	}
}
