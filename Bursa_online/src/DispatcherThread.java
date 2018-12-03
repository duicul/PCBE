import java.util.List;

public class DispatcherThread extends Thread{
	private Event e;
	public DispatcherThread()
	{}
	
	public void run() {
		Dispatcher.create().post();}
}
