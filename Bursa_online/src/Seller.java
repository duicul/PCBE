import java.util.Random;

public class Seller extends Thread implements Subscriber{
	private int id_seller,no_stock,price;
	private int seller_readers=0,seller_writers=0,seller_writereq=0;
	private Object o_sell;
	private Bursa b;
	private Dispatcher d;
	private volatile boolean kill=false;
    
	public Seller(int id_seller,int no_stock,int price,Bursa b,Dispatcher d){
		System.out.println("Seller create");
		this.id_seller=id_seller;
		this.no_stock=no_stock;
		this.price=price;
		this.b=b;
		this.d=d;
		d.addSubscriber(this, new Filter("buy"), "buy");}
    
    public boolean raisePrice(int percent){
    	if(percent>0&&percent<100){
    		this.lock_write_seller();
    		this.price+=(this.price*percent)/100;
    		this.unlock_write_seller();
    		return true;}
    	return false;}
    
    public boolean lowerPrice(int percent){
    	if(percent>0&&percent<100) {
    		this.lock_write_seller();
    		this.price-=(this.price*percent)/100;
    		this.unlock_write_seller();
    		return true;}
    	return false;}
    
    public Seller(int id_seller,Bursa b,Dispatcher d){
    	this(id_seller,new Random().nextInt(20)+30,new Random().nextInt(200)+300,b,d);
    	o_sell=new Object();
    	//System.out.println("Seller "+this.getId()+" "+this.getNo_stock()+" "+this.getPrice());
    	}
    
    public void generate(){
    	int price_aux=/*(b.getAverageTransactionPriceBuying()+b.getAverageTransactionPriceSelling()+*/b.getMaximumTransactionPriceSold();
    	int no_stock_aux=(b.getAverageTransactionNoStockSelling()+b.getAverageTransactionNoStockBuying()+b.getMaximumTransactionNoStockSold()+new Random().nextInt(10)+20+new Random().nextInt(10)+20)/5;
    	this.lock_write_seller();
    	//System.out.println("generate buyer");
    	this.no_stock=no_stock_aux;
    	this.price=price_aux;
    	//System.out.println("generate "+this.id_seller+" "+this.no_stock+" "+this.price);    
    	this.unlock_write_seller();
    	//this.lock_read_seller();
    	d.publish(new Event("sell",this.price,this.no_stock,this.id_seller));
    	//this.unlock_read_seller();
    }
    
    public void sell_stock(Buyer bu){
    	//System.out.println("sell stock "+this.getId()+this.getPrice());
    	if(bu.getPrice()==this.getPrice()) {
    		b.add_transaction(bu, this);
    		this.generate();
    		bu.calculateStock();}
    	}
    
    public void run(){
    	while(!kill)
    	{if(new Random().nextFloat()>0.7)
    		this.generate();
    	if(new Random().nextFloat()>0.9)
    		this.raisePrice((int)new Random().nextFloat()*50);
    	if(new Random().nextFloat()>0.9)
    		this.lowerPrice((int)new Random().nextFloat()*50);
    	}
    	System.out.println("Seller kill");
    }
    
    public synchronized void inform(Event e)
    {if(e.name.equals("kill"))
    	{kill=true;
    	System.out.println("Seller interrupted");
    	}
    else if(e.name.equals("buy"))
    {this.sell_stock(b.getBuyer(e.id));}
    
    }   

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
	
	private void lock_read_seller(){
		//System.out.println("lock_read_seller"+this.seller_readers+" "+this.seller_writers+" "+this.seller_writereq);
		synchronized(this.o_sell){
			while(seller_writers>0||seller_writereq>0)
				try{
					this.o_sell.wait();
				}catch (InterruptedException e) {				
					e.printStackTrace();}
			this.seller_readers++;
			this.o_sell.notifyAll();}}
	   
	private  void unlock_read_seller(){
		//System.out.println("unlock_read_seller"+this.seller_readers+" "+this.seller_writers+" "+this.seller_writereq);
		synchronized(this.o_sell){
			this.seller_readers--;
			this.o_sell.notifyAll();}}

	private void lock_write_seller(){
		//System.out.println("lock_write_seller"+this.seller_readers+" "+this.seller_writers+" "+this.seller_writereq);
		synchronized(this.o_sell){
			seller_writereq++;
			while(seller_writers>0||seller_readers>0)
				try{
					this.o_sell.wait();
			    }catch (InterruptedException e) {				
			    	e.printStackTrace();}
			this.seller_writereq--;
			this.seller_writers++;
			this.o_sell.notifyAll();}}
	   
	private void unlock_write_seller(){
		//System.out.println("unlock_write_seller"+this.seller_readers+" "+this.seller_writers+" "+this.seller_writereq);
		synchronized(this.o_sell){
			this.seller_writers--;
			this.o_sell.notifyAll();}}
    
}