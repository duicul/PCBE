import java.util.List;
import java.util.Random;

public class Seller extends Thread {
private int id_seller,no_stock,price;
private Bursa b;
    public Seller(int id_seller,int no_stock,int price,Bursa b)
    {this.id_seller=id_seller;
    this.no_stock=no_stock;
    this.price=price;
    this.b=b;
    }
    public Seller(int id_seller,Bursa b)
    {this(id_seller,new Random().nextInt(50)+10,new Random().nextInt(500)+100,b);
    System.out.println("Seller "+this.getNo_stock()+" "+this.getPrice());}
    
    public synchronized void generate(List<Seller> seller_list)
    {List<Buyer> buyer_list=b.getBuyer_list();
    System.out.println("calc sell stock "+this.getNo_stock()+" "+this.getPrice());
    List<Tranzactie> transactions=b.getTransactions();
    Buyer bu=buyer_list.get(new Random().nextInt(buyer_list.size()));
    Seller se=seller_list.get(new Random().nextInt(seller_list.size()));
    if(transactions.size()==0)
    {this.price=(se.getPrice()+bu.getPrice())/2;	
    this.no_stock=(se.getNo_stock()+bu.getNo_stocks())/2;}	
    else
    {Tranzactie tr=transactions.get(new Random().nextInt(transactions.size()));
    this.price=(se.getPrice()+tr.getPrice()+bu.getPrice())/3;	
    this.no_stock=(se.getNo_stock()+tr.getNo_stocks()+bu.getNo_stocks())/3;}
    System.out.println("generate "+this.getNo_stock()+" "+this.getPrice());}
    
    public void sell_stock(List<Seller> seller_list)
    {System.out.println("sell stock "+this.getNo_stock()+" "+this.getPrice());
    	this.generate(seller_list);}
    
    public void run()
    {long init=System.currentTimeMillis();while(System.currentTimeMillis()-init<60000);}

    public int getId_seller() {
		return id_seller;
	}

	public int getNo_stock() {
		return no_stock;
	}
	public int getPrice() {
		return price;
	}
    
}
