/**
 * 
 */
package nl.naiaden.twistinator.indexer.input;

import java.util.Date;

/**
 * Object that contains the metadata for a document
 * @author louis
 *
 */
public class TextMetadata implements Comparable<TextMetadata>
{

	/**
	 * @return the category
	 */
	public String getCategory()
	{
		return this.category;
	}

	/**
	 * @return the date
	 */
	public Date getDate()
	{
		return this.date;
	}
	
	private Date date;
	
	private String category;
	
	public TextMetadata(Date date, String category)
	{
		this.date = date;
		this.category = category;
	}
	
	public TextMetadata(String category)
	{
		this(null, category);
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(TextMetadata rhs)
	{
		return date.compareTo(rhs.getDate());
	}
}
