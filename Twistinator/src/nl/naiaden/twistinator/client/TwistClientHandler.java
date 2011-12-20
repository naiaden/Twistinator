/**
 * 
 */
package nl.naiaden.twistinator.client;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

import nl.naiaden.twistinator.objects.SearchQuery;
import nl.naiaden.twistinator.objects.SearchResult;
import nl.naiaden.twistinator.objects.ThankYouMessage;

import org.apache.log4j.Logger;
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
public class TwistClientHandler extends SimpleChannelUpstreamHandler
{
	static Logger log = Logger.getLogger(TwistClientHandler.class);

	private final AtomicLong transferredMessages = new AtomicLong();
	final BlockingQueue<SearchResult> answer = new LinkedBlockingQueue<SearchResult>(1);
	final BlockingQueue<SearchQuery> queries = new LinkedBlockingQueue<SearchQuery>(1);

	public boolean done = true;

	/**
	 * Creates a client-side handler.
	 */
	public TwistClientHandler()
	{

	}

	public void addQuery(SearchQuery query)
	{
		queries.add(query);
		log.debug("Query added: " + query.toString());
	}

	@Override
	public void channelConnected(final ChannelHandlerContext ctx, final ChannelStateEvent e)
	{
		// Send the first message if this handler is a client-side handler.
		// e.getChannel().write(new SearchQuery(new Triple(new Keyword("assembly"), new Relator("DET"), new Keyword("a"))));
		try
		{
			SearchQuery query = queries.take();
			if(query != null)
			{
				e.getChannel().write(query);
			}
		} catch (InterruptedException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Override
	public void exceptionCaught(final ChannelHandlerContext ctx, final ExceptionEvent e)
	{
		log.warn("Unexpected exception from downstream.", e.getCause());
		e.getChannel().close();
	}

	public SearchResult getMessage()
	{
		boolean interrupted = false;
		for (;;)
		{
			try
			{
				SearchResult result = answer.take();
				if (interrupted)
				{
					Thread.currentThread().interrupt();
				}
				return result;
			} catch (InterruptedException e)
			{
				interrupted = true;
			}
		}
	}

	public long getTransferredMessages()
	{
		return transferredMessages.get();
	}

	@Override
	public void handleUpstream(final ChannelHandlerContext ctx, final ChannelEvent e) throws Exception
	{
		if (e instanceof ChannelStateEvent && ((ChannelStateEvent) e).getState() != ChannelState.INTEREST_OPS)
		{
			log.info(e.toString());
		}
		super.handleUpstream(ctx, e);
	}

	@Override
	public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e)
	{
		// Echo back the received object to the client.
		transferredMessages.incrementAndGet();





		if(done)
		{
			e.getChannel().write(new ThankYouMessage());
			e.getChannel().close().addListener(new ChannelFutureListener()
			{
				@Override
				public void operationComplete(ChannelFuture future)
				{
					boolean offered = answer.offer((SearchResult) e.getMessage());
					assert offered;
					log.info("post-closure");
				}
			});
		}
	}


}
