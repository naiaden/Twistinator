/**
 * 
 */
package nl.naiaden.twistinator.indexer.input;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.concurrent.BlockingQueue;

import nl.naiaden.twistinator.indexer.Index;
import nl.naiaden.twistinator.indexer.TextRegister;
import nl.naiaden.twistinator.indexer.document.Triple;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;

/**
 * @author louis
 *
 */
public class AsynchronousSentsReader implements Reader
{
	static Logger log = Logger.getLogger(AsynchronousSentsReader.class);
	
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

	private boolean ignoreId;
	
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
	 * Creates an asynchronous sents reader
	 * @param file the that contains the documents
	 * @param documentQueue the queue that holds the sentences
	 */
	public AsynchronousSentsReader(File file, BlockingQueue<Document> documentQueue)
	{
		super();
		this.file = file;
		this.documentQueue = documentQueue;
		textRegister = new TextRegister();
	}

	/*
	 * (non-Javadoc)
	 * @see nl.naiaden.twistinator.indexer.input.Reader#getTextRegister()
	 */
	public TextRegister getTextRegister()
	{
		return textRegister;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
		log.info("+++ Asynchronous Sents Reader");
		log.info("Ignore id? " + (ignoreId ? "yes" : "no"));
		
		try
		{
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String bufferedLine = bufferedReader.readLine();
	
			Sent sent = new Sent();
			
			String fakeParent = Index.generateTextId();
	
			if (bufferedLine.startsWith("# (null)"))
			{
				sent.setHeader(bufferedLine);
	
				while ((bufferedLine = bufferedReader.readLine()) != null)
				{
					if (!StringUtils.isBlank(bufferedLine))
					{
						if (bufferedLine.startsWith("# (null)"))
						{
							sent.setParentDocument(fakeParent);
							if(ignoreId)
							{
								sent.setId(Index.generateSentId());
							}
							Document d = sent.toDocument();
							
							documentQueue.put(d);
							if(ignoreId)
							{
								textRegister.add(fakeParent, Index.generateSentId());
							} else
							{
								textRegister.add(fakeParent, d.get("id"));
//								textRegister.add(d.get("parentId"), d.get("id"));
							}
							if(++nrDocs % 10000 == 0)
							{
								log.info("AsyncReader: " + nrDocs);
							}
							log.debug(d.get("id") + " >>> " + d.get("sentence"));
							sent = new Sent();
							sent.setHeader(bufferedLine);
						} else if (!bufferedLine.startsWith("# Error:") && !bufferedLine.startsWith("UNKN:"))
						{
							sent.addTriple(new Triple(bufferedLine));
						}
					}
				}
	
				sent.setParentDocument(fakeParent);
				if(ignoreId)
				{
					sent.setId(Index.generateSentId());
				}
				Document d = sent.toDocument();
				documentQueue.put(d); 
				if(ignoreId)
				{
					textRegister.add(fakeParent, Index.generateSentId());
				} else
				{
					textRegister.add(fakeParent, d.get("id"));
//					textRegister.add(d.get("parentId"), d.get("id"));
				}
				++nrDocs;
				log.debug(d.get("id") + " >>> " + d.get("sentence"));
				log.info("--- Asynchronous Sents Reader");		
				
			}
	
			bufferedReader.close();
			isRunning = false;
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void setIgnoreId(boolean ignoreId) 
	{
		this.ignoreId = ignoreId;	
	}
}
