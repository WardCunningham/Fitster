// $Id: Fitster.java,v 1.7 2003/03/27 10:27:00 ward Exp $
import fit.Parse;
import fit.Fixture;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.text.html.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Enumeration;


/**
 * Fitster is a JFrame holding the UI for the Fitster application.  It
 * builds and lays out the UI and instantiates model objects for the
 * components.
 *
 * @version $Revision: 1.7 $
 */
public class Fitster extends JFrame
{

    //-----------------------------------------------------------------------
    // Constructors
    //-----------------------------------------------------------------------
    Fitster() {
        super("Fitster");
        try {
            init();
            setSize(700,400);
            setLocation(200,300);
            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e){
                    exit();}
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    //-----------------------------------------------------------------------
    // Public methods
    //-----------------------------------------------------------------------

    /**
     * A card layout and panel are used to manage several different
     * views in the work area of the UI.  Depending on what is selected
     * in the tree, you will see a different view in the
     * the work area.
     */
    private CardLayout workAreaSelector = new CardLayout();
    private JPanel  workAreaPane = new JPanel(workAreaSelector);

    /** A possible one line message area. */
    private JTextField statusText = new JTextField();

    /** One of the work area views. */
    private JTextPane htmlView = new JTextPane();

    /** One of the work area views. */
    private JTable testTable = new JTable();

    /** Special Editor for Parse Cells */
    private ParseEditor cellEditor = new ParseEditor();


    /** One of the work area views. */
    private JLabel instructions =  new JLabel("Select a test file on the left.", JLabel.CENTER);


    /** * The tree model. */
    private DefaultTreeModel treeModel;

    /** The control for selecting files and fixtures, on the left hand side. */
    private JTree fixturesTree = new JTree();

    /** The file chooser to use to select the current working file or directory.
     */
    JFileChooser chooser = null;

    //-----------------------------------------------------------------------
    // UI Construction
    //-----------------------------------------------------------------------

    /**
     * Make the menu bar, using menu items built from actions.  The
     * actions are defined as Action instances in this class.
     */
    private JMenuBar makeMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");

        menu.add(fileOpenAction);
        menu.add(fileSaveAction);

        JMenuItem exitMenu = menu.add(exitAction);
        KeyStroke altF4key = KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_MASK);
        exitMenu.setAccelerator(altF4key);
        menuBar.add(menu);

        menu = new JMenu("Actions");
        //menu.add();

        JMenuItem runMenu = menu.add(runAction);
        KeyStroke f5key = KeyStroke.getKeyStroke(KeyEvent.VK_F5,0,false);
        runMenu.setAccelerator(f5key);
        menu.add(runRevertAction);
        menuBar.add(menu);

