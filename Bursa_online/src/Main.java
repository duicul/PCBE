
public class Main {
	public static void main(String args[])
	{   Bursa b=Bursa.create(30,30);//buyer,seller
		b.start();
		try {
			b.join();
		}catch (InterruptedException e) {
			e.printStackTrace();}
	}	
	

}
