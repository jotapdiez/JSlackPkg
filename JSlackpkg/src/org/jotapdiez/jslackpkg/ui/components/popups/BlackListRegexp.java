package org.jotapdiez.jslackpkg.ui.components.popups;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Standalone Swing GUI application for demonstrating REs. <br/>
 * TODO: Show the entire match, and $1 and up as captures that matched.
 * 
 * @author Ian Darwin, http://www.darwinsys.com/
 * @version #Id$
 */
public class BlackListRegexp extends JDialog {

	protected Pattern pattern;

	protected Matcher matcher;

	protected JTextField patternTF;
	protected JList packagesList;

	protected JCheckBox compiledOK;

	/** "main program" method - construct and show */
	public static void main(String[] av) {
		BlackListRegexp comp = new BlackListRegexp(null);
		comp.setVisible(true);
	}

	/** Construct the REDemo object including its GUI */
	public BlackListRegexp(JFrame parent) {
		super(parent, true);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout(3, 3));
		
		{
			JPanel top = new JPanel();
			top.add(new JLabel("Pattern:", JLabel.RIGHT)); //TODO: Al archivo de lenguajes
			patternTF = new JTextField(20);
			patternTF.getDocument().addDocumentListener(new PatternListener());
			top.add(patternTF);
			top.add(new JLabel("Syntax OK?"));
			compiledOK = new JCheckBox();
			compiledOK.setEnabled(false);
			top.add(compiledOK);
			getContentPane().add(top, BorderLayout.NORTH);
		}
		
		{
			JPanel strPane = new JPanel();
			strPane.setLayout(new BorderLayout(5, 2));
			JLabel lblPackages = new JLabel("Paquetes", JLabel.RIGHT); //TODO: Al archivo de lenguajes
			lblPackages.setVerticalAlignment(SwingConstants.TOP);
			strPane.add(lblPackages, BorderLayout.WEST);
			packagesList = new JList();
			packagesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			packagesList.setEnabled(false);
//			DefaultListModel model = new DefaultListModel();
			packagesList.setModel(new AbstractListModel() {
				private static final long serialVersionUID = 6736409540115822230L;
				String[] values = new String[] {"coreutils-8.19-i486-1", "eject-2.1.5-i486-3", "mkinitrd-1.4.7-i486-6", "calligra-l10n-ca-2.4.3-noarch-1", "calligra-l10n-ca\\@valencia-2.4.3-noarch-1", "calligra-l10n-cs-2.4.3-noarch-1", "calligra-l10n-da-2.4.3-noarch-1", "calligra-l10n-de-2.4.3-noarch-1", "calligra-l10n-el-2.4.3-noarch-1", "calligra-l10n-en_GB-2.4.3-noarch-1", "calligra-l10n-es-2.4.3-noarch-1", "calligra-l10n-et-2.4.3-noarch-1", "calligra-l10n-fi-2.4.3-noarch-1", "calligra-l10n-fr-2.4.3-noarch-1", "calligra-l10n-hu-2.4.3-noarch-1", "calligra-l10n-it-2.4.3-noarch-1", "calligra-l10n-kk-2.4.3-noarch-1", "calligra-l10n-nb-2.4.3-noarch-1", "calligra-l10n-nds-2.4.3-noarch-1", "calligra-l10n-nl-2.4.3-noarch-1", "calligra-l10n-pl-2.4.3-noarch-1", "calligra-l10n-pt-2.4.3-noarch-1", "calligra-l10n-pt_BR-2.4.3-noarch-1", "calligra-l10n-ru-2.4.3-noarch-1", "calligra-l10n-sk-2.4.3-noarch-1", "calligra-l10n-sv-2.4.3-noarch-1", "calligra-l10n-uk-2.4.3-noarch-1", "calligra-l10n-zh_CN-2.4.3-noarch-1", "calligra-l10n-zh_TW-2.4.3-noarch-1"};
				public int getSize() {
					return values.length;
				}
				public Object getElementAt(int index) {
					return values[index];
				}
			});
//			packagesList.setEnabled(false);
//			packagesList.setCellRenderer(new ListRender());
			
			JScrollPane scroll = new JScrollPane(packagesList);
			strPane.add(scroll, BorderLayout.CENTER);
			getContentPane().add(strPane);
		}
		
		{
			JPanel panel = new JPanel();
			getContentPane().add(panel, BorderLayout.SOUTH);
			
			JButton btnAceptar = new JButton("Aceptar"); //TODO: Al archivo de lenguajes
			panel.add(btnAceptar);
			
			JButton btnCancelar = new JButton("Cancelar"); //TODO: Al archivo de lenguajes
			panel.add(btnCancelar);
		}
		pack();
	}

	class ListRender implements ListCellRenderer
	{
		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			
			JLabel comp = new JLabel(value.toString());
			if (isSelected)
				comp.setBackground(Color.RED);
			return comp;
		}
	}
	
	protected void tryCompile() {
		pattern = null;
		try {
			pattern = Pattern.compile(patternTF.getText(), Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
			matcher = pattern.matcher("");
			compiledOK.setSelected(true);
			tryMatch();
		} catch (PatternSyntaxException ex) {
			compiledOK.setSelected(false);
		}
	}

	protected boolean tryMatch() {
		if (pattern == null)
			return false;
		
		packagesList.clearSelection();
		
		List<Integer> selected = new LinkedList<Integer>();
		ListModel model = packagesList.getModel();
		
		int[] sell = new int[model.getSize()];
		int index = 0;
		for (int i = 0 ; i<model.getSize() ; ++i)
		{
			String item = model.getElementAt(i).toString();
			Matcher m = pattern.matcher(item);
			
			if (m.find())
			{
				sell[index++]=i;
				selected.add(i);
			}
		}
		
		if (selected.size()>0)
		{
			int[] sel2 = new int[selected.size()];
			System.arraycopy(sell, 0, sel2, 0, selected.size());
			packagesList.setSelectedIndices(sel2);
		}
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
	
	private static final long serialVersionUID = 4956520988700360228L;
}