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
	public static enum ApplicationMode {add, create, delete, search}
	
	static Logger log = Logger.getLogger(ApplicationContext.class);
	private boolean logTimes = false;
	
	private ApplicationMode mode = ApplicationMode.search;;
	private Triple triple;
	
	private Map variables = new Hashtable();
	private String word;
	
	public ApplicationMode getMode()
	{
		return mode;
	}
	
	public Triple getTriple()
	{
		return triple;
	}
	
	public Object getVariable(String name)
	{
		return variables.get(name);
	}
	
	public String getWord()
	{
		return word;
	}
	
	/**
	 * @return the logTimes
	 */
	public boolean isLogTimes()
	{
		return logTimes;
	}
	
	/**
	 * @param logTimes the logTimes to set
	 */
	public void setLogTimes(boolean logTimes)
	{
		this.logTimes = logTimes;
	}
	
	public void setMode(ApplicationMode mode)
	{
		this.mode = mode;
	}
	
	public void setTriple(Triple triple)
	{
		this.triple = triple;
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

	public void setWord(String word)
	{
		this.word = word;
	}
}
