
public class Main {
	public static void main(String args[])
	{Bursa b=Bursa.create(30, 10);
	b.start();
	try {
		b.join();
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}	
	

}
