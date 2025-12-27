package br.com.megacenter.ui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class ValidacaoRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        String v = value == null ? "" : value.toString().trim();

        if (!isSelected) {
            if ("ERRO".equalsIgnoreCase(v)) {
                c.setBackground(new Color(255, 220, 220));
                c.setForeground(Color.BLACK);
            } else {
                c.setBackground(Color.WHITE);
                c.setForeground(Color.BLACK);
            }
        }

        setHorizontalAlignment(CENTER);
        return c;
    }
}
