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
}
