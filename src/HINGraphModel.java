
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HINGraphModel {

	private Map<Integer, Node> graph;
	private Map<Integer, Set<Integer>> edges;
	private List<DBLPObject> objects;

	private String targetAuthor;
	private double[] weights;

	public HINGraphModel(List<DBLPObject> objects, String targetAuthor) {
		this.objects = new ArrayList<DBLPObject>(objects);
		this.targetAuthor = targetAuthor;
		this.weights = new double[] { 0.6, 0.1, 0.25, 0.05 };

		graph = new HashMap<Integer, Node>();
		edges = new HashMap<Integer, Set<Integer>>();

		for (DBLPObject object : objects) {
			boolean found = false;
			List<String> authors = object.getAuthors();
			for (String author : authors) {
				if (author.startsWith(targetAuthor) && author.indexOf("00") >= 0) {
					found = true;
					break;
				}
			}
			if (!found)
				continue;
			addRecordToGraph(object);
		}
		// printGraph();
		System.out.println("graph construction complete!");
	}

	private Map<Integer, Double> PAPADistribution(int paperId) {
		// current we assume P-A-P-A
		Set<Integer> aSet = getNeighbors(paperId, NodeType.AUTHOR);
		// System.out.println(aSet.size() + " authors");
		Map<Integer, Double> authorDistMap = new HashMap<Integer, Double>();
		for (int a : aSet) {
			authorDistMap.put(a, 1d / aSet.size());
		}
		// printMap(paperDistMap);
		Map<Integer, Double> coPaperDistMap = new HashMap<Integer, Double>();
		for (int author : authorDistMap.keySet()) {
			double dist = authorDistMap.get(author);
			Set<Integer> pSet = getNeighbors(author, NodeType.PAPER);
			for (int p : pSet) {
				if (!coPaperDistMap.containsKey(p))
					coPaperDistMap.put(p, dist / pSet.size());
				else {
					double oldDist = coPaperDistMap.get(p);
					oldDist += dist / pSet.size();
					coPaperDistMap.put(p, oldDist);
				}
			}
		}
		// System.out.println(coPaperDistMap.keySet().size() + " co-papers");
		// printMap(coAuthorDistMap);

		Map<Integer, Double> coAuthorDistMap = new HashMap<Integer, Double>();
		for (int coPaper : coPaperDistMap.keySet()) {
			double dist = coPaperDistMap.get(coPaper);
			Set<Integer> coASet = getTargetedAuthorsAsNeighbors(coPaper);
			for (int ca : coASet) {
				if (!coAuthorDistMap.containsKey(ca))
					coAuthorDistMap.put(ca, dist / coASet.size());
				else {
					double oldDist = coAuthorDistMap.get(ca);
					oldDist += dist / coASet.size();
					coAuthorDistMap.put(ca, oldDist);
				}
			}
		}
		// System.out.println(coAuthorDistMap.keySet().size() + " co-authors");

		return coAuthorDistMap;
	}

	private Map<Integer, Double> PVPADistribution(int paperId) {
		// current we assume P-V-P-A
		Set<Integer> vSet = getNeighbors(paperId, NodeType.VENUE);
		// System.out.println(vSet.size() + " venues");
		Map<Integer, Double> venueDistMap = new HashMap<Integer, Double>();
		for (int v : vSet) {
			venueDistMap.put(v, 1d / vSet.size());
		}
		// printMap(paperDistMap);
		Map<Integer, Double> coPaperDistMap = new HashMap<Integer, Double>();
		for (int venue : venueDistMap.keySet()) {
			double dist = venueDistMap.get(venue);
			Set<Integer> pSet = getNeighbors(venue, NodeType.PAPER);
			for (int p : pSet) {
				if (!coPaperDistMap.containsKey(p))
					coPaperDistMap.put(p, dist / pSet.size());
				else {
					double oldDist = coPaperDistMap.get(p);
					oldDist += dist / pSet.size();
					coPaperDistMap.put(p, oldDist);
				}
			}
		}
		// System.out.println(coPaperDistMap.keySet().size() + " co-papers");
		// printMap(coAuthorDistMap);

		Map<Integer, Double> coAuthorDistMap = new HashMap<Integer, Double>();
		for (int coPaper : coPaperDistMap.keySet()) {
			double dist = coPaperDistMap.get(coPaper);
			Set<Integer> coASet = getTargetedAuthorsAsNeighbors(coPaper);
			for (int ca : coASet) {
				if (!coAuthorDistMap.containsKey(ca))
					coAuthorDistMap.put(ca, dist / coASet.size());
				else {
					double oldDist = coAuthorDistMap.get(ca);
					oldDist += dist / coASet.size();
					coAuthorDistMap.put(ca, oldDist);
				}
			}
		}
		// System.out.println(coAuthorDistMap.keySet().size() + " co-authors");

		return coAuthorDistMap;
	}

	private Map<Integer, Double> PTPADistribution(int paperId) {
		// current we assume P-T-P-A
		Set<Integer> tSet = getNeighbors(paperId, NodeType.TERM);
		// System.out.println(tSet.size() + " terms");
		Map<Integer, Double> termDistMap = new HashMap<Integer, Double>();
		for (int t : tSet) {
			termDistMap.put(t, 1d / tSet.size());
		}
		// printMap(paperDistMap);
		Map<Integer, Double> coPaperDistMap = new HashMap<Integer, Double>();
		for (int term : termDistMap.keySet()) {
			double dist = termDistMap.get(term);
			Set<Integer> pSet = getNeighbors(term, NodeType.PAPER);
			for (int p : pSet) {
				if (!coPaperDistMap.containsKey(p))
					coPaperDistMap.put(p, dist / pSet.size());
				else {
					double oldDist = coPaperDistMap.get(p);
					oldDist += dist / pSet.size();
					coPaperDistMap.put(p, oldDist);
				}
			}
		}
		// System.out.println(coPaperDistMap.keySet().size() + " co-papers");
		// printMap(coAuthorDistMap);

		Map<Integer, Double> coAuthorDistMap = new HashMap<Integer, Double>();
		for (int coPaper : coPaperDistMap.keySet()) {
			double dist = coPaperDistMap.get(coPaper);
			Set<Integer> coASet = getTargetedAuthorsAsNeighbors(coPaper);
			for (int ca : coASet) {
				if (!coAuthorDistMap.containsKey(ca))
					coAuthorDistMap.put(ca, dist / coASet.size());
				else {
					double oldDist = coAuthorDistMap.get(ca);
					oldDist += dist / coASet.size();
					coAuthorDistMap.put(ca, oldDist);
				}
			}
		}
		// System.out.println(coAuthorDistMap.keySet().size() + " co-authors");

		return coAuthorDistMap;
	}

	private Map<Integer, Double> PYPADistribution(int paperId) {
		// current we assume P-Y-P-A
		Set<Integer> ySet = getNeighbors(paperId, NodeType.YEAR);
		// System.out.println(ySet.size() + " years");
		Map<Integer, Double> yearDistMap = new HashMap<Integer, Double>();
		for (int y : ySet) {
			yearDistMap.put(y, 1d / ySet.size());
		}
		// printMap(paperDistMap);
		Map<Integer, Double> coPaperDistMap = new HashMap<Integer, Double>();
		for (int year : yearDistMap.keySet()) {
			double dist = yearDistMap.get(year);
			Set<Integer> pSet = getNeighbors(year, NodeType.PAPER);
			for (int p : pSet) {
				if (!coPaperDistMap.containsKey(p))
					coPaperDistMap.put(p, dist / pSet.size());
				else {
					double oldDist = coPaperDistMap.get(p);
					oldDist += dist / pSet.size();
					coPaperDistMap.put(p, oldDist);
				}
			}
		}
		// System.out.println(coPaperDistMap.keySet().size() + " co-papers");
		// printMap(coAuthorDistMap);

		Map<Integer, Double> coAuthorDistMap = new HashMap<Integer, Double>();
		for (int coPaper : coPaperDistMap.keySet()) {
			double dist = coPaperDistMap.get(coPaper);
			Set<Integer> coASet = getTargetedAuthorsAsNeighbors(coPaper);
			for (int ca : coASet) {
				if (!coAuthorDistMap.containsKey(ca))
					coAuthorDistMap.put(ca, dist / coASet.size());
				else {
					double oldDist = coAuthorDistMap.get(ca);
					oldDist += dist / coASet.size();
					coAuthorDistMap.put(ca, oldDist);
				}
			}
		}
		// System.out.println(coAuthorDistMap.keySet().size() + " co-authors");

		return coAuthorDistMap;
	}

	public Map<String, Double> authorDistributionForTain(String paper) {
		int paperId = getId(paper, NodeType.PAPER);
		Map<String, Double> outcome = new HashMap<String, Double>();
		Set<Integer> allAuthors = getTargetedAuthors();
		Map<Integer, Double> papa = PAPADistribution(paperId);
		Map<Integer, Double> pvpa = PVPADistribution(paperId);
		Map<Integer, Double> ptpa = PTPADistribution(paperId);
		Map<Integer, Double> pypa = PYPADistribution(paperId);
		double aWeight = weights[0];
		double vWeight = weights[1];
		double tWeight = weights[2];
		double yWeight = weights[3];
		for (int authorId : allAuthors) {
			double totalWeight = 0;
			if (papa.containsKey(authorId))
				totalWeight += aWeight * papa.get(authorId);
			if (pvpa.containsKey(authorId))
				totalWeight += vWeight * pvpa.get(authorId);
			if (ptpa.containsKey(authorId))
				totalWeight += tWeight * ptpa.get(authorId);
			if (pypa.containsKey(authorId))
				totalWeight += yWeight * pypa.get(authorId);
			Node authorNode = graph.get(authorId);
			outcome.put(authorNode.name, totalWeight);
		}
		return outcome;
	}

	public Map<String, Double> authorDistributionForTainSHINE(String paper) {
		// load PriorMap
		Map<Integer, Double> priorMap = new HashMap<Integer, Double>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File("filter/" + targetAuthor + "/prior.txt")));
			String line;
			while ((line = br.readLine()) != null) {
				String[] items = line.split("\t");
				int author = getId(items[0], NodeType.AUTHOR);
				double prior = Double.parseDouble(items[1]);
				priorMap.put(author, prior);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		int paperId = getId(paper, NodeType.PAPER);
		Map<String, Double> outcome = new HashMap<String, Double>();
		Set<Integer> allAuthors = getTargetedAuthors();
		Map<Integer, Double> papa = PAPADistribution(paperId);
		Map<Integer, Double> pvpa = PVPADistribution(paperId);
		Map<Integer, Double> ptpa = PTPADistribution(paperId);
		Map<Integer, Double> pypa = PYPADistribution(paperId);
		double aWeight = weights[0];
		double vWeight = weights[1];
		double tWeight = weights[2];
		double yWeight = weights[3];
		for (int authorId : allAuthors) {
			double totalWeight = 0;
			if (papa.containsKey(authorId))
				totalWeight += aWeight * papa.get(authorId);
			if (pvpa.containsKey(authorId))
				totalWeight += vWeight * pvpa.get(authorId);
			if (ptpa.containsKey(authorId))
				totalWeight += tWeight * ptpa.get(authorId);
			if (pypa.containsKey(authorId))
				totalWeight += yWeight * pypa.get(authorId);
			totalWeight = totalWeight * priorMap.get(authorId);
			Node authorNode = graph.get(authorId);
			outcome.put(authorNode.name, totalWeight);
		}
		// normalize
		double total = 0;
		for (String s : outcome.keySet()) {
			total += outcome.get(s);
		}
		for (String s : outcome.keySet()) {
			outcome.put(s, outcome.get(s) / total);
		}
		return outcome;
	}

	private double getRawProbability(int authorId, int paperId, NodeType type) {
		double outcome = 0;
		switch (type) {
		case AUTHOR:
			Map<Integer, Double> papa = PAPADistribution(paperId);
			if (papa.containsKey(authorId))
				outcome = papa.get(authorId);
			break;
		case VENUE:
			Map<Integer, Double> pvpa = PVPADistribution(paperId);
			if (pvpa.containsKey(authorId))
				outcome = pvpa.get(authorId);
			break;
		case TERM:
			Map<Integer, Double> ptpa = PTPADistribution(paperId);
			if (ptpa.containsKey(authorId))
				outcome = ptpa.get(authorId);
			break;
		case YEAR:
			Map<Integer, Double> pypa = PYPADistribution(paperId);
			if (pypa.containsKey(authorId))
				outcome = pypa.get(authorId);
			break;
		default:
			break;
		}
		return outcome;
	}

	public Map<String, Double> authorDistributionForPrediction(DBLPObject object) {
		Map<Integer, Node> tempGraph = new HashMap<Integer, Node>(graph);
		Map<Integer, Set<Integer>> tempEdges = new HashMap<Integer, Set<Integer>>(edges);
		// add new records
		addUnlinkedRecordToGraph(object);
		// prediction
		String paper = object.getPaper();
		Map<String, Double> outcome = authorDistributionForTain(paper);
		// replace
		graph = new HashMap<Integer, Node>(tempGraph);
		edges = new HashMap<Integer, Set<Integer>>(tempEdges);
		return outcome;
	}

	public Map<String, Double> authorDistributionForPredictionSHINE(DBLPObject object) {
		Map<Integer, Node> tempGraph = new HashMap<Integer, Node>(graph);
		Map<Integer, Set<Integer>> tempEdges = new HashMap<Integer, Set<Integer>>(edges);
		// add new records
		addUnlinkedRecordToGraph(object);
		// prediction
		String paper = object.getPaper();
		Map<String, Double> outcome = authorDistributionForTainSHINE(paper);
		// replace
		graph = new HashMap<Integer, Node>(tempGraph);
		edges = new HashMap<Integer, Set<Integer>>(tempEdges);
		return outcome;
	}

	public double logDataLikelihood() {
		double logLikelihood = 0;
		for (DBLPObject object : objects) {
			List<String> authors = object.getAuthors();
			Map<String, Double> probs = authorDistributionForTain(object.getPaper());
			for (String s : authors) {
				if (s.startsWith(targetAuthor)) {
					double prob = probs.get(s);
					logLikelihood += Math.log(prob);
					break;
				}
			}
		}
		return logLikelihood;
	}

	public void train() {
		// initialize
		double rate = 0.001;
		int iterCount = 0;
		// learn
		double[] oldWeights = new double[] { 1, 0, 0, 0 };
		while (arrDiff(oldWeights, weights) > 0.01) {
			iterCount++;
			System.out.println("iter:\t" + iterCount);
			for (double w : weights)
				System.out.print(w + "\t");
			System.out.println();

			oldWeights = Arrays.copyOfRange(weights, 0, weights.length);
			// author
			double aWeight = weights[0];
			for (DBLPObject object : objects) {
				int paperId = getId(object.getPaper(), NodeType.PAPER);
				int authorId = getPaperAuthorIdForTrain(object);
				if (paperId == -1 || authorId == -1)
					continue;

				double aWeightUpdateTotal = weights[0] * getRawProbability(authorId, paperId, NodeType.AUTHOR)
						+ weights[1] * getRawProbability(authorId, paperId, NodeType.VENUE)
						+ weights[2] * getRawProbability(authorId, paperId, NodeType.TERM)
						+ weights[3] * getRawProbability(authorId, paperId, NodeType.YEAR);
				aWeight += rate * getRawProbability(authorId, paperId, NodeType.AUTHOR) / aWeightUpdateTotal;

			}
			// venue
			double vWeight = weights[1];
			for (DBLPObject object : objects) {
				int paperId = getId(object.getPaper(), NodeType.PAPER);
				int authorId = getPaperAuthorIdForTrain(object);
				if (paperId == -1 || authorId == -1)
					continue;
				double vWeightUpdateTotal = weights[0] * getRawProbability(authorId, paperId, NodeType.AUTHOR)
						+ weights[1] * getRawProbability(authorId, paperId, NodeType.VENUE)
						+ weights[2] * getRawProbability(authorId, paperId, NodeType.TERM)
						+ weights[3] * getRawProbability(authorId, paperId, NodeType.YEAR);
				vWeight += rate * getRawProbability(authorId, paperId, NodeType.VENUE) / vWeightUpdateTotal;

			}
			// term
			double tWeight = weights[2];
			for (DBLPObject object : objects) {
				int paperId = getId(object.getPaper(), NodeType.PAPER);
				int authorId = getPaperAuthorIdForTrain(object);
				if (paperId == -1 || authorId == -1)
					continue;
				double tWeightUpdateTotal = weights[0] * getRawProbability(authorId, paperId, NodeType.AUTHOR)
						+ weights[1] * getRawProbability(authorId, paperId, NodeType.VENUE)
						+ weights[2] * getRawProbability(authorId, paperId, NodeType.TERM)
						+ weights[3] * getRawProbability(authorId, paperId, NodeType.YEAR);
				tWeight += rate * getRawProbability(authorId, paperId, NodeType.TERM) / tWeightUpdateTotal;

			}
			// year
			double yWeight = weights[3];
			for (DBLPObject object : objects) {
				int paperId = getId(object.getPaper(), NodeType.PAPER);
				int authorId = getPaperAuthorIdForTrain(object);
				if (paperId == -1 || authorId == -1)
					continue;
				double yWeightUpdateTotal = weights[0] * getRawProbability(authorId, paperId, NodeType.AUTHOR)
						+ weights[1] * getRawProbability(authorId, paperId, NodeType.VENUE)
						+ weights[2] * getRawProbability(authorId, paperId, NodeType.TERM)
						+ weights[3] * getRawProbability(authorId, paperId, NodeType.YEAR);
				yWeight += rate * getRawProbability(authorId, paperId, NodeType.YEAR) / yWeightUpdateTotal;
			}
			// normalize
			double sum = aWeight + vWeight + tWeight + yWeight;
			weights[0] = aWeight / sum;
			weights[1] = vWeight / sum;
			weights[2] = tWeight / sum;
			weights[3] = yWeight / sum;
		}
	}

	private int getPaperAuthorIdForTrain(DBLPObject object) {
		for (String author : object.getAuthors()) {
			if (author.startsWith(targetAuthor)) {
				return getId(author, NodeType.AUTHOR);
			}
		}
		return -1;
	}

	private Set<Integer> getNeighbors(int fromId, NodeType toType) {
		Set<Integer> nodeSet = edges.get(fromId);
		Set<Integer> outcome = new HashSet<Integer>();
		for (int i : nodeSet) {
			Node n = graph.get(i);
			if (n != null && n.type == toType)
				outcome.add(n.id);
		}
		return outcome;
	}

	private Set<Integer> getTargetedAuthorsAsNeighbors(int fromId) {
		Set<Integer> nodeSet = edges.get(fromId);
		Set<Integer> outcome = new HashSet<Integer>();
		for (int i : nodeSet) {
			Node n = graph.get(i);
			if (n.type == NodeType.AUTHOR && n.name.startsWith(targetAuthor))
				outcome.add(n.id);
		}
		return outcome;
	}

	public Set<Integer> getAllNodes(NodeType type) {
		Set<Integer> outcome = new HashSet<Integer>();
		for (int i : graph.keySet()) {
			Node node = graph.get(i);
			if (node.type == type) {
				outcome.add(node.id);
			}
		}
		return outcome;
	}

	public Set<Integer> getTargetedAuthors() {
		Set<Integer> outcome = new HashSet<Integer>();
		for (int i : graph.keySet()) {
			Node n = graph.get(i);
			if (n.type == NodeType.AUTHOR && n.name.startsWith(targetAuthor))
				outcome.add(n.id);
		}
		return outcome;
	}

	private void addEdge(int from, int to) {
		if (!edges.containsKey(from))
			edges.put(from, new HashSet<Integer>());
		Set<Integer> set = edges.get(from);
		set.add(to);
		edges.put(from, set);
	}

	public void addRecordToGraph(DBLPObject object) {
		String title = object.getPaper();
		int titleId = getId(title, NodeType.PAPER);
		if (titleId == -1) {
			int nodeCount = graph.keySet().size();
			graph.put(nodeCount, new Node(nodeCount, title, NodeType.PAPER));
			titleId = nodeCount;
		}

		String venue = object.getVenue();
		int venueId = getId(venue, NodeType.VENUE);
		if (venueId == -1) {
			int nodeCount = graph.keySet().size();
			graph.put(nodeCount, new Node(nodeCount, venue, NodeType.VENUE));
			venueId = nodeCount;

		}
		addEdge(titleId, venueId);
		addEdge(venueId, titleId);

		int year = object.getYear();
		int yearId = getId(String.valueOf(year), NodeType.YEAR);
		if (yearId == -1) {
			int nodeCount = graph.keySet().size();
			graph.put(nodeCount, new Node(nodeCount, String.valueOf(year), NodeType.YEAR));
			yearId = nodeCount;

		}
		addEdge(titleId, yearId);
		addEdge(yearId, titleId);

		List<String> terms = object.getPaperTerms();
		for (String term : terms) {
			int termId = getId(term, NodeType.TERM);
			if (termId == -1) {
				int nodeCount = graph.keySet().size();
				graph.put(nodeCount, new Node(nodeCount, term, NodeType.TERM));
				termId = nodeCount;
			}
			addEdge(titleId, termId);
			addEdge(termId, titleId);
		}

		List<String> authors = object.getAuthors();
		for (String author : authors) {
			int authorId = getId(author, NodeType.AUTHOR);
			if (authorId == -1) {
				int nodeCount = graph.keySet().size();
				graph.put(nodeCount, new Node(nodeCount, author, NodeType.AUTHOR));
				authorId = nodeCount;
			}
			addEdge(titleId, authorId);
			addEdge(authorId, titleId);
		}
	}

	public void printGraph() {
		System.out.println("nodes");
		for (int i : graph.keySet()) {
			Node node = graph.get(i);
			System.out.println(node.id + "\t" + node.name + "\t" + node.type);
		}
		/*
		 * System.out.println("edges"); for (int i : edges.keySet()) {
		 * Set<Integer> set = edges.get(i); for (int j : set) {
		 * System.out.println(i + "\t" + j); } }
		 */

	}

	private void addUnlinkedRecordToGraph(DBLPObject object) {
		String title = object.getPaper();
		int titleId = getId(title, NodeType.PAPER);
		int nodeCount = graph.keySet().size();
		if (titleId == -1) {
			graph.put(nodeCount, new Node(nodeCount, title, NodeType.PAPER));
			titleId = nodeCount;
		}

		String venue = object.getVenue();
		int venueId = getId(venue, NodeType.VENUE);
		if (venueId == -1) {
			nodeCount = graph.keySet().size();
			graph.put(nodeCount, new Node(nodeCount, venue, NodeType.VENUE));
			venueId = nodeCount;

		}
		addEdge(titleId, venueId);
		addEdge(venueId, titleId);

		int year = object.getYear();
		int yearId = getId(String.valueOf(year), NodeType.YEAR);
		if (yearId == -1) {
			nodeCount = graph.keySet().size();
			graph.put(nodeCount, new Node(nodeCount, String.valueOf(year), NodeType.YEAR));
			yearId = nodeCount;

		}
		addEdge(titleId, yearId);
		addEdge(yearId, titleId);

		List<String> terms = object.getPaperTerms();
		for (String term : terms) {
			int termId = getId(term, NodeType.TERM);
			if (termId == -1) {
				nodeCount = graph.keySet().size();
				graph.put(nodeCount, new Node(nodeCount, term, NodeType.TERM));
				termId = nodeCount;
			}
			addEdge(titleId, termId);
			addEdge(termId, titleId);
		}

		List<String> authors = object.getAuthors();
		for (String author : authors) {
			if (author.equals(targetAuthor))
				continue;
			int authorId = getId(author, NodeType.AUTHOR);
			if (authorId == -1) {
				nodeCount = graph.keySet().size();
				graph.put(nodeCount, new Node(nodeCount, author, NodeType.AUTHOR));
				authorId = nodeCount;
			}
			addEdge(titleId, authorId);
			addEdge(authorId, titleId);
		}
	}

	private int getId(String name, NodeType type) {
		for (int i : graph.keySet()) {
			Node node = graph.get(i);
			if (node.name.equals(name) && node.type == type)
				return node.id;
		}
		return -1;
	}

	private double arrDiff(double[] oldWeights, double[] newWeights) {
		double outcome = 0;
		for (int i = 0; i < oldWeights.length; i++)
			outcome += Math.pow(oldWeights[i] - newWeights[i], 2);
		return Math.sqrt(outcome);
	}

	public void addRecords(DBLPObject object) {
		objects.add(object);
	}

	public void checkGraph() {
		System.out.println("node size:\t" + graph.keySet().size());
		Set<Integer> totalEdges = new HashSet<Integer>();
		for (int i : edges.keySet()) {
			totalEdges.addAll(edges.get(i));
		}
		for (int i : totalEdges) {
			if (!graph.containsKey(i))
				System.out.println(i + "\tnot found in node set!");
		}
		for (int i : graph.keySet()) {
			if (!totalEdges.contains(i))
				System.out.println(i + "\tnot found in edge set!");
		}
		System.out.println("graph check ok!");
	}

	public List<DBLPObject> getObjects() {
		return objects;
	}

}
