/**
 * 
 */
package nl.naiaden.twistinator.indexer.input;

import java.util.TreeSet;

import nl.naiaden.twistinator.objects.Returnable;

/**
 * @author louis
 *
 */
public class Text implements Returnable
{
	private String textId;

	private TreeSet<String> sentIds;

	private TextMetadata metadata;

	public Text(String textId)
	{
		this(textId, new TextMetadata());
	}

	public Text(String textId, TextMetadata metadata)
	{
		this(textId, metadata, new TreeSet<String>());
	}

	public Text(String textId, TextMetadata metadata, TreeSet<String> sentIds)
	{
		this.textId = textId;
		this.metadata = metadata;
		this.sentIds = sentIds;
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
	 * Add the sentence ids to the Text. Only unique sentence identifiers are
	 * stored.
	 * @param sentIdsNew the ids of the sentences: the children of the document
	 */
	public void add(TreeSet<String> sentIdsNew)
	{
		sentIds.addAll(sentIdsNew);
	}

	/**
	 * @return the metadata
	 */
	public TextMetadata getMetadata()
	{
		return metadata;
	}
	/**
	 * @return the sentIds
	 */
	public TreeSet<String> getSentIds()
	{
		return sentIds;
	}
	/**
	 * @return the textId
	 */
	public String getTextId()
	{
		return textId;
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

	/**
	 * @param metadata the metadata to set
	 */
	public void setMetadata(TextMetadata metadata)
	{
		this.metadata = metadata;
	}

	/**
	 * @param sentIds the sentIds to set
	 */
	public void setSentIds(TreeSet<String> sentIds)
	{
		this.sentIds = sentIds;
	}

	/**
	 * The number of children sentences
	 * @return the number of children sentences
	 */
	public int size()
	{
		return sentIds.size();
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		sb.append("[" + textId + "] " + sentIds.toString() + " " + metadata.toString() );

		return sb.toString();
	}

}
