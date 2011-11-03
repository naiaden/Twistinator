/**
 * 
 */
package nl.naiaden.twistinator.client;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import nl.naiaden.twistinator.server.PingPong;

import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

/**
 * @author louis
 *
 */
public class TwistClient
{

	static Logger log = Logger.getLogger(TwistClient.class);

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{


		// Parse options.
		int port = 8125;
		int nbMessage = 256;


		int size = 16384;

		// *** Start the Netty configuration ***

		// Start client with Nb of active threads = 3 as maximum.
		ChannelFactory factory = new NioClientSocketChannelFactory(Executors
				.newCachedThreadPool(), Executors.newCachedThreadPool(), 3);
		// Create the bootstrap
		ClientBootstrap bootstrap = new ClientBootstrap(factory);
		// Create the global ChannelGroup
		ChannelGroup channelGroup = new DefaultChannelGroup(
				TwistClient.class.getName());
		// Create the associated Handler
		TwistClientHandler handler = new TwistClientHandler(nbMessage, size);

		// Add the handler to the pipeline and set some options
		bootstrap.getPipeline().addLast("handler", handler);
		bootstrap.setOption("tcpNoDelay", true);
		bootstrap.setOption("keepAlive", true);
		bootstrap.setOption("reuseAddress", true);
		bootstrap.setOption("connectTimeoutMillis", 100);

		// *** Start the Netty running ***

		// Connect to the server, wait for the connection and get back the channel
		Channel channel = bootstrap.connect(new InetSocketAddress("localhost", port))
				.awaitUninterruptibly().getChannel();
		// Add the parent channel to the group
		channelGroup.add(channel);
		// Wait for the PingPong to finish
		PingPong pingPong = handler.getPingPong();
		log.info("Result: " + pingPong.toString() + " for 2x" +
				nbMessage + " messages and " + size +
				" bytes as size of array");

		// *** Start the Netty shutdown ***

		// Now close all channels
		log.info("close channelGroup");
		channelGroup.close().awaitUninterruptibly();
		// Now release resources
		log.info("close external resources");
		factory.releaseExternalResources();

	}

}
