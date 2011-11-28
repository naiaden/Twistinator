/**
 * 
 */
package nl.naiaden.twistinator.server;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import nl.naiaden.twistinator.indexer.Index;
import nl.naiaden.twistinator.objects.SearchQuery;
import nl.naiaden.twistinator.objects.ThankYouMessage;

import org.apache.log4j.Logger;
import org.apache.lucene.search.ScoreDoc;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelState;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

/**
 * @author louis
 *
 */
public class TwistServerHandler extends SimpleChannelUpstreamHandler
{
	static Logger log = Logger.getLogger(TwistServerHandler.class);

	private final AtomicLong transferredMessages = new AtomicLong();

	@Override
	public void exceptionCaught(
			final ChannelHandlerContext ctx, final ExceptionEvent e) {
		log.warn(
				"Unexpected exception from downstream.",
				e.getCause());
		e.getChannel().close();
	}

	public long getTransferredMessages() {
		return transferredMessages.get();
	}

	@Override
	public void handleUpstream(
			final ChannelHandlerContext ctx, final ChannelEvent e) throws Exception {
		if (e instanceof ChannelStateEvent &&
				((ChannelStateEvent) e).getState() != ChannelState.INTEREST_OPS) {
			log.info(e.toString());
		}
		super.handleUpstream(ctx, e);
	}

	@Override
	public void messageReceived(
			final ChannelHandlerContext ctx, final MessageEvent e) {
		// Echo back the received object to the client.
		if(e.getMessage() instanceof ThankYouMessage)
		{
			log.info("Incoming: Thanks for serving me");

			final ChannelFuture future = e.getChannel().close();
			future.addListener(new ChannelFutureListener()
			{
				@Override
				public void operationComplete(final ChannelFuture future)
				{
					log.info("post-closure");
				}
			});
		} if(e.getMessage() instanceof SearchQuery)
		{
			log.info("Incoming: " + ((SearchQuery) e.getMessage()).toString());


			SearchQuery sq = (SearchQuery) e.getMessage();
			transferredMessages.incrementAndGet();

			// process input
			try
			{
				Index index = new Index(new File("/tmp/twistinator"));
				ScoreDoc[] docs = index.searchIndex(sq, 1000);
				log.info("nr docs: " + docs.length);
			} catch (IOException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			log.info("Incoming: " + ((SearchQuery) e.getMessage()).toString());
			//			e.getChannel().write(new SearchResult("test"));
		}
	}
}
