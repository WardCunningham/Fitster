// Copyright (c) 2003 Cunningham & Cunningham, Inc.
// Read license.txt in this directory.

import fit.Parse;

import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class ParseRenderer extends DefaultTableCellRenderer {

    public void setValue(Object value) {
        Parse p = (Parse)value;
        if (p != null && p.body != null) {
            setText(p.text());
        } else {
            setText("");
        }
        setBackground(color("bgcolor", p, Color.white));
        setForeground(color("color", p, Color.black));
    }

    Color color(String attribute, Parse cell, Color otherwise) {
        if (cell==null) return otherwise;
        String pattern = " " + attribute + "=\"";
        int index = cell.tag.indexOf(pattern);
        if (index >= 0) {
            int len = pattern.length();
            return Color.decode(cell.tag.substring(index+len, index+len+7));
        } else {
            return otherwise;
        }
    }


}
