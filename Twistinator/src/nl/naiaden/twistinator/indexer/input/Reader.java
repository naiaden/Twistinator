/**
 * 
 */
package nl.naiaden.twistinator.indexer.input;

import nl.naiaden.twistinator.indexer.TextRegister;

/**
 * @author louis
 *
 */
public interface Reader extends Runnable
{
	public TextRegister getTextRegister();
	
	/**
	 * If the files used to populate the index do not have unique identifiers
	 * this might lead to unwanted results. With this function one can choose
	 * whether the identifiers used in the files are also used to identify the
	 * documents in the index. If the id's are ignored, the reader chooses an
	 * identifier for itself.
	 * @param ignoreId <code>true</code> is the identifiers in the files should
	 * be ignored, <code>false</code> if the original identifiers should be 
	 * used
	 */
	public void setIgnoreId(boolean ignoreId);
}
