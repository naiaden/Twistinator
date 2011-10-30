/**
 * 
 */
package nl.naiaden.twistinator;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;

import nl.naiaden.twistinator.indexer.document.Triple;

import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

/**
 * @author louis
 *
 */
public class CommandLineParser
{
	static Logger log = Logger.getLogger(CommandLineParser.class);
	
	private Options cmdLineOptions;
	
	public ApplicationContext invokeCommandLine(String[] args)
	{
		log.debug("Commandline arguments: " + Arrays.toString(args));
		
		try
		{
			parseCommandLineOptions(args);
		} catch (ParseException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		final org.apache.commons.cli.CommandLineParser cmdLineParser = new GnuParser();
		
		org.apache.commons.cli.CommandLine cl = null;
		try
		{
			cl = cmdLineParser.parse(cmdLineOptions, args, false);
		} catch (ParseException pe)
		{
			log.error("Encountered exception whilst parsing commandline arguments:\n" + 
					pe.getMessage());
			printHelp(80, "Help for indexer", "end of help", 3, 5, true, System.err);
			System.exit(1);
		}
		
		if(cl.hasOption("help"))
		{
			printHelp(80, "Help for indexer", "end of help", 3, 5, true, System.out);
			System.exit(1);
		}
		
		if(cl.hasOption("version"))
		{
			System.err.println("Twistinator " + Application.ApplicationVersion);
			System.err.println("Comments, questions and feedback are welcome at");
			System.err.println("    l.onrust@student.science.ru.nl");
			System.err.println("");
			System.exit(1);
		}
		
		ApplicationContext context = new ApplicationContext();
		
		if(cl.hasOption("index"))
		{
			context.setVariable("index", cl.getOptionValue("index"));
		}
		if(cl.hasOption("file"))
		{
			context.setVariable("file", cl.getOptionValue("file"));
		}
		
		if(cl.hasOption("create"))
		{
			context.setMode(ApplicationContext.ApplicationMode.create);
		} else if(cl.hasOption("delete"))
		{
			context.setMode(ApplicationContext.ApplicationMode.delete);
		} else if(cl.hasOption("add"))
		{
			context.setMode(ApplicationContext.ApplicationMode.add);
		} else // search is default
		{
			context.setMode(ApplicationContext.ApplicationMode.search);
			if(cl.hasOption("triple"))
			{
				context.setTriple(new Triple(cl.getOptionValue("triple")));
			} else if	(cl.hasOption("word"))
			{
				context.setWord(cl.getOptionValue("word"));
			} 
			
			if(cl.hasOption("number"))
			{
				int numberOfResults;
				try
				{
					numberOfResults = Integer.parseInt(cl.getOptionValue("number"));
					if(numberOfResults < 1) 
					{
						throw new Exception("Invalid number argument");
					}
					context.setVariable("number", numberOfResults);
				} catch (Exception e)
				{
					log.error("Argument of number is invalid: use an integer > 0");
					System.exit(-1);
				}
			} else
			{
				context.setVariable("number", 50);
			}
		}
		
		return context;
	}
	
 public void parseCommandLineOptions(String[] args) throws ParseException {
	cmdLineOptions = new Options();	
	 
	 // Input
//		cmdLineOptions(OptionBuilder.hasArg(true).isRequired(false).withLongOpt("input").withArgName("file").withDescription("input file").create("f"));
	 cmdLineOptions.addOption(OptionBuilder.hasArg(true).isRequired(true).withLongOpt("index").withArgName("index").withDescription("index directory").create("i"));
		
		// Index
		OptionGroup indexOptionGroup = new OptionGroup();
		indexOptionGroup.setRequired(true);
		indexOptionGroup.addOption(OptionBuilder.hasArg(true).withArgName("input").withLongOpt("create").withDescription("create an index").create("c"));
		indexOptionGroup.addOption(OptionBuilder.hasArg(true).withArgName("input").withLongOpt("add").withDescription("add to index").create("a"));
		indexOptionGroup.addOption(OptionBuilder.hasArg(true).withArgName("input").withLongOpt("delete").withDescription("delete an index").create("d"));
		indexOptionGroup.addOption(OptionBuilder.hasArg(false).withLongOpt("search").withDescription("search the index").create("s"));
		cmdLineOptions.addOptionGroup(indexOptionGroup);
		
		// Searching
		OptionGroup searchOptionGroup = new OptionGroup();
		searchOptionGroup.addOption(OptionBuilder.hasArg(true).withArgName("word").isRequired(true).withLongOpt("word").withDescription("search the index for words").create("w"));
		searchOptionGroup.addOption(OptionBuilder.hasArg(true).withArgName("triple").isRequired(true).withLongOpt("triple").withDescription("search the index for triples").create("t"));
		cmdLineOptions.addOptionGroup(searchOptionGroup);
		cmdLineOptions.addOption(OptionBuilder.hasArg(true).withArgName("number").withLongOpt("number").withDescription("find number results").create("n"));
		
//		searchOptionGroup.
		
		// Output
		cmdLineOptions.addOption("time", false, "time the operations");
		
		// Program
		cmdLineOptions.addOption("h", "help", false, "print this message");
		cmdLineOptions.addOption("v", "verbose", false, "be extra verbose");
		cmdLineOptions.addOption("version", false, "print the version information and exit");
     }
 
	/**
	 * Write help information to the provided OutputStream
	 * @param printedRowWidth
	 * @param header
	 * @param footer
	 * @param spacesBeforeOption
	 * @param spacesBeforeOptionDescription
	 * @param displayUsage
	 * @param out
	 */
	private void printHelp(final int printedRowWidth,
			final String header,
			final String footer,
			final int spacesBeforeOption,
			final int spacesBeforeOptionDescription,
			final boolean displayUsage,
			final OutputStream out)
	{
		final String commandLineSyntax = "java -cp Application.jar";
		final PrintWriter writer = new PrintWriter(out);
		final HelpFormatter helpFormatter = new HelpFormatter();
		helpFormatter.printHelp(
				writer,
				printedRowWidth,
				commandLineSyntax,
				header,
				cmdLineOptions,
				spacesBeforeOption,
				spacesBeforeOptionDescription,
				footer,
				displayUsage);
		writer.close();
	}
	
	/**
	 * Print usage information to provided OutputStream
	 * @param out
	 */
	private void printUsage(final OutputStream out)
	{
		final PrintWriter writer = new PrintWriter(out);
		final HelpFormatter usageFormatter = new HelpFormatter();
		usageFormatter.printUsage(writer, 80, Application.applicationName, cmdLineOptions);
		writer.close();
	}
	
}
