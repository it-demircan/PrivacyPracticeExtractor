package services;

import java.util.HashMap;

import model.Label;
import model.Tree;

/**
 * This Class serialize and deserialize a tree containing label objects.
 * @author Muhammed Demircan
 *
 */
public class LabelConverter {

	/**
	 * Serialize tree and writes it to an text file.
	 * @param root Root node of the tree
	 * @param writer Text Writer 
	 * @param fileName Saving serialized tree into this text file
	 * @throws Exception
	 */
    public static void write(Tree<Label> root, ITextWriter writer, String fileName) throws Exception{
    	String result = toOutputString(root);
    	writer.write(result, fileName);
    }
    
    /**
     * Reads an serialized tree and deserialize it to a tree object.
     * @param reader File Reader
     * @param fileName Path to File, which contains serialized tree
     * @return Root node of deserialized tree.
     * @throws Exception
     */
    public static Tree<Label> read(ITextReader reader, String fileName) throws Exception{
    	String input = reader.readText(fileName);
    	return convertToTree(input);
    }
    
    
    /**
     * Serializes a tree node and appends all serialized children nodes to return.
     * @param node - Node, which should be serialized (to string)
     * @return Serialized tree node. Serialization Rule: [parentName, name]
     */
    private static String toOutputString(Tree<Label> node){
    	String result = "";
    	String appending = "[?,?]";
    	if(node.getParent() == null){
    		appending = "[null,"+node.getData().getName()+"]";
    		result += appending;
    	}
    	else{
    		appending = "["+node.getParent().getData().getName()+","+node.getData().getName()+"]";
    		result += ";"+appending;
    	}
    	
    	for(Tree<Label> child : node.getChildren())
    		result += toOutputString(child);
    	return result;
    }

    
    /**
     * Converts an serialized tree (string format) into a tree object based on following rule:
     * [parentName, name] -> node with name, connected to node named parentName.
     * @param input -Serialized Tree
     * @return Tree Root Node
     */
    private static Tree<Label> convertToTree(String input) throws Exception {
    	HashMap<String,Tree<Label>> mapping = new HashMap<String,Tree<Label>>();
    	String[] treeStructure = input.split(";");
    	
    	String[] content;
    	String name;
    	String parentName;
    	Tree<Label> parent;
    	Tree<Label> node;
    	Tree<Label> root = null;
    	
    	for(int i = 0;i < treeStructure.length;i++){
    		content = treeStructure[i].split(",");
    		name = content[1].replace("]", "");
    		parentName = content[0].replace("[","");    		
    		
    		
    		if(!parentName.equals("null")){
    			parent = mapping.get(parentName);
    			node  = parent.addChild(new Label(name));
    		}
    		else{//Root Node
    			node = new Tree<Label>(new Label(name));
    			root = node;
    		}
    		mapping.put(name, node);
    	}
    	return root;
    }
}
