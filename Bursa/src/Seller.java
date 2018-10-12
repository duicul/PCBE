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
    {this(id_seller,new Random().nextInt(50)+10,new Random().nextInt(500)+100,b);}
    
    public void generate()
    {this.no_stock=new Random().nextInt(50)+10;
    this.price=new Random().nextInt(500)+100;}
    
    public void sell_stock()
    {this.generate();}
    public void run(){}
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
