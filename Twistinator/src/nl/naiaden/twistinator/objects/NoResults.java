/**
 * 
 */
package nl.naiaden.twistinator.objects;

/**
 * This class is used by the server to tell there were no results found for a
 * query. The values of members must be ignored, as their content is undefined.
 * 
 * @author louis
 *
 */
public class NoResults extends SearchResult
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 410936723156268926L;

	public NoResults()
	{
		this(null);
	}

	/**
	 * @param result
	 */
	public NoResults(Returnable result)
	{
		super(result);
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		sb.append("SearchResult: No results");

		return sb.toString();
	}

}
