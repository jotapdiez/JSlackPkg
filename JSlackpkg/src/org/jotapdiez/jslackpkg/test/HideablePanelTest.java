package org.jotapdiez.jslackpkg.test;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.border.BevelBorder;

import org.jotapdiez.jslackpkg.ui.components.custom.splitPane.SplitPane;

public class HideablePanelTest extends JFrame {
	final JPanel hideablePanel = new JPanel();
	
	SplitPane split = null;
	
	public HideablePanelTest() {
		super("HideablePanelTest");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(443, 525));
		pack();
		
		initComponents();
		
		setVisible(true);
	}
	
	private void initComponents()
	{
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		split = new SplitPane(false);
		getContentPane().add(split, BorderLayout.CENTER);
		
		JPanel top = new JPanel();
//		getContentPane().add(panel, BorderLayout.CENTER);
		split.addTopComponent(top);
		top.setLayout(new BorderLayout(0, 0));
		
		JTree tree = new JTree();
		top.add(tree, BorderLayout.CENTER);
		
		JPanel panelShowHideButtons = new JPanel();
		top.add(panelShowHideButtons, BorderLayout.SOUTH);
		
		JButton btnShow = new JButton("Show");
		btnShow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				split.showPanel();
			}
		});
		panelShowHideButtons.add(btnShow);
		
		JButton btnHide = new JButton("Hide");
		btnHide.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				split.hidePanel();
			}
		});
		panelShowHideButtons.add(btnHide);
		
		JButton btnToggle = new JButton("Toggle");
		btnToggle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				split.togglePanel();
			}
		});
		panelShowHideButtons.add(btnToggle);
		
		hideablePanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
//		getContentPane().add(hideablePanel, BorderLayout.SOUTH);
		
		JButton btnNewButton = new JButton("New button");
		hideablePanel.add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("New button");
		hideablePanel.add(btnNewButton_1);
		
		JButton btnNewButton_2 = new JButton("New button");
		hideablePanel.add(btnNewButton_2);
		split.addBottomComponent(hideablePanel);
	}
	
	public static void main(String[] args)
	{
		javax.swing.SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				new HideablePanelTest();
			}
		});
	}
	
	private static final long	serialVersionUID	= 4002691113799269777L;
}
