import java.util.Random;

public class Buyer extends Thread {
private int id_buyer,price,no_stock;
private Bursa b;
        public Buyer(int id_buyer,Bursa b)
        {this.id_buyer=id_buyer;
        this.b=b;}
        
       public synchronized void calculateStock()
       {if(b.getSeller_list().size()==0)
       {   this.no_stock=0;
           this.price=0;}
       else
       {int aux=0;
        for(Seller sell:b.getSeller_list())
        	aux+=sell.getNo_stock();
    	   Seller se=b.getSeller_list().get(new Random().nextInt(b.getSeller_list().size()));
    	   this.price=se.getPrice();
    	   this.no_stock=aux/b.getSeller_list().size();}
       System.out.println("calculate stock "+this.getId()+" "+this.getNo_stocks()+" "+this.getPrice());}
       
    	public void run(){long init=System.currentTimeMillis();
    		while(System.currentTimeMillis()-init<60000)
    		{this.calculateStock();
    		this.buy();}
    		
    	}
    
    public void buy()
    {while(!b.buy_stock(this))
    {this.calculateStock();}
    System.out.println("Buy stock "+this.getId()+" "+this.getNo_stocks()+" "+this.getPrice());}
    	
	public int getId_buyer() {
			return id_buyer;
		}
	public int getPrice() {
		return price;
	}
	public int getNo_stocks() {
		return no_stock;
	}
}
