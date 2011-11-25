/**
 * 
 */
package nl.naiaden.twistinator.indexer.output;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import nl.naiaden.twistinator.indexer.TextRegister;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;

/**
 * @author louis
 *
 */
public class AsynchronousIndexerWriter implements Runnable
{
	static Logger log = Logger.getLogger(AsynchronousIndexerWriter.class);
	
	private BlockingQueue<Document> documentQueue;
	private IndexWriter writer;
	
	public boolean keepRunning = true;
	public boolean isRunning = true;
	
	public long nrDocs = 0;
	private long sleepOnEmpty = 100;
	
	public void addDocument(Document aDocument) throws InterruptedException
	{
		documentQueue.put(aDocument);
	}
	
	public AsynchronousIndexerWriter(IndexWriter anIndexWriter)
	{
		this(anIndexWriter, 100, 100);
	}
	
	public AsynchronousIndexerWriter(IndexWriter anIndexWriter, int queueSize)
	{
		this(anIndexWriter, queueSize, 100);
	}
	
	public AsynchronousIndexerWriter(IndexWriter anIndexWriter, int queueSize, long sleepTime)
	{
		this(anIndexWriter, new LinkedBlockingQueue<Document>(queueSize), sleepTime);
	}
	
	public AsynchronousIndexerWriter(IndexWriter anIndexWriter, BlockingQueue<Document> aQueue, long sleepTime)
	{
		writer = anIndexWriter;
		documentQueue = aQueue;
		sleepOnEmpty = sleepTime;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		log.info("+++ Asynchronous Index Writer");
		
		while(keepRunning || !documentQueue.isEmpty())
		{
			Document doc = documentQueue.poll();
			
			try
			{
				if(doc != null)
				{
//					log.debug(doc.get("id") + " <<< " + doc.get("sentence"));
					writer.addDocument(doc); 
					if(++nrDocs % 10000 == 0)
					{
						log.info("AsyncWriter: " + nrDocs);
					}
				} else
				{
					// Nothing in queue: wait
					Thread.sleep(sleepOnEmpty);
				}
			} catch (Exception e)
			{
//				e.printStackTrace();
				log.info("Exception found! " + e.getMessage());
				break;
			}
		}
		
		isRunning = false;
		log.info("--- Asynchronous Index Writer");
	}
	
	private void stopWriting()
	{
		keepRunning = false;
		
		try
		{
			while(isRunning)
			{
				Thread.sleep(sleepOnEmpty);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void optimize() throws CorruptIndexException, IOException
	{
		writer.optimize();
	}
	
	public void close() throws CorruptIndexException, IOException
	{
		stopWriting();
		
		writer.close();
	}

}
