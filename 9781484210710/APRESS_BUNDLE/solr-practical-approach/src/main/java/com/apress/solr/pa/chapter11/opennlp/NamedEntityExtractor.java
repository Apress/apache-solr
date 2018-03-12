package com.apress.solr.pa.chapter11.opennlp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.Span;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements Named Entity Extraction using OpenNLP.
 * It allows to extract only one entity type.
 * 
 * @author DikshantS
 *
 */
public class NamedEntityExtractor {
	private static final Logger logger = LoggerFactory.getLogger(PartOfSpeechTagger.class);
	
	private String entityName = "";
	private TokenNameFinderModel model = null;
	
	/**
	 * This method loads the model, which will be used for extraction
	 *   
	 * @param fileName
	 * @param entityName
	 */
	public void setup(String fileName, String entityName) {
		  this.entityName = entityName;
	    InputStream modelIn = null;
	    try {
	      modelIn = new FileInputStream(fileName);
	      model = new TokenNameFinderModel(modelIn);

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
	   * Method to extract the named entity
	   * @param query
	   * @return 
	   */
	  public List<NamedEntity> tag(String query) {
		List<NamedEntity> neList = new ArrayList<>();
		
		// A quick way to generate tokens from the stream of text.
		// As an alternative appropriate tokenizer such as WhitespaceTokenizer
		// can be used.
		String [] sentence = query.split(" ");
		
		// Instantiate the finder from the model
	    NameFinderME nameFinder = new NameFinderME(model);
	    
	    // generates name tags for the given sequence
	    Span[] spans = nameFinder.find(sentence);
	    
	    // get the entities and add it to the list
	    for (Span span : spans) {
	    	NamedEntity entity = new NamedEntity();
		      StringBuilder match = new StringBuilder();
		      for (int i = span.getStart(); i < span.getEnd(); i++) {
		        match.append(sentence[i]).append(" ");
		      }
		      
		      entity.setToken(match.toString().trim());
		      entity.setEntity(entityName);
		      neList.add(entity);
	    }
	    
	    //  forget all adaptive data that is collected
	    nameFinder.clearAdaptiveData();
	    
	    // return the named entities
	    return neList;
	  }
	  
	  /**
	   * Runs in loop for 100000 times
	   * @param args 
	   */
	  public static void main(String[] args) {
		  NamedEntityExtractor tagger = new NamedEntityExtractor();
		  tagger.setup("D:\\Projects\\semantic_home\\opennlp\\en-ner-person.bin", "person");
		  List<NamedEntity> neList = tagger.tag("While Bob took the initial lead in race Marley finally won it");
		  System.out.println(neList);
	  }
}
