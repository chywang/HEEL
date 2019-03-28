
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class EMAlg {

	private HINGraphModel graphModel;
	private List<DBLPObject> linklessObjects;
	private List<String> unlinkedPapers;
	private String targetAuthor;
	private int newAuthorCount = 0;

	public EMAlg(List<DBLPObject> objects, String targetAuthor) {
		this.targetAuthor = targetAuthor;
		this.linklessObjects = new ArrayList<DBLPObject>();
		this.unlinkedPapers = new ArrayList<String>();
		for (DBLPObject object : objects) {
			List<String> authors = object.getAuthors();
			for (String author : authors) {
				if (author.startsWith(targetAuthor) && author.indexOf("00") < 0) {
					linklessObjects.add(object);
					unlinkedPapers.add(object.getPaper());
					break;
				}
			}
		}
		graphModel = new HINGraphModel(objects, targetAuthor);
	}

	public void init() {
		graphModel.train();
	}

	public void iterate() {
		int size = linklessObjects.size();
		while (true) {
			graphModel.train();
			tryLinkForExistingAuthors();
			boolean outcome = tryAddNewAuthor();
			if (!outcome)
				break;
			int newSize = linklessObjects.size();
			if (newSize >= size)
				break;
			else
				size = newSize;
		}
	}

	public String generateTuplesAndPredict() {
		List<DBLPObject> allRecords = getAllRecords();
		int ran1 = (int) (Math.random() * allRecords.size());
		int ran2 = (int) (Math.random() * allRecords.size());
		while (!unlinkedPapers.contains(allRecords.get(ran1).getPaper())
				|| !unlinkedPapers.contains(allRecords.get(ran2).getPaper())) {
			ran1 = (int) (Math.random() * allRecords.size());
			ran2 = (int) (Math.random() * allRecords.size());
		}
		String author1 = getAuthorName(allRecords.get(ran1));
		String author2 = getAuthorName(allRecords.get(ran2));
		int outcome;
		if (author1.indexOf("00") < 0 && author2.indexOf("00") < 0)
			outcome = 0;
		else if (!author1.equals(author2))
			outcome = -1;
		else
			outcome = 1;
		return allRecords.get(ran1).getPaper() + "\t" + allRecords.get(ran2).getPaper() + "\t" + outcome;
	}

	public boolean tryAddNewAuthor() {
		CliqueObjectsGenerator generator = new CliqueObjectsGenerator(linklessObjects, targetAuthor);
		List<DBLPObject> objects = generator.getCliqueObjects(10);
		System.out.println(objects.size() + " new cluster formed!");
		createNewAuthorAndInsert(objects);
		if (objects.size() == 0)
			return false;
		else
			return true;
	}

	public void createNewAuthorAndInsert(List<DBLPObject> objects) {
		String newAuthorName = targetAuthor + " 0000" + newAuthorCount;
		for (DBLPObject object : objects) {
			DBLPObject changeAuthor = changeAuthor(object, newAuthorName);
			System.out.println(changeAuthor);
			graphModel.addRecords(changeAuthor);
			graphModel.addRecordToGraph(changeAuthor);
		}
		newAuthorCount++;
	}

	public List<DBLPObject> getAllRecords() {
		List<DBLPObject> list = new ArrayList<DBLPObject>(linklessObjects);
		list.addAll(graphModel.getObjects());
		return list;
	}

	public void tryLinkForExistingAuthors() {
		int count = 0;
		for (int i = 0; i < linklessObjects.size(); i++) {
			DBLPObject object = linklessObjects.get(i);
			// System.out.println(object);
			Map<String, Double> map = graphModel.authorDistributionForPrediction(object);
			String predictedAuthor = linkBasedOnOddsThres(0.1, map);
			if (predictedAuthor != null) {
				// System.out.println(predictedAuthor+"\t"+object);
				count++;
				// add records to graph
				DBLPObject newObject = changeAuthor(object, predictedAuthor);
				linklessObjects.remove(i);
				System.out.println("newlink:" + "\t" + newObject);
				graphModel.addRecordToGraph(newObject);
				graphModel.addRecords(newObject);
			}
		}
		System.out.println(count);
		System.out.println("records size:\t" + linklessObjects.size());
	}

	public String linkPredict(DBLPObject object) {
		Map<String, Double> map = graphModel.authorDistributionForPrediction(object);
		String predictedAuthor = linkBasedOnOddsThres(0.1, map);
		return predictedAuthor;
	}

	public String getAuthorName(DBLPObject object) {
		String outcome = "";
		List<String> authors = object.getAuthors();
		for (String s : authors) {
			if (s.startsWith(targetAuthor))
				outcome = s;
		}
		return outcome;
	}

	public DBLPObject removeAuthorID(DBLPObject object) {
		List<String> authors = object.getAuthors();
		for (int i = 0; i < authors.size(); i++) {
			String s = authors.get(i);
			if (s.startsWith(targetAuthor) && s.indexOf("00") >= 0) {
				String s1 = "";
				String[] items = s.split(" ");
				for (int j = 0; j < items.length - 1; j++)
					s1 += items[j] + " ";
				s1 = s1.trim();
				authors.set(i, s1);
			}
		}
		return new DBLPObject(object.getKey(), object.getPaper(), object.getVenue(), object.getYear(),
				object.getAuthors());
	}

	private String linkBasedOnOddsThres(double thres, Map<String, Double> map) {
		List<Pair> pairs = new ArrayList<Pair>();
		for (String s : map.keySet()) {
			double score = map.get(s);
			pairs.add(new Pair(s, score));
		}
		Collections.sort(pairs);
		Collections.reverse(pairs);
		String largestAuthor = (String) (pairs.get(0).getElement());
		double max1Score = pairs.get(0).getWeight();
		double max2Score = pairs.get(1).getWeight();
		if (max1Score / max2Score > 1.5 && max1Score > thres)
			return largestAuthor;
		else
			return null;
	}

	private DBLPObject changeAuthor(DBLPObject object, String predictedAuthor) {
		List<String> authors = new ArrayList<String>(object.getAuthors());
		for (int i = 0; i < authors.size(); i++) {
			String s = authors.get(i);
			if (s.equals(targetAuthor)) {
				s = predictedAuthor;
				authors.set(i, s);
			}
		}
		return new DBLPObject(object.getKey(), object.getPaper(), object.getVenue(), object.getYear(), authors);
	}

}
