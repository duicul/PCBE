import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Bursa extends Thread {
	private List<Seller> seller_list;
	private List<Buyer> buyer_list;
	private List<Tranzactie> transactions;
	private int seller_list_readers=0,seller_list_writers=0,seller_list_writereq=0;
	private int buyer_list_readers=0,buyer_list_writers=0,buyer_list_writereq=0;
	private int trans_readers=0,trans_writers=0,trans_writereq=0;
	private int simulation_time;
	private Object o_sell,o_buy,o_trans;
	private static Bursa b=null;
  
    private Bursa(int no_seller,int no_buyer,int simulation_time)
    {  	int i;
    	seller_list=new ArrayList<Seller>();
    	this.simulation_time=simulation_time;
    	for(i=0;i<no_seller;i++)
    		seller_list.add(new Seller(i,this,Dispatcher.create()));
    	buyer_list=new ArrayList<Buyer>();
    	for(i=0;i<no_buyer;i++)
    		buyer_list.add(new Buyer(i,this,Dispatcher.create()));
    	this.transactions=new ArrayList<Tranzactie>();
    	this.o_buy=new Object();
    	this.o_sell=new Object();
    	this.o_trans=new Object();}
    	
    	 public static Bursa create(int no_seller,int no_buyer,int simulation_time)
    { 	System.out.println("Bursa create");
    		 b=(b==null)?new Bursa(no_buyer,no_seller,simulation_time):b;	
    	return b;}
    
    private Bursa()
    {  	this.transactions=new ArrayList<Tranzactie>();
    	this.o_buy=new Object();
    	this.o_sell=new Object();
    	this.o_trans=new Object();}
    
    public static Bursa create()
    {   System.out.println("Bursa create");
    	b=(b==null)?new Bursa():b;	
    	return b;}
   
    
    
	public void run(){
		for(Seller s:this.seller_list)
			s.start();
		for(Buyer bu:this.buyer_list)
			bu.start();
		long init=System.currentTimeMillis();
		while(System.currentTimeMillis()-init<this.simulation_time);
		Dispatcher.create().kill();
		for(Seller s:this.seller_list)
			try {
				s.join();
			}catch (InterruptedException e) {
				e.printStackTrace();}
		for(Buyer bu:this.buyer_list)
			try {
				bu.join();
			}catch (InterruptedException e) {
				e.printStackTrace();}
		System.out.println("Tranzactii: "+this.transactions.size());
		/*for(Tranzactie t:this.transactions)
			System.out.println(t);
		*/}
	
	public Buyer getBuyer(int id)
	{this.lock_read_buyer_list();
	for(Buyer buy:this.buyer_list)
		{if(buy.getIdSub()==id)
          return buy;}
	return null;}
	
	public Seller getSeller(int id)
	{this.lock_read_seller_list();
	for(Seller sel:this.seller_list)
		{if(sel.getIdSub()==id)
          return sel;}
	return null;}
	
	public List<Tranzactie> getTransactions() {
		this.lock_read_transactions();
		ArrayList<Tranzactie> aux=new ArrayList<Tranzactie>(transactions);
		//System.out.println("get trans list");
		this.unlock_read_transactions();
		return aux;}
	
	public void add_transaction(Buyer bu,Seller se){
		int no_stock_real=bu.getNo_stock()<se.getNo_stock()?bu.getNo_stock():se.getNo_stock();
		this.lock_write_transactions();
		this.transactions.add(new Tranzactie(se.getIdSub(),bu.getIdSub(),bu.getPrice(),no_stock_real));
		this.unlock_write_transactions();}

	private void lock_read_seller_list(){
		//System.out.println("lock_read_seller_list"+this.seller_list_readers+" "+this.seller_list_writers+" "+this.seller_list_writereq);
		synchronized(this.o_sell){
			while(seller_list_writers>0||seller_list_writereq>0)
				try {
					this.o_sell.wait();
				}catch (InterruptedException e) {
					e.printStackTrace();}
		this.seller_list_readers++;
		this.o_sell.notifyAll();}}
	   
	private  void unlock_read_seller_list(){
		//System.out.println("unlock_read_seller_list"+this.seller_list_readers+" "+this.seller_list_writers+" "+this.seller_list_writereq);
		synchronized(this.o_sell){
			this.seller_list_readers--;
			this.o_sell.notifyAll();}}

	private void lock_write_seller_list(){
		//System.out.println("lock_write_seller_list"+this.seller_list_readers+" "+this.seller_list_writers+" "+this.seller_list_writereq);
		synchronized(this.o_sell){
			seller_list_writereq++;
			while(seller_list_writers>0||seller_list_readers>0)
				try {
					this.o_sell.wait();
			    }catch (InterruptedException e) {
			    	e.printStackTrace();}
			this.seller_list_writereq--;
			this.seller_list_writers++;
			this.o_sell.notifyAll();}}
	   
	private void unlock_write_seller_list(){
		//System.out.println("unlock_write_seller_list"+this.seller_list_readers+" "+this.seller_list_writers+" "+this.seller_list_writereq);
		synchronized(this.o_sell){
			this.seller_list_writers--;
			this.o_sell.notifyAll();}}
	   
	private void lock_read_buyer_list(){
		//System.out.println("lock_read_buyer_list"+this.buyer_list_readers+" "+this.buyer_list_writers+" "+this.buyer_list_writereq);
		synchronized(this.o_buy){
			while(buyer_list_writers>0||buyer_list_writereq>0)
				try {
					this.o_buy.wait();
			    }catch (InterruptedException e) {
			    	e.printStackTrace();}
			this.buyer_list_readers++;
			this.o_buy.notifyAll();}}
	   
	private void unlock_read_buyer_list(){
		//System.out.println("unlock_read_buyer_list"+this.buyer_list_readers+" "+this.buyer_list_writers+" "+this.buyer_list_writereq);
		synchronized(this.o_buy){
			this.buyer_list_readers--;
			this.o_buy.notifyAll();}}

	private void lock_write_buyer_list(){
		//System.out.println("lock_write_buyer_list"+this.buyer_list_readers+" "+this.buyer_list_writers+" "+this.buyer_list_writereq);
        synchronized(this.o_buy){
        	buyer_list_writereq++;
        	while(buyer_list_writers>0||buyer_list_readers>0)
        		try {
        			this.o_buy.wait();
			    }catch (InterruptedException e) {
			    	e.printStackTrace();}
        	this.buyer_list_writereq--;
        	this.buyer_list_writers++;
        	this.o_buy.notifyAll();}}
	   
	private void unlock_write_buyer_list(){
		//System.out.println("unlock_write_buyer_list"+this.buyer_list_readers+" "+this.buyer_list_writers+" "+this.buyer_list_writereq);
		synchronized(this.o_buy){
			this.buyer_list_writers--;
			this.o_buy.notifyAll();}}
   
	private void lock_read_transactions(){
		//System.out.println("lock_read_trans_list"+this.trans_readers+" "+this.trans_writers+" "+this.trans_writereq);
		synchronized(this.o_trans){
			while(trans_writers>0||trans_writereq>0)
				try {
					this.o_trans.wait();
			    }catch (InterruptedException e) {
			    	e.printStackTrace();}
			this.trans_readers++;
			this.o_trans.notifyAll();}}
	   
	private void unlock_read_transactions(){
		//System.out.println("unlock_read_trans_list"+this.trans_readers+" "+this.trans_writers+" "+this.trans_writereq);
		synchronized(this.o_trans){
			this.trans_readers--;
			this.o_trans.notifyAll();}}

	private void lock_write_transactions(){
		//System.out.println("lock_write_trans_list"+this.trans_readers+" "+this.trans_writers+" "+this.trans_writereq);
		synchronized(this.o_trans){
			trans_writereq++;
			while(trans_writers>0||trans_readers>0)
				try {
					this.o_trans.wait();}
				catch (InterruptedException e) {
					e.printStackTrace();}
			this.trans_writereq--;
			this.trans_writers++;
			this.o_trans.notifyAll();}}
	   
	private void unlock_write_transactions(){
		//System.out.println("unlock_write_trans_list"+this.trans_readers+" "+this.trans_writers+" "+this.trans_writereq);
		synchronized(this.o_trans){
			this.trans_writers--;
			this.o_trans.notifyAll();}}
	
	public int getAverageTransactionPriceSelling(){
		this.lock_read_seller_list();
		int sum=0;
		for(Seller s:this.seller_list)
			sum+=s.getPrice();
		sum/=this.seller_list.size();
		this.unlock_read_seller_list();
		return sum;}
		
	public int getAverageTransactionPriceBuying(){
		this.lock_read_buyer_list();
		int sum=0;
		for(Buyer b:this.buyer_list)
			sum+=b.getPrice();
		sum/=this.buyer_list.size();
		this.unlock_read_buyer_list();
		return sum;}
		
	public int getAverageTransactionPriceSold(){
		this.lock_read_transactions();
		int sum=new Random().nextInt(500);
		if(this.transactions.size()!=0){
			sum=0;
			for(Tranzactie t:this.transactions)
				sum+=t.getPrice();
			sum/=this.transactions.size();}
		this.unlock_read_transactions();
		return sum;}
		
	public int getAverageTransactionNoStockSelling(){
		this.lock_read_seller_list();
		int sum=0;
		for(Seller s:this.seller_list)
			{sum+=s.getNo_stock();}
		sum/=this.seller_list.size();
		this.unlock_read_seller_list();
		return sum;	}
		
	public int getAverageTransactionNoStockBuying(){
		this.lock_read_buyer_list();
		int sum=0;
		for(Buyer b:this.buyer_list)
			{sum+=b.getNo_stock();}
		sum/=this.buyer_list.size();
		this.unlock_read_buyer_list();
		return sum;}
		
	public int getAverageTransactionNoStockSold(){
		this.lock_read_transactions();
		int sum=new Random().nextInt(500);
		if(this.transactions.size()!=0){
			sum=0;
		for(Tranzactie t:this.transactions)
			{sum+=t.getNo_stocks();}
		sum/=this.transactions.size();}
		this.unlock_read_transactions();
		return sum;}

	public int getMinimumTransactionPriceSelling(){
		this.lock_read_seller_list();
		int sum=new Random().nextInt(500)+100;
		if(this.seller_list.size()>0){
			sum=this.seller_list.get(0).getPrice();
			for(Seller s:this.seller_list)
				sum=sum<s.getPrice()?sum:s.getPrice();}
		this.unlock_read_seller_list();
		return sum;}
		
	public int getMinimumTransactionPriceBuying(){
		this.lock_read_buyer_list();
		int sum=new Random().nextInt(500)+100;
		if(this.buyer_list.size()>0){
			sum=this.buyer_list.get(0).getPrice();
			for(Buyer s:this.buyer_list)
				sum=sum<s.getPrice()?sum:s.getPrice();}
		this.unlock_read_buyer_list();
		return sum;}
		
	public int getMinimumTransactionPriceSold(){
		this.lock_read_transactions();
		int sum=new Random().nextInt(500)+100;
		if(this.transactions.size()>0)
			{sum=this.transactions.get(0).getPrice();
			for(Tranzactie s:this.transactions)
				sum=sum<s.getPrice()?sum:s.getPrice();}
		this.unlock_read_transactions();
		return sum;}
		
	public int getMinimumTransactionNoStockSelling(){
		this.lock_read_seller_list();
		int sum=new Random().nextInt(10)+10;
		if(this.seller_list.size()>0){
			sum=this.seller_list.get(0).getPrice();
			for(Seller s:this.seller_list)
				sum=sum<s.getNo_stock()?sum:s.getPrice();}
		this.unlock_read_seller_list();
		return sum;}
		
	public int getMinimumTransactionNoStockBuying(){
		this.lock_read_buyer_list();
		int sum=new Random().nextInt(10)+10;
		if(this.buyer_list.size()>0){
			sum=this.buyer_list.get(0).getPrice();
			for(Buyer s:this.buyer_list)
				{sum=sum<s.getNo_stock()?sum:s.getPrice();}}
		this.unlock_read_buyer_list();
		return sum;}
		
	public int getMinimumTransactionNoStockSold(){
		this.lock_read_transactions();
		int sum=new Random().nextInt(10)+10;
		if(this.transactions.size()>0){
			sum=this.transactions.get(0).getPrice();
			for(Tranzactie s:this.transactions)
				{sum=sum<s.getNo_stocks()?sum:s.getPrice();}}
		this.unlock_read_transactions();
		return sum;}
		
	public int getMaximumTransactionPriceSelling(){
		this.lock_read_seller_list();
		int sum=new Random().nextInt(500)+100;
		if(this.seller_list.size()>0){
			sum=this.seller_list.get(0).getPrice();
			for(Seller s:this.seller_list)
				sum=sum>s.getPrice()?sum:s.getPrice();}
		this.unlock_read_seller_list();
		return sum;}
		
	public int getMaximumTransactionPriceBuying(){
		this.lock_read_buyer_list();
		int sum=new Random().nextInt(500)+100;
		if(this.buyer_list.size()>0){
			sum=this.buyer_list.get(0).getPrice();
			for(Buyer s:this.buyer_list)
				sum=sum>s.getPrice()?sum:s.getPrice();}
		this.unlock_read_buyer_list();
		return sum;}
		
	public int getMaximumTransactionPriceSold(){
		this.lock_read_transactions();
		int sum=new Random().nextInt(500)+100;
		if(this.transactions.size()>0){
			sum=this.transactions.get(0).getPrice();
			for(Tranzactie s:this.transactions)
				sum=sum>s.getPrice()?sum:s.getPrice();}
		this.unlock_read_transactions();
		return sum;}
		
	public int getMaximumTransactionNoStockSelling(){
		this.lock_read_seller_list();
		int sum=new Random().nextInt(10)+10;
		if(this.seller_list.size()>0){
			sum=this.seller_list.get(0).getPrice();
			for(Seller s:this.seller_list)
				sum=sum>s.getNo_stock()?sum:s.getPrice();}
		this.unlock_read_seller_list();
		return sum;}
		
	public int getMaximumTransactionNoStockBuying(){
		this.lock_read_buyer_list();
		int sum=new Random().nextInt(10)+10;
		if(this.buyer_list.size()>0){
			sum=this.buyer_list.get(0).getPrice();
			for(Buyer s:this.buyer_list)
				{sum=sum>s.getNo_stock()?sum:s.getPrice();}}
		this.unlock_read_buyer_list();
		return sum;}
		
	public int getMaximumTransactionNoStockSold(){
		this.lock_read_transactions();
		int sum=new Random().nextInt(10)+10;
		if(this.transactions.size()>0){
			sum=this.transactions.get(0).getPrice();
			for(Tranzactie s:this.transactions)
				{sum=sum>s.getNo_stocks()?sum:s.getPrice();}}
		this.unlock_read_transactions();
		return sum;}
	   
}
