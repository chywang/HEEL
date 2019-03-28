import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class HINMain {

	public static void main(String[] args) {
		if (args.length < 3) {
			System.out.println("Input parameters error! Program terminated!");
			return;
		}

		String inFile = args[0];
		String outFile = args[1];
		String authorName = "";
		for (int i = 2; i < args.length; i++)
			authorName += args[i] + " ";
		authorName = authorName.trim();

		BufferedReader br;
		List<DBLPObject> objects = new ArrayList<DBLPObject>();

		try {
			br = new BufferedReader(new FileReader(new File(inFile)));
			String line;
			while ((line = br.readLine()) != null) {
				DBLPObject object = DBLPObject.parse(line);
				objects.add(object);
			}
			br.close();
		} catch (IOException e) {
			System.out.println("Input file error! Program terminated!");
			return;
		}

		EMAlg emAlg = new EMAlg(objects, authorName);
		emAlg.init();
		emAlg.iterate();

		List<DBLPObject> newObjects = emAlg.getAllRecords();
		PrintWriter pw;
		try {
			pw = new PrintWriter(outFile);
			for (DBLPObject object : newObjects) {
				pw.println(object);
				pw.flush();
			}
			pw.close();
		} catch (FileNotFoundException e) {
			System.out.println("Output file error! Program terminated!");
			return;
		}

	}
}
