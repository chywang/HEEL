
import java.util.ArrayList;
import java.util.List;

public class DBLPObject {

	private String key;
	private String paper;
	private String venue;
	private int year;
	private List<String> authors;

	public DBLPObject(String key, String paper, String venue, int year, List<String> authors) {
		super();
		this.key = key;
		this.paper = paper;
		this.venue = venue;
		this.year = year;
		this.authors = authors;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getPaper() {
		return paper;
	}

	public List<String> getPaperTerms() {
		List<String> list = new ArrayList<String>();
		String[] items = paper.split(" ");
		for (String s : items) {
			list.add(s);
		}
		return list;
	}

	public void setPaper(String paper) {
		this.paper = paper;
	}

	public String getVenue() {
		return venue;
	}

	public void setVenue(String venue) {
		this.venue = venue;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public List<String> getAuthors() {
		return authors;
	}

	public void setAuthors(List<String> authors) {
		this.authors = authors;
	}

	@Override
	public String toString() {
		String outcome = key + "\t" + paper + "\t" + venue + "\t" + year + "\t";
		for (String au : authors)
			outcome += au + "\t";
		return outcome.trim();
	}

	public static DBLPObject parse(String line) {
		String[] items = line.split("\t");
		String key = items[0];
		String paper = items[1];
		String venue = items[2];
		int year = Integer.parseInt(items[3]);
		List<String> authors = new ArrayList<String>();
		for (int i = 4; i < items.length; i++)
			authors.add(items[i]);
		return new DBLPObject(key, paper, venue, year, authors);
	}

}
