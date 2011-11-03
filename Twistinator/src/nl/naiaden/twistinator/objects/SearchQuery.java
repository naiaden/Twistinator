/**
 * 
 */
package nl.naiaden.twistinator.objects;

import java.io.Serializable;

/**
 * @author louis
 *
 */
public class SearchQuery implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5259322374034472167L;
	public String query;

	public SearchQuery(String string)
	{
		query = string;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		sb.append("SearchQuery: ");
		sb.append(query);

		return sb.toString();
	}
}
