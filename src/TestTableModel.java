// Copyright (c) 2003 Cunningham & Cunningham, Inc.
// Read license.txt in this directory.

import fit.Parse;

import javax.swing.table.AbstractTableModel;

public class TestTableModel extends AbstractTableModel {

    Parse table;
    Parse labelRow=null;
    Parse dataRows=null;

    public TestTableModel (Parse table) {
        this.table = table;
//        labelRow = table.parts.more;
//        if (labelRow!=null)
//            dataRows = labelRow.more;
//        else
            dataRows = table.parts.more;
    }

    public  Object getValueAt(int row, int column) {
        return isCellPresent(row, column) ? dataRows.at(row, column) : null;
//        return dataRows.at(row, column);
    }

    public  int getColumnCount() {
        if (labelRow != null) {
            return labelRow.parts.size();
        } else {
            int max = 0;
            for (Parse row = dataRows; row!=null; row=row.more) {
                int size = row.parts.size();
                max = max >= size ? max : size;
            }
            return max;
        }
    }

    public  int getRowCount() {
        return dataRows==null ? 0 : dataRows.size();
    }

    public String getColumnName(int column) {
        return labelRow == null ? null : labelRow.at(0, column).text();
    }

    public Class getColumnClass(int column) {
        return Parse.class;
    }

    public boolean isCellEditable (int row, int column) {
        return isCellPresent(row,column);
    }

    public boolean isCellPresent (int row, int column) {
        return dataRows.at(row).parts.size() > column;

    }


}
