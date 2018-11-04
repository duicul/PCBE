public class Buyer extends Thread {
	private int id_buyer,price,no_stock;
	private int buyer_readers=0,buyer_writers=0,buyer_writereq=0;
	private Object o_buy;
	private Bursa b;

	public Buyer(int id_buyer,Bursa b){
		this.o_buy=new Object();
		this.id_buyer=id_buyer;
		this.b=b;}
        
	public void calculateStock(){ 
	   int price_aux=(b.getAverageTransactionPriceSelling()+b.getMinimumTransactionPriceSelling())/2;//(b.getAverageTransactionPriceSelling()+b.getAverageTransactionPriceBuying()+b.getMinimumTransactionPriceSold())/3;
	   int no_stock_aux=(b.getMaximumTransactionNoStockSelling()+b.getAverageTransactionNoStockSold())/2;//+b.getAverageTransactionNoStockBuying()+b.getMaximumTransactionNoStockSold())/3;
	   this.lock_write_buyer();
	   //System.out.println("calculate buyer");
	   this.price=price_aux;      
	   this.no_stock=no_stock_aux;
	   //System.out.println("calculate stock "+this.id_buyer+" "+this.no_stock+" "+this.price);
	   this.unlock_write_buyer();}
       
	public void run(){
		long init=System.currentTimeMillis();
		while(System.currentTimeMillis()-init<10000){
			this.calculateStock();
			this.buy();}}
    
	public void buy(){
		while(!b.buy_stock(this))
			{this.calculateStock();}
    //System.out.println("Buy stock "+this.getId()+" "+this.getNo_stocks()+" "+this.getPrice());
		}
    	
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
