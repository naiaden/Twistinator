/**
 * 
 */
package nl.naiaden.twistinator.indexer.input;

import java.io.File;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

import org.apache.lucene.document.Document;

/**
 * @author louis
 *
 */
public abstract class ReaderFactory
{
	public static Reader create(Class<? extends Reader> readerClass, File file, BlockingQueue<Document> documentQueue)
	{
		if(readerClass.equals(AsynchronousCollectionReader.class))
		{
			return new AsynchronousCollectionReader(file, documentQueue);
		}
		if(readerClass.equals(AsynchronousSentsReader.class))
		{
			return new AsynchronousSentsReader(file, documentQueue);
		}
		
		new ClassNotFoundException("Cannot create for: " + readerClass.toString());
		return null;
	}
}

