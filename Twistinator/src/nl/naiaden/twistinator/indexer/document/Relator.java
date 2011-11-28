/**
 * 
 */
package nl.naiaden.twistinator.indexer.document;

import java.io.Serializable;

/**
 * @author louis
 * 
 */
public class Relator implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8421829753892371436L;
	private String relator;

	public Relator(String relator)
	{
		this.relator = relator;
	}

	public boolean containsWildcard()
	{
		return relator.contains("*") || relator.contains("?");
	}

	/**
	 * @param relator
	 *            the relator to set
	 */
	public void setRelator(Relator relator)
	{
		this.relator = relator.toString();
	}

	@Override
	public String toString()
	{
		return relator;
	}

	/**
	 * @return the relator
	 */
	protected String getRelator()
	{
		return relator;
	}
}
