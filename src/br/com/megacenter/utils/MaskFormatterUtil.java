package br.com.megacenter.utils;

import javax.swing.JFormattedTextField;
import javax.swing.JTextField;
import javax.swing.text.MaskFormatter;

public class MaskFormatterUtil {

    private static void apply(JFormattedTextField field, String mask) {
        try {
            MaskFormatter mf = new MaskFormatter(mask);
            mf.setPlaceholderCharacter('_');
            mf.install(field);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void applyPhoneMask(JFormattedTextField field) {
        apply(field, "(##) ####-####");
    }

    public static void applyCellMask(JFormattedTextField field) {
        apply(field, "(##) #####-####");
    }

    public static void applyCepMask(JFormattedTextField field) {
        apply(field, "#####-###");
    }

    public static void applyCpfMask(JFormattedTextField field) {
        apply(field, "###.###.###-##");
    }

    public static void applyDateMask(JFormattedTextField field) {
        apply(field, "##/##/####");
    }
}
