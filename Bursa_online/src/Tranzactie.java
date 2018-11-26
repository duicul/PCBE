
public class Tranzactie {
private int id_seller,id_buyer,no_stocks,price;
public Tranzactie(int id_seller,int id_buyer,int no_stocks,int price)
{this.id_seller=id_seller;
this.id_buyer=id_buyer;
this.no_stocks=no_stocks;
this.price=price;
System.out.println("Transaction"+this.getId_buyer()+" "+this.getId_seller()+" "+this.getPrice()+" "+this.getNo_stocks());
}
public int getId_seller() {
	return id_seller;
}
public int getId_buyer() {
	return id_buyer;
}
public int getNo_stocks() {
	return no_stocks;
}
public int getPrice() {
	return price;
}
public String toString()
{return "Transaction"+this.getId_buyer()+" "+this.getId_seller()+" "+this.getPrice()+" "+this.getNo_stocks();}
}
