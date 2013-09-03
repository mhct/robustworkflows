package be.kuleuven.robustworkflows.playground;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;

import com.google.common.io.Files;

import be.kuleuven.robustworkflows.infrastructure.configuration.GephiGraphImporter;

public class CloudReader {

	public static void main(String[] args) throws IOException {
		String filename = "1000c-100f-long-times.gexf";
		File gephiFile = new File("datasets/" + filename);
		Graph g = GephiGraphImporter.loadDirectedGraphFrom(gephiFile);
		
		File gephiCSV = new File("/tmp/" + filename + "A.csv");
//		Files.append(factoriesToCSV(g), gephiCSV, Charset.defaultCharset());
		BufferedWriter bw = Files.newWriter(gephiCSV, Charset.defaultCharset());
		bw.write(factoriesToCSV(g).toString());
		bw.close();
	}
	
	private static StringBuilder factoriesToCSV(Graph graph) {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("id,ServiceType,time\n");
		for (Node n: graph.getNodes()) {
			String st = (String) n.getAttributes().getValue("ServiceType");
			if (st != null && st.equals("A")) {
				sb.append(n.getId());
				sb.append(",");
				sb.append(n.getAttributes().getValue("ServiceType"));
				sb.append(",");
				sb.append(n.getAttributes().getValue("ProcessingTimePerRequest"));
				sb.append("\n");
			}
			
		}
		
		return sb;
	}
}
