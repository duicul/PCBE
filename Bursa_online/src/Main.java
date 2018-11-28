
public class Main {
	public static void main(String args[])
	{   int sell=5,buy=5,simulation_time=10000;
	    if(args.length>=2)
	    {try {buy=Integer.parseInt(args[0]);
	    sell=Integer.parseInt(args[1]);    	
	    }catch(Exception e)
	    {e.printStackTrace();}}
	    if(args.length==3)
	    {try {simulation_time=Integer.parseInt(args[2]);    	
	    }catch(Exception e)
	    {e.printStackTrace();}}	
		Dispatcher.create().start();
	    Bursa b=Bursa.create(buy,sell,simulation_time);//buyer,seller
		b.start();
		try {
			b.join();
			Dispatcher.create().join();
		}catch (InterruptedException e) {
			e.printStackTrace();}
		System.out.println("total events "+Dispatcher.ev);
		System.out.println("total transactions "+b.getTransactions().size());
	}	
	

}
