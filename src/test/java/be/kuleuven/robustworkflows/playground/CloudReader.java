package be.kuleuven.robustworkflows.playground;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;

import be.kuleuven.robustworkflows.infrastructure.configuration.GephiGraphImporter;

import com.google.common.io.Files;

public class CloudReader {

	public static void main(String[] args) throws IOException {
		String filename = "exp1/ABC100c-15f-2050et-1950aet-1.0asp-another_distribution.gexf";
		String outputfile = "factories-trial1.csv";
		File gephiFile = new File("datasets/" + filename);
		Graph g = GephiGraphImporter.loadDirectedGraphFrom(gephiFile);
		
		File gephiCSV = new File("/tmp/" + outputfile);
		BufferedWriter bw = Files.newWriter(gephiCSV, Charset.defaultCharset());
		bw.write(factoriesToCSV(g).toString());
		bw.close();
	}
	
	private static StringBuilder factoriesToCSV(Graph graph) {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("id,ServiceType,time\n");
		for (Node n: graph.getNodes()) {
			String st = (String) n.getAttributes().getValue("ServiceType");
			if (st != null && ! "".equals(st)) {
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
