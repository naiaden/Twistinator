/**
 * 
 */
package nl.naiaden.twistinator.server;

import org.apache.log4j.Logger;

/**
 * @author louis
 *
 */
public class ThroughputMonitor extends Thread
{
	static Logger log = Logger.getLogger(ThroughputMonitor.class);

	public ThroughputMonitor()
	{

	}

	@Override
	public void run()
	{
		try
		{
			long oldCounter = TwistServerHandler.getTransferredBytes();
			long startTime = System.currentTimeMillis();
			for(;;)
			{
				Thread.sleep(3000);

				long endTime = System.currentTimeMillis();
				long newCounter = TwistServerHandler.getTransferredBytes();


				log.debug((newCounter - oldCounter) * 1000 / (endTime - startTime) / 1048576.0 + " MiB/s%n");
				oldCounter = newCounter;
				startTime = endTime;
			}
		} catch(InterruptedException e)
		{
			return;
		}
	}
}
