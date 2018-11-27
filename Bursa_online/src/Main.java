
public class Main {
	public static void main(String args[])
	{   Bursa b=Bursa.create(5,5);//buyer,seller
		b.start();
		try {
			b.join();
		}catch (InterruptedException e) {
			e.printStackTrace();}
		System.out.println("total events "+Dispatcher.ev);
	}	
	

}
