package io.loli.util.osm;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import io.loli.util.osm.ui.OsmUi;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class OsmApplication {
    public static void main(String[] args) throws InterruptedException {
        try {
            // Set cross-platform Java L&F (also called "Metal")
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
        ch.qos.logback.classic.Logger logger = (Logger) LoggerFactory.getLogger("root");
        logger.setLevel(Level.INFO);
        SwingUtilities.invokeLater(OsmUi::new);
    }
}
