/**
 * 
 */
package nl.naiaden.twistinator.server;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.group.ChannelGroup;

/**
 * @author louis
 *
 */
public class TwistServerHandler extends SimpleChannelHandler
{
	static Logger log = Logger.getLogger(TwistServerHandler.class);

	/**
	 * Is there any Pong message to send
	 */
	private final AtomicInteger isPong = new AtomicInteger(0);

	/**
	 * Bytes monitor
	 */
	public static final AtomicLong transferredBytes = new AtomicLong();

	/**
	 * Returns the number of transferred bytes
	 * @return the number of transferred bytes
	 */
	public static long getTransferredBytes() {
		return transferredBytes.get();
	}

	/**
	 * Pong object
	 */
	private PingPong pp;

	/**
	 * Channel Group
	 */
	private ChannelGroup channelGroup = null;

	/**
	 * Constructor
	 * @param channelGroup
	 */
	public TwistServerHandler(ChannelGroup channelGroup) {
		this.channelGroup = channelGroup;
	}

	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.SimpleChannelHandler#channelConnected(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.ChannelStateEvent)
	 */
	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		channelGroup.add(ctx.getChannel());
	}

	/**
	 * If write of Pong was not possible before, just do it now
	 */
	@Override
	public void channelInterestChanged(ChannelHandlerContext ctx,
			ChannelStateEvent e) {
		generatePongTraffic(e);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
		log.warn("Unexpected exception from downstream.", e
				.getCause());
		Channels.close(e.getChannel());
	}

	/**
	 * When a Ping message is received, send a new Pong
	 */
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
		PingPong pptmp = (PingPong) e.getMessage();
		if (pptmp != null) {
			pp = pptmp;
			log.info(pp.toString());
			TwistServerHandler.transferredBytes.addAndGet(pp.status.length +
					pp.test1.length() + 16);
			isPong.incrementAndGet();
			generatePongTraffic(e);
		}
	}

	/**
	 * Used when write is possible
	 * @param e
	 */
	private void generatePongTraffic(ChannelStateEvent e) {
		if (isPong.intValue() > 0) {
			Channel channel = e.getChannel();
			sendPongTraffic(channel);
		}
	}

	/**
	 * Used when a Ping message is received
	 * @param e
	 */
	private void generatePongTraffic(MessageEvent e) {
		if (isPong.intValue() > 0) {
			Channel channel = e.getChannel();
			sendPongTraffic(channel);
		}
	}

	/**
	 * Truly send the Pong
	 * @param channel
	 */
	private void sendPongTraffic(Channel channel) {
		if ((channel.getInterestOps() & Channel.OP_WRITE) == 0) {
			pp.rank ++;
			isPong.decrementAndGet();
			Channels.write(channel, pp);
		}
	}
}
