/**
 * 
 */
package nl.naiaden.twistinator;

import java.util.Hashtable;
import java.util.Map;

import nl.naiaden.twistinator.indexer.document.Triple;

import org.apache.log4j.Logger;

/**
 * @author louis
 *
 */
public class ApplicationContext
{
	static Logger log = Logger.getLogger(ApplicationContext.class);
	
	private Map variables = new Hashtable();
	private boolean logTimes = false;
	
	public static enum ApplicationMode {create, add, delete, search};
	private ApplicationMode mode = ApplicationMode.search;
	
	private Triple triple;
	private String word;
	
	public ApplicationMode getMode()
	{
		return mode;
	}
	
	public Triple getTriple()
	{
		return triple;
	}
	
	public void setTriple(Triple triple)
	{
		this.triple = triple;
	}
	
	public String getWord()
	{
		return word;
	}
	
	public void setWord(String word)
	{
		this.word = word;
	}
	
	public void setMode(ApplicationMode mode)
	{
		this.mode = mode;
	}
	
	public Object getVariable(String name)
	{
		return variables.get(name);
	}
	
	public void setVariable(String name, Object value)
	{
		if(value == null)
		{
			variables.remove(name);
		} else
		{
			variables.put(name, value);
		}
	}

	/**
	 * @param logTimes the logTimes to set
	 */
	public void setLogTimes(boolean logTimes)
	{
		this.logTimes = logTimes;
	}

	/**
	 * @return the logTimes
	 */
	public boolean isLogTimes()
	{
		return logTimes;
	}
}
