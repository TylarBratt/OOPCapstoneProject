package beans;

import java.util.TimerTask;

/*
 * A background time task who's purpose is simply to call process_finished_auctions via the database.
 */
public class ProcessFinishedAuctionsTask implements Runnable {
	final Database connection;
	
	ProcessFinishedAuctionsTask(Database connection){
		this.connection = connection;
	}
	@Override
	public void run() {
		connection.processFinishedAuctions();
	}

}
