/**
 * 
 */
package nl.naiaden.twistinator.indexer.input;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;

import nl.naiaden.twistinator.indexer.Index;
import nl.naiaden.twistinator.indexer.TextRegister;
import nl.naiaden.twistinator.indexer.document.Triple;
import nl.naiaden.twistinator.indexer.document.Triples;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * This reader reads collections asynchronously.
 * 
 * The input are xml collection files with the following layout:
 * <pre>
 * {@code
 * <documents>
 *    <document id="DOC1">
 *       <metadata>
 *          <date>
 *             <year>2011</year>
 *             <month>11</month>
 *             <day>26</day>
 *          </date>
 *          <category>medical</category>
 *       </metadata>
 *       <sentences>
 *          <sentence id="1">
 *             <sent>This is an example sentence</sent>
 *             <triples>
 *                <triple>[example,DET,an]</triple>
 *                <triple>[this,SUBJ,is]</triple>
 *             </triples>
 *          </sentence>
 *       </sentences>
 *    </document>
 * </documents>
 * }
 * </pre>
 * The identifiers are strings, and mandatory. It is the task of the user to
 * make sure the identifiers are unique, since these values are used internally.
 * 
 * <p>
 * The files are processed asynchronously. Because writing the files to the index
 * is usually a slower task, we can use the asynchronicity to have multiple index
 * writers perform the task.
 * 
 * @author louis
 *
 */
public class AsynchronousCollectionReader implements Reader
{
	static Logger log = Logger.getLogger(AsynchronousCollectionReader.class);
	
	private BlockingQueue<Document> documentQueue;
	private TextRegister textRegister = null;
	
	private File file;
	
	/**
	 * 
	 */
	public boolean keepRunning = true;
	/**
	 * 
	 */
	public boolean isRunning = true;
	
	/**
	 * Number of sentences read so far
	 */
	private long nrDocs = 0;
	/**
	 * Number of texts read so far
	 */
	private long nrTexts = 0;

	private boolean ignoreId = false;
	
	/**
	 * @return the nrDocs
	 */
	public long getNrDocs()
	{
		return this.nrDocs;
	}

	/**
	 * @return the nrTexts
	 */
	public long getNrTexts()
	{
		return this.nrTexts;
	}

	/**
	 * Creates an asynchronous collection reader
	 * @param file the that contains the documents
	 * @param documentQueue the queue that holds the sentences
	 */
	public AsynchronousCollectionReader(File file, BlockingQueue<Document> documentQueue)
	{
		super();
		this.file = file;
		this.documentQueue = documentQueue;
		textRegister = new TextRegister();
	}
	
	/* 
	 * (non-Javadoc)
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
			for(Iterator<?> i = root.elementIterator(); i.hasNext(); )
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
	 * @param textRoot the root node that is parent of the document
	 * @throws InterruptedException
	 */
	private void processText(Element textRoot) throws InterruptedException
	{
		String textId = textRoot.attributeValue("id");
		if(ignoreId)
		{
			textId = Index.generateTextId();
		}
		
		// iterate through meta data
		TextMetadata metadata = processMetaData(textRoot.element("metadata"));
		
		textRegister.add(textId, new Text(textId, metadata));
		
		// iterate through sentences (Sents)
		processSents(textRoot.element("sentences"), textId);
		
		
		++nrTexts;
	}
	
	/**
	 * Process the metadata for the document. The metadata consists of a date
	 * and a category
	 * @param metaDataRoot the root node that is parent of the metadata
	 * @return the metadata
	 */
	private TextMetadata processMetaData(Element metaDataRoot) {
		//date
		Element dateRoot = metaDataRoot.element("date");
		int year = Integer.parseInt(dateRoot.elementText("year"));
		int month = Integer.parseInt(dateRoot.elementText("month")) - 1;
		int day = Integer.parseInt(dateRoot.elementText("day"));
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, day);
		Date date = new Date(calendar.getTimeInMillis());
		
		//category
		String category = metaDataRoot.elementText("category");
		
		return new TextMetadata(date, category);
	}

	/**
	 * Process the sentences (Sents)
	 * @param sentsRoot the root node that is the parent of the sentences
	 * @param textId the id of the parent document
	 * @throws InterruptedException
	 */
	private void processSents(Element sentsRoot, String textId) throws InterruptedException
	{
		for(Iterator<?> i = sentsRoot.elementIterator(); i.hasNext();)
		{
			Element sentRoot = (Element) i.next();
			processSent(sentRoot, textId);
		}
	}
	
	/**
	 * Process the sentence (Document)
	 * @param sentRoot the root node that is parent of the sentence
	 * @param textId the id of the parent document
	 * @throws InterruptedException
	 */
	private void processSent(Element sentRoot, String textId) throws InterruptedException
	{
		String sentId = sentRoot.attributeValue("id");
		if(ignoreId)
		{
			sentId = Index.generateSentId();
		}
		
		String sentence = sentRoot.elementText("sent");
		Triples triples = new Triples();
		
		Element triplesRoot = sentRoot.element("triples");
		
		for(Iterator<?> i = triplesRoot.elementIterator(); i.hasNext();)
		{
			Element tripleElement = (Element) i.next();
			triples.add(new Triple(tripleElement.getText()));
		}
		
		Sent sent = new Sent(sentId, sentence, triples, textId);
		
		documentQueue.put(sent.toDocument());
		textRegister.add(textId, sentId);
		++nrDocs;
	}

	/* (non-Javadoc)
	 * @see nl.naiaden.twistinator.indexer.input.Reader#getTextRegister()
	 */
	@Override
	public TextRegister getTextRegister()
	{
		return textRegister;
	}

	@Override
	public void setIgnoreId(boolean ignoreId) {
		this.ignoreId = ignoreId;
	}
}
