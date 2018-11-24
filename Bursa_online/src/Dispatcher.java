import java.util.ArrayList;
import java.util.List;

public class Dispatcher {
	private List<Triplet<String,Filter,Buyer>> buyer_subscr;
	private List<Triplet<String,Filter,Seller>> seller_subscr;
	private static Dispatcher d;
	
	private Dispatcher()
	{this.buyer_subscr=new ArrayList<Triplet<String,Filter,Buyer>>();
    this.seller_subscr=new ArrayList<Triplet<String,Filter,Seller>>();}
	
	public static Dispatcher create()
    {
    	d=(d==null)?new Dispatcher():d;	
    	return d;}
	
	public Triplet<String,Filter,Buyer> addSubscriber(Buyer b,Filter f,String e)
	{this.buyer_subscr.add(new Triplet<String, Filter, Buyer>(e,f,b));
	return new Triplet<String, Filter, Buyer>(e,f,b);}
	
	public Triplet<String,Filter,Seller> addSubscriber(Seller s,Filter f,String e)
	{this.seller_subscr.add(new Triplet<String, Filter, Seller>(e,f,s));
	return new Triplet<String, Filter, Seller>(e,f,s);}
	
	public void kill()
	{for(Triplet<String,Filter,Seller> ts:this.seller_subscr)
		{ts.third.inform(null,new Event("kill",-1,-1));}
	for(Triplet<String,Filter,Buyer> tb:this.buyer_subscr)
	{tb.third.inform(null,new Event("kill",-1,-1));}
	}
	
	public void publish(Seller s,Event e)
	{//System.out.println("publish seller "+e.value+e.name);
	for(Triplet<String,Filter,Buyer> ts:this.buyer_subscr)
	if(e.name.equals(ts.first)&&ts.second.apply(e))
	{ts.third.inform(s,e);
	}}
	
	public void publish(Buyer b,Event e)
	{//System.out.println("publish buyer "+e.value+e.name);
		for(Triplet<String,Filter,Seller> ts:this.seller_subscr)
		if(e.name.equals(ts.first)&&ts.second.apply(e))
		{ts.third.inform(b,e);
		}}	
	
	public boolean unsubscribe(Buyer b,Triplet<String,Filter,Buyer> tb)
	{if(tb.third==b)
		{this.buyer_subscr.remove(tb);
		return true;}
	return false;}
	
	public boolean unsubscribe(Seller s,Triplet<String,Filter,Seller> ts)
	{if(ts.third==s)
		{this.seller_subscr.remove(ts);
		return true;}
	return false;}
}
