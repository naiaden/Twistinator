/**
 * 
 */
package nl.naiaden.twistinator;

import java.io.File;
import java.io.IOException;
import nl.naiaden.twistinator.indexer.Index;
import nl.naiaden.twistinator.indexer.input.AsynchronousCollectionReader;
import nl.naiaden.twistinator.indexer.input.AsynchronousSentsReader;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;



/**
 * @author louis
 *
 */
public class Test_ServerClient
{

	static
	{
		final org.apache.log4j.Logger rootLogger = org.apache.log4j.Logger.getRootLogger();
		if (!rootLogger.getAllAppenders().hasMoreElements())
		{
			rootLogger.setLevel(Level.DEBUG);
			rootLogger.addAppender(new ConsoleAppender(new PatternLayout("%d{ABSOLUTE} [%-11t] %x %-5p %c{1} - %m%n")));
		}
	}

	/**
	 * @param args
	 */
	public static void main(final String[] args)
	{
		//		Thread serverThread = new Thread()
		//		{
		//			@Override
		//			public void run()
		//			{
		//				try
		//				{
		//					TwistServer.main(args);
		//				} catch (Exception e)
		//				{
		//					// TODO Auto-generated catch block
		//					e.printStackTrace();
		//				}
		//			}
		//		};

//		AsynchronousSentsReader reader = new AsynchronousSentsReader(new File("/home/louis/Desktop/git/Twistinator/Indexer/tinyFile.txt"), new LinkedBlockingQueue<Document>());
//		Thread readerThread = new Thread(reader, "AsyncSentsReader");
//		readerThread.start();
		
		try
		{
//			Index index = new Index(new File("/tmp/indextest"), AsynchronousCollectionReader.class);
//			index.addToIndex(new File("doc/example.collection"));
			Index index = new Index(new File("/tmp/indextest"), AsynchronousSentsReader.class);
			index.addToIndex(new File("doc/example.sents"));
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		final Thread clientThread = new Thread()
//		{
//			@Override
//			public void run()
//			{
//				try
//				{
////					TwistClient.main(args);
//					AsynchronousSentsReader.();
//				} catch (final Exception e)
//				{
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		};

		//		serverThread.start();
		//		try
		//		{
		//			Thread.sleep(100);
		//		} catch (Exception e)
		//		{
		//			// TODO: handle exception
		//		}

//		clientThread.start();

		



	}

}
