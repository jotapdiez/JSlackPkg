package org.jotapdiez.jslackpkg.ui.components.custom;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;

import javax.swing.JComponent;
import javax.swing.Timer;

/**
 * Swing component to monitor Java heap usage.
 * 
 * @author Tom Morris <tfmorris@gmail.com>
 */
public class HeapMonitor extends JComponent implements ActionListener {

	// % thresholds for bar color changes
    private static final int WARN_THRESHOLD = 75;
    private static final int CRITICAL_THRESHOLD = 90;
    
    private static final Color WARN_COLOR  = new Color(255, 190, 125);
    private static final Color CRITICAL_COLOR = new Color(255, 70, 70);
    private static final Color TOTAL_COLOR = new Color(255,255,0);

    private static final long M = 1024 * 1024;
    
    // Virtual memory (heap) stats
    private long free;
    private long total;
    private long max;
    private long used;
    
    /**
     * Construct a graphical JVM heap monitor component.
     */
    public HeapMonitor() {
         super();
        Dimension size = new Dimension(150, 15);
        setPreferredSize(size);

        // TODO: Add a button to force garbage collection

        updateStats();

        Timer timer = new Timer(1000, this);
        timer.start();
    }
    
    public void paint (Graphics g) {        
        Rectangle bounds = getBounds();
        int usedX = (int) (used * bounds.width / max);
        int totalX = (int) (total * bounds.width / max);
        int warnX = WARN_THRESHOLD * bounds.width / 100;
        int dangerX = CRITICAL_THRESHOLD * bounds.width / 100;
        
        Color savedColor = g.getColor();
        
        g.setColor(getBackground().darker());
        g.fillRect(0, 0, Math.min(usedX, warnX), bounds.height);
        
        g.setColor(WARN_COLOR);
        g.fillRect(warnX, 0, 
                Math.min(usedX - warnX, dangerX - warnX), 
                bounds.height);
        
        g.setColor(CRITICAL_COLOR);
        g.fillRect(dangerX, 0, 
                Math.min(usedX - dangerX, bounds.width - dangerX), 
                bounds.height);

        // Thin bar to show current allocated heap size
        g.setColor(TOTAL_COLOR);
        g.fillRect(totalX-2, 0, 
                Math.min(2, bounds.width-totalX), 
                bounds.height);

        g.setColor(getForeground());

        String s = MessageFormat.format("{0}M used of {1}M max",
                new Object[] {(long) (used / M), (long) (max / M) });
        int x = (bounds.width - g.getFontMetrics().stringWidth(s)) / 2;
        int y = (bounds.height + g.getFontMetrics().getHeight()) / 2;
        g.drawString(s, x, y);
        
        g.setColor(savedColor);
    }

    /*
     * Timer action method.  Periodically update our stats and force a repaint.
     */
    public void actionPerformed(ActionEvent e) {
        updateStats();
        repaint();
    }
    
    private void updateStats() {
        free = Runtime.getRuntime().freeMemory();
        total = Runtime.getRuntime().totalMemory();
        max = Runtime.getRuntime().maxMemory();
        used = total - free;

        String tip = MessageFormat.format(
                "Heap use: {0}%  {1}M used of {2}M heap.  Max: {3}M", 
                new Object[] {used * 100 / max, (long) (used / M),
                              (long) (total / M), (long) (max / M)
                });
        setToolTipText(tip);
    }
	private static final long serialVersionUID = -876022826945378012L;
}
