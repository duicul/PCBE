import java.util.Random;

public class Buyer extends Thread implements Subscriber {
	private int id_buyer,price,no_stock;
	private int buyer_readers=0,buyer_writers=0,buyer_writereq=0;
	private Object o_buy;
	private Bursa b;
	private Dispatcher d;
	private volatile boolean kill=false;

	public Buyer(int id_buyer,Bursa b,Dispatcher d){
		System.out.println("Buyer create");
		this.o_buy=new Object();
		this.id_buyer=id_buyer;
		this.b=b;
		this.d=d;
		d.addSubscriber(this, new Filter("sell"), "sell");}
        
	public void calculateStock(){ 
	   int price_aux=/*(b.getAverageTransactionPriceSelling()+b.getAverageTransactionPriceBuying()+*/b.getMinimumTransactionPriceSelling();
	   int no_stock_aux=(b.getMaximumTransactionNoStockSelling()+b.getAverageTransactionNoStockSold())/2;//+b.getAverageTransactionNoStockBuying()+b.getMaximumTransactionNoStockSold())/3;
	   this.lock_write_buyer();
	   //System.out.println("calculate buyer");
	   this.price=price_aux;      
	   this.no_stock=no_stock_aux;
	   //System.out.println("calculate stock "+this.id_buyer+" "+this.no_stock+" "+this.price);
	   this.unlock_write_buyer();
	   //this.lock_read_buyer();
	   d.publish(new Event("buy",this.price,this.no_stock,this.id_buyer));
	   //this.unlock_read_buyer();
	}
	
	public void buy_stock(Seller se){
    	//System.out.println("buy stock "+this.getId()+" "+this.getPrice()+" "+se.getPrice());
    	if(se.getPrice()==this.getPrice()) {
    		b.add_transaction(this, se);
    		this.calculateStock();
    		se.generate();}
    	}
	
	public synchronized void inform(Event e)
    {if(e.name.equals("kill"))
    	{kill=true;
    	System.out.println("Buyer interrupted");
    	return;}
    else if(e.name.equals("sell"))
    {this.buy_stock(b.getSeller(e.id));}
		
    }
       
	public void run(){
		this.calculateStock();
		while(!kill)
		{if(new Random().nextFloat()>0.8)
    		this.calculateStock();}
		System.out.println("Buyer kill");
	}
    
	public void buy(){}
    	
	public int getId_buyer() {
		return id_buyer;}
	
	public int getPrice() {
		this.lock_read_buyer();
		int aux=price;
		this.unlock_read_buyer();
		return aux;}
	
	public int getNo_stocks() {
		this.lock_read_buyer();
		int aux=no_stock;
		this.unlock_read_buyer();
		return aux;}
	
	private void lock_read_buyer(){
		//System.out.println("lock_read_buyer"+this.buyer_readers+" "+this.buyer_writers+" "+this.buyer_writereq);
		synchronized(this.o_buy){
			while(buyer_writers>0||buyer_writereq>0)
				try {
					this.o_buy.wait();
				}catch (InterruptedException e) {
					e.printStackTrace();}
			this.buyer_readers++;
			this.o_buy.notifyAll();}}
	   
	private void unlock_read_buyer(){
		//System.out.println("unlock_read_buyer"+this.buyer_readers+" "+this.buyer_writers+" "+this.buyer_writereq);
		synchronized(this.o_buy){
			this.buyer_readers--;
			this.o_buy.notifyAll();}}

	private void lock_write_buyer(){
		//System.out.println("lock_write_buyer"+this.buyer_readers+" "+this.buyer_writers+" "+this.buyer_writereq);
		synchronized(this.o_buy){
			buyer_writereq++;
			while(buyer_writers>0||buyer_readers>0)
				try {
					this.o_buy.wait();
			    }catch (InterruptedException e) {		
			    	e.printStackTrace();}
			this.buyer_writereq--;
			this.buyer_writers++;
			this.o_buy.notifyAll();}}
	   
	private void unlock_write_buyer(){
		//System.out.println("unlock_write_buyer"+this.buyer_readers+" "+this.buyer_writers+" "+this.buyer_writereq);
		synchronized(this.o_buy){
			this.buyer_writers--;
			this.o_buy.notifyAll();}}
}
