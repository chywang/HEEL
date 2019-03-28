
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MaxCliqueGraph {

	private Map<Integer, DBLPObject> objects;
	private Map<Integer, DBLPObject> selectedObjects;
	private Map<String, Double> weightMap;
	private Map<String, Double> selectedWeightMap;

	private String targetAuthor;

	public MaxCliqueGraph(List<DBLPObject> linklessObjects, String targetAuthor) {
		this.targetAuthor = targetAuthor;
		objects = new HashMap<Integer, DBLPObject>();
		for (int i = 0; i < linklessObjects.size(); i++)
			objects.put(i, linklessObjects.get(i));
		selectedObjects = new HashMap<Integer, DBLPObject>();
		weightMap = new HashMap<String, Double>();
		selectedWeightMap = new HashMap<String, Double>();
		// graph construction
		for (int i = 0; i < objects.size(); i++) {
			for (int j = 0; j < i; j++) {
				DBLPObject bigObject = objects.get(i);
				DBLPObject samllObject = objects.get(j);
				double affinity = affinityScore(samllObject, bigObject);
				if (affinity > 0)
					weightMap.put(j + "\t" + i, affinity);
				// else
				// weightMap.put(j+"\t"+i, 0.0001);

			}
		}
	}

	public List<DBLPObject> getCliqueObjects() {
		List<DBLPObject> outcome = new ArrayList<DBLPObject>();
		for (int i : selectedObjects.keySet()) {
			outcome.add(selectedObjects.get(i));
		}
		return outcome;
	}

	public double getAvgAffinityScore() {
		double total = 0;
		for (String s : selectedWeightMap.keySet()) {
			total += selectedWeightMap.get(s);
		}
		return total;
	}

	public void selectClique() {
		while (!weightMap.isEmpty()) {
			double ran = Math.random();
			double totalSum = 0;
			for (String s : weightMap.keySet())
				totalSum += weightMap.get(s);
			double weightCount = 0;
			String findIndex = "";
			for (String s : weightMap.keySet()) {
				weightCount += weightMap.get(s) / totalSum;
				if (weightCount > ran) {
					findIndex = s;
					break;
				}
			}
			String[] items = findIndex.split("\t");
			// System.out.println("select edge\t"+findIndex);
			// now select this edge
			int smallIndex = Integer.parseInt(items[0]);
			int bigIndex = Integer.parseInt(items[1]);
			// System.out.println("add:\t"+objects.get(smallIndex));
			selectedObjects.put(smallIndex, objects.get(smallIndex));
			// objects.remove(smallIndex);
			// System.out.println("add:\t"+objects.get(bigIndex));
			selectedObjects.put(bigIndex, objects.get(bigIndex));
			// objects.remove(bigIndex);
			selectedWeightMap.put(findIndex, weightMap.get(findIndex));
			weightMap.remove(findIndex);
			// remove nodes that not connect with these two and these edges
			Set<Integer> neighborNodeSet = new HashSet<Integer>();
			for (String s : weightMap.keySet()) {
				String[] items1 = s.split("\t");
				// now select this edge
				int smallIndex1 = Integer.parseInt(items1[0]);
				int bigIndex1 = Integer.parseInt(items1[1]);
				if (smallIndex1 == smallIndex) {
					neighborNodeSet.add(bigIndex1);
				}
				if (bigIndex1 == bigIndex)
					neighborNodeSet.add(smallIndex1);
			}
			// removeNodes
			/*
			 * Map<Integer, DBLPObject> newObjects=new HashMap<Integer,
			 * DBLPObject>(objects); for (int i:objects.keySet()) { if
			 * (!neighborNodeSet.contains(i)) newObjects.remove(i); }
			 * objects=new HashMap<Integer, DBLPObject>(newObjects);
			 */
			// removeEdges
			Map<String, Double> newWeightMap = new HashMap<String, Double>(weightMap);
			for (String s : weightMap.keySet()) {
				String[] items1 = s.split("\t");
				// now select this edge
				int smallIndex1 = Integer.parseInt(items1[0]);
				int bigIndex1 = Integer.parseInt(items1[1]);
				if (!neighborNodeSet.contains(smallIndex1) && !neighborNodeSet.contains(bigIndex1))
					newWeightMap.remove(s);
			}
			weightMap = new HashMap<String, Double>(newWeightMap);
			// System.out.println("weight map
			// size\t"+weightMap.keySet().size());
		}
	}

	private double affinityScore(DBLPObject object1, DBLPObject object2) {
		Set<String> a1 = new HashSet<String>(object1.getAuthors());
		a1.remove(targetAuthor);
		Set<String> a2 = new HashSet<String>(object2.getAuthors());
		a2.remove(targetAuthor);
		double authorSim = sim(a1, a2);
		double termSim = sim(new HashSet<String>(object1.getPaperTerms()),
				new HashSet<String>(object2.getPaperTerms()));
		double sim = (authorSim + termSim) / 2;
		if (authorSim > 0)
			return sim;
		else
			return 0;
	}

	private double sim(Set<String> aSet, Set<String> bSet) {
		Set<String> joinSet = new HashSet<String>();
		for (String s : aSet) {
			if (bSet.contains(s))
				joinSet.add(s);
		}
		Set<String> unionSet = new HashSet<String>();
		unionSet.addAll(aSet);
		unionSet.addAll(bSet);
		return (double) joinSet.size() / unionSet.size();
	}

}
