package nl.naiaden.twistinator.indexer;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import nl.naiaden.twistinator.indexer.input.Text;

/**
 * This class is a mapping between parents (Texts) and children (Sents).
 * Because Lucene's API is still experimental, and some parts are planned
 * for version 4.0, we use this method for now.
 * 
 * The goal is to find all the children of a certain text. Normally, a 
 * document consists of sentences, and we want to store a whole document
 * in the index. But since the sentences are our basic structures, these
 * are ones that get indexed. 
 * As not to loose the connection between a document and its sentences, we
 * do some administration in this register.
 * 
 * @author louis
 *
 */
public class TextRegister {
	private Map<String, Text> register = null;
	
	/**
	 * 
	 */
	public TextRegister()
	{
		register = Collections.synchronizedMap(new TreeMap<String, Text>());
	}
	
	/**
	 * Number of Texts in the register
	 * @return
	 */
	public int size()
	{
		return register.size();
	}
	
	/**
	 * Number of Sents for a Text
	 * @param textId the id of the document
	 * @return the number of sentences in the document
	 */
	public int size(String textId)
	{
		return register.get(textId).size();
	}
	
	/**
	 * Add the sentence ids to the Text id. Only unique sentence identifiers
	 * are stored.
	 * @param textId the id of the document
	 * @param sentIdsNew the ids of the sentences: the children of the document
	 */
	public void add(String textId, TreeSet<String> sentIdsNew)
	{
		register.get(textId).add(sentIdsNew);
	}
	
	/**
	 * If there are already sentence identifiers for a certain Text, the 
	 * current content is replaced.
	 * @param textId the id of the document
	 * @param sentIds the ids of the sentences: the children of the document
	 */
	public void replace(String textId, TreeSet<String> sentIds)
	{
		register.get(textId).replace(sentIds);
	}
	
	/**
	 * Add the sentence id to the Text id. Only unique sentence identifiers
	 * are stored.
	 * @param textId the id of the document
	 * @param sentId the id of the sentence: a child of the document
	 */
	public void add(String textId, String sentId)
	{
		register.get(textId).add(sentId);
	}
	
	/**
	 * Get the sentence identifiers belong to a certain document
	 * @param textId the id of the document
	 * @return an ordered set of unique sentence identifiers for the
	 * document. <code>null</code> if the Text id is not known
	 */
	public TreeSet<String> getSentences(String textId)
	{
		return register.get(textId).getSentIds();
	}
}
