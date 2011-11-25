package nl.naiaden.twistinator.indexer;

import java.util.Collections;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;
import java.util.Vector;

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
 * @author louis
 *
 */
public class TextRegister {
	private Map register = null;
	
	public TextRegister()
	{
		Map m = Collections.synchronizedMap(new TreeMap<String, PriorityQueue<String>>());
	}
	
	public void add(String textId, PriorityQueue<String> sentIdsNew)
	{
		PriorityQueue<String> sentIds = (PriorityQueue<String>) register.get(textId);
		sentIds.addAll(sentIdsNew);
		register.put(textId, sentIds);
	}
	
	public void replace(String textId, PriorityQueue<String> sentIds)
	{
		register.put(textId, sentIds);
	}
	
	public void add(String textId, String sentId)
	{
		PriorityQueue<String> sentIds = (PriorityQueue<String>) register.get(textId);
		sentIds.add(sentId);
		register.put(textId, sentIds);
	}
	
	public PriorityQueue<String> get(String textId)
	{
		return (PriorityQueue<String>) register.get(textId);
	}
}
