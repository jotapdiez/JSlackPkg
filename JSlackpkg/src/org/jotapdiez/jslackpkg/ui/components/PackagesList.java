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
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jotapdiez.jslackpkg.core.entities.Package;
import org.jotapdiez.jslackpkg.core.interfaces.PackageManager;
import org.jotapdiez.jslackpkg.ui.MainUI;
import org.jotapdiez.jslackpkg.ui.components.models.PackagesTable;
import org.jotapdiez.jslackpkg.ui.components.models.PackagesTableModel;
import org.jotapdiez.jslackpkg.ui.components.popups.BlackListRegexp;
import org.jotapdiez.jslackpkg.utils.ResourceMap;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class PackagesList extends JPanel
{
	private PackagesTable	table				= null;
	private JTextField		txtFilter			= null;
	private final JLabel	lblPackageCount		= new JLabel();
	private JPanel					panelButtonsActions	= new JPanel();
	
	private JButton btnBulkUpgrade = new JButton(ResourceMap.getInstance().getString("packagesList.button.upgrade.text"));
	private JButton btnBulkRemove = new JButton(ResourceMap.getInstance().getString("packagesList.button.remove.text"));
	private JButton btnBulkInstall = new JButton(ResourceMap.getInstance().getString("packagesList.button.install.text"));

	private PackageManager packageManager = null;
	
	public PackagesList(PackageManager packageManager)
	{
		super();
		this.packageManager = packageManager;
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
				// long timeTyped = System.currentTimeMillis();
				// long diff = (timeTyped-lastTimeTyped);
				// System.out.println("lastTimeTyped: " + lastTimeTyped + " | timeTyped: " + timeTyped + " | diff: " + diff );
				// if ( diff > 1000 )
				// {
				// JTextField source = (JTextField) event.getSource();
				// table.filter(source.getText());
				// System.out.println("source length: " + source.getText().length() );
				// if (source.getText().length()>3)
				filter();
				// }
				// lastTimeTyped = timeTyped;
				// super.keyTyped(event);
			}
		});
		filterPanel.add(txtFilter, "1, 2, fill, top");
		txtFilter.setColumns(10);

		JButton btnFiltrar = new JButton(ResourceMap.getInstance().getString("packagesList.button.filter.text"));
		btnFiltrar.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				filter();
				// String text = txtFilter.getText();
				// table.filter(text);
			}
		});
		filterPanel.add(btnFiltrar, "2, 2");

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout(0, 0));
		// add(panel, BorderLayout.NORTH);

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

		btnBulkInstall.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				bulkInstall();
			}
		});
		panelAcciones.add(btnBulkInstall);

		btnBulkUpgrade.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				bulkUpgrade();
			}
		});
		panelAcciones.add(btnBulkUpgrade);

		btnBulkRemove.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				bulkRemove();
			}
		});
		panelAcciones.add(btnBulkRemove);

		JPanel panelBlackList = new JPanel();
		panelBlackList.setBorder(new TitledBorder(null, ResourceMap.getInstance().getString("packagesList.panel.blacklist.border.text"), TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelButtonsActions.add(panelBlackList);

		JCheckBox chckbxShowHideBlacklist = new JCheckBox(ResourceMap.getInstance().getString("packagesList.ckeckbox.showHide.text"));
		chckbxShowHideBlacklist.addChangeListener(new ChangeListener()
		{

			@Override
			public void stateChanged(ChangeEvent e)
			{
				// TODO: Ver que manda muchos eventos
				JCheckBox source = (JCheckBox) e.getSource();
				showHideBlackList(source.isSelected());
			}
		});

		panelBlackList.add(chckbxShowHideBlacklist);

		JButton btnBulkBlackListAdd = new JButton(ResourceMap.getInstance().getString("packagesList.button.addBlackList.text"));
		btnBulkBlackListAdd.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				bulkBlackListAdd();
			}
		});
		panelBlackList.add(btnBulkBlackListAdd);

		JButton btnBulkBlackListRemove = new JButton(ResourceMap.getInstance().getString("packagesList.button.removeBlackList.text"));
		btnBulkBlackListRemove.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				bulkBlackListRemove();
			}
		});
		panelBlackList.add(btnBulkBlackListRemove);

		add(lblPackageCount, BorderLayout.SOUTH);
	}

	private void showHideBlackList(boolean show)
	{
		System.out.println("showHideBlackList::show: " + show);
	}

	private void bulkInstall()
	{
		Runnable runn = new Runnable() {
			public void run() {
				List<Package> list = table.getSelectedPackages();
				packageManager.install(list);
			}
		};
		Thread t = new Thread(runn);
		t.start();
	}

	private void bulkRemove()
	{
		Runnable runn = new Runnable() {
			public void run() {
				List<Package> list = table.getSelectedPackages();
				packageManager.remove(list);
			}
		};
		Thread t = new Thread(runn);
		t.start();
	}

	private void bulkUpgrade()
	{
		Runnable runn = new Runnable() {
			public void run() {
				List<Package> list = table.getSelectedPackages();
				packageManager.upgrade(list);
			}
		};
		Thread t = new Thread(runn);
		t.start();
	}

	BlackListRegexp popupBlackListRegex = null;
	private void bulkBlackListAdd()
	{
		if (popupBlackListRegex == null)
			popupBlackListRegex = new BlackListRegexp(null);
		
		popupBlackListRegex.setPackages(table.getSelectedPackages());
		popupBlackListRegex.setVisible(true);
		//TODO: Preguntar si:
		// 1.- Abrir popup BlackListRegexp para crear una nueva regex
		// 2.- Agregar item por item a la blacklist
	}

	private void bulkBlackListRemove()
	{
		//TODO: Chekear si cada uno de los paquetes pertenece a una regexp o no
		// Si pertenece a una regex abrir popup BlackListRegexp para editar o eliminar la regex
	}

	public void enableOnlyBulkInstall()
	{
		boolean enable = true; 
		enableBulkInstall(enable);
		enableBulkRemove(!enable);
		enableBulkUpgrade(!enable);
	}
	
	public void enableOnlyBulkRemove()
	{
		boolean enable = true; 
		enableBulkInstall(!enable);
		enableBulkRemove(enable);
		enableBulkUpgrade(!enable);
	}

	public void enableOnlyBulkUpgrade()
	{
		boolean enable = true; 
		enableBulkInstall(!enable);
		enableBulkRemove(!enable);
		enableBulkUpgrade(enable);
	}

	private void enableBulkInstall(boolean enable)
	{
		btnBulkInstall.setVisible(enable);
	}
	
	private void enableBulkRemove(boolean enable)
	{
		btnBulkRemove.setVisible(enable);
	}
	
	private void enableBulkUpgrade(boolean enable)
	{
		btnBulkUpgrade.setVisible(enable);
	}
	
	private void showPackageInformation(Package selectedPackage)
	{
		MainUI.getInstance().showPackageInformation(selectedPackage);
	}

	public void addPackages(List<Package> packages)
	{
		resetFilter();

		if (packages == null || packages.size() == 0)
			return;

		table.setData(packages);
		lblPackageCount.setText(ResourceMap.getInstance().getString("packagesList.info.packagesSize.text") + packages.size());
	}

	private void filter()
	{
		String text = txtFilter.getText();
		table.filter(text);
	}

	private void resetFilter()
	{
		txtFilter.setText("");
		table.filter("");
	}

	public void addPackage(Package packageItem)
	{
		table.addPackage(packageItem);		
	}

	public void removePackage(Package packageItem)
	{
		table.removePackage(packageItem);		
	}

	public void updatePackage(Package packageItem)
	{
		table.updatePackage(packageItem);		
	}

	private static final long	serialVersionUID	= -8724282637131335003L;
}
