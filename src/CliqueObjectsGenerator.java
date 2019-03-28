
import java.util.List;

public class CliqueObjectsGenerator {

	private List<DBLPObject> linklessObjects;
	private String targetAuthor;

	public CliqueObjectsGenerator(List<DBLPObject> linklessObjects, String targetAuthor) {
		this.linklessObjects = linklessObjects;
		this.targetAuthor = targetAuthor;
	}

	public List<DBLPObject> getCliqueObjects(int k) {
		MaxCliqueGraph graph = new MaxCliqueGraph(linklessObjects, targetAuthor);
		graph.selectClique();
		List<DBLPObject> objects = graph.getCliqueObjects();
		double score = graph.getAvgAffinityScore();
		int iter = 0;
		while (iter < k) {
			System.out.println("iter:\t" + iter + "\t" + objects.size());
			MaxCliqueGraph newGraph = new MaxCliqueGraph(linklessObjects, targetAuthor);
			newGraph.selectClique();
			List<DBLPObject> newObjects = newGraph.getCliqueObjects();
			double newScore = newGraph.getAvgAffinityScore();
			if (newScore > score) {
				score = newScore;
				objects = newObjects;
			}
			iter++;
		}
		return objects;
	}

}
