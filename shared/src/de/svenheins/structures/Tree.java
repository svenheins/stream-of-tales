package de.svenheins.structures;
import java.awt.Graphics2D;
import java.util.ArrayList;


public class Tree<DataType> {
    private Node<DataType> root;

    public Tree(DataType rootData) {
        root = new Node<DataType>();
        root.setData(rootData);
        root.setChildren( new ArrayList<Node<DataType>>());
    }

    public void addChild(DataType data){
    	Node<DataType> nAdd = new Node<DataType>(data, this.getRoot());
    	this.root.getChildren().add(nAdd);
    }
    
    public void addChildren(DataType[] data){
    	for(int i =0; i< data.length; i++) {
	    	Node<DataType> nAdd = new Node<DataType>(data[i], this.getRoot());
	    	this.root.getChildren().add(nAdd);
    	}
    }
    
    public Node<DataType> getRoot() {
		return root;
	}
    
    public void paint(Graphics2D g){
    	this.getRoot().paint(g);
    }
    
    
}
