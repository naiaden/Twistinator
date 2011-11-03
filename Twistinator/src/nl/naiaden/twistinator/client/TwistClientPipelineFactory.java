/**
 * 
 */
package nl.naiaden.twistinator.client;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.serialization.ObjectDecoder;
import org.jboss.netty.handler.codec.serialization.ObjectEncoder;

/**
 * @author louis
 *
 */
public class TwistClientPipelineFactory implements ChannelPipelineFactory
{

	static Logger log = Logger.getLogger(TwistClientPipelineFactory.class);

	public TwistClientPipelineFactory()
	{
	}

	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.ChannelPipelineFactory#getPipeline()
	 */
	@Override
	public ChannelPipeline getPipeline() throws Exception
	{
		ChannelPipeline pipeline = Channels.pipeline();
		pipeline.addLast("decoder", new ObjectDecoder());
		pipeline.addLast("encoder", new ObjectEncoder());

		pipeline.addLast("handler", new TwistClientHandler());

		return pipeline;
	}

}
