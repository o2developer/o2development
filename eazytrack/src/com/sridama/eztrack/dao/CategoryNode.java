/**
 * 
 */
package com.sridama.eztrack.dao;

import java.util.ArrayList;

/**
 * @author admin
 *
 */
public class CategoryNode {
	
	private CategoryNode parent = null;
	private ArrayList<CategoryNode> children = new ArrayList<CategoryNode>();
	
	//...
	private int categoryId = 0;
	private String name = null;
	private String desc = null;
	private int parentId = 0;

	public CategoryNode(int categoryId, String name, String desc, int parentId) {
		this.categoryId = categoryId;
		this.name = name;
		this.desc = desc;
		this.parentId = parentId;
	}
	
	public int getParentId() {
		return parentId;
	}

	public int getId() {
		return this.categoryId;
	}

	public boolean isRoot() {
		return this.categoryId == this.parentId;
	}
	
	
	public void addChild(CategoryNode node) {
		
		children.add(node);
		
		// TODO add your code here...
	}
	
	/**
	 * Searches recursively through the children of "this" node
	 * to see if it has a node specified by Id.
	 * @param id
	 * @return
	 */
	public CategoryNode find(int id) {
		
		CategoryNode node = null;
		if (this.categoryId == id) {
			return this;
		}
		else {
			for (int i=0; i<children.size(); i++) {
				node = children.get(i).find(id);
				if (node != null)
					break;
			}
		}
		return node;
	}
	
	
	/**
	 * Returns a JSON representation of category node and its descendants.
	 * @return
	 */
	public String toJSON() {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("{");
		sb.append("\"category_id\" : " + this.getId() + ", ");
		sb.append("\"name\" : \"" + this.name + "\", ");
		sb.append("\"parent\" : \"" + this.parentId + "\"");
		if (this.children.size() > 0) {
			sb.append(", \"children\": [" );
			for (int i=0; i<children.size(); i++){
				if (i>0) sb.append(",");
				sb.append(children.get(i).toJSON());
			}
			sb.append("]");
		}
		sb.append("}");
		return sb.toString();
	}
}
