package org.jotapdiez.jslackpkg.ui.components;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.RowFilter;
import javax.swing.ScrollPaneConstants;
import org.jotapdiez.jslackpkg.core.entities.Package;
import org.jotapdiez.jslackpkg.core.entities.Package.STATE;
import org.jotapdiez.jslackpkg.core.interfaces.PackageManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;
import java.util.List;

import javax.swing.JTable;

import org.jotapdiez.jslackpkg.ui.components.models.TableModel;

import javax.swing.JTextField;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;
import javax.swing.JButton;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import javax.swing.JLabel;

public class PackagesList extends JPanel
{
	// private PackageManager packageManager = null;

	private JTable		 table            = null;
	private JTextField	 txtFilter        = null;
	private final JLabel lblPackageCount  = new JLabel();
	
	private transient final TableRowSorter<TableModel>	sorter	= new TableRowSorter<TableModel>();
//	private long lastTimeTyped = 0;
	
	public PackagesList(PackageManager packageManager)
	{
		super();
		setLayout(new BorderLayout(0, 0));

		final JScrollPane scrollPane = new JScrollPane();
		final JSplitPane splitPane = new JSplitPane();
		
		splitPane.setOneTouchExpandable(true);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		add(splitPane);

		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		table = new JTable();
		table.setAutoCreateColumnsFromModel(true);
		// table.setAutoCreateRowSorter(true);
		
		table.setRowSorter(sorter);
		table.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent event)
			{
				if (event.getClickCount() >= 2)
				{
					TableModel model = (TableModel) table.getModel();
					int indexSelectedRow = table.getSelectedRow();
					Package selectedPackage = model.getValueAt(indexSelectedRow);
					PackageInformation.getInstance(null).setPackage(selectedPackage);
				}
			}
		});

		scrollPane.setViewportView(table);
		splitPane.setBottomComponent(scrollPane);

		JPanel filterPanel = new JPanel();
		filterPanel.setBorder(new TitledBorder(null, "Filtrar", TitledBorder.LEADING, TitledBorder.TOP, null, null)); // TODO: A archivo de lenguajes
		filterPanel.setMaximumSize(filterPanel.getMinimumSize());
		splitPane.setTopComponent(filterPanel);
		filterPanel.setLayout(new FormLayout(new ColumnSpec[] { ColumnSpec.decode("280px:grow"), ColumnSpec.decode("114px"), }, new RowSpec[] { FormFactory.LINE_GAP_ROWSPEC, RowSpec.decode("19px"), }));

		txtFilter = new JTextField();
		txtFilter.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyReleased(KeyEvent event)
			{
//				long timeTyped = System.currentTimeMillis(); 
//				long diff = (timeTyped-lastTimeTyped); 
//				System.out.println("lastTimeTyped: " + lastTimeTyped + " | timeTyped: " + timeTyped + " | diff: " + diff );
//				if ( diff > 1000 )
//				{
					JTextField source = (JTextField) event.getSource();
//					System.out.println("source length: " + source.getText().length() );
//					if (source.getText().length()>3)
						doFilter(source.getText());
//				}
//				lastTimeTyped = timeTyped;
//				super.keyTyped(event);
			}
		});
		filterPanel.add(txtFilter, "1, 2, fill, top");
		txtFilter.setColumns(10);

		JButton btnFiltrar = new JButton("Filtrar"); // TODO: A archivo de lenguajes
		btnFiltrar.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String text = txtFilter.getText();
				doFilter(text);
			}
		});
		filterPanel.add(btnFiltrar, "2, 2");
		
		add(lblPackageCount, BorderLayout.SOUTH);
	}

	private void configureRowSorter()
	{
		final String regexpRemoveNonNumbers = "[^0-9]*";
		
		sorter.setSortsOnUpdates(true);
		//Harcoding columna Estado
		sorter.setComparator(2, new Comparator<STATE>()
		{
			public int compare(STATE o1, STATE o2)
			{
				return o1.compareTo(o2);
			}
		});
		
		//Harcoding columna version
		sorter.setComparator(3, new Comparator<String>()
		{
			public int compare(String o1, String o2)
			{
				Long o1l = Long.valueOf(o1.replaceAll(regexpRemoveNonNumbers, ""));
				Long o2l = Long.valueOf(o2.replaceAll(regexpRemoveNonNumbers, ""));
				return o1l.compareTo(o2l);
			}
		});
		//Harcoding columna build
		sorter.setComparator(3, new Comparator<String>()
		{
			public int compare(String o1, String o2)
			{
				Long o1l = Long.valueOf(o1.replaceAll(regexpRemoveNonNumbers, ""));
				Long o2l = Long.valueOf(o2.replaceAll(regexpRemoveNonNumbers, ""));
				return o1l.compareTo(o2l);
			}
		});
	}

	public void addPackages(List<Package> packages)
	{
		resetFilter();
		
		TableModel model = new TableModel();
		model.setData(packages);
		sorter.setModel(model);
		table.setModel(model);
		lblPackageCount.setText("Cantidad de paquetes: "+ packages.size());  // TODO: A archivo de lenguajes
	}

	private void resetFilter()
	{
		txtFilter.setText("");
		doFilter("");
	}
	private void doFilter(String text)
	{
//		System.out.println("doFilter(String text): " + text + " | text.length(): " + text.length());
		RowFilter<Object, Object> filter = null;
		
		if (text.length() > 0)
			filter = RowFilter.regexFilter(text);

		sorter.setRowFilter(filter);
	}
	
	private static final long	serialVersionUID	= -8724282637131335003L;
}
