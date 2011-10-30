package nl.naiaden.twistinator.indexer.document;

import java.io.Reader;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.AbstractField;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.Field.TermVector;

public class TriplesField extends AbstractField
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3169560810653331919L;

	public TriplesField()
	{
		// TODO Auto-generated constructor stub
	}

	public TriplesField(String name, Store store, Index index, TermVector termVector)
	{
		super(name, store, index, termVector);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Reader readerValue()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String stringValue()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TokenStream tokenStreamValue()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