        return menuBar;
    }

    /**
     * Do the basic layout.  Organize the UI components.  No models yet.
     */
    private void init() throws java.io.IOException {

        setJMenuBar(makeMenuBar());
        getContentPane().setLayout(new BorderLayout());

        // Could add the toolbar someday.  Probably goes with the
        // tableScrollPane below.
        //JToolBar tools = new JToolBar();
        //getContentPane().add(tools, BorderLayout.NORTH);
        testTable.setShowGrid(true);
        testTable.setGridColor(Color.gray);
        testTable.setShowHorizontalLines(true);
        testTable.setShowVerticalLines(true);
        testTable.setDefaultRenderer(Parse.class, new ParseRenderer());
        testTable.setCellSelectionEnabled(true);
        ListSelectionListener listener = new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                int row = testTable.getSelectedRow();
                int column = testTable.getSelectedColumn();
                Parse cell = (Parse) testTable.getValueAt(row, column);
                String message = cell == null ? "" : cell.text();
                status(message);
            }
        };
        testTable.getSelectionModel().addListSelectionListener(listener);
        testTable.getColumnModel().getSelectionModel().addListSelectionListener(listener);


        JScrollPane tableScrollPane = new JScrollPane(testTable);
        tableScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        /*
        * Define the tree pane to show a tree view of directories,
        * files, and tables.  Start with a default of the current working
        * directory.
        */
        FolderNode node = new FolderNode(new File(".").getAbsoluteFile().getParentFile());
        node.fitster = this;
        fixturesTree.setModel(new DefaultTreeModel(node, true));
        fixturesTree.addTreeSelectionListener(new TreeSelectionListener () {
            public void valueChanged(TreeSelectionEvent event) {
                TreePath p = event.getPath();
                selectNode((AbstractNode)p.getLastPathComponent());
            }
        });


        JScrollPane treeScrollPane = new JScrollPane(fixturesTree);
        treeScrollPane.setHorizontalScrollBarPolicy
                (JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        treeScrollPane.setVerticalScrollBarPolicy
                (JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);



        /*
        * Define the html view for HTML files.
        */
        htmlView.setEditorKit(new HTMLEditorKit());
        htmlView.setText("<center><p><h1>Fitster</h1></P><p><i>Bill Kayser<br>Ward Cunningham</i></center>");
        JScrollPane htmlScrollPane = new JScrollPane(htmlView);
        htmlScrollPane.setHorizontalScrollBarPolicy
                (JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        htmlScrollPane.setVerticalScrollBarPolicy
                (JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);


        /*
        * Make the RHS work area a card layout that shows either
        * an HTML view of the file, a table view of a fixture, or
        * a label indicating an html file should be selected.
        */
        workAreaPane.add(tableScrollPane, "table");
        workAreaPane.add(htmlScrollPane, "html");
        workAreaPane.add(instructions, "message");
        workAreaSelector.show(workAreaPane, "html");
        JSplitPane splitPane =
                new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                        treeScrollPane, workAreaPane);

        // tableScrollPane.setPreferredSize(new Dimension(250, 145));
        //treeScrollPane.setPreferredSize(new Dimension(200, 200));
        splitPane.setDividerLocation(150);

        /*
        * Lay out the window with the split pane in the middle
        * and the status text across the bottom.
        */
        getContentPane().add(splitPane, BorderLayout.CENTER);
        getContentPane().add(statusText, BorderLayout.SOUTH);
        enableDisable();
    }

    //-----------------------------------------------------------------------
    // Fitster event handlers
    //-----------------------------------------------------------------------

    private void selectNode(AbstractNode n) {
        n.fitster = this;
        if (n instanceof FileNode) {
            FileNode node = (FileNode) n;
            //String html = node.html();
            //htmlView.setText(html);
            //workAreaSelector.show(workAreaPane, "html");
            instructions.setText(new java.util.Date(node.file.lastModified()).toString());
            workAreaSelector.show(workAreaPane, "message");
        } else if (n instanceof TestTableNode) {
            TestTableModel tpm = new TestTableModel(((TestTableNode) n).table);
            testTable.setModel(tpm);
            setCellEditor();
            workAreaSelector.show(workAreaPane, "table");
        } else {
            instructions.setText(n.toString());
            workAreaSelector.show(workAreaPane, "message");
        }
        enableDisable();
    }

    private void setCellEditor() {
        Enumeration columns = testTable.getColumnModel().getColumns();
        while (columns.hasMoreElements()) {
            TableColumn column = (TableColumn) columns.nextElement();
            column.setCellEditor(cellEditor);
        }
    }

    //-----------------------------------------------------------------------
    // Fitster actions
    //-----------------------------------------------------------------------

    /*
    * Fister action methods, invoked by the action instances.
    */
    private void exit() {
        stopCellEditing();
        System.exit(0);
    }

    private void run() {
        stopCellEditing();
        FileNode fileNode = selectedFileNode();
        if (fileNode != null) {
           fileNode.run();
//            try {
//                Thread t = new Thread(fileNode);
//                t.join();
//            } catch (Exception e) {
//                System.out.println(e);
//            }

           fireTableStructureChanged();
            setCellEditor();
        }
    }

    private void runRevert() {
        stopCellEditing();
        FileNode fileNode = selectedFileNode();
        if (fileNode != null) {
            fileNode.runRevert();
            fireTableStructureChanged();
            setCellEditor();
        }
    }

    private void openFile() {
        stopCellEditing();

        if (chooser == null) {
            chooser = new JFileChooser();
            chooser.setDialogTitle("Choose a directory of test files");
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        }
        int returnVal = chooser.showOpenDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            File selection = chooser.getSelectedFile();
            AbstractNode node;
            if (selection.isDirectory())
                node = new FolderNode(selection);
            else
                node = new FileNode(selection);
            node.fitster = this;
            fixturesTree.setModel(new DefaultTreeModel(node, true));
        }
    }

    private void saveFile() {
        stopCellEditing();

        try {
            File selection = selectedFileNode().file;
            Parse tables = selectedFileNode().tables;
            PrintWriter pw = new PrintWriter(new FileWriter(selection));
            tables.print(new PrintWriter(pw));
            pw.close();
        } catch (IOException e) {
            openAlert("Can't write file", e);
        }
    }



/*
* Fitster Action instances, for use in menus, toolbars, or keystrokes.
* These invoke the action methods and also have context for the UI
* like label, icon, enabled state, etc.
*/
    private Action fileOpenAction = new AbstractAction("Open...") {
        public void actionPerformed(ActionEvent a) {
            openFile();
        }
    };

    private Action fileSaveAction = new AbstractAction("Save") {
        public void actionPerformed(ActionEvent a) {
            saveFile();
        }
    };

    private Action exitAction = new AbstractAction("Exit") {
        public void actionPerformed(ActionEvent a) {
            exit();
        }
    };

    private Action runAction = new AbstractAction("Run") {
        public void actionPerformed(ActionEvent a) {
            run();
        }
    };

    private Action runRevertAction = new AbstractAction("Revert") {
        public void actionPerformed(ActionEvent a) {
            runRevert();
        }
    };


//---------------------
// Utility Methods
//--------------------

    private TestTableNode selectedTestTableNode() {
        if (fixturesTree.getSelectionCount()>0) {
            Object[] p = fixturesTree.getSelectionPath().getPath();
            for (int i=p.length-1; i>=0; i--) {
                if (p[i] instanceof TestTableNode) {
                    return (TestTableNode) p[i];
                }
            }
        }
        return null;
    }

    private FileNode selectedFileNode() {
        stopCellEditing();
        if (fixturesTree.getSelectionCount()>0) {
            Object[] p = fixturesTree.getSelectionPath().getPath();
            for (int i=p.length-1; i>=0; i--) {
                if (p[i] instanceof FileNode) {
                    return (FileNode) p[i];
                }
            }
        }
        return null;
    }

    private void fireTableStructureChanged() {
        TestTableNode tableNode = selectedTestTableNode();
        if (tableNode != null) {
            TestTableModel t = (TestTableModel) testTable.getModel();
            if (t != null) {
                t.fireTableStructureChanged();
            }
        }
    }

    public void stopCellEditing() {
        cellEditor.stopCellEditing();
    }

    public void openAlert(String message, Throwable exception) {
        status(message + ": " + exception);
    }

    public void status(String message) {
        statusText.setText(message);
    }

    public void enableDisable() {
        fileSaveAction.setEnabled(selectedFileNode() != null);
        runAction.setEnabled(selectedTestTableNode() != null);
        runRevertAction.setEnabled(selectedTestTableNode() != null);
    }

    public static void main(String args[]){
        new Fitster().setVisible(true);
    }
}

