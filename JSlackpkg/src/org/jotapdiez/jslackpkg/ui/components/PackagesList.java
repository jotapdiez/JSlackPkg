package org.jotapdiez.jslackpkg.ui.components;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;

import org.jotapdiez.jslackpkg.core.entities.Package;
import org.jotapdiez.jslackpkg.core.interfaces.PackageManager;
import org.jotapdiez.jslackpkg.ui.MainUI;
import org.jotapdiez.jslackpkg.ui.components.models.PackagesTable;
import org.jotapdiez.jslackpkg.ui.components.models.PackagesTableModel;
import org.jotapdiez.jslackpkg.utils.ResourceMap;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class PackagesList extends JPanel
{
	// private PackageManager packageManager = null;

	private PackagesTable		 table            = null;
	private JTextField	 txtFilter        = null;
	private final JLabel lblPackageCount  = new JLabel();
	JPanel panelButtonsActions = new JPanel();
	
//	private long lastTimeTyped = 0;
	
	public PackagesList(PackageManager packageManager)
	{
		super();
		setLayout(new BorderLayout(0, 0));
		final JSplitPane splitPane = new JSplitPane();
		
		splitPane.setOneTouchExpandable(true);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		add(splitPane);

		JPanel filterPanel = new JPanel();
		filterPanel.setBorder(new TitledBorder(null, ResourceMap.getInstance().getString("packagesList.button.filter.text"), TitledBorder.LEADING, TitledBorder.TOP, null, null));
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
						table.filter(source.getText());
//				}
//				lastTimeTyped = timeTyped;
//				super.keyTyped(event);
			}
		});
		filterPanel.add(txtFilter, "1, 2, fill, top");
		txtFilter.setColumns(10);

		JButton btnFiltrar = new JButton(ResourceMap.getInstance().getString("packagesList.button.filter.text"));
		btnFiltrar.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String text = txtFilter.getText();
				table.filter(text);
			}
		});
		filterPanel.add(btnFiltrar, "2, 2");
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout(0, 0));
//		add(panel, BorderLayout.NORTH);
		
		final JScrollPane scrollPane = new JScrollPane();
		panel.add(scrollPane);
				
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		table = new PackagesTable();
		
		table.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent event)
			{
				if (event.getClickCount() >= 2)
				{
					PackagesTableModel model = (PackagesTableModel) table.getModel();
					int indexSelectedRow = table.getSelectedRow();
					indexSelectedRow = table.convertRowIndexToModel(indexSelectedRow);
					
					Package selectedPackage = model.getValueAt(indexSelectedRow);
					showPackageInformation(selectedPackage);
				}
			}
		});
		
		scrollPane.setViewportView(table);
		splitPane.setBottomComponent(panel);
		
		FlowLayout fl_panelButtonsActions = (FlowLayout) panelButtonsActions.getLayout();
		fl_panelButtonsActions.setHgap(1);
		fl_panelButtonsActions.setAlignment(FlowLayout.LEFT);
		fl_panelButtonsActions.setVgap(2);
		panel.add(panelButtonsActions, BorderLayout.SOUTH);
		
		JPanel panelAcciones = new JPanel();
		panelAcciones.setBorder(new TitledBorder(null, ResourceMap.getInstance().getString("packagesList.panel.actions.border.text"), TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelButtonsActions.add(panelAcciones);
		
		JButton btnInstallSelected = new JButton(ResourceMap.getInstance().getString("packagesList.button.install.text"));
		panelAcciones.add(btnInstallSelected);
		
		JButton btnActualizar = new JButton(ResourceMap.getInstance().getString("packagesList.button.upgrade.text"));
		panelAcciones.add(btnActualizar);
		
		JButton btnEliminar = new JButton(ResourceMap.getInstance().getString("packagesList.button.remove.text"));
		panelAcciones.add(btnEliminar);
		
		JPanel panelBlackList = new JPanel();
		panelBlackList.setBorder(new TitledBorder(null, ResourceMap.getInstance().getString("packagesList.panel.blacklist.border.text"), TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelButtonsActions.add(panelBlackList);
		
		JCheckBox chckbxShowHideBlacklist = new JCheckBox(ResourceMap.getInstance().getString("packagesList.ckeckbox.showHide.text"));
		panelBlackList.add(chckbxShowHideBlacklist);
		
		JButton btnAgregar = new JButton(ResourceMap.getInstance().getString("packagesList.button.addBlackList.text"));
		panelBlackList.add(btnAgregar);
		
		JButton btnEliminar_1 = new JButton(ResourceMap.getInstance().getString("packagesList.button.removeBlackList.text"));
		panelBlackList.add(btnEliminar_1);
		btnInstallSelected.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				List<Package> list = table.getSelectedPackages();
				if (list.size()>0)
				{
					System.out.println("SELECTED PACKAGES ===============");
					for (Package item : list)
						System.out.println("selected: "+item.getName());
					System.out.println("===============");
				}
				
//				int[] indexSelectedRow = table.getSelectedRows();
//				
//				if (indexSelectedRow.length>0)
//					System.out.println("SELECTED PACKAGES ===============");
//				for (int index : indexSelectedRow)
//				{
//					index = table.convertRowIndexToModel(index);
//					Package selectedPackage = ((PackagesTableModel) table.getModel()).getValueAt(index);
//					System.out.println(selectedPackage.getName());
//				}
//				System.out.println("===============");
				
//				
//				Package selectedPackage = model.getValueAt(indexSelectedRow);
//
//				List<Package> list = ((PackagesTableModel) table.getModel()).getSelected();
//				for (Package item : list)
//					System.out.println("selected: "+item.getName());
			}
		});
		
		add(lblPackageCount, BorderLayout.SOUTH);
	}

	private void showPackageInformation(Package selectedPackage)
	{
		MainUI.getInstance().showPackageInformation(selectedPackage);
	}
	
	public void addPackages(List<Package> packages)
	{
		resetFilter();
		
		if (packages==null || packages.size()==0)
			return;
		
		table.setData(packages);
		lblPackageCount.setText(ResourceMap.getInstance().getString("packagesList.info.packagesSize.text") + packages.size());
	}

	private void resetFilter()
	{
		txtFilter.setText("");
		table.filter("");
	}
	
	private static final long	serialVersionUID	= -8724282637131335003L;
}
