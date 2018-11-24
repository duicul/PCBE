
public class FilterValue extends Filter {
private final float value;
	public FilterValue(float value,String name) {
		super(name);
		this.value=value;}

	@Override
	public boolean apply(Event e) {
		return e.value==this.value&&this.name.equals(e.name);}

}
