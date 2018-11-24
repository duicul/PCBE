import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Bursa extends Thread {
	private List<Triplet<String,Filter,Buyer>> buyer_subscr;
	private List<Triplet<String,Filter,Seller>> seller_subscr;
	private List<Tranzactie> transactions;
	private int seller_list_readers=0,seller_list_writers=0,seller_list_writereq=0;
	private int buyer_list_readers=0,buyer_list_writers=0,buyer_list_writereq=0;
	private int trans_readers=0,trans_writers=0,trans_writereq=0;
	private Object o_sell,o_buy,o_trans;
	private static Bursa b=null;
  
    /*private Bursa(int no_seller,int no_buyer)
    {
    	int i;
    	this.buyer_subscr=new ArrayList<Triplet<Event,Filter,Buyer>>();
    	this.seller_subscr=new ArrayList<Triplet<Event,Filter,Seller>>();
    	seller_list=new ArrayList<Seller>();
    	for(i=0;i<no_seller;i++)
    		seller_list.add(new Seller(i,this));
    	buyer_list=new ArrayList<Buyer>();
    	for(i=0;i<no_buyer;i++)
    		buyer_list.add(new Buyer(i,this));
    	this.transactions=new ArrayList<Tranzactie>();
    	this.o_buy=new Object();
    	this.o_sell=new Object();
    	this.o_trans=new Object();}
    	
    	 public static Bursa create(int no_seller,int no_buyer)
    {
    	b=(b==null)?new Bursa(no_buyer,no_seller):b;	
    	return b;}*/
    
    private Bursa()
    {   this.buyer_subscr=new ArrayList<Triplet<String,Filter,Buyer>>();
	    this.seller_subscr=new ArrayList<Triplet<String,Filter,Seller>>();
    	this.transactions=new ArrayList<Tranzactie>();
    	this.o_buy=new Object();
    	this.o_sell=new Object();
    	this.o_trans=new Object();}
    
    public static Bursa create()
    {
    	b=(b==null)?new Bursa():b;	
    	return b;}
   
    
    
	public void run(){
		long init=System.currentTimeMillis();
		while(System.currentTimeMillis()-init<10000);
		for(Triplet<String,Filter,Buyer> ts:this.buyer_subscr)
		{ts.third.inform(null,new Event("kill",-1));}
		for(Triplet<String,Filter,Seller> ts:this.seller_subscr)
		{ts.third.inform(null,new Event("kill",-1));}
		}
	
	public void addSubscriber(Buyer b,Filter f,Event e)
	{this.buyer_subscr.add(new Triplet(e.name,f,b));}
	
	public void addSubscriber(Seller s,Filter f,Event e)
	{this.seller_subscr.add(new Triplet(e.name,f,s));}
	
	public void publish(Seller s,Event e)
	{for(Triplet<String,Filter,Buyer> ts:this.buyer_subscr)
	if(e.name.equals(ts.first)&&ts.second.apply(e))
	{ts.third.inform(s,e);
	}}
	
	public void publish(Buyer b,Event e)
	{for(Triplet<String,Filter,Seller> ts:this.seller_subscr)
		if(e.name.equals(ts.first)&&ts.second.apply(e))
		{ts.third.inform(b,e);
		}}	
	
	public List<Tranzactie> getTransactions() {
		this.lock_read_transactions();
		ArrayList<Tranzactie> aux=new ArrayList<Tranzactie>(transactions);
		//System.out.println("get trans list");
		this.unlock_read_transactions();
		return aux;}
	
	public void add_transaction(Buyer bu,Seller se){
		int no_stock_real=bu.getNo_stocks()<se.getNo_stock()?bu.getNo_stocks():se.getNo_stock();
		this.lock_write_transactions();
		this.transactions.add(new Tranzactie(se.getId_seller(),bu.getId_buyer(),bu.getPrice(),no_stock_real));
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
	   
}
