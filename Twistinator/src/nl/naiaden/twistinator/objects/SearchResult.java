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
	public String result;

	/**
	 * @param string
	 */
	public SearchResult(String string)
	{
		result = string;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		sb.append("SearchResult: " + result);

		return sb.toString();
	}
}
