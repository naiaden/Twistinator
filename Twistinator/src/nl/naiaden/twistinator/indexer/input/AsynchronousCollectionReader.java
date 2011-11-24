/**
 * 
 */
package nl.naiaden.twistinator.indexer.input;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * @author louis
 *
 */
public class AsynchronousCollectionReader implements Runnable
{
	static Logger log = Logger.getLogger(AsynchronousCollectionReader.class);
	
	private BlockingQueue<Document> documentQueue;
	private File file;
	
	public boolean keepRunning = true;
	public boolean isRunning = true;
	
	public long nrDocs = 0;
	public long nrTexts = 0;
	
	public AsynchronousCollectionReader(File file, BlockingQueue<Document> documentQueue)
	{
		this.file = file;
		this.documentQueue = documentQueue;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		log.info("+++ Asynchronous Collection Reader");
		
		try
		{
			SAXReader reader = new SAXReader();
			org.dom4j.Document document = reader.read(file);
			
			// documents (Collection)
			Element root = document.getRootElement();
			
			// iterate through documents (Texts)
			for(Iterator i = root.elementIterator(); i.hasNext(); )
			{
				Element textRoot = (Element) i.next();
				processText(textRoot);
			}
			
		} catch (DocumentException e) 
		{
			e.printStackTrace();
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		log.info("--- Asynchronous Collection Reader");
	}
	
	private void processText(Element textRoot) throws InterruptedException
	{
		// iterate through meta data
		for(Iterator i = textRoot.elementIterator(); i.hasNext();)
		{
			Element metaDataRoot = (Element) i.next();
//			processMetaData(metaDataRoot);
		}
		// iterate through sentences (Sents)
		for(Iterator i = textRoot.elementIterator(); i.hasNext();)
		{
			Element sentRoot = (Element) i.next();
			processSent(sentRoot);
		}
		++nrTexts;
	}
	
	private void processSent(Element sentRoot) throws InterruptedException
	{
		Sent sent = new Sent();
		
//		String sentence = sentRoot.g
		
		Document d = sent.toDocument();
		documentQueue.put(d); ++nrDocs;
	}
}
