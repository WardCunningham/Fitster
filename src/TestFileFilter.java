import java.io.File;

/**
 * TestFileFilter is used to filter directories and HTML files.
 * It is used by the file chooser as well as by the FolderNode object
 * to descend.
 */

public class TestFileFilter extends javax.swing.filechooser.FileFilter
  implements java.io.FileFilter 
{
  public TestFileFilter (){
  }
  public boolean accept(File f) {
    return f.isDirectory() || f.getName().indexOf(".htm")>0;
  }
  public String getDescription() {
    return "Fixture Files (*.htm)";
  }
}// TestFileFilter
