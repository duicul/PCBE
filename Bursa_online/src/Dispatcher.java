import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class Dispatcher extends Thread{
	private List<Triplet<String,Filter,Subscriber>>  subscr;
	private volatile Queue<Event> events;
	private static Dispatcher d;
	private volatile boolean kill=false;
	private int dispatcher_readers=0,dispatcher_writers=0,dispatcher_writereq=0;
	private Object o_dispatcher,o_subs;
	public static int ev=0;
	
	private Dispatcher()
	{this.subscr=new ArrayList<Triplet<String,Filter,Subscriber>>();
	this.events=new LinkedBlockingQueue<Event>();
	this.o_dispatcher=new Object();
	this.o_subs=new Object();
	this.start();
	}
	
	public static Dispatcher create()
    {
    	d=(d==null)?new Dispatcher():d;	
    	return d;}
	
	public void post(Event e)
	{this.lock_read_dispatcher();
	for(Triplet<String,Filter,Subscriber> s:this.subscr)
	   if(s.first.equals(e.name)&&s.second.apply(e))
		s.third.inform(e);	
	this.unlock_read_dispatcher();
	}
	
	public void run(){
		while(!kill)
		{//System.out.println("Events "+this.events.size());
			if(this.events.size()>0)
		{this.lock_write_dispatcher();
		Event e=this.events.remove();
		this.unlock_write_dispatcher();
		//new DispatcherThread(e).start();
		this.post(e);
		}}
		
		System.out.println("Events "+this.events.size());
		/*this.lock_write_dispatcher();
		for(Event e:this.events)
			this.post(e);
		this.unlock_write_dispatcher();*/
		
		for(Triplet<String,Filter,Subscriber> ts:this.subscr)
		{ts.third.inform(new Event("kill",-1,-1,-1));}
		System.out.println("Dispatcher kill");
		this.d=null;
	}
	
	public Triplet<String,Filter,Subscriber>  addSubscriber(Subscriber s,Filter f,String e)
	{synchronized (this.o_subs) {Triplet<String, Filter, Subscriber> sub=new Triplet<String, Filter, Subscriber>(e,f,s);
		this.subscr.add(sub);
	return sub;}}
	
	
	public void kill()
	{this.kill=true;}
	
	public void publish(Event e)
	{synchronized (this.o_dispatcher)
		{ev++;if(!kill)
			this.events.offer(e);}}
	
	private void lock_read_dispatcher(){
		//System.out.println("lock_read_dispatcher"+this.dispatcher_readers+" "+this.dispatcher_writers+" "+this.dispatcher_writereq);
		synchronized(this.o_dispatcher){
			while(this.dispatcher_writers>0||this.dispatcher_writereq>0)
				try {
					this.o_dispatcher.wait();
				}catch (InterruptedException e) {
					e.printStackTrace();}
			this.dispatcher_readers++;
			this.o_dispatcher.notifyAll();}}
	   
	private void unlock_read_dispatcher(){
		///System.out.println("unlock_read_dispatcher"+this.dispatcher_readers+" "+this.dispatcher_writers+" "+this.dispatcher_writereq);
		synchronized(this.o_dispatcher){
			this.dispatcher_readers--;
			this.o_dispatcher.notifyAll();}}

	private void lock_write_dispatcher(){
		//System.out.println("lock_write_dispatcher"+this.dispatcher_readers+" "+this.dispatcher_writers+" "+this.dispatcher_writereq);
		synchronized(this.o_dispatcher){
			this.dispatcher_writereq++;
			while(this.dispatcher_writers>0||this.dispatcher_readers>0)
				try {
					this.o_dispatcher.wait();
			    }catch (InterruptedException e) {		
			    	e.printStackTrace();}
			this.dispatcher_writereq--;
			this.dispatcher_writers++;
			this.o_dispatcher.notifyAll();}}
	   
	private void unlock_write_dispatcher(){
		//System.out.println("unlock_write_dispatcher"+this.dispatcher_readers+" "+this.dispatcher_writers+" "+this.dispatcher_writereq);
		synchronized(this.o_dispatcher){
			this.dispatcher_writers--;
			this.o_dispatcher.notifyAll();}}
}
