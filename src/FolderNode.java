import javax.swing.tree.*;
import java.io.*;
import java.util.*;
/**
 * Simple model for a directory in the tree object.  Children are 
 * FileNodes and DirNodes.  The children are lazily initialized so
 * we don't have to traverse an entire file hierarchy just to open
 * the root.
 *
 */
public class FolderNode extends AbstractNode {

    File files[];
    public FolderNode (File dir, FolderNode parent){
        super(dir, parent);
    }
    public FolderNode (File dir){
        this(dir, null);
        files = dir.listFiles(new TestFileFilter());
    }
    public boolean getAllowsChildren()
    {
        return true;
    }

    public boolean isLeaf()
    {
        return false;
    }

    public List getChildren() {
        if (children == null) {
            children = new ArrayList(files.length);
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                AbstractNode node;
                if (file.isDirectory())
                    node = new FolderNode(file);
                else
                    node = new FileNode(file);
                node.fitster = this.fitster;
                children.add(node);
            }
        }
        return super.getChildren();
    }

}// FolderNode
