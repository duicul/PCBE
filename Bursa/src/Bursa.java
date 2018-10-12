import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Bursa extends Thread {
private List<Seller> seller_list;
private List<Buyer> buyer_list;
private List<Tranzactie> transactions;
private static Bursa b=null;
    private Bursa(int no_seller,int no_buyer)
    {int i;
    seller_list=new ArrayList<Seller>();
    for(i=0;i<no_seller;i++)
    	seller_list.add(new Seller(i,b));
    buyer_list=new ArrayList<Buyer>();
    for(i=0;i<no_buyer;i++)
    	buyer_list.add(new Buyer(i,b));
    this.transactions=new ArrayList<Tranzactie>();
    	
    }
    public static Bursa create(int no_seller,int no_buyer)
    {b=(b==null)?new Bursa(no_seller,no_buyer):b;	
    return b;
    }
	public void run(){}
	
	public List<Seller> getSeller_list() {
		ArrayList<Seller> aux=new ArrayList<Seller>(seller_list.size());
		Collections.copy(aux, seller_list);
		return aux;}
	
	public List<Buyer> getBuyer_list() {
		ArrayList<Buyer> aux=new ArrayList<Buyer>(buyer_list.size());
		Collections.copy(aux, buyer_list);
		return aux;}
	
	public List<Tranzactie> getTransactions() {
		ArrayList<Tranzactie> aux=new ArrayList<Tranzactie>(transactions.size());
		Collections.copy(aux, transactions);
		return aux;}

	public synchronized void buy_stock(Seller seller,Buyer buyer,int price,int no_stock)
	{
		this.transactions.add(new Tranzactie(seller.getId_seller(),buyer.getId_buyer(),price,no_stock<seller.getNo_stock()?no_stock:seller.getNo_stock()));
	    seller.sell_stock();}
}
