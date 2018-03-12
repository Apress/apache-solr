Apache Solr: A Practical Approach to Enterprise Search, is a book for developers who are building a search engine using Apache Solr.

This document provides the steps for using the provided source code and setting it in a Solr core to utilize the provided capabilities.

Directory Structure:
-------------------
The following is the checkout directory structure:

APRESS_BUNDLE
	|
	|-- configsets (contains a sample core)
	|
	|-- sample-input (contains sample documents for indexing)
	|
	|-- semantic-home (contains the dependencies for Chapter 11)
	|
	|-- solr-practical-approach (contains the source code)

Note: This document uses the term $APRESS_BUNDLE which refers to the root of the checkout directory.

Prerequisites
-------------
The following  are the prerequisites to build the provided sample code:
1. JDK 1.8+
2. Maven 3.0 or higher

Download
---------
The Solr plugins discussed in Chapter 11 has external dependencies which should be downloaded from the respective websites. The following is the list of required dependencies:
1.  OpenNLP Models:
	Download the en-pos-maxent.bin and en-ner-person.bin models from http://opennlp.sourceforge.net/models-1.5/. Alternatively,, you can evaluate named-entity extraction using any other en-ner-*.bin models also.
2. WordNet Dictionary:
	a. Download the Wordnet 3.1 database files from https://wordnet.princeton.edu/wordnet/download/. 
	b. A sample properties.xml is provided in semantic-home/wordnet directory. Refer to http://jwordnet.sourceforge.net/handbook.html for details on configuring the file.
	
Build
-----
The following are the steps to build the project.
1. Ensure that you have Java installed and it meets the prerequisites, using  the following command:
	$ java -version
2. Ensure that you have Maven installed and it meets the above mentioned prerequisites, using  the following command:
	$ mvn -version
3. Build the project using Maven. Following is the command to build it from the terminal.
	$ cd source-code/solr-practical-approach
	$ mvn clean install

Install
-------
The following are the steps to integrate the provided features to Solr.
1. Copy the $APRESS_BUNDLE/configset/apress directory to your $SOLR_HOME directory.
	$ cp -r $APRESS_BUNDLE/configset/apress $SOLR_HOME

2. Build the provided source code as mentioned in the earlier step and add the generated jar files to the core libraries.
	$ cd $SOLR_HOME/apress
	$ mkdir lib
	$ cp $APRESS_BUNDLE/source-code/target/solr-practical-approach.*.jar $SOLR_DIST/server/solr/apress/lib
	
3. Copy the downloaded & extracted Wordnet dictionary directory to $APRESS_BUNDLE/semantic-home/wordnet. After copying the dictionary, the wordnet directory will look as follows:
	$ cd $APRESS_BUNDLE/semantic-home/wordnet
	$ ls
		dict
		properties.xml

4. Edit the properties.xml in $APRESS_BUNDLE/semantic-home/wordnet and provide the path of WordNet dictionary.
	<param name="dictionary_path" value="/path-to-apress-bundle/semantic-home/wordnet/dict"/>
	
5. Copy the downloaded models to $APRESS_BUNDLE/semantic-home/opennlp directory 
	
6. Edit the core.properties file in the created core directory and add the path of POS model, NER model and JWNL properties.xml file.
	opennlp.pos.model.path=/path-to-apress-bundle/semantic-home/opennlp/en-pos-maxent.bin
	opennlp.ner.model.path=/path-to-apress-bundle/semantic-home/opennlp/en-ner-person.bin
	wordnet.jwnl.properties.path=/path-to-apress-bundle/semantic-home/wordnet/properties.xml

7. Start the server.
	$ solr start -p 8983
	
This completes the Solr setup, integration of Solr plugins and core creation for indexing and searching. 

You can now index the files provides in $APRESS_BUNDLE/sample-input directory. Refer to Chapter 2 and 5 to learn more about indexing data.









 


	
	
