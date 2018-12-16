package io.loli.util.osm;


import io.loli.util.osm.ui.OsmUi;

import javax.swing.*;

public class OsmApplication {
    public static void main(String[] args) throws InterruptedException {
        try {
            // Set cross-platform Java L&F (also called "Metal")
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
        SwingUtilities.invokeLater(OsmUi::new);
    }
}
