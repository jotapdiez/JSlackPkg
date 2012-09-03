package org.jotapdiez.jslackpkg.ui.components;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.JLabel;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.jotapdiez.jslackpkg.core.entities.Package;
import org.jotapdiez.jslackpkg.core.interfaces.PackageManager;
import org.jotapdiez.jslackpkg.utils.ResourceMap;
import javax.swing.JCheckBox;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PackageInformation extends JPanel
{
	private final JTextField			txtName				= new JTextField();
	private final JTextField			txtVersion			= new JTextField();
	private final JTextField			txtState			= new JTextField();
	private final JTextField			txtSize				= new JTextField();
	private final JTextField			txtInstalledSize	= new JTextField();
	private final JTextArea				txtDescription		= new JTextArea();

	private final JButton				btnInstall			= new JButton("Install");							    // TODO: A archivo de lenguajes
	private final JButton				btnUninstall		= new JButton("Uninstall");						        // TODO: A archivo de lenguajes
	private final JButton				btnBlackListAdd		= new JButton("Add"   , ResourceMap.searchRemoveIcon ); // TODO: A archivo de lenguajes
	private final JButton				btnBlackListRemove	= new JButton("Remove", ResourceMap.searchAddIcon    ); // TODO: A archivo de lenguajes

	private PackageManager				packageManager		= null;

	private Package						_packageItem		= null;

//	private static PackageInformation	_instance			= null;
//
//	public static PackageInformation getInstance(PackageManager packageManager)
//	{
//		if (_instance == null)
//			_instance = new PackageInformation(packageManager);
//		return _instance;
//	}

	public PackageInformation(PackageManager packageManager)
	{
		this.packageManager = packageManager;
		setLayout(new BorderLayout(0, 0));

		buildInformationPanel();
		buildActionsPanel();
	}

	public void buildInformationPanel()
	{
		JPanel informationPanel = new JPanel();
		informationPanel.setBorder(new TitledBorder(null, "Information", TitledBorder.LEADING, TitledBorder.TOP, null, null)); // TODO: A archivo de lenguajes

		add(informationPanel);
		informationPanel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("121px:grow"),
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,}));

		JLabel lblName = new JLabel("Nombre");
		informationPanel.add(lblName, "2, 1, left, default");

		informationPanel.add(txtName, "5, 1, fill, default");
		txtName.setColumns(10);
		txtName.setEditable(false);

		JLabel lblSize = new JLabel("Tamano"); // TODO: A archivo de lenguajes
		informationPanel.add(lblSize, "2, 7");

		txtSize.setColumns(10);
		txtSize.setEditable(false);
		informationPanel.add(txtSize, "5, 7, fill, default");

		JLabel lblInstalledSize = new JLabel("Tamano Instalado"); // TODO: A archivo de lenguajes
		informationPanel.add(lblInstalledSize, "2, 9");

		txtInstalledSize.setColumns(10);
		txtInstalledSize.setEditable(false);
		informationPanel.add(txtInstalledSize, "5, 9, fill, default");

		JLabel lblDescription = new JLabel("Descripcion"); // TODO: A archivo de lenguajes
		informationPanel.add(lblDescription, "2, 11, left, top");

		txtDescription.setRows(5);
		txtDescription.setColumns(10);
		txtDescription.setEditable(false);
		
		JScrollPane scrollPane = new JScrollPane(txtDescription);
		informationPanel.add(scrollPane, "5, 11, fill, fill");

		JLabel lblVersion = new JLabel("Version"); // TODO: A archivo de lenguajes
		informationPanel.add(lblVersion, "2, 3, left, default");

		informationPanel.add(txtVersion, "5, 3, fill, default");
		txtVersion.setColumns(10);
		txtVersion.setEditable(false);
		
		JLabel lblState = new JLabel("Estado"); // TODO: A archivo de lenguajes
		informationPanel.add(lblState, "2, 5, left, default");

		informationPanel.add(txtState, "5, 5, fill, default");
		txtState.setColumns(10);
		txtState.setEditable(false);
	}

	public void buildActionsPanel()
	{
		JPanel actionsPanel = new JPanel();
		add(actionsPanel, BorderLayout.EAST);
		actionsPanel.setLayout(new GridLayout(0, 1, 0, 0));

		{
			JPanel addRemovePanel = new JPanel();
			actionsPanel.add(addRemovePanel);
			addRemovePanel.setBorder(new TitledBorder(null, "Actions", TitledBorder.LEADING, TitledBorder.TOP, null, null)); // TODO: A archivo de lenguajes
			addRemovePanel.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, FormFactory.GROWING_BUTTON_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] { FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, }));
	
			btnInstall.setIcon(ResourceMap.addIcon);
			addRemovePanel.add(btnInstall, "2, 1, fill, center");
	
			btnUninstall.setIcon(ResourceMap.removeIcon);
			addRemovePanel.add(btnUninstall, "2, 3, fill, center");
		}

		{
			JPanel blackListPanel = new JPanel();
			blackListPanel.setBorder(new TitledBorder(null, "Blacklist", TitledBorder.LEADING, TitledBorder.TOP, null, null)); // TODO: A archivo de lenguajes
			actionsPanel.add(blackListPanel);
			blackListPanel.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.GROWING_BUTTON_COLSPEC, }, new RowSpec[] { FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, }));
	
			btnInstall.setEnabled(false);
			btnInstall.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					/*boolean succesful = */packageManager.install(_packageItem);
				}
			});
			btnUninstall.setEnabled(false);
			btnUninstall.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					/*boolean succesful = */packageManager.remove(_packageItem);
				}
			});
			btnBlackListAdd.setEnabled(false);
			btnBlackListRemove.setEnabled(false);
			blackListPanel.add(btnBlackListAdd, "1, 3");
			blackListPanel.add(btnBlackListRemove, "1, 5, fill, center");
		}
	}

	public void setPackage(String packageFileName)
	{
		Package packageItem = packageManager.getPackage(packageFileName);
		if (packageItem != null)
			setPackage(packageItem);
	}

	public void setPackage(Package packageItem)
	{
		_packageItem = packageItem;
		mapPackage();
	}

	private void mapPackage()
	{
		txtName.setText(_packageItem.getName());
		txtVersion.setText(_packageItem.getVersion());
		txtInstalledSize.setText(_packageItem.getUncompressedSize());
		txtSize.setText(_packageItem.getCompressedSize());
		txtState.setText(_packageItem.getState().toString());
		txtDescription.setText(_packageItem.getDescription());

		updateButtons();
	}

	private void updateButtons()
	{
		boolean installed = _packageItem.getState().equals(Package.STATE.INSTALLED);
		
		btnInstall.setEnabled(!installed);
		btnUninstall.setEnabled(installed);
	}

	private static final long	serialVersionUID	= 7546213147519598392L;
}
