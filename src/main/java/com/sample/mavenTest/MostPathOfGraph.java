package com.sample.mavenTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 *  路径规划
 *  假设现在有一个有向无环图，每个节点上都带有正数权重。我们希望找到一条最优路径，使得这个路径上经过的节点的权重之和最大。
 *  输入：n个节点，m个路径，起点
 *  输出：最优路径的权重值之和
 *  举例：3个节点： A 1、B 2、	C 2，3条路径：A->B，B->C，A->C
 *  起点：A
 *  输出：5 （最优路径是 A->B->C ， 权重之和是 1+2+2=5）
 *  附加问题：我们要求的输入是有向无环图，但是没人知道实际使用的时候会有什么数据输入进来，如何避免输入了带环路的图导致的死循环呢？
 *  
 * @date 2018年1月18日
 */
public class MostPathOfGraph {

	private Map<String, Node> nodeMap = new HashMap<String, Node>();

	public Map<String, Node> getNodeMap() {
		return nodeMap;
	}

	public void addNode(Node node) {
		if (node == null) {
			throw new RuntimeException("bad Node");
		}
		this.nodeMap.put(node.getName(), node);
	}

	public void addEdge(String startNodeName, String endNodeName) {
		if (nodeMap == null) {
			throw new RuntimeException("nodeMap is null");
		}
		if (nodeMap.get(startNodeName) == null) {
			throw new RuntimeException("startNode is null");
		}
		if (nodeMap.get(endNodeName) == null) {
			throw new RuntimeException("endNode is null");
		}
		nodeMap.get(startNodeName).addEdge(endNodeName);
	}

	/**
	 * 此方法返回fromNode到toNode的最短路径，但是如果有环路则会陷入死循环 
	 * @param fromNodeName 
	 * @param toNodeName 
	 * @return
	 */
	public int getMostPath(String fromNodeName, String toNodeName) {
		Node fromNode = nodeMap.get(fromNodeName);
		List<Edge> edges = fromNode.getEdges();
		if (edges == null) {
			return -1;
		}
		int path = 0;
		for (Edge edge : edges) {
			int tempPath = fromNode.getWeight();
			String endNodeName = edge.getEndNode();
			if (!endNodeName.equals(toNodeName)) {
				int mostPath = getMostPath(endNodeName, toNodeName);
				if (mostPath == -1) {
					continue;
				}
				tempPath += mostPath;
			} else {
				tempPath += nodeMap.get(endNodeName).getWeight();
			}
			if (path < tempPath) {
				path = tempPath;
			}
		}
		return path;
	}

	/**
	 * 可跳过闭环并且返回路径list的方法 
	 * @param fromNodeName 
	 * @param toNodeName 
	 * @param pathList 
	 * @return
	 */
	public int getMostPathResult(String fromNodeName, String toNodeName, LinkedHashSet<String> pathList) {
		if (fromNodeName.equals(toNodeName)) {
			System.out.println("ERR : fromNode == toNode");
			return -1;
		}
		Node fromNode = nodeMap.get(fromNodeName);
		List<Edge> edges = fromNode.getEdges();
		if (edges == null) {
			return -1;
		}
		boolean add = pathList.add(fromNodeName);
		if (!add) {
			System.out.println("有闭环！" + "node:" + fromNodeName + "，path:"
					+ pathList);
			return -1;
		}
		int path = 0;
		LinkedHashSet<String> temp = new LinkedHashSet<String>();
		temp.addAll(pathList);
		for (Edge edge : edges) {
			LinkedHashSet<String> temp2 = new LinkedHashSet<String>();
			temp2.addAll(temp);
			int tempPath = fromNode.getWeight();
			String endNodeName = edge.getEndNode();
			if (!endNodeName.equals(toNodeName)) {
				int mostPath = getMostPathResult(endNodeName, toNodeName, temp2);
				if (mostPath == -1) {
					continue;
				}
				tempPath += mostPath;
			} else {
				tempPath += nodeMap.get(endNodeName).getWeight();
				temp2.add(toNodeName);
			}
			if (path < tempPath) {
				path = tempPath;
				pathList.clear();
				pathList.addAll(temp2);
			}
		}
		return path;
	}
	
	public static void main(String[] args) {
		MostPathOfGraph graph = new MostPathOfGraph();
		Node a = new Node("A", 1);
		Node b = new Node("B", 2);
		Node c = new Node("C", 2);
		graph.addNode(a);
		graph.addNode(b);
		graph.addNode(c);
		a.addEdge("B");
		b.addEdge("C");
		a.addEdge("C");
		LinkedHashSet<String> temp = new LinkedHashSet<String>();
		System.out.println(graph.getMostPath("A", "C"));
		System.out.println(graph.getMostPathResult("A", "C", temp));
		System.out.println(temp);
	}
}

class Edge {
	private String startNode;
	private String endNode;

	public String getStartNode() {
		return startNode;
	}

	public void setStartNode(String startNode) {
		this.startNode = startNode;
	}

	public String getEndNode() {
		return endNode;
	}

	public void setEndNode(String endNode) {
		this.endNode = endNode;
	}
}

class Node {
	private String name;
	private Integer weight;
	private List<Edge> edges = new ArrayList<Edge>();
	
	public Node(){		
	}
	
	public Node(String name, Integer weight){
		this.name = name;
		this.weight = weight;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public List<Edge> getEdges() {
		return edges;
	}

	public void addEdge(String endNode) {
		Edge edge = new Edge();
		edge.setStartNode(this.getName());
		edge.setEndNode(endNode);
		this.edges.add(edge);
	}
}
