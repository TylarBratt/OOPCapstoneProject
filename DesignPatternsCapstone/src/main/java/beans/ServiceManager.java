package beans;

import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ServiceManager implements ServletContextListener {
	Database dbConnection = null;
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	int interval = 3000; //milliseconds
	
	Timer timer = new Timer();
	
	@Override
	public void contextInitialized(ServletContextEvent event) {
		dbConnection = new Database();
		
		System.out.println("Starting finished auction processor service..");
		scheduler.scheduleAtFixedRate(new ProcessFinishedAuctionsTask(dbConnection), 0, interval, TimeUnit.MILLISECONDS);
		
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		scheduler.shutdown();
		
		//Wait for all processes to terminate before performing cleanup.
		try {
			scheduler.awaitTermination(3, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		dbConnection.shutdown();
	}

	
}