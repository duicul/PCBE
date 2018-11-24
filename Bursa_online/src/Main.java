
public class Main {
	public static void main(String args[])
	{
		Bursa b=Bursa.create(3,3);//buyer,seller
		b.start();
		try {
			b.join();
		}catch (InterruptedException e) {
			e.printStackTrace();}
	}	
	

}
