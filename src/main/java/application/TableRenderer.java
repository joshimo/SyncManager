package application;

import commons.Status;

import java.util.Vector;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

class TableRenderer extends JTextPane implements TableCellRenderer {

    Vector<Vector<String>> data;

    public TableRenderer(Vector<Vector<String>> data) {
        this.data = data;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        if (data.get(row).get(3).equals(Status.UP_TO_DATE)) setForeground(new Color(90, 140, 90));
        else
        if (data.get(row).get(3).equals(Status.OLD)) setForeground(new Color(160, 90, 90));
        else
        if (data.get(row).get(3).equals(Status.SYNC_ERROR)) setForeground(new Color(180, 0, 0));
        else
            setForeground(new Color(0, 0, 0));

        if (isSelected) {
            setOpaque(true);
            setBackground(new Color(180, 180, 225));
        }
        else
            setOpaque(false);

        setText(data.get(row).get(column));

        return this;
    }
}