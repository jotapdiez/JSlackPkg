package org.jotapdiez.jslackpkg.ui.components.custom.splitPane;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.jotapdiez.jslackpkg.utils.ResourceMap;

public class SplitPane extends JPanel
{
	Component topComponent = new JButton("Top");
	Component bottomComponent = new JButton("Bottom");
	
	JPanel bottomContainer = new JPanel();
	
	public SplitPane()
	{
		this(true);
	}
	
	public SplitPane(boolean initialState)
	{
		this(new JButton("Top"), new JButton("Bottom"), initialState);
	}
	
	public SplitPane(Component top, Component bottom, boolean initialState)
	{
		setLayout(new BorderLayout(0, 0));
		
		topComponent = top;
		bottomComponent = bottom;
		
		add(topComponent, BorderLayout.CENTER);
		
		add(bottomContainer, BorderLayout.SOUTH);
		bottomContainer.setLayout(new BorderLayout(0, 0));
		
		JPanel btns = new JPanel();
		FlowLayout flowLayout = (FlowLayout) btns.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		flowLayout.setAlignOnBaseline(true);
		flowLayout.setVgap(0);
		flowLayout.setHgap(0);
		bottomContainer.add(btns, BorderLayout.NORTH);
		
		JButton btnX = new JButton(ResourceMap.closeIcon);
		btnX.setMargin(new Insets(1, 1, 1, 1));
		btnX.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				hidePanel();
			}
		});
		btns.add(btnX);
		
		bottomContainer.add(bottomComponent, BorderLayout.CENTER);
		bottomContainer.setVisible(initialState);
	}

	public void addBottomComponent(Component bottom)
	{
		bottomComponent = bottom;
		bottomContainer.add(bottomComponent, BorderLayout.CENTER);
	}
	
	public void addTopComponent(Component top)
	{
		topComponent = top;
		add(topComponent, BorderLayout.CENTER);
	}
	
	public void hidePanel() {
//		bottomContainer.setPreferredSize(new Dimension(hideablePanel.getPreferredSize().width, 0));
		bottomContainer.setVisible(false);
//		bottomContainer.repaint();
	}
	
	public void showPanel() {
//		bottomContainer.setPreferredSize(new Dimension(hideablePanel.getPreferredSize().width, 100));
		bottomContainer.setVisible(true);
//		bottomContainer.repaint();
	}

	public void togglePanel()
	{
		if (bottomContainer.isVisible())
			hidePanel();
		else
			showPanel();
	}

	public Component getTopComponent() {
		return topComponent;
	}

	public Component getBottomComponent() {
		return bottomComponent;
	}

	private static final long serialVersionUID = -4795940303375975535L;
}
