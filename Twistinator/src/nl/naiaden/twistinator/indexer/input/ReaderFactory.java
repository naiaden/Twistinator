/**
 * 
 */
package nl.naiaden.twistinator.indexer.input;

import java.io.File;
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
	
	public static Class<? extends Reader> toClass(String className)
	{
		if(className.equals(AsynchronousCollectionReader.class.getName()))
		{
			return AsynchronousCollectionReader.class;
		}
		if(className.equals(AsynchronousSentsReader.class.getName()))
		{
			return AsynchronousSentsReader.class;
		}
		
		new ClassNotFoundException("Cannot cast '" + className + "' to a Reader");
		return null;
	}
}


