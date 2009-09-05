import fit.Parse;

import javax.swing.tree.*;
import java.io.*;


/**
 * Simple model for a file in the tree object.
 *
 */
public class TestTableNode extends AbstractNode {
    Parse table;

  public TestTableNode (Parse table, FileNode parent){
    super(null, parent);
      this.table = table;
  }
  public boolean getAllowsChildren()
  {
    return false;
  }

  public boolean isLeaf()
  {
    return true;
  }

  public String toString() {
      try {
          String s = table.at(0,0,0).text();
          return s;
      }
      catch (Exception e) {
          return "exception";
      }
  }

}// TestTableNode
