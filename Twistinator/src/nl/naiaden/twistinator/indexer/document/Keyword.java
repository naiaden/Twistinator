/**
 * 
 */
package nl.naiaden.twistinator.indexer.document;

import java.util.Vector;

/**
 * @author louis
 * 
 */
public class Keyword
{
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

	/**
	 * @param keyword
	 *            the keyword to set
	 */
	public void setKeyword(String keyword)
	{
		this.keyword = keyword;
	}

	/**
	 * @return the keyword
	 */
	public String getKeyword()
	{
		return keyword;
	}

	/**
	 * @param partsOfSpeech
	 *            the partsOfSpeech to set
	 */
	public void setPartsOfSpeech(Vector<String> partsOfSpeech)
	{
		this.partsOfSpeech = partsOfSpeech;
	}

	/**
	 * @return the partsOfSpeech
	 */
	public Vector<String> getPartsOfSpeech()
	{
		return partsOfSpeech;
	}

	@Override
	public String toString()
	{
		return keyword;
	}
}
