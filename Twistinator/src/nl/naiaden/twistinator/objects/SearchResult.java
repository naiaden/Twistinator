/**
 * 
 */
package nl.naiaden.twistinator.objects;

import java.io.Serializable;

/**
 * @author louis
 *
 */
public class SearchResult implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4848065184695219363L;
	public Returnable result;

	/**
	 * @param string
	 */
	public SearchResult(Returnable result)
	{
		this.result = result;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		sb.append("SearchResult: " + result.toString());

		return sb.toString();
	}
}
