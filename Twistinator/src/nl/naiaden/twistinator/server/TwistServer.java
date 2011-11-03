/**
 * 
 */
package nl.naiaden.twistinator.server;

import java.net.InetSocketAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.execution.OrderedMemoryAwareThreadPoolExecutor;

/**
 * @author louis
 *
 */
public class TwistServer
{
	static Logger log = Logger.getLogger(TwistServer.class);

	/**
	 * Take two arguments :<br>
	 * -port to listen to<br>
	 * -nb of connections before shutting down
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// Print usage if no argument is specified.

		// Parse options.
		int port = 8125;
		int nbconn = 20;

		// *** Start the Netty configuration ***

		// Start server with Nb of active threads = 2*NB CPU + 1 as maximum.
		ChannelFactory factory = new NioServerSocketChannelFactory(Executors
				.newCachedThreadPool(), Executors.newCachedThreadPool(),
				Runtime.getRuntime().availableProcessors() * 2 + 1);

		ServerBootstrap bootstrap = new ServerBootstrap(factory);
		// Create the global ChannelGroup
		ChannelGroup channelGroup = new DefaultChannelGroup(
				TwistServer.class.getName());
		// Create the blockingQueue to wait for a limited number of client
		BlockingQueue<Integer> answer = new LinkedBlockingQueue<Integer>();
		// 200 threads max, Memory limitation: 1MB by channel, 1GB global, 100 ms of timeout
		OrderedMemoryAwareThreadPoolExecutor pipelineExecutor = new OrderedMemoryAwareThreadPoolExecutor(
				200, 1048576, 1073741824, 100, TimeUnit.MILLISECONDS, Executors
				.defaultThreadFactory());

		bootstrap.setPipelineFactory(new TwistPipelineFactory(channelGroup,
				pipelineExecutor, answer, nbconn));
		bootstrap.setOption("child.tcpNoDelay", true);
		bootstrap.setOption("child.keepAlive", true);
		bootstrap.setOption("child.reuseAddress", true);
		bootstrap.setOption("child.connectTimeoutMillis", 100);
		bootstrap.setOption("readWriteFair", true);

		// *** Start the Netty running ***

		// Create the monitor
		ThroughputMonitor monitor = new ThroughputMonitor();

		// Add the parent channel to the group
		Channel channel = bootstrap.bind(new InetSocketAddress(port));
		channelGroup.add(channel);

		// Starts the monitor
		monitor.start();

		// Wait for the server to stop
		answer.take();

		// *** Start the Netty shutdown ***

		// End the monitor
		System.out.println("End of monitor");
		monitor.interrupt();
		// Now close all channels
		System.out.println("End of channel group");
		channelGroup.close().awaitUninterruptibly();
		// Close the executor for Pipeline
		System.out.println("End of pipeline executor");
		pipelineExecutor.shutdownNow();
		// Now release resources
		System.out.println("End of resources");
		factory.releaseExternalResources();
	}
}
