import fit.Parse;
import fit.Fixture;

import javax.swing.tree.*;
import java.io.*;
import java.util.*;
/**
 * Simple model for a file in the tree object.  Should be an HTML
 * file.  Child nodes would be the set of fixtures.
 *
 */
public class FileNode extends AbstractNode implements Runnable {

    boolean isFixtureSet = true;
    Parster tables;

    public FileNode (File file) {
        this(file, null);
    }
    public FileNode (File file, FolderNode parent){
        super(file, parent);
    }
    public boolean getAllowsChildren()
    {
        return isFixtureSet;
    }

    public List getChildren() {
        if (children == null) {
            // sample of how fixtures get done.
            // here is where we want to do the parse and look for tables.
            children = new ArrayList();
            try {
                Parse t = tables = new Parster(read(file));
                while (t != null) {
                    children.add(new TestTableNode(t, this));
                    t = t.more;
                }
            }
            catch (Exception e) {
                Parse trouble = new Parse ("body", "trouble reading input", null, null);
                (new Fixture()).exception(trouble, e);
                children.add(new TestTableNode(trouble, this));
            }
        }
        return super.getChildren();
    }

    public boolean isLeaf()
    {
        return !isFixtureSet;
    }

    public String html() {
        try {
            return read(file);
        } catch (IOException e) {
            return e.getMessage();
        }
    }


    protected String read(File input) throws IOException {
        char chars[] = new char[(int)(input.length())];
        FileReader in = new FileReader(input);
        in.read(chars);
        in.close();
        return new String(chars);
    }


    public void run() {
        Fixture f = new Fixture();
        // tables.debug("", tables);
        tables.revertAll();
        f.doTables(tables);
        status(f.counts().toString());
    }

    public void runRevert() {
        tables.revertAll();
        status("revert");
    }


}// FileNode
