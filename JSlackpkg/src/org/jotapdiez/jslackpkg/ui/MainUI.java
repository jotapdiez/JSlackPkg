package org.jotapdiez.jslackpkg.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.jotapdiez.jslackpkg.core.entities.Package;
import org.jotapdiez.jslackpkg.core.impl.JSlackpkgPackageManager;
import org.jotapdiez.jslackpkg.core.impl.PackageManagerImpl;
import org.jotapdiez.jslackpkg.core.observers.impl.InstalledObserver;
import org.jotapdiez.jslackpkg.core.observers.impl.NewObserver;
import org.jotapdiez.jslackpkg.core.observers.impl.RemoveObserver;
import org.jotapdiez.jslackpkg.core.observers.impl.UpgradeObserver;
import org.jotapdiez.jslackpkg.ui.components.PackageInformation;
import org.jotapdiez.jslackpkg.ui.components.PackagesList;
import org.jotapdiez.jslackpkg.ui.components.Settings;
import org.jotapdiez.jslackpkg.ui.components.custom.StatusBar;
import org.jotapdiez.jslackpkg.ui.components.custom.splitPane.SplitPane;
import org.jotapdiez.jslackpkg.utils.ResourceMap;

public class MainUI extends JFrame
{
    private final StatusBar sggProgressBar = StatusBar.getInstance();

	private final JToolBar toolBar = new JToolBar();
	private final JButton btnInstalled = new JButton(ResourceMap.getInstance().getString("mainui.toolbar.installed.text"), ResourceMap.installedIcon);
	private final JButton btnUpdate = new JButton(ResourceMap.getInstance().getString("mainui.toolbar.update.text"), ResourceMap.updateIcon);
	private final JButton btnUpgradeall = new JButton(ResourceMap.getInstance().getString("mainui.toolbar.upgrade.text"), ResourceMap.upgradeIcon);
	private final JButton btnInstallnew = new JButton(ResourceMap.getInstance().getString("mainui.toolbar.new.text"), ResourceMap.installIcon);
	private final JButton btnClean = new JButton(ResourceMap.getInstance().getString("mainui.toolbar.clean.text"), ResourceMap.cleanIcon);
	private final JButton btnSettings = new JButton(ResourceMap.getInstance().getString("mainui.toolbar.settings.text"), ResourceMap.settingsIcon);

	private final SplitPane splitPane = new SplitPane(false);
    
	private PackageManagerImpl packageManager = null;
	private PackageInformation packageInformation = null;
	private PackagesList packagesList = null;
	
	private static MainUI instance = null;
	
	
	public static MainUI getInstance()
	{
		if (instance == null)
			instance = new MainUI();
		return instance;
	}
	
	private MainUI() {
		packageManager = new JSlackpkgPackageManager();
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		setTitle(ResourceMap.getInstance().getString("mainui.title"));
		setBounds(100, 100, 1024, 768);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        getContentPane().add(sggProgressBar, BorderLayout.PAGE_END);
        
        {
        	buildMenuBar();
        	setDefaults();
        }
    	{
    		toolBar.setFloatable(false);
    		getContentPane().add(toolBar, BorderLayout.NORTH);
    		toolBar.add(btnInstalled);
    		btnInstalled.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					showInstalledPackages();
				}
			});
    		
    		toolBar.addSeparator();
    		toolBar.add(btnUpdate);
    		btnUpdate.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					doUpdate();
				}
			});
    		
    		toolBar.addSeparator();
    		toolBar.add(btnUpgradeall);
    		btnUpgradeall.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					doUpgrade();
				}
			});
    		toolBar.add(btnInstallnew);
    		btnInstallnew.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					doNew();
				}
			});
    		
//    		toolBar.addSeparator();
    		toolBar.add(btnClean);
    		btnClean.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					doClean();
				}
			});
    		
    		toolBar.addSeparator();
    		toolBar.add(btnSettings);
    		btnSettings.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
				}
			});
    	}
    	
    	getContentPane().add(tabbedPane, BorderLayout.CENTER);
    	tabbedPane.add(ResourceMap.getInstance().getString("mainui.tabs.packages.text"), splitPane);
    	tabbedPane.add(ResourceMap.getInstance().getString("mainui.tabs.settings.text"), new Settings());
    	
    	packageInformation = new PackageInformation(packageManager);
    	packageInformation.setSplitPanel(splitPane);
    	
    	packagesList = new PackagesList(packageManager);
		packageManager.loadInstalledPackages();

		showInstalledPackages();
		
    	splitPane.addTopComponent(packagesList);
    	splitPane.addBottomComponent(packageInformation);
    	
		Runnable loadsStartup = new Runnable() {
			public void run() {
				packageManager.getPackage(""); // Para tener la lista completa de paquetes (instalados, sin instalar, etc)
			}
		};
		Thread t = new Thread(loadsStartup);
		t.start();
		
