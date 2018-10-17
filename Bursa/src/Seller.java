import java.util.List;
import java.util.Random;

public class Seller extends Thread {
private int id_seller,no_stock,price;
private int seller_readers=0,seller_writers=0,seller_writereq=0;
private Object o_sell;
private Bursa b;
    public Seller(int id_seller,int no_stock,int price,Bursa b)
    {this.id_seller=id_seller;
    this.no_stock=no_stock;
    this.price=price;
    this.b=b;
    
    }
    public Seller(int id_seller,Bursa b)
    {this(id_seller,new Random().nextInt(10)+10,new Random().nextInt(500)+100,b);
    o_sell=new Object();
    System.out.println("Seller "+this.getId()+" "+this.getNo_stock()+" "+this.getPrice());}
    
    private void generate(){
    int price_aux=(b.getAverageTransactionPriceSelling()+b.getAverageTransactionPriceBuying()+b.getMaximumTransactionPriceSold()+new Random().nextInt(500)+100)/4;
    int no_stock_aux=(b.getAverageTransactionNoStockSelling()+b.getAverageTransactionNoStockBuying()+b.getMaximumTransactionNoStockSold()+new Random().nextInt(10)+10)/4;
    this.lock_write_seller();
    System.out.println("generate buyer");
    this.no_stock=no_stock_aux;
    this.price=price_aux;
    System.out.println("generate "+this.id_seller+" "+this.no_stock+" "+this.price);    
    this.unlock_write_seller();}
    
    public void sell_stock(Buyer bu)
    {System.out.println("sell stock "+this.getId()+this.getPrice());
    b.add_transaction(bu, this);
    bu.calculateStock();
    this.generate();}
    
    public void run()
    {long init=System.currentTimeMillis();while(System.currentTimeMillis()-init<10000);}

    public int getId_seller() {
		return id_seller;}

	public int getNo_stock() {
		this.lock_read_seller();
		int aux=no_stock;
		this.unlock_read_seller();
		return aux;}
	
	public int getPrice() {
		this.lock_read_seller();
		int aux=price;
		this.unlock_read_seller();
		return aux;}	
	
	private void lock_read_seller()
	   {System.out.println("lock_read_seller"+this.seller_readers+" "+this.seller_writers+" "+this.seller_writereq);
		synchronized(this.o_sell)
		{while(seller_writers>0||seller_writereq>0)
			try {
				this.o_sell.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();}
		this.seller_readers++;
		this.o_sell.notifyAll();}}
	   
	   private  void unlock_read_seller()
	   {System.out.println("unlock_read_seller"+this.seller_readers+" "+this.seller_writers+" "+this.seller_writereq);
      synchronized(this.o_sell)
		{this.seller_readers--;
		this.o_sell.notifyAll();}}

	   private void lock_write_seller()
	   {System.out.println("lock_write_seller"+this.seller_readers+" "+this.seller_writers+" "+this.seller_writereq);
    synchronized(this.o_sell)
		{seller_writereq++;
		 while(seller_writers>0||seller_readers>0)
			try {
				this.o_sell.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		this.seller_writereq--;
		this.seller_writers++;
		this.o_sell.notifyAll();}}
	   
	   private void unlock_write_seller()
	   {System.out.println("unlock_write_seller"+this.seller_readers+" "+this.seller_writers+" "+this.seller_writereq);
    synchronized(this.o_sell)
		{this.seller_writers--;
		this.o_sell.notifyAll();}}
    
}
