package id.ac.itats.skripsi.orm.generator.builder;

import id.ac.itats.skripsi.orm.generator.parser.OSMParser;
import id.ac.itats.skripsi.orm.generator.parser.model.OSM;
import id.ac.itats.skripsi.orm.generator.parser.model.OSMNode;
import id.ac.itats.skripsi.orm.generator.parser.model.Way;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class GraphBuilder {
	
	private OSM osmData;
	private Set<OSMNode> nodeAwalAkhir = new HashSet<OSMNode>();
	private Graph graph = new Graph();
	
	public GraphBuilder(String file) throws ParserConfigurationException,
			SAXException, IOException {
		osmData = OSMParser.parse(file);
		nodeAwalAkhir = getNodeAwalAkhir();
		initiateGraph();
	}

	public Graph getGraph() {
		return graph;
	}
	
	public Set<OSMNode> getNodesInGraph(){
		return nodeAwalAkhir;
	}
	
	
	private Set<OSMNode> getNodeAwalAkhir() {
		Set<OSMNode> result = new HashSet<OSMNode>();
		for (Way way : osmData.getWays()) {
			if (((way.getVisible() == null) || (way.getVisible().equals("true")))
					&& (way.isAccessibleByCar())) {
				List<OSMNode> nodes = way.getNodes();
				if (nodes.size() > 0) {
					result.add(nodes.get(0));
					result.add(nodes.get(nodes.size() - 1));
				}
			}
		}
		return result;
	}
		
	private void initiateGraph() {
		for (Way way : this.osmData.getWays()) {
			if (((way.getVisible() == null) || (way.getVisible().equals("true"))) && (way.isAccessibleByCar())) {
				
				OSMNode previousNode = null;
				int previousPosition = -1;
				int position = 0;
				for(OSMNode node : way.getNodes()) {
					if (this.nodeAwalAkhir.contains(node)) {
						if (previousNode != null) {
							double length = way.getWayPartLength(previousPosition, position+1);
							if (way.getOnewayDirection() >= 0) {
								
								this.graph.addEdge(way, previousNode, node, length);
							}
							if (way.getOnewayDirection() <= 0) {
								this.graph.addEdge(way, node, previousNode, length);
							}
						}
						previousNode = node;
						previousPosition = position;
					}
					position++;
				}
			}
		}				
	}

}
