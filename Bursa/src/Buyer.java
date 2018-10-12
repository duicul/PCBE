
public class Buyer extends Thread {
private int id_buyer,price,no_stock;
private Bursa b;
        public Buyer(int id_buyer,Bursa b)
        {this.id_buyer=id_buyer;
        this.b=b;
        this.calculateStock();
        }
       public void calculateStock()
       {
    	   
    	   
    	   
       }
    	public void run(){}
    	
	public int getId_buyer() {
			return id_buyer;
		}
}
