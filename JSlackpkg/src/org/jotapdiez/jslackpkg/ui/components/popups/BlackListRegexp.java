package org.jotapdiez.jslackpkg.ui.components.popups;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.AbstractListModel;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jotapdiez.jslackpkg.core.blacklist.BlackListManager;
import org.jotapdiez.jslackpkg.core.entities.Package;

/**
 * Standalone Swing GUI application for demonstrating REs. <br/>
 * TODO: Show the entire match, and $1 and up as captures that matched.
 * 
 * @author Ian Darwin, http://www.darwinsys.com/
 * @version #Id$
 */
public class BlackListRegexp extends JDialog {

	enum MODE
	{
		REGEX, SINGLE;
		
		public String getName()
		{
			switch (this)
			{
			case SINGLE:
				return "single";
			default:
			case REGEX:
				return "regex";
			}
		}
	}
	protected Pattern pattern;

	protected Matcher matcher;

	protected JTextField patternTF;
	protected JList packagesListRegex;
	protected JList packagesListSingle;

	private MODE mode = MODE.REGEX;
	private AbstractListModel model = null;
	
	private JPanel panelEdit = new JPanel();
	/** "main program" method - construct and show */
	public static void main(String[] av) {
		BlackListRegexp comp = new BlackListRegexp();
		comp.setVisible(true);
	}

	/** Construct the REDemo object including its GUI */
	public BlackListRegexp() {
		this(null);
	}
	public BlackListRegexp(JFrame parent) {
		super(parent, "Editor de Regex para blacklist", true); //TODO: Al archivo de lenguajes

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout(3, 3));
		
		JPanel panelMode = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panelMode.getLayout();
		flowLayout.setHgap(20);
		flowLayout.setVgap(1);
		panelMode.setBorder(new TitledBorder(null, "Modo", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		getContentPane().add(panelMode, BorderLayout.NORTH);
		
		CheckboxListener cbListener = new CheckboxListener();
		
		JRadioButton rbRegex = new JRadioButton("Regex");
		rbRegex.setSelected(true);
		rbRegex.setName(MODE.REGEX.getName());
		rbRegex.addItemListener(cbListener);
		buttonGroup.add(rbRegex);
		panelMode.add(rbRegex);
		
		JRadioButton rbSingle = new JRadioButton("Individual");
		rbSingle.setName(MODE.SINGLE.getName());
		rbSingle.addItemListener(cbListener);
		buttonGroup.add(rbSingle);
		panelMode.add(rbSingle);
		panelEdit.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		getContentPane().add(panelEdit, BorderLayout.CENTER);
		panelEdit.setLayout(new CardLayout(0, 0));
		
		JPanel panelRegex = new JPanel();
		panelEdit.add(panelRegex, rbRegex.getName());
		panelRegex.setLayout(new BorderLayout(0, 0));
		
		{
			JPanel top = new JPanel();
			panelRegex.add(top, BorderLayout.NORTH);
			top.add(new JLabel("Pattern:", JLabel.RIGHT)); //TODO: Al archivo de lenguajes
			patternTF = new JTextField(20);
			patternTF.getDocument().addDocumentListener(new PatternListener());
			top.add(patternTF);
		}
		
		{
			JPanel strPane = new JPanel();
			panelRegex.add(strPane, BorderLayout.CENTER);
			strPane.setLayout(new BorderLayout(5, 2));
			JLabel lblPackages = new JLabel("Paquetes", JLabel.RIGHT); //TODO: Al archivo de lenguajes
			lblPackages.setVerticalAlignment(SwingConstants.TOP);
			strPane.add(lblPackages, BorderLayout.WEST);
//			DefaultListModel model = new DefaultListModel();
//			packagesList.setModel(new AbstractListModel() {
//				private static final long serialVersionUID = 6736409540115822230L;
//				String[] values = new String[] {"coreutils-8.19-i486-1", "eject-2.1.5-i486-3", "mkinitrd-1.4.7-i486-6", "calligra-l10n-ca-2.4.3-noarch-1", "calligra-l10n-ca\\@valencia-2.4.3-noarch-1", "calligra-l10n-cs-2.4.3-noarch-1", "calligra-l10n-da-2.4.3-noarch-1", "calligra-l10n-de-2.4.3-noarch-1", "calligra-l10n-el-2.4.3-noarch-1", "calligra-l10n-en_GB-2.4.3-noarch-1", "calligra-l10n-es-2.4.3-noarch-1", "calligra-l10n-et-2.4.3-noarch-1", "calligra-l10n-fi-2.4.3-noarch-1", "calligra-l10n-fr-2.4.3-noarch-1", "calligra-l10n-hu-2.4.3-noarch-1", "calligra-l10n-it-2.4.3-noarch-1", "calligra-l10n-kk-2.4.3-noarch-1", "calligra-l10n-nb-2.4.3-noarch-1", "calligra-l10n-nds-2.4.3-noarch-1", "calligra-l10n-nl-2.4.3-noarch-1", "calligra-l10n-pl-2.4.3-noarch-1", "calligra-l10n-pt-2.4.3-noarch-1", "calligra-l10n-pt_BR-2.4.3-noarch-1", "calligra-l10n-ru-2.4.3-noarch-1", "calligra-l10n-sk-2.4.3-noarch-1", "calligra-l10n-sv-2.4.3-noarch-1", "calligra-l10n-uk-2.4.3-noarch-1", "calligra-l10n-zh_CN-2.4.3-noarch-1", "calligra-l10n-zh_TW-2.4.3-noarch-1"};
//				public int getSize() {
//					return values.length;
//				}
//				public Object getElementAt(int index) {
//					return values[index];
//				}
//			});
//			packagesList.setEnabled(false);
			
			packagesListRegex = new JList();
			packagesListRegex.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			packagesListRegex.setEnabled(false);
			
			JScrollPane scroll = new JScrollPane(packagesListRegex);
			strPane.add(scroll, BorderLayout.CENTER);
		}
		
		{
			JPanel panelSingle = new JPanel();
			panelSingle.setLayout(new BorderLayout(5, 2));
			packagesListSingle = new JList();
			packagesListSingle.setEnabled(false);
			packagesListSingle.setCellRenderer(new ListRenderSingle());
			
			JScrollPane scroll = new JScrollPane(packagesListSingle);
			panelSingle.add(scroll);
			panelEdit.add(panelSingle, rbSingle.getName());
			
			JLabel lblNewLabel = new JLabel("Paquetes");
			lblNewLabel.setVerticalAlignment(SwingConstants.TOP);
			panelSingle.add(lblNewLabel, BorderLayout.WEST);
		}
		
		{
			JPanel panelButtons = new JPanel();
			getContentPane().add(panelButtons, BorderLayout.SOUTH);
			FlowLayout fl_panelButtons = (FlowLayout) panelButtons.getLayout();
			fl_panelButtons.setVgap(3);
			
			JButton btnAceptar = new JButton("Aceptar"); //TODO: Al archivo de lenguajes
			btnAceptar.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					if (mode == MODE.REGEX)
						BlackListManager.getInstance().add(patternTF.getText());
					else
					{
						for (int i=0 ; i<model.getSize() ; ++i)
						{
							BlackListManager.getInstance().add((Package) model.getElementAt(i));
						}
					}
					
					dispose();
				}
			});
			panelButtons.add(btnAceptar);
			
