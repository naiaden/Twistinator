/**
 * 
 */
package nl.naiaden.twistinator.indexer.document;

/**
 * @author louis
 * 
 */
public class Relator
{
	private String relator;

	public Relator(String relator)
	{
		this.relator = relator;
	}

	/**
	 * @param relator
	 *            the relator to set
	 */
	public void setRelator(Relator relator)
	{
		this.relator = relator.toString();
	}

	/**
	 * @return the relator
	 */
	protected String getRelator()
	{
		return relator;
	}

	@Override
	public String toString()
	{
		return relator;
	}
}
