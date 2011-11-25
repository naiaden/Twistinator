/**
 * 
 */
package nl.naiaden.twistinator.indexer.input;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import nl.naiaden.twistinator.indexer.document.Triple;
import nl.naiaden.twistinator.indexer.document.Triples;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
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
	
	static
	{
		final org.apache.log4j.Logger rootLogger = org.apache.log4j.Logger.getRootLogger();
		if (!rootLogger.getAllAppenders().hasMoreElements())
		{
			rootLogger.setLevel(Level.DEBUG);
			rootLogger.addAppender(new ConsoleAppender(new PatternLayout("%d{ABSOLUTE} [%-11t] %x %-5p %c{1} - %m%n")));
		}
	}
	
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
		
		log.info("nrTexts: " + nrTexts + " nrDocs: " + nrDocs);
		log.info("--- Asynchronous Collection Reader");
	}
	
	/**
	 * Process the document (Text). A document consists of its meta data and sentences
	 * @param textRoot
	 * @throws InterruptedException
	 */
	private void processText(Element textRoot) throws InterruptedException
	{
		// iterate through meta data
//		for(Iterator i = textRoot.elementIterator(); i.hasNext();)
//		{
//			Element metaDataRoot = (Element) i.next();
//			System.out.println(metaDataRoot.attributeValue("id"));
////			processMetaData(metaDataRoot);
//		}
		// iterate through sentences (Sents)
		
		for(Iterator i = textRoot.elementIterator(); i.hasNext();)
		{
			Element sentsRoot = (Element) i.next();
			processSents(sentsRoot, textRoot.attributeValue("id"));
		}
		++nrTexts;
	}
	
	/**
	 * Process the sentences (Sents)
	 * @param sentsRoot the root node that is the parent of the sentences
	 * @throws InterruptedException
	 */
	private void processSents(Element sentsRoot, String textId) throws InterruptedException
	{
		for(Iterator i = sentsRoot.elementIterator(); i.hasNext();)
		{
			Element sentRoot = (Element) i.next();
			processSent(sentRoot, textId);
		}
	}
	
	/**
	 * Process the sentence (Document)
	 * @param sentRoot
	 * @throws InterruptedException
	 */
	private void processSent(Element sentRoot, String textId) throws InterruptedException
	{
		String sentId = sentRoot.attributeValue("id");
		String sentence = sentRoot.elementText("sent");
		Triples triples = new Triples();
		
		Element triplesRoot = sentRoot.element("triples");
		
		for(Iterator i = triplesRoot.elementIterator(); i.hasNext();)
		{
			Element tripleElement = (Element) i.next();
			triples.add(new Triple(tripleElement.getText()));
		}
		
		Sent sent = new Sent(sentId, sentence, triples, textId);
		
		documentQueue.put(sent.toDocument()); 
		++nrDocs;
	}
}
