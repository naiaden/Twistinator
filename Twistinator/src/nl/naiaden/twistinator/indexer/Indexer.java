/**
 * 
 */
package nl.naiaden.twistinator.indexer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;

/**
 * @author louis
 *
 */
public class Indexer implements Runnable
{
	static Logger log = Logger.getLogger(Indexer.class);

	private BlockingQueue<Document> documentQueue = new LinkedBlockingQueue<Document>(10);
	public Indexer(int workQueueSize) throws InterruptedException
	{
		documentQueue = new LinkedBlockingQueue<Document>(workQueueSize);
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		while(!Thread.currentThread().isInterrupted())
		{
			try
			{
				Document doc = documentQueue.take();
				log.debug(doc.get(Index.FIELD_ID) + " <<< " + doc.get(Index.FIELD_SENTENCE));
				// process item
			} catch (InterruptedException e)
			{
				log.error("Encountered error: " + e.getMessage());
				Thread.currentThread().interrupt();
				break;
			}
		}

	}


}
