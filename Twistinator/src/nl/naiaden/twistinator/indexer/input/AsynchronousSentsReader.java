/**
 * 
 */
package nl.naiaden.twistinator.indexer.input;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.concurrent.BlockingQueue;

import nl.naiaden.twistinator.indexer.document.Triple;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.lucene.document.Document;

/**
 * @author louis
 *
 */
public class AsynchronousSentsReader implements Runnable
{
	static Logger log = Logger.getLogger(AsynchronousSentsReader.class);
	
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
	
	public AsynchronousSentsReader(File file, BlockingQueue<Document> documentQueue)
	{
		this.file = file;
		this.documentQueue = documentQueue;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
		log.info("+++ Asynchronous Sents Reader");
		
		try
		{

		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String bufferedLine = bufferedReader.readLine();

		Sent sent = new Sent();

		if (bufferedLine.startsWith("# (null)"))
		{
			sent.setHeader(bufferedLine);

			while ((bufferedLine = bufferedReader.readLine()) != null)
			{
				if (!StringUtils.isBlank(bufferedLine))
				{
					if (bufferedLine.startsWith("# (null)"))
					{
						Document d = sent.toDocument();
						documentQueue.put(d);
						if(++nrDocs % 10000 == 0)
						{
							log.info("AsyncReader: " + nrDocs);
						}
//						log.debug(d.get("id") + " >>> " + d.get("sentence"));
						sent = new Sent();
						sent.setHeader(bufferedLine);
					} else if (!bufferedLine.startsWith("# Error:") && !bufferedLine.startsWith("UNKN:"))
					{
						sent.addTriple(new Triple(bufferedLine));
					}
				}
			}

			Document d = sent.toDocument();
			documentQueue.put(d); ++nrDocs;
//			log.debug(d.get("id") + " >>> " + d.get("sentence"));
			log.info("--- Asynchronous Sents Reader");		
			
		}

		bufferedReader.close();
		isRunning = false;
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