//    	fillPackagesList(packageManager.getInstalledPackages());
	}
	
	private void buildMenuBar()
	{
        JMenuBar menuBar = new JMenuBar();
        menuBar.setName("menuBar");
        setJMenuBar(menuBar);

        JMenu menuArchivo = new JMenu();
        JMenuItem archivoSalir = new JMenuItem();

        JMenu menuAcciones = new JMenu();
        JMenuItem accionesInstalled = new JMenuItem();
        JMenuItem accionesUpdate = new JMenuItem();
        JMenuItem accionesUpgrade = new JMenuItem();
        JMenuItem accionesNew = new JMenuItem();
        JMenuItem accionesClean = new JMenuItem();
        
        JMenu menuOpciones = new JMenu();
        JMenuItem opcionesConfig = new JMenuItem();
        
        JMenu menuAyuda = new JMenu();
        JMenuItem ayudaAyuda = new JMenuItem();
        JMenuItem ayudaAcercaDe = new JMenuItem();

        { // Menu Archivo
        	menuArchivo.setText(ResourceMap.getInstance().getString("mainui.menu.archivo.text"));
        	menuArchivo.setName("menuArchivo");
        	menuArchivo.setMnemonic('A');
	        menuBar.add(menuArchivo);
	        
        	archivoSalir.setText( ResourceMap.getInstance().getString("mainui.menu.archivo.salir.text") );
        	archivoSalir.setName("menuSalir");
        	archivoSalir.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
        		}
			});
        	menuArchivo.add(archivoSalir);
        }
        
        { // Menu Acciones
	        menuAcciones.setText(ResourceMap.getInstance().getString("mainui.menu.actions.text"));
	        menuAcciones.setName("menuAcciones");
	        menuAcciones.setMnemonic('C');
	        menuBar.add(menuAcciones);

	        accionesInstalled.setText( ResourceMap.getInstance().getString("mainui.menu.actions.installed.text") );
	        accionesInstalled.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
        			showInstalledPackages();
        		}
			});
        	menuAcciones.add(accionesInstalled);
        	menuAcciones.add(new JSeparator());

        	accionesUpdate.setText( ResourceMap.getInstance().getString("mainui.menu.actions.update.text") );
        	accionesUpdate.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
        			doUpdate();
        		}
			});
        	menuAcciones.add(accionesUpdate);
        	menuAcciones.add(new JSeparator());
        	
        	accionesUpgrade.setText( ResourceMap.getInstance().getString("mainui.menu.actions.upgrade.text") );
        	accionesUpgrade.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
        			doUpgrade();
        		}
			});
        	menuAcciones.add(accionesUpgrade);
        	accionesNew.setText( ResourceMap.getInstance().getString("mainui.menu.actions.new.text") );
        	accionesNew.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
        			doNew();
        		}
			});
        	menuAcciones.add(accionesNew);
        	menuAcciones.add(new JSeparator());
        	
        	accionesClean.setText( ResourceMap.getInstance().getString("mainui.menu.actions.clean.text") );
        	accionesClean.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
        			doClean();
        		}
			});
        	menuAcciones.add(accionesClean);
        }
        
        { // Menu Opciones
        	menuOpciones.setText(ResourceMap.getInstance().getString("mainui.menu.options.text"));
        	menuOpciones.setName("menuOpciones");
        	menuOpciones.setMnemonic('O');
	        menuBar.add(menuOpciones);
	        
	        menuOpciones.add(new JSeparator());
	        opcionesConfig.setText(ResourceMap.getInstance().getString("mainui.menu.options.settings.text"));
	        opcionesConfig.setName("opcionesConfig");
        	menuOpciones.add(opcionesConfig);
        }
        
        { // Menu Ayuda
        	menuAyuda.setText(ResourceMap.getInstance().getString("mainui.menu.help.text"));
        	menuAyuda.setName("menuAyudaSimbol");
        	menuAyuda.setMnemonic('H');
	        menuBar.add(menuAyuda);
	        
	        ayudaAyuda.setText(ResourceMap.getInstance().getString("mainui.menu.help.help.text"));
	        ayudaAyuda.setName("ayudaAyuda");
        	menuAyuda.add(ayudaAyuda);
        	menuAyuda.add(new JSeparator());
        	
        	ayudaAcercaDe.setText(ResourceMap.getInstance().getString("mainui.menu.help.about.text"));
        	ayudaAcercaDe.setName("ayudaAcercaDe");
        	menuAyuda.add(ayudaAcercaDe);
        }
	}
	
	protected void showInstalledPackages() {
		packageManager.addObserver(new InstalledObserver(packagesList));
		packagesList.enableOnlyBulkRemove();
		fillPackagesList(packageManager.getInstalledPackages());
	}

	protected void doClean() {
		packageManager.addObserver(new RemoveObserver(packagesList));
		packagesList.enableOnlyBulkRemove();
		fillPackagesList(packageManager.getInstalledPackages());
	}

	protected void doNew() {
		packageManager.addObserver(new NewObserver(packagesList));
		packagesList.enableOnlyBulkInstall();
		fillPackagesList(packageManager.getNewPackages());
	}

	protected void doUpgrade() {
		packageManager.addObserver(new UpgradeObserver(packagesList));
		packagesList.enableOnlyBulkUpgrade();
		fillPackagesList(packageManager.getUpgradedPackages());
	}

	private void doUpdate()
	{
		Runnable runn = new Runnable() {
			public void run() {
				packageManager.update();
			}
		};
		Thread t = new Thread(runn);
		t.start();
	}
	
	private void fillPackagesList(List<Package> packages)
	{
		packagesList.addPackages( packages );
	}
	
	private void setDefaults() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
        		setExtendedState(JFrame.MAXIMIZED_BOTH);
            }
        });
    }

	private static final long serialVersionUID = 5009947455770939715L;
	private final JTabbedPane tabbedPane = new JTabbedPane();

	public void showPackageInformation(Package packageItem) {
		packageInformation.setPackage(packageItem);
		splitPane.showPanel();
	}
}
