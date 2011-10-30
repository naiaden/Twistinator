/**
 * 
 */
package nl.naiaden.twistinator;

import java.io.File;

import nl.naiaden.twistinator.indexer.Index;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 * Twistinator: "Triples and words, index and search tool"-inator
 * 
 * @author louis
 */
public class Application
{
	public static String applicationName = "Twistinator";

	public static String ApplicationVersion = "0.1";

	private static ApplicationContext applicationContext;

	/**
	 * Logger for this class
	 */
	private static final Logger log = Logger.getLogger(Application.class);

	/**
	 * The application singleton
	 */
	private static Application theApplication;

	/*
	 * This can be overruled by a JVM argument, e.g.:
	 * -Dlog4j.configuration=file:configuration/log4j-local.xml where the
	 * configuration file is in the sub directory configuration of the project
	 */
	static
	{
		org.apache.log4j.Logger rootLogger = org.apache.log4j.Logger.getRootLogger();
		if (!rootLogger.getAllAppenders().hasMoreElements())
		{
			rootLogger.setLevel(Level.DEBUG);
			rootLogger.addAppender(new ConsoleAppender(new PatternLayout("%d{ABSOLUTE} [%-11t] %x %-5p %c{1} - %m%n")));
		}
	}

	/**
	 * @return The application singleton
	 */
	public static Application getApplication()
	{
		if (Application.theApplication == null)
		{
			Application.theApplication = new Application();
		}
		return Application.theApplication;
	}

	public static void logTiming(Logger logger, String s)
	{
		if (applicationContext.isLogTimes())
		{
			logger.info(s);
		}
	}

	/**
	 * The indexer main function
	 * 
	 * @param args
	 *            The command line arguments
	 */
	public static void main(String[] args)
	{
		Application.getApplication().init();

		CommandLineParser cmdLineParser = new CommandLineParser();
		applicationContext = cmdLineParser.invokeCommandLine(args);

		try
		{
			Index index = new Index(new File((String) applicationContext.getVariable("index")));
			File file;

			switch (applicationContext.getMode())
			{
			case create:
				file = new File((String) applicationContext.getVariable("file"));
				index.createIndex(file);
				break;
			case add:
				file = new File((String) applicationContext.getVariable("file"));
				index.addToIndex(file);
				break;
			case delete:
				index.removeIndex();
				break;
			case search:
				int numberOfResults = (Integer) applicationContext.getVariable("number");
				if (applicationContext.getTriple() != null)
				{
					index.searchIndexForTriple(applicationContext.getTriple(), numberOfResults);
				} else
					// word is default
				{
					index.searchIndexForWord(applicationContext.getWord(), numberOfResults);
				}
				break;
			default:
				log.error("Unknown application mode");
				break;
			}

			index.close();
		} catch (Exception e)
		{
			log.error("Encountered error: " + e.getMessage());
			e.printStackTrace(System.err);
		}

		Application.getApplication().deinit();
	}

	/**
	 * 
	 * @param newMessage
	 */
	public static synchronized void traceMessage(String newMessage)
	{
		log.debug(newMessage);
	}

	/**
	 * 
	 */
	private Application()
	{

	}

	private void deinit()
	{
		log.info("Stopping " + applicationName);
	}

	private void init()
	{
		Thread.currentThread().setName("Application");
		log.info("Starting " + applicationName);
	}
}
