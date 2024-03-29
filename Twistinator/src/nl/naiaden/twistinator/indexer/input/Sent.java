/**
 * 
 */
package nl.naiaden.twistinator.indexer.input;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.naiaden.twistinator.indexer.Index;
import nl.naiaden.twistinator.indexer.document.Triple;
import nl.naiaden.twistinator.indexer.document.Triples;
import nl.naiaden.twistinator.objects.Returnable;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

/**
 * @author louis
 * 
 */
public class Sent implements Returnable
{
	static Logger log = Logger.getLogger(Sent.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -8400710086703648175L;

	public static final String headerRegex = "^# \\(null\\) (\\w+) (\\d+)-(\\d+)\\|(.*)$";

	private String id;
	private String sentence;

	private Triples triples;

	private String parentDocument;
	public Sent()
	{
		id = null;
		sentence = null;
		triples = new Triples();
		parentDocument = "";
	}
	public Sent(Document doc)
	{
		id = doc.get("id");
		parentDocument = doc.get("parentId");
		sentence = doc.get("sentence");

		//		triples = new Triples();
		triples = Triples.fromString(doc.get("triples"));
		// triples = doc.get("triples").fromString();
		//		Pattern tripleSplitter = Triple.tripleSplitter;
	}

	public Sent(String id, String sentence, Triples triples, String parentDocument)
	{
		this.id = id;
		this.parentDocument = parentDocument;
		this.sentence = sentence;
		this.triples = triples;
	}

	/**
	 * 
	 * @param header
	 * @param triples
	 */
	public Sent(String header, Triples triples)
	{
		this(header,triples,null);
	}

	/**
	 * 
	 * @param header
	 * @param triples
	 * @param parentDocument
	 */
	public Sent(String header, Triples triples, String parentDocument)
	{
		processHeader(header);
		this.triples = triples;
		this.parentDocument = parentDocument;
	}

	public void addTriple(Triple triple)
	{
		triples.add(triple);
	}

	public String getId() {
		return id;
	}

	/**
	 * @return the parentDocument
	 */
	public String getParentDocument()
	{
		return parentDocument;
	}

	public String getSentence()
	{
		return sentence;
	}

	/**
	 * @return the triples
	 */
	public Triples getTriples()
	{
		return triples;
	}

	public void processHeader(String header) {
		Pattern headerPattern = Pattern.compile(Sent.headerRegex);
		Matcher headerMatcher = headerPattern.matcher(header);
		while (headerMatcher.find())
		{
			id = headerMatcher.group(1);
			sentence = headerMatcher.group(4);
		}

	}

	/**
	 * @param header
	 *            the header to set
	 */
	public void setHeader(String header)
	{
		processHeader(header);
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @param parentDocument
	 *            the parentDocument to set
	 */
	public void setParentDocument(String parentDocument)
	{
		this.parentDocument = parentDocument;
	}

	public void setSentence(String sentence)
	{
		this.sentence = sentence;
	}

	/**
	 * @param triples
	 *            the triples to set
	 */
	public void setTriples(Triples triples)
	{
		this.triples = triples;
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

		doc.add(new Field(Index.FIELD_ID, id, Field.Store.YES, Field.Index.NOT_ANALYZED));
		doc.add(new Field(Index.FIELD_PARENTID, parentDocument, Field.Store.YES, Field.Index.NOT_ANALYZED));
		doc.add(new Field(Index.FIELD_SENTENCE, sentence, Field.Store.YES, Field.Index.ANALYZED)); // NO
		doc.add(new Field(Index.FIELD_TRIPLES, triples.toString(), Field.Store.YES, Field.Index.ANALYZED)); // NA

		//		log.debug(doc.get("id") + " +++ " + sent.getTriples().toString());

		return doc;
	}

	@Override
	public String toString()
	{
		StringBuilder s = new StringBuilder();
		String newLine = System.getProperty("line.separator");

		s.append("[" + id + "/" + parentDocument + "] " + sentence + newLine);
		for (Triple triple : triples)
		{
			s.append(triple.toString() + newLine);
		}

		return s.toString();
	}
}
