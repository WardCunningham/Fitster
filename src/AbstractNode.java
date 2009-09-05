import javax.swing.tree.*;
import java.util.*;
import java.io.File;
/**
 * AbstractNode is the base class for tree nodes.  
 */

public abstract class AbstractNode implements TreeNode {
    public AbstractNode (File file, AbstractNode parent){
        this.file = file;
        this.parent = parent;
    }

    /*
    * List of children.  If null, that means they have not
    * been determined yet, or they are not allowed.
    */
    protected List children = null;

    protected File file;

    protected AbstractNode parent = null;
    protected Fitster fitster = null;

    //-----------------------------------------------------------------------
    // TreeNode Implementation
    //-----------------------------------------------------------------------

    /**
     * This returns the children.  Subclasses should override to do
     * lazy initialization.
     */
    protected List getChildren() {
        return children;
    }
    public TreeNode getParent()
    {
        return parent;
    }

    public int getIndex(TreeNode node)
    {
        return getChildren().indexOf(node);
    }

    public Enumeration children()
    {
        return Collections.enumeration(getChildren());
    }

    public TreeNode getChildAt(int num)
    {
        return (TreeNode)getChildren().get(num);
    }

    public int getChildCount()
    {
        return getChildren().size();
    }
    public String toString() {
        String name = file.getName();
//    int pos = name.lastIndexOf('.');
//    if (pos > 0)
//      name = name.substring(0, pos);
        return name;
    }

    public void status(String status) {
        if(fitster!=null){
            fitster.status(status);
        } else {
            System.out.println(status);
        }
    }

}// AbstractNode
