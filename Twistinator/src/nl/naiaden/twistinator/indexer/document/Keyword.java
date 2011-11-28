/**
 * 
 */
package nl.naiaden.twistinator.indexer.document;

import java.util.Vector;

import nl.naiaden.twistinator.objects.Searchable;

/**
 * @author louis
 * 
 */
public class Keyword implements Searchable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4866659661240584202L;
	private String keyword;
	private Vector<String> partsOfSpeech;

	public Keyword(String keyword)
	{
		this.setKeyword(keyword);
		this.setPartsOfSpeech(null);
	}

	public Keyword(String keyword, Vector<String> partsOfSpeech)
	{
		this.setKeyword(keyword);
		this.setPartsOfSpeech(partsOfSpeech);
	}

	public boolean containsWildcard()
	{
		return keyword.contains("*") || keyword.contains("?");
	}

	/**
	 * @return the keyword
	 */
	public String getKeyword()
	{
		return keyword;
	}

	/**
	 * @return the partsOfSpeech
	 */
	public Vector<String> getPartsOfSpeech()
	{
		return partsOfSpeech;
	}

	/**
	 * @param keyword
	 *            the keyword to set
	 */
	public void setKeyword(String keyword)
	{
		this.keyword = keyword;
	}

	/**
	 * @param partsOfSpeech
	 *            the partsOfSpeech to set
	 */
	public void setPartsOfSpeech(Vector<String> partsOfSpeech)
	{
		this.partsOfSpeech = partsOfSpeech;
	}

	@Override
	public String toString()
	{
		return keyword;
	}
}
