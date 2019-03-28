
public class Pair implements Comparable<Pair> {

	private Object element;
	private double weight;

	public Pair(Object element, double weight) {
		super();
		this.element = element;
		this.weight = weight;
	}

	public Object getElement() {
		return element;
	}

	public void setElement(Object element) {
		this.element = element;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public int compareTo(Pair o) {
		if (weight > o.weight)
			return 1;
		else if (weight < o.weight)
			return -1;
		else
			return 0;
	}

}
