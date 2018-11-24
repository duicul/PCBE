
public class Filter {
protected String name;
public Filter(String name)
{this.name=name;}
public boolean apply(Event e)
{return e.name.equals(this.name);}
}
