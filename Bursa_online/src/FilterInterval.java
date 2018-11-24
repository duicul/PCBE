
public class FilterInterval extends Filter {
private float a,b;
public FilterInterval(float a,float b,String name)
{super(name);
	this.a=a;
this.b=b;}
@Override
public boolean apply(Event e) {
	return e.value>=a&&e.value<=b&&(this.name==null||this.name.equals(e.name));
}

}
