/**
 * 
 */
package nl.naiaden.twistinator.indexer.input;

import java.util.TreeSet;

/**
 * @author louis
 *
 */
public class Text 
{
	/**
	 * @return the sentIds
	 */
	public TreeSet<String> getSentIds()
	{
		return this.sentIds;
	}

	/**
	 * @param sentIds the sentIds to set
	 */
	public void setSentIds(TreeSet<String> sentIds)
	{
		this.sentIds = sentIds;
	}

	/**
	 * @return the metadata
	 */
	public TextMetadata getMetadata()
	{
		return this.metadata;
	}

	/**
	 * @param metadata the metadata to set
	 */
	public void setMetadata(TextMetadata metadata)
	{
		this.metadata = metadata;
	}

	/**
	 * @return the textId
	 */
	public String getTextId()
	{
		return this.textId;
	}

	private String textId;
	private TreeSet<String> sentIds;
	private TextMetadata metadata;
	
	/**
	 * The number of children sentences
	 * @return the number of children sentences
	 */
	public int size()
	{
		return sentIds.size();
	}
	
	/**
	 * Add the sentence ids to the Text. Only unique sentence identifiers are 
	 * stored.
	 * @param sentIdsNew the ids of the sentences: the children of the document
	 */
	public void add(TreeSet<String> sentIdsNew)
	{
		sentIds.addAll(sentIdsNew);
	}
	
	/**
	 * Add the sentence id to the document. Only unique sentence identifiers
	 * are stored.
	 * @param sentId the id of the sentence: a child of the document
	 */
	public void add(String sentId)
	{
		sentIds.add(sentId);
	}
	
	/**
	 * If there are already sentence identifiers for this document, the 
	 * current content is replaced.
	 * @param sentIds the ids of the sentences: the children of the document
	 */
	public void replace(TreeSet<String> sentIdsNew)
	{
		sentIds = sentIdsNew;
	}
	
	
}
