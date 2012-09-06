package org.jotapdiez.jslackpkg.test;

import java.awt.GridLayout;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import javax.swing.SwingConstants;
import java.awt.FlowLayout;
import javax.swing.JList;

/**
 * Standalone Swing GUI application for demonstrating REs. <br/>
 * TODO: Show the entire match, and $1 and up as captures that matched.
 * 
 * @author Ian Darwin, http://www.darwinsys.com/
 * @version #Id$
 */
public class TestPopupBlackListRegexp extends JPanel {
	protected Pattern pattern;

	protected Matcher matcher;

	protected JTextField patternTF;
	protected JList stringTF;

	protected JCheckBox compiledOK;

	protected JRadioButton match, find, findAll;

	protected JTextField matchesTF;
	private JPanel panel;

	/** "main program" method - construct and show */
	public static void main(String[] av) {
		JFrame f = new JFrame("REDemo");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		TestPopupBlackListRegexp comp = new TestPopupBlackListRegexp();
		f.setContentPane(comp);
		f.pack();
		f.setLocation(200, 200);
		f.setVisible(true);
	}

	/** Construct the REDemo object including its GUI */
	public TestPopupBlackListRegexp() {
		super();

		JPanel top = new JPanel();
		top.add(new JLabel("Pattern:", JLabel.RIGHT));
		patternTF = new JTextField(20);
		patternTF.getDocument().addDocumentListener(new PatternListener());
		top.add(patternTF);
		top.add(new JLabel("Syntax OK?"));
		compiledOK = new JCheckBox();
		top.add(compiledOK);

		ChangeListener cl = new ChangeListener() {
			public void stateChanged(ChangeEvent ce) {
				tryMatch();
			}
		};
		JPanel switchPane = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) switchPane.getLayout();
		flowLayout_1.setVgap(0);
		ButtonGroup bg = new ButtonGroup();
		match = new JRadioButton("Match");
		match.setSelected(true);
		match.addChangeListener(cl);
		bg.add(match);
		switchPane.add(match);
		find = new JRadioButton("Find");
		find.addChangeListener(cl);
		bg.add(find);
		switchPane.add(find);
		findAll = new JRadioButton("Find All");
		findAll.addChangeListener(cl);
		bg.add(findAll);
		switchPane.add(findAll);

		JPanel strPane = new JPanel();
		strPane.setLayout(new BorderLayout(0, 0));
		JLabel label_1 = new JLabel("String:", JLabel.RIGHT);
		label_1.setVerticalAlignment(SwingConstants.TOP);
		strPane.add(label_1, BorderLayout.WEST);
		stringTF = new JList();
		strPane.add(stringTF, BorderLayout.CENTER);
		
		panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setVgap(0);
		strPane.add(panel, BorderLayout.EAST);
		JLabel label = new JLabel("Matches:");
		panel.add(label);
		matchesTF = new JTextField(3);
		panel.add(matchesTF);
		setLayout(new BorderLayout(0, 0));
		add(top, BorderLayout.NORTH);
		add(strPane);
		add(switchPane, BorderLayout.SOUTH);
	}

	protected void setMatches(boolean b) {
		if (b)
			matchesTF.setText("Yes");
		else
			matchesTF.setText("No");
	}

	protected void setMatches(int n) {
		matchesTF.setText(Integer.toString(n));
	}

	protected void tryCompile() {
		pattern = null;
		try {
			pattern = Pattern.compile(patternTF.getText());
			matcher = pattern.matcher("");
			compiledOK.setSelected(true);
		} catch (PatternSyntaxException ex) {
			compiledOK.setSelected(false);
		}
	}

	protected boolean tryMatch() {
		if (pattern == null)
			return false;
		
		//TODO: Si machea la regex (pattern) con alguno de los items de la lista marcarlo como seleccionado en la lista
		
//		matcher.reset(stringTF.getText());
//		if (match.isSelected() && matcher.matches()) {
//			setMatches(true);
//			return true;
//		}
//		if (find.isSelected() && matcher.find()) {
//			setMatches(true);
//			return true;
//		}
//		if (findAll.isSelected()) {
//			int i = 0;
//			while (matcher.find()) {
//				++i;
//			}
//			if (i > 0) {
//				setMatches(i);
//				return true;
//			}
//		}
//		setMatches(false);
		return false;
	}

	/** Any change to the pattern tries to compile the result. */
	class PatternListener implements DocumentListener {

		public void changedUpdate(DocumentEvent ev) {
			tryCompile();
		}

		public void insertUpdate(DocumentEvent ev) {
			tryCompile();
		}

		public void removeUpdate(DocumentEvent ev) {
			tryCompile();
		}
	}

	/** Any change to the input string tries to match the result */
	class StringListener implements DocumentListener {

		public void changedUpdate(DocumentEvent ev) {
			tryMatch();
		}

		public void insertUpdate(DocumentEvent ev) {
			tryMatch();
		}

		public void removeUpdate(DocumentEvent ev) {
			tryMatch();
		}
	}
}