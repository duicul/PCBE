
public class FilterSubscrValue extends Filter{
private final Subscriber sub;
	public FilterSubscrValue(String name,Subscriber sub) {
		super(name);
		this.sub=sub;
	}
	public boolean apply(Event e)
	{return super.apply(e)&&e.value==sub.getPrice();}
}
