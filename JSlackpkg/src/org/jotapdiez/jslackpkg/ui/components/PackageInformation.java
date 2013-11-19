package org.jotapdiez.jslackpkg.ui.components;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultCaret;

import org.jotapdiez.jslackpkg.core.blacklist.BlackListManager;
import org.jotapdiez.jslackpkg.core.entities.Package;
import org.jotapdiez.jslackpkg.core.interfaces.PackageManager;
import org.jotapdiez.jslackpkg.ui.components.custom.splitPane.SplitPane;
import org.jotapdiez.jslackpkg.utils.Conversions;
import org.jotapdiez.jslackpkg.utils.ResourceMap;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class PackageInformation extends JPanel
{
	private final JTextField	txtName				= new JTextField();
	private final JTextField	txtVersion			= new JTextField();
	private final JTextField	txtState			= new JTextField();
	private final JTextField	txtSize				= new JTextField();
	private final JTextField	txtInstalledSize	= new JTextField();
	private final JTextArea		txtDescription		= new JTextArea();

	private final JButton		btnInstall			= new JButton(ResourceMap.getInstance().getString("packageInformation.button.actions.install.text"), ResourceMap.addIcon);
	private final JButton		btnUninstall		= new JButton(ResourceMap.getInstance().getString("packageInformation.button.actions.remove.text"), ResourceMap.removeIcon);
	private final JButton		btnBlackListAdd		= new JButton(ResourceMap.getInstance().getString("packageInformation.button.blacklist.add.text"), ResourceMap.searchRemoveIcon);
	private final JButton		btnBlackListRemove	= new JButton(ResourceMap.getInstance().getString("packageInformation.button.blacklist.remove.text"), ResourceMap.searchAddIcon);
	private final JButton		btnUpgrade			= new JButton(ResourceMap.getInstance().getString("packageInformation.button.blacklist.upgrade.text"), ResourceMap.upgradeIcon);
	private PackageManager		packageManager		= null;

	private Package				_packageItem		= null;
	private SplitPane parent = null;

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
		informationPanel.setBorder(new TitledBorder(null, ResourceMap.getInstance().getString("packageInformation.panel.info.border.text"), TitledBorder.LEADING, TitledBorder.TOP, null, null));

		add(informationPanel);
		informationPanel.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("121px:grow"),
				FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] { FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("default:grow"), FormFactory.RELATED_GAP_ROWSPEC, }));

		JLabel lblName = new JLabel(ResourceMap.getInstance().getString("packageInformation.label.name.text"));
		informationPanel.add(lblName, "2, 1, left, default");

		informationPanel.add(txtName, "5, 1, fill, default");
		txtName.setColumns(10);
		txtName.setEditable(false);

		JLabel lblSize = new JLabel(ResourceMap.getInstance().getString("packageInformation.label.size.text"));
		informationPanel.add(lblSize, "2, 7");

		txtSize.setColumns(10);
		txtSize.setEditable(false);
		informationPanel.add(txtSize, "5, 7, fill, default");

		JLabel lblInstalledSize = new JLabel(ResourceMap.getInstance().getString("packageInformation.label.sizeInstalled.text"));
		informationPanel.add(lblInstalledSize, "2, 9");

		txtInstalledSize.setColumns(10);
		txtInstalledSize.setEditable(false);
		informationPanel.add(txtInstalledSize, "5, 9, fill, default");

		JLabel lblDescription = new JLabel(ResourceMap.getInstance().getString("packageInformation.label.description.text"));
		informationPanel.add(lblDescription, "2, 11, left, top");

		txtDescription.setRows(5);
		txtDescription.setColumns(10);
		txtDescription.setEditable(false);

		// Evita el scoll-down al actualizar el contenido
		DefaultCaret caret = (DefaultCaret) txtDescription.getCaret();
		caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);

		JScrollPane scrollPane = new JScrollPane(txtDescription);
		informationPanel.add(scrollPane, "5, 11, fill, fill");

		JLabel lblVersion = new JLabel(ResourceMap.getInstance().getString("packageInformation.label.version.text"));
		informationPanel.add(lblVersion, "2, 3, left, default");

		informationPanel.add(txtVersion, "5, 3, fill, default");
		txtVersion.setColumns(10);
		txtVersion.setEditable(false);

		JLabel lblState = new JLabel(ResourceMap.getInstance().getString("packageInformation.label.state.text"));
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
			addRemovePanel.setBorder(new TitledBorder(null, ResourceMap.getInstance().getString("packageInformation.panel.actions.border.text"), TitledBorder.LEADING, TitledBorder.TOP, null, null));
			addRemovePanel.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, FormFactory.GROWING_BUTTON_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] { FormFactory.DEFAULT_ROWSPEC,
					FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, }));

			addRemovePanel.add(btnInstall, "2, 1, center, center");
			addRemovePanel.add(btnUninstall, "2, 3, center, center");
			addRemovePanel.add(btnUpgrade, "2, 5, center, fill");
		}

		{
			JPanel blackListPanel = new JPanel();
			blackListPanel.setBorder(new TitledBorder(null, ResourceMap.getInstance().getString("packageInformation.panel.blacklist.border.text"), TitledBorder.LEADING, TitledBorder.TOP, null, null));
			actionsPanel.add(blackListPanel);
			blackListPanel.setLayout(new FormLayout(new ColumnSpec[] {
					FormFactory.RELATED_GAP_COLSPEC,
					FormFactory.GROWING_BUTTON_COLSPEC,
					FormFactory.RELATED_GAP_COLSPEC,},
				new RowSpec[] {
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.RELATED_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.RELATED_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC,}));

			btnInstall.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					doInstall(_packageItem);
				}
			});
			btnUpgrade.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					doUpgrade(_packageItem);
				}
			});
			btnUninstall.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					doRemove(_packageItem);
				}
			});
			btnBlackListAdd.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					doBlackListAdd();
				}
			});
			blackListPanel.add(btnBlackListAdd, "2, 1");

			btnBlackListRemove.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					doBlackListRemove();
				}
			});
			blackListPanel.add(btnBlackListRemove, "2, 3, fill, center");
		}
	}

	private void doBlackListAdd()
	{
		if (_packageItem == null)
			return;

		BlackListManager.getInstance().add(_packageItem);
		updateButtons();
	}

	private void doBlackListRemove()
	{
		if (_packageItem == null)
			return;

		BlackListManager.getInstance().remove(_packageItem);
		updateButtons();
	}

	private void doRemove(final Package packageItem)
	{
		Runnable runn = new Runnable() {
			public void run() {
				if (packageItem == null)
					return;

				boolean succesful = packageManager.remove(packageItem);
				if (succesful)
				{
					setPackage(null);
					parent.hidePanel();
				}
			}
		};
		Thread t = new Thread(runn);
		t.start();
	}

	private void doInstall(final Package packageItem)
	{
		Runnable runn = new Runnable() {
			public void run() {
				if (packageItem == null)
					return;

				boolean succesful = packageManager.install(packageItem);
				if (succesful)
					setPackage(packageItem);
			}
		};
		Thread t = new Thread(runn);
		t.start();
	}

	private void doUpgrade(final Package packageItem)
	{
		Runnable runn = new Runnable() {
			public void run() {
				if (packageItem == null)
					return;

				boolean succesful = packageManager.upgrade(packageItem);
				if (succesful)
					setPackage(packageItem);
			}
		};
		Thread t = new Thread(runn);
		t.start();
	}

	public void setPackage(Package packageItem)
	{
		cleanPackage();
		
		_packageItem = packageItem;

		if (_packageItem == null)
			return;

		mapPackage();
	}

	private void mapPackage()
	{
		txtName.setText(_packageItem.getName());
		txtVersion.setText(_packageItem.getVersion());

		{
			double val = Double.parseDouble(_packageItem.getUncompressedSize());
			txtInstalledSize.setText(Conversions.parseSize(val));
		}
		{
			double val = Double.parseDouble(_packageItem.getCompressedSize());
			txtSize.setText(Conversions.parseSize(val));
		}
		txtState.setText(_packageItem.getState().toString());
		txtDescription.setText(_packageItem.getDescription());

		updateButtons();
	}

	private void cleanPackage()
	{
		txtName.setText("");
		txtVersion.setText("");

		txtInstalledSize.setText("");
		txtSize.setText("");
		txtState.setText("");
		txtDescription.setText("");

		btnInstall.setVisible(false);
		btnUninstall.setVisible(false);

		btnBlackListAdd.setVisible(false);
		btnBlackListRemove.setVisible(false);
	}

	private void updateButtons()
	{
		boolean toUpgrade = _packageItem != null && _packageItem.getState().equals(Package.STATE.TO_UPGRADE);
		btnUpgrade.setVisible(toUpgrade);
		
		if (!toUpgrade)
		{
			boolean installed = _packageItem != null && _packageItem.getState().equals(Package.STATE.INSTALLED);

			btnInstall.setVisible(!installed);
			btnUninstall.setVisible(installed);
		}

		boolean isInBlackList = _packageItem != null && _packageItem.isInBlackList();
		btnBlackListAdd.setVisible(!isInBlackList);
		btnBlackListRemove.setVisible(isInBlackList);

	}

	public void setSplitPanel(SplitPane splitPane)
	{
		parent = splitPane;
	}

	private static final long	serialVersionUID	= 7546213147519598392L;
}
