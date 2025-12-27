package br.com.megacenter.ui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class ImportacaoStatusRenderer extends DefaultTableCellRenderer {

    private final int colInvalidas;

    public ImportacaoStatusRenderer(int colInvalidas) {
        this.colInvalidas = colInvalidas;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (isSelected) return c;

        int invalidas = 0;
        try {
            Object val = table.getValueAt(row, colInvalidas);
            if (val != null) invalidas = Integer.parseInt(val.toString());
        } catch (Exception ignored) {}

        if (invalidas > 0) {
            c.setBackground(new Color(255, 235, 235));
        } else {
            c.setBackground(Color.WHITE);
        }

        c.setForeground(Color.BLACK);
        return c;
    }
}
