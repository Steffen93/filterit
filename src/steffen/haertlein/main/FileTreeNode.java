package steffen.haertlein.main;

import java.io.File;

import javax.swing.tree.DefaultMutableTreeNode;

import steffen.haertlein.file.FileObject;

public class FileTreeNode extends DefaultMutableTreeNode {
	private static final long serialVersionUID = 1L;

	public FileTreeNode(Object userObject) {
		super(userObject);
	}

	public FileTreeNode(){
		super();
	}
	
	public FileTreeNode(Object userObject, boolean allowsChildren){
		super(userObject, allowsChildren);
	}
	
	@Override
	public String toString(){
		Object userObject = super.getUserObject();
		if (userObject instanceof File){
			return ((File)userObject).getName();
		}
		else if (userObject instanceof FileObject) {
				return ((FileObject)userObject).getFile().getName();
		}
		else {
			return super.toString();
		}
	}
}