			JButton btnCancelar = new JButton("Cancelar"); //TODO: Al archivo de lenguajes
			btnCancelar.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					dispose();
				}
			});
			panelButtons.add(btnCancelar);
		}
		pack();
	}

	public void setPackages(final List<Package> packages)
	{
		model = new AbstractListModel() {
			private static final long serialVersionUID = 6736409540115822230L;
			List<Package> _data = packages;
			public int getSize() {
				return _data.size();
			}
			public Object getElementAt(int index) {
				return _data.get(index);
			}
		};
		
		packagesListRegex.setModel(model);
		packagesListSingle.setModel(model);
	}
	
	class CheckboxListener implements ItemListener
	{
		@Override
		public void itemStateChanged(ItemEvent e)
		{
			 CardLayout cl = (CardLayout)(panelEdit.getLayout());
			 JComponent source = (JComponent) e.getSource();
			 if (source.getName().equals(MODE.REGEX.getName()))
				 mode = MODE.REGEX;
			 else
				 mode = MODE.SINGLE;
			 cl.show(panelEdit, mode.getName());
		}
	}
	
//	class ListRenderRegex implements ListCellRenderer
//	{
//		@Override
//		public Component getListCellRendererComponent(JList list, Object value,
//				int index, boolean isSelected, boolean cellHasFocus) {
//			
//			JLabel comp = new JLabel(value.toString());
//			if (isSelected)
//				comp.setBackground(Color.RED);
//			return comp;
//		}
//	}
	
	class ListRenderSingle implements ListCellRenderer
	{
		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			
			Package item = (Package) value;
			JLabel comp = new JLabel(item.getName());
			return comp;
		}
	}
	
	protected void tryCompile()
	{
		pattern = null;
		try {
			pattern = Pattern.compile(patternTF.getText(), Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
			matcher = pattern.matcher("");

			patternTF.setBackground(Color.GREEN);
			tryMatch();
		} catch (PatternSyntaxException ex) {
			patternTF.setBackground(Color.RED);
		}
	}

	protected boolean tryMatch() {
		if (pattern == null)
			return false;
		
		packagesListRegex.clearSelection();
		
		List<Integer> selected = new LinkedList<Integer>();
		ListModel model = packagesListRegex.getModel();
		
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
			packagesListRegex.setSelectedIndices(sel2);
		}
		return false;
	}

	/** Any change to the pattern tries to compile the result. */
	class PatternListener implements DocumentListener 
	{
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
	class StringListener implements DocumentListener
	{
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
	private final ButtonGroup buttonGroup = new ButtonGroup();
}