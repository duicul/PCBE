
public abstract class Filter {
protected String name;
public Filter(String name)
{this.name=name;}
public abstract boolean apply(Event e);
}
