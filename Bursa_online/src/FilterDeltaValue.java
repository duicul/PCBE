
public class FilterDeltaValue extends Filter {
private final float value,delta;
	public FilterDeltaValue(float value,float delta,String name) {
		super(name);
		this.delta=delta;
		this.value=value;
	}

	@Override
	public boolean apply(Event e) {
		return (this.value-this.delta>=e.value)&&(this.value+this.delta<=e.value)&&this.name.equals(e.name);}

}
