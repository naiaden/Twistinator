/**
 * 
 */
package nl.naiaden.twistinator.indexer.document;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class represents the dependency triple. Triples are based on binary
 * relations between two keywords: head and modifier. These are therefore also
 * called head-modifier pairs. The head is modified by the modifier through the
 * relation, described by a relator.
 * 
 * @author louis
 * 
 */
public class Triple
{
	public static final Pattern dupiraTripleRegex = Pattern.compile("\\[([a-zA-Z0-9()+'%/: -.#]+),(\\w+),([a-zA-Z0-9()/+'%: -.#]+)\\]"); // "[(.*?),(.*?),(.*?)]";

	/**
	 * The internal triple representation can be split into its fields using
	 * this regular expression
	 */
	public static final Pattern tripleSplitter = Pattern.compile("> <|^<|>$"); // "\\] \\[|^\\[|\\]$";

	private Keyword head;
	private Relator relator;
	private Keyword modifier;

	/**
	 * Generates a triple
	 * 
	 * @param head
	 * @param relator
	 * @param modifier
	 */
	public Triple(Keyword head, Relator relator, Keyword modifier)
	{
		this.head = head;
		this.relator = relator;
		this.modifier = modifier;
	}

	/**
	 * Generates a triple if the input <code>s</code> corresponds to an existing
	 * regex pattern
	 * 
	 * @param s
	 */
	public Triple(String s)
	{
		Pattern triplePattern = dupiraTripleRegex;
		Matcher tripleMatcher = triplePattern.matcher(s);

		while (tripleMatcher.find())
		{
			// for(int i=0; i <= tripleMatcher.groupCount(); ++i)
			// {
			// System.out.println(">" + i + tripleMatcher.group(i) + "<");
			// }
			this.head = new Keyword(tripleMatcher.group(1));
			this.relator = new Relator(tripleMatcher.group(2));
			this.modifier = new Keyword(tripleMatcher.group(3));
		}
	}

	/**
	 * 
	 * @return the head
	 */
	public Keyword getLeft()
	{
		return head;
	}

	/**
	 * 
	 * @param keyword
	 *            the head
	 */
	public void setLeft(Keyword keyword)
	{
		head = keyword;
	}

	/**
	 * 
	 * @return relator
	 */
	public Relator getMiddle()
	{
		return relator;
	}

	/**
	 * 
	 * @param relator
	 *            the relation
	 */
	public void setMiddle(Relator relator)
	{
		this.relator = relator;
	}

	/**
	 * 
	 * @return modifier
	 */
	public Keyword getRight()
	{
		return modifier;
	}

	/**
	 * 
	 * @param keyword
	 *            the modifier
	 */
	public void setRight(Keyword keyword)
	{
		modifier = keyword;
	}

	/**
	 * Note that a triple is marked with angled brackets. This is in order to prevent the indexers
	 * from interpreting the bracket: the square brackets are to denote a range in Lucene for
	 * example.
	 */
	@Override
	public String toString()
	{
		StringBuilder s = new StringBuilder();
		
		if(head != null && relator != null && modifier != null)
		{
			s.append("<" + head.toString() + "," + relator.toString() + "," + modifier.toString() + ">");
		} else
		{
			s.append("");
		}

		return s.toString();
	}
}
