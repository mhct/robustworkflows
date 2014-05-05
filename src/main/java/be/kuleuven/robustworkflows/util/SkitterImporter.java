package be.kuleuven.robustworkflows.util;

/*
 * Copyright 2008-2010 Gephi
 * Authors : Cezary Bartosiak
 * Website : http://www.gephi.org
 * 
 * This file is part of Gephi.
 *
 * Gephi is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Gephi is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Gephi.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.gephi.io.exporter.api.ExportController;
import org.gephi.io.generator.spi.Generator;
import org.gephi.io.generator.spi.GeneratorUI;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.ContainerFactory;
import org.gephi.io.importer.api.ContainerLoader;
import org.gephi.io.importer.api.EdgeDefault;
import org.gephi.io.importer.api.EdgeDraft;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.importer.api.NodeDraft;
import org.gephi.io.importer.api.Report;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.project.api.ProjectController;
import org.gephi.utils.progress.Progress;
import org.gephi.utils.progress.ProgressTicket;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

import com.google.common.io.Files;

/**
 * Generates an undirected connected graph.
 *
 * http://en.wikipedia.org/wiki/Barabási–Albert_model
 * http://www.barabasilab.com/pubs/CCNR-ALB_Publications/199910-15_Science-Emergence/199910-15_Science-Emergence.pdf
 * http://www.facweb.iitkgp.ernet.in/~niloy/COURSE/Spring2006/CNT/Resource/ba-model-2.pdf
 *
 * N  > 0
 * m0 > 0 && m0 <  N
 * M  > 0 && M  <= m0
 *
 * O(N^2 * M)
 *
 * @author Cezary Bartosiak
 */
@ServiceProvider(service = Generator.class)
public class SkitterImporter implements Generator {
	private boolean cancel = false;
	private ProgressTicket progressTicket;
	private BufferedReader reader;

	private AgentNodesCreator agentsCreator;
	
	public void setAgentsCreator(AgentNodesCreator agentsCreator) {
		this.agentsCreator = agentsCreator;
	}
	
	private NodeDraft[] nodes;
	private int nbNodes;
	private int nbCreatedNodes;

	
	/**
	 * Limits the maximum number of nodes to be loaded from a file
	 * 
	 * @param nbNodes
	 */
	public void setNbNodes(int nbNodes) {
		this.nbNodes = nbNodes;
	}
	
	/**
	 * Retrieves a cached NodeDraft, or creates a new one, if there are no existing one with the desired ID
	 * 
	 * @param nodeId
	 * @return
	 */
	private NodeDraft getOrCreateNodeDraft(ContainerLoader container, String nodeId) {
		NodeDraft ret = null;
		if (nodes[Integer.valueOf(nodeId)] == null) {
			ret = agentsCreator.newNodeDraft();
			ret.setId(nodeId);
			container.addNode(ret);
			nodes[Integer.valueOf(nodeId)] = ret;
			
			nbCreatedNodes++;
		} else {
			ret = nodes[Integer.valueOf(nodeId)];
		}
		
		return ret;
	}
	
	@Override
	public void generate(ContainerLoader container) {
		nodes = new NodeDraft[2000000];
		
//		Progress.start(progressTicket, nbNodes);
		RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister());
		container.setEdgeDefault(EdgeDefault.UNDIRECTED);


		
		try {
			String line = null;
			nbCreatedNodes = 0;
			while ((line = reader.readLine()) != null && nbCreatedNodes <= nbNodes) {
				if (nbCreatedNodes%1000 == 0) {
					System.out.println(nbCreatedNodes);
				}
				String[] nodeIds = line.split("\t");
				
				if (nodeIds.length == 2) {
					NodeDraft source = getOrCreateNodeDraft(container, nodeIds[0]);
					NodeDraft target = getOrCreateNodeDraft(container, nodeIds[1]);
					
					EdgeDraft edge = container.factory().newEdgeDraft();
					edge.setSource(source);
					edge.setTarget(target);
					container.addEdge(edge);
					
				} else {
					throw new RuntimeException("File is not in the proper format : sourceId targetId");
				}
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		Progress.finish(progressTicket);
		progressTicket = null;
	}

	public void setSkinnerFile(final String path) throws FileNotFoundException {
		reader = Files.newReader(new File(path), Charset.defaultCharset());
	}
	
	@Override
	public String getName() {
		return "Barabasi-Albert Scale Free model";
	}

	@Override
	public GeneratorUI getUI() {
//		return Lookup.getDefault().lookup(BarabasiAlbertUI.class);
		return null;
	}

	@Override
	public boolean cancel() {
		cancel = true;
		return true;
	}

	@Override
	public void setProgressTicket(ProgressTicket progressTicket) {
		this.progressTicket = progressTicket;
	}
	

	public static void main(String[] args) throws FileNotFoundException {
		int nbNodes = 0;
		float percentageClients = 0.1f;
		if (args.length == 2) {
			nbNodes = Integer.valueOf(args[0]);
			percentageClients = Float.valueOf(args[1]);
		} else {
			System.err.println("usage: SkitterImporter [nbNodes] [percentageClients]");
			System.exit(1);
		}
		
		final SkitterImporter generator = new SkitterImporter();
		
		generator.setSkinnerFile("/tmp/as-skitter.txt");
		generator.setNbNodes(nbNodes);
		Container container = Lookup.getDefault().lookup(ContainerFactory.class).newContainer();
		container.setReport(new Report());

		generator.setAgentsCreator(new AgentNodesCreator(percentageClients, container.getLoader()));
		
		ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
		pc.newProject();
		
		// Generate the graph
		generator.generate(container.getLoader());
		
		// Imports the data to the current workspace
		ImportController importController = Lookup.getDefault().lookup(ImportController.class);
		importController.process(container, new DefaultProcessor(), pc.getCurrentWorkspace());
		
		final ExportController ec = Lookup.getDefault().lookup(ExportController.class);
		try {
			String graphFilename = "/tmp/" + generator.nbNodes + "_skinner0.1c-1.0asp.gexf";
			ec.exportFile(new File(graphFilename), pc.getCurrentWorkspace());
		} catch (IOException e) {
			System.err.println(e);
		}
	}

	
}

