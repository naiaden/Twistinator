/**
 * 
 */
package nl.naiaden.twistinator.indexer.input;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import nl.naiaden.twistinator.indexer.Index;
import nl.naiaden.twistinator.indexer.document.Triple;
import nl.naiaden.twistinator.indexer.document.Triples;

/**
 * @author louis
 * 
 */
public class Sent
{
	public static final String headerRegex = "^# \\(null\\) (\\d+) (\\d+)-(\\d+)\\|(.*)$";

	private String header;
	private Triples triples;
	private int parentDocument;

	/**
	 * 
	 * @param header
	 * @param triples
	 * @param parentDocument
	 */
	public Sent(String header, Triples triples, int parentDocument)
	{
		this.header = header;
		this.triples = triples;
		this.parentDocument = parentDocument;
	}

	/**
	 * 
	 * @param header
	 * @param triples
	 */
	public Sent(String header, Triples triples)
	{
		this.header = header;
		this.triples = triples;
		this.parentDocument = -1;
	}

	public Sent()
	{
		this.header = new String();
		this.triples = new Triples();
		this.parentDocument = -1;
	}

	/**
	 * @param header
	 *            the header to set
	 */
	public void setHeader(String header)
	{
		this.header = header;
	}

	/**
	 * @return the header
	 */
	public String getHeader()
	{
		return header;
	}

	/**
	 * @param triples
	 *            the triples to set
	 */
	public void setTriples(Triples triples)
	{
		this.triples = triples;
	}

	/**
	 * @return the triples
	 */
	public Triples getTriples()
	{
		return triples;
	}

	/**
	 * @param parentDocument
	 *            the parentDocument to set
	 */
	public void setParentDocument(int parentDocument)
	{
		this.parentDocument = parentDocument;
	}

	/**
	 * @return the parentDocument
	 */
	public int getParentDocument()
	{
		return parentDocument;
	}

	public void addTriple(Triple triple)
	{
		triples.add(triple);
	}

	@Override
	public String toString()
	{
		StringBuilder s = new StringBuilder();
		String newLine = System.getProperty("line.separator");

		s.append("Header: " + header + newLine);
		for (Triple triple : triples)
		{
			s.append("Triple: " + triple.toString() + newLine);
		}

		return s.toString();
	}
	
	/*
	 * # (null) x y-z|Dit is een testzin. # parsing 1 time 0.006 penalty 3
	 * posmemo: known 323, blocked 1502, unknown 13066 [N:C:testzin,DET,een]
	 * [N:C:testzin,NP,sgthirdacc] [P:dit,NP,sgthirdnom] [P:dit,SUBJ,V:zijn]
	 * [V:zijn,PRED,N:C:testzin] [V:zijn,VP,sgthird]
	 * 
	 * x = id y = ? z = #parses?
	 */
	public Document toDocument()
	{
		Document doc = new Document();

		Pattern headerPattern = Pattern.compile(Sent.headerRegex);
		Matcher headerMatcher = headerPattern.matcher(header);

		while (headerMatcher.find())
		{
			// for(int i=0; i <= headerMatcher.groupCount(); ++i)
			// {
			// System.out.println(headerMatcher.group(i));
			// }

			// headerMatcher.group(0) is the complete sentence header
			doc.add(new Field(Index.FIELD_ID, headerMatcher.group(1), Field.Store.YES, Field.Index.NOT_ANALYZED));
			// doc.add(new Field("id", headerMatcher.group(2), Field.Store.YES,
			// Field.Index.NOT_ANALYZED));
			doc.add(new Field(Index.FIELD_NRPARSES, headerMatcher.group(3), Field.Store.YES, Field.Index.NOT_ANALYZED));
			doc.add(new Field(Index.FIELD_SENTENCE, headerMatcher.group(4), Field.Store.YES, Field.Index.ANALYZED)); // NO
		}

		doc.add(new Field(Index.FIELD_TRIPLES, triples.toString(), Field.Store.YES, Field.Index.ANALYZED)); // NA

//		log.debug(doc.get("id") + " +++ " + sent.getTriples().toString());

		return doc;
	}
}
