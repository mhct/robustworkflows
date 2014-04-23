package be.kuleuven.robustworkflows.util;

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
		
		String filename = "/tmp/a.gexf";
		String outputfile = "/tmp/factories-trial3.csv";

		if (args.length == 2 ) {
			filename = args[0];
			outputfile = args[1];
		}
		
		File gephiFile = new File(filename);
		Graph g = GephiGraphImporter.loadDirectedGraphFrom(gephiFile);
		
		File gephiCSV = new File(outputfile);
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
