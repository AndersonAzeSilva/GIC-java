package br.com.megacenter.ui;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class ValidacaoRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col
    ) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

        String v = (value == null) ? "" : value.toString();

        if (!isSelected) {
            if ("ERRO".equalsIgnoreCase(v)) {
                c.setBackground(new java.awt.Color(255, 220, 220));
            } else {
                c.setBackground(java.awt.Color.WHITE);
            }
        }

        return c;
    }
}
