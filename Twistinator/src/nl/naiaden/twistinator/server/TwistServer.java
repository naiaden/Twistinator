/**
 * 
 */
package nl.naiaden.twistinator.server;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.serialization.ObjectDecoder;
import org.jboss.netty.handler.codec.serialization.ObjectEncoder;

/**
 * @author louis
 * 
 */
public class TwistServer
{
	static Logger log = Logger.getLogger(TwistServer.class);

	static
	{
		final org.apache.log4j.Logger rootLogger = org.apache.log4j.Logger.getRootLogger();
		if (!rootLogger.getAllAppenders().hasMoreElements())
		{
			rootLogger.setLevel(Level.DEBUG);
			rootLogger.addAppender(new ConsoleAppender(new PatternLayout("%d{ABSOLUTE} [%-11t] %x %-5p %c{1} - %m%n")));
		}
	}

	public static void main(final String[] args) throws Exception
	{
		final ServerBootstrap bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));

		// Set up the pipeline factory.
		bootstrap.setPipelineFactory(new ChannelPipelineFactory()
		{
			public ChannelPipeline getPipeline() throws Exception
			{
				return Channels.pipeline(new ObjectEncoder(), new ObjectDecoder(), new TwistServerHandler());
			}
		});

		// Bind and start to accept incoming connections.
		bootstrap.bind(new InetSocketAddress(8123));
	}
}
