package de.svenheins.structures;

import java.awt.Graphics2D;
import java.util.ArrayList;

import de.svenheins.objects.Space;


/**
 * @author Sven Heins
 *
 * @param <DataType>
 * 
 * creates a node of the specific data type DataTye
 * for example: Space, String
 * 
 * implementation: for each unknown DataType, the "paint()"-method has to be completed
 * 
 */
public class Node<DataType> {
        private DataType data;
        private Node<DataType> parent = null;
        private ArrayList<Node<DataType>> children;
        
        public Node() {
        	this.parent = null;
        }
        
        public Node(DataType data, Node<DataType> parent) {
        	this.data = data;
        	this.parent = parent;
        	this.children = new ArrayList<Node<DataType>>();
        }
        
        public boolean hasChildren(){
        	if( this.children ==null)
        	return false;
        	else return true;
        }
        
        public void addChild(DataType object) {
        	Node<DataType> child = new Node<DataType>(object, this);
        	this.children.add(child);
        }
        
        public void addChildren(DataType[] data){
        	for(int i =0; i< data.length; i++) {
    	    	Node<DataType> nAdd = new Node<DataType>(data[i], this);
    	    	this.children.add(nAdd);
        	}
        }
        
        public ArrayList<Node<DataType>> getChildren() {
        	if(this.hasChildren()) {
        		return this.children;
        	}
        	else return null;
        }
        
        public boolean isRoot() {
        	if(this.parent != null) return false;
        	else return true;
        }
        
        public DataType getData() {
			return data;
		}
        
        public Node<DataType> getParent() {
			if(this.isRoot()) return null;
			else return parent;
		}
        
        public void setData(DataType data) {
			this.data = data;
		}
        
        public void setParent(Node<DataType> parent) {
			this.parent = parent;
		}
        
        public void setChildren(ArrayList<Node<DataType>> children) {
			this.children = children;
		}
        
        public void paint() {
		}
        
        /**
         * @param g
         * control output for each DataType
         */
        public void paint(Graphics2D g) {
        	// DataType == Space
     		if (this.getData() instanceof Space) {
				Space castSpace = (Space) this.getData();
     			castSpace.paint(g, 0, 0);
			} else {
				// TODO: this DataType is not paintable-output
			}
     		// additionally paint all children -> recursive
     		if (this.hasChildren()){
     			for(int i = 0; i< this.getChildren().size(); i++) {
     				if(this.getChildren().get(i) != null)
     				this.getChildren().get(i).paint(g);
     			}
     		}
		}
       
    }