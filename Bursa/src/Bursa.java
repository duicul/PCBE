import java.util.ArrayList;
import java.util.List;

public class Bursa extends Thread {
private List<Seller> seller_list;
private List<Buyer> buyer_list;
private List<Tranzactie> transactions;
private int seller_list_readers=0,seller_list_writers=0,seller_list_writereq=0;
private int buyer_list_readers=0,buyer_list_writers=0,buyer_list_writereq=0;
private int trans_readers=0,trans_writers=0,trans_writereq=0;
private Object o_sell,o_buy,o_trans;
private static Bursa b=null;
  
    private Bursa(int no_seller,int no_buyer)
    {int i;
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
    {b=(b==null)?new Bursa(no_seller,no_buyer):b;	
    return b;
    }
	public void run(){
		long init=System.currentTimeMillis();
		for(Seller s:this.seller_list)
			s.start();
		for(Buyer bu:this.buyer_list)
			bu.start();
		while(System.currentTimeMillis()-init<60000);
		for(Tranzactie tr:b.getTransactions()) 
			{System.out.println("Transaction"+tr.getId_seller()+" "+tr.getId_buyer()+" "+tr.getNo_stocks()+" "+tr.getPrice());
	}}
	
	public List<Seller> getSeller_list() {
		this.lock_read_seller_list();
		ArrayList<Seller> aux=new ArrayList<Seller>(seller_list);
        System.out.println("get seller list");
		this.unlock_read_seller_list();
        return aux;}
	
	public List<Buyer> getBuyer_list() {
		this.lock_read_buyer_list();
		ArrayList<Buyer> aux=new ArrayList<Buyer>(buyer_list);
		System.out.println("get buyer list");
		this.unlock_read_buyer_list();
		return aux;}
	
	public List<Tranzactie> getTransactions() {
		this.lock_read_transactions();
		ArrayList<Tranzactie> aux=new ArrayList<Tranzactie>(transactions);
		System.out.println("get trans list");
		this.unlock_read_transactions();
		return aux;}

	public void buy_stock(Seller seller,Buyer buyer,int price,int no_stock)
	{   this.lock_write_transactions();
		this.transactions.add(new Tranzactie(seller.getId_seller(),buyer.getId_buyer(),price,no_stock<seller.getNo_stock()?no_stock:seller.getNo_stock()));
	    seller.sell_stock(this.getSeller_list());
	    this.unlock_write_transactions();}
	
	public Seller getSeller(int price)
	{for(Seller se:this.getSeller_list())
    	if(se.getPrice()==price)
    		return se;
	return null;
		
	}
	
	public boolean buy_stock(Buyer bu)
	{    Seller se;
	    se=this.getSeller(bu.getPrice());
	    if(se==null)
	    	return false;
	    int no_stock_real=bu.getNo_stocks()<se.getNo_stock()?bu.getNo_stocks():se.getNo_stock();
	    List<Seller> seller_list=this.getSeller_list();
	    //this.lock_write_seller_list();
	    se.sell_stock(seller_list);
	    //this.unlock_write_seller_list();
	    this.lock_write_transactions();
	    this.transactions.add(new Tranzactie(se.getId_seller(),bu.getId_buyer(),bu.getPrice(),no_stock_real));
	    this.unlock_write_transactions();
	    //this.lock_write_buyer_list();
	    bu.calculateStock();
	    //this.unlock_write_buyer_list();
	    return true;}

	private void lock_read_seller_list()
	   {System.out.println("lock_read_seller_list"+this.seller_list_readers+" "+this.seller_list_writers+" "+this.seller_list_writereq);
		synchronized(this.o_sell)
		{while(seller_list_writers>0||seller_list_writereq>0)
			try {
				this.o_sell.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();}
		this.seller_list_readers++;
		this.o_sell.notifyAll();}}
	   
	   private  void unlock_read_seller_list()
	   {System.out.println("unlock_read_seller_list"+this.seller_list_readers+" "+this.seller_list_writers+" "+this.seller_list_writereq);
         synchronized(this.o_sell)
		{this.seller_list_readers--;
		this.o_sell.notifyAll();}}

	   private void lock_write_seller_list()
	   {synchronized(this.o_sell)
		{System.out.println("lock_write_seller_list"+this.seller_list_readers+" "+this.seller_list_writers+" "+this.seller_list_writereq);
         seller_list_writereq++;
		 while(seller_list_writers>0||seller_list_readers>0)
			try {
				this.o_sell.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		this.seller_list_writereq--;
		this.seller_list_writers++;
		this.o_sell.notifyAll();}}
	   
	   private void unlock_write_seller_list()
	   {System.out.println("unlock_write_seller_list"+this.seller_list_readers+" "+this.seller_list_writers+" "+this.seller_list_writereq);
       synchronized(this.o_sell)
		{this.seller_list_writers--;
		this.o_sell.notifyAll();}}
	   
	   private void lock_read_buyer_list()
	   {System.out.println("lock_read_buyer_list"+this.buyer_list_readers+" "+this.buyer_list_writers+" "+this.buyer_list_writereq);
		synchronized(this.o_buy)
		{while(buyer_list_writers>0||buyer_list_writereq>0)
			try {
				this.o_buy.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		this.buyer_list_readers++;
		this.o_buy.notifyAll();}}
	   
	   private void unlock_read_buyer_list()
	   {System.out.println("unlock_read_buyer_list"+this.buyer_list_readers+" "+this.buyer_list_writers+" "+this.buyer_list_writereq);
          synchronized(this.o_buy)
		{this.buyer_list_readers--;
		this.o_buy.notifyAll();}}

	   private void lock_write_buyer_list()
	   {System.out.println("lock_write_buyer_list"+this.buyer_list_readers+" "+this.buyer_list_writers+" "+this.buyer_list_writereq);
        synchronized(this.o_buy)
		{buyer_list_writereq++;
		 while(buyer_list_writers>0||buyer_list_readers>0)
			try {
				this.o_buy.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();}
		this.buyer_list_writereq--;
		this.buyer_list_writers++;
		this.o_buy.notifyAll();}}
	   
	   private void unlock_write_buyer_list()
	   {System.out.println("unlock_write_buyer_list"+this.buyer_list_readers+" "+this.buyer_list_writers+" "+this.buyer_list_writereq);
       synchronized(this.o_buy)
		{this.buyer_list_writers--;
		this.o_buy.notifyAll();}}
	   
	   private void lock_read_transactions()
	   {System.out.println("lock_read_trans_list"+this.trans_readers+" "+this.trans_writers+" "+this.trans_writereq);
		synchronized(this.o_trans)
		{while(trans_writers>0||trans_writereq>0)
			try {
				this.o_trans.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();}
		this.trans_readers++;
		this.o_trans.notifyAll();}}
	   
	   private void unlock_read_transactions()
	   {System.out.println("unlock_read_trans_list"+this.trans_readers+" "+this.trans_writers+" "+this.trans_writereq);
		synchronized(this.o_trans)
		{this.trans_readers--;
		this.o_trans.notifyAll();}}

	   private void lock_write_transactions()
	   {System.out.println("lock_write_trans_list"+this.trans_readers+" "+this.trans_writers+" "+this.trans_writereq);
		synchronized(this.o_trans)
		{trans_writereq++;
		 while(trans_writers>0||trans_readers>0)
			try {
				this.o_trans.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();}
		this.trans_writereq--;
		this.trans_writers++;
		this.o_trans.notifyAll();}}
	   
	   private void unlock_write_transactions()
	   {System.out.println("unlock_write_trans_list"+this.trans_readers+" "+this.trans_writers+" "+this.trans_writereq);
		synchronized(this.o_trans)
		{this.trans_writers--;
		this.o_trans.notifyAll();}}
}
