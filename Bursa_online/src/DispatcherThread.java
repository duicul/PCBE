import java.util.List;

public class DispatcherThread extends Thread{
	private Event e;
	public DispatcherThread(Event e)
	{this.e=e;}
	
	public void run() {
		Dispatcher.create().post(e);}
}
