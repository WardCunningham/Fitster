// Copyright (c) 2003 Cunningham & Cunningham, Inc.
// Read license.txt in this directory.

import fit.*;
import javax.swing.*;


public class ParseEditor extends DefaultCellEditor {
    ParseEditor () {
        super (new JTextField());
        final JTextField textField = (JTextField)editorComponent;
        textField.removeActionListener(delegate);
        delegate = new EditorDelegate() {
                public void setValue(Object newValue) {
                    value = newValue;
                    ((Parster)value).revert();
                    textField.setText(((Parse)value).text());
                }
                public Object getCellEditorValue() {
//                    System.out.println(value+" =  "+textField.getText());
                    ((Parse)value).body = textField.getText();
                    return value;
                }
        };
        textField.addActionListener(delegate);
    }


}
