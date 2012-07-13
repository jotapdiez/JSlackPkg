package org.jotapdiez.jslackpkg.ui.components.custom;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.BevelBorder;

public class StatusBar extends JPanel
{
    private static StatusBar _instance = null;
	public  static StatusBar getInstance()
    {
	    if (_instance == null)
	        _instance = new StatusBar();
	    return _instance;
    }

    private JLabel lComponentFocusStatusText = new JLabel();
    private JLabel lLastActionStatusText = new JLabel();
    private JProgressBar progressBar = new JProgressBar();
    private int progressBarValue = 0;
	
    private StatusBar() {
        initComponents();
    }

    private void initComponents() {
        setName("SGGProgressBar"); // NOI18N

        setLayout(new BorderLayout(0, 0));
        
        {
	        JPanel panel = new JPanel();
	        add(panel);
	        panel.setLayout(new BorderLayout(0, 0));
	        panel.add(lComponentFocusStatusText, BorderLayout.CENTER);
        
	        lComponentFocusStatusText.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
	        lComponentFocusStatusText.setName("lComponentFocusStatusText"); // NOI18N
	        lComponentFocusStatusText.setFocusable(false);
	        
	        {
		        JPanel panelTextos = new JPanel();
		        panelTextos.setLayout(new BorderLayout(0, 0));
	            lLastActionStatusText.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
	            lLastActionStatusText.setName("lLastActionStatusText"); // NOI18N
	            lLastActionStatusText.setFocusable(false);
		        panelTextos.add(lLastActionStatusText, BorderLayout.CENTER);
	            panelTextos.add(progressBar, BorderLayout.EAST);
	            
	            progressBar.setName("progressBar"); // NOI18N
	            progressBar.setStringPainted(false);
	            //progressBar.setValue(0);
	            progressBar.setIndeterminate(false);
		        panel.add(panelTextos, BorderLayout.EAST);
	        }
        }
        HeapMonitor memory = new HeapMonitor();
        add(memory, BorderLayout.EAST);
    }

    public void setTotal(int total)
    {
    	progressBar.setMaximum(total);
    }
    
    public void reduceTotal()
    {
    	reduceTotal(1);
    }
    
    public void reduceTotal(int cant)
    {
    	int prevMax = progressBar.getMaximum();
    	setTotal(prevMax-cant);
    }
    
    public void startIndeterminated()
    {
    	progressBar.setIndeterminate(true);
    }
    
    public void stopIndeterminated()
    {
    	progressBar.setIndeterminate(false);
    }
    
    public void increaseProgress()
    {
    	progressBar.setValue(++progressBarValue);
    }
    
    public void resetProgress()
    {
    	progressBar.setValue(progressBar.getMinimum());
    	progressBar.setValue(0);
    }
    
    public void setFocusComponentText(String text)
    {
    	lComponentFocusStatusText.setText(text);
    }

    public void setLastActionText(String text)
    {
    	lLastActionStatusText.setText(text);
    }
    
	private static final long serialVersionUID = 5684880052627715552L;
}
