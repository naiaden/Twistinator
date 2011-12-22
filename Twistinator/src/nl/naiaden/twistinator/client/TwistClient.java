/**
 * 
 */
package nl.naiaden.twistinator.client;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import nl.naiaden.twistinator.indexer.document.Keyword;
import nl.naiaden.twistinator.indexer.document.Relator;
import nl.naiaden.twistinator.indexer.document.Triple;
import nl.naiaden.twistinator.objects.SearchQuery;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

/**
 * @author louis
 *
 */
public class TwistClient
{
	static Logger log = Logger.getLogger(TwistClient.class);

	static
	{
		final org.apache.log4j.Logger rootLogger = org.apache.log4j.Logger.getRootLogger();
		if (!rootLogger.getAllAppenders().hasMoreElements())
		{
			rootLogger.setLevel(Level.DEBUG);
			rootLogger.addAppender(new ConsoleAppender(new PatternLayout("%d{ABSOLUTE} [%-11t] %x %-5p %c{1} - %m%n")));
		}
	}

	public static void main(final String[] args) throws Exception {

		// Parse options.
		final String host = "localhost";
		final int port = 8123;


		// Configure the client.
		ClientBootstrap bootstrap = new ClientBootstrap(
				new NioClientSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool()));


		// Set up the pipeline factory.
		bootstrap.setPipelineFactory(new TwistClientPipelineFactory());

		// Start the connection attempt.
		ChannelFuture connectFuture = bootstrap.connect(new InetSocketAddress(host, port));
		Channel channel = connectFuture.awaitUninterruptibly().getChannel();

		TwistClientHandler handler = (TwistClientHandler) channel.getPipeline().getLast();
		//		handler.addQuery(new SearchDocumentId(new DocumentId("e4")));
		//		handler.addQuery(new SearchDocumentId(new ParentId("doc3")));
		//		handler.addQuery(new SearchQuery(new Triple(new Keyword("assembly"), new Relator("DET"), new Keyword("a"))));
		handler.addQuery(new SearchQuery(new Triple(new Keyword("assembly"), new Relator("DET"), new Keyword("?"))));
		//		[network,ATTR,associated]
		log.info("Result: " + handler.getMessage().toString());

		handler.done = true;

		//		if()

		bootstrap.releaseExternalResources();
	}

}
