/**
 * 
 */
package nl.naiaden.twistinator.server;

import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.handler.codec.serialization.ObjectDecoder;
import org.jboss.netty.handler.codec.serialization.ObjectEncoder;
import org.jboss.netty.handler.execution.OrderedMemoryAwareThreadPoolExecutor;

/**
 * @author louis
 * 
 */
public class TwistPipelineFactory implements ChannelPipelineFactory
{
	static Logger log = Logger.getLogger(TwistPipelineFactory.class);
	private ChannelGroup channelGroup = null;

	private OrderedMemoryAwareThreadPoolExecutor pipelineExecutor = null;

	private BlockingQueue<Integer> answer = null;

	private int max = 100; // default is 100 max connections

	/**
	 * Constructor
	 * 
	 * @param channelGroup
	 * @param pipelineExecutor
	 * @param answer
	 * @param max
	 *            max connection
	 */
	public TwistPipelineFactory(ChannelGroup channelGroup, OrderedMemoryAwareThreadPoolExecutor pipelineExecutor, BlockingQueue<Integer> answer, int max)
	{
		super();
		this.channelGroup = channelGroup;
		this.pipelineExecutor = pipelineExecutor;
		this.answer = answer;
		this.max = max;
	}

	/**
	 * Initiate the Pipeline for the newly active connection with ObjectXxcoder.
	 * 
	 * @see org.jboss.netty.channel.ChannelPipelineFactory#getPipeline()
	 */
	public ChannelPipeline getPipeline() throws Exception
	{
		if (max == 0)
		{
			// stop globally
			answer.add(new Integer(0));
			throw new Exception("End of server");
		}
		ChannelPipeline pipeline = Channels.pipeline();
		pipeline.addLast("decoder", new ObjectDecoder());
		pipeline.addLast("encoder", new ObjectEncoder());

		pipeline.addLast("handler", new TwistServerHandler());
		max--;
		System.out.println("Continue... " + max);
		return pipeline;
	}
}
