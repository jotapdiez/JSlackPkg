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
import javax.swing.SwingUtilities;

import org.jotapdiez.jslackpkg.core.entities.Package;
import org.jotapdiez.jslackpkg.core.impl.JSlackpkgPackageManager;
import org.jotapdiez.jslackpkg.core.interfaces.PackageManager;
import org.jotapdiez.jslackpkg.ui.components.PackageInformation;
import org.jotapdiez.jslackpkg.ui.components.PackagesList;
import org.jotapdiez.jslackpkg.ui.components.Settings;
import org.jotapdiez.jslackpkg.ui.components.custom.StatusBar;
import org.jotapdiez.jslackpkg.utils.ResourceMap;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.JTabbedPane;

public class MainUI extends JFrame
{
    private final StatusBar sggProgressBar = StatusBar.getInstance();

	private final JToolBar toolBar = new JToolBar();
	private final JButton btnInstalled = new JButton("Installed", ResourceMap.installedIcon); //TODO: A archivo de lenguajes
	private final JButton btnUpdate = new JButton("Update", ResourceMap.updateIcon); //TODO: A archivo de lenguajes
	private final JButton btnClean = new JButton("Clean", ResourceMap.cleanIcon); //TODO: A archivo de lenguajes
	private final JButton btnUpgradeall = new JButton("Upgrade", ResourceMap.upgradeIcon); //TODO: A archivo de lenguajes
	private final JButton btnInstallnew = new JButton("Install New", ResourceMap.installIcon); //TODO: A archivo de lenguajes
	private final JButton btnSettings = new JButton("Settings", ResourceMap.settingsIcon); //TODO: A archivo de lenguajes

	private final JSplitPane splitPane = new JSplitPane();
    
	private PackageManager packageManager = null;
	
	public MainUI() {
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
    		
    		toolBar.addSeparator();
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
    	tabbedPane.add("Packages", splitPane); //TODO: A archivo de lenguajes
    	tabbedPane.add("Settings", new Settings()); //TODO: A archivo de lenguajes
    	splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
    	
    	splitPane.setTopComponent(new PackagesList(packageManager));
    	splitPane.setBottomComponent(PackageInformation.getInstance(packageManager));
    	
		Runnable loadsStartup = new Runnable() {
			public void run() {
				packageManager.getPackage("");
			}
		};
		Thread t = new Thread(loadsStartup);
		t.start();
    	fillPackagesList(packageManager.getInstalledPackages());
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
	        
        	archivoSalir.setText( ResourceMap.getInstance().getString("mainui.menu.salir.text") );
        	archivoSalir.setName("menuSalir");
        	archivoSalir.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
        		}
			});
        	menuArchivo.add(archivoSalir);
        }
        
        { // Menu Acciones
	        menuAcciones.setText("Acciones"); //TODO: A archivo de lenguajes
	        menuAcciones.setName("menuAcciones");
	        menuAcciones.setMnemonic('C');
	        menuBar.add(menuAcciones);

	        accionesInstalled.setText("Installed"); //TODO: A archivo de lenguajes
	        accionesInstalled.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
        			showInstalledPackages();
        		}
			});
        	menuAcciones.add(accionesInstalled);
        	menuAcciones.add(new JSeparator());

        	accionesUpdate.setText("Update"); //TODO: A archivo de lenguajes
        	accionesUpdate.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
        			doUpdate();
        		}
			});
        	menuAcciones.add(accionesUpdate);
        	menuAcciones.add(new JSeparator());
        	
        	accionesUpgrade.setText("Upgrade"); //TODO: A archivo de lenguajes
        	accionesUpgrade.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
        			doUpgrade();
        		}
			});
        	menuAcciones.add(accionesUpgrade);
        	accionesNew.setText("Install"); //TODO: A archivo de lenguajes
        	accionesNew.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
        			doNew();
        		}
			});
        	menuAcciones.add(accionesNew);
        	menuAcciones.add(new JSeparator());
        	
        	accionesClean.setText("Clean"); //TODO: A archivo de lenguajes
        	accionesClean.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
        			doClean();
        		}
			});
        	menuAcciones.add(accionesClean);
        }
        
        { // Menu Opciones
        	menuOpciones.setText("Opciones"); //TODO: A archivo de lenguajes?
        	menuOpciones.setName("menuOpciones");
        	menuOpciones.setMnemonic('O');
	        menuBar.add(menuOpciones);
	        
	        menuOpciones.add(new JSeparator());
	        opcionesConfig.setText( "Configuracion" ); //TODO: A archivo de lenguajes
	        opcionesConfig.setName("opcionesConfig");
        	menuOpciones.add(opcionesConfig);
        }
        
        { // Menu Ayuda
        	menuAyuda.setText("?"); //TODO: A archivo de lenguajes?
        	menuAyuda.setName("menuAyudaSimbol");
        	menuAyuda.setMnemonic('H');
	        menuBar.add(menuAyuda);
	        
	        ayudaAyuda.setText( "Ayuda" ); //TODO: A archivo de lenguajes
	        ayudaAyuda.setName("ayudaAyuda");
        	menuAyuda.add(ayudaAyuda);
        	menuAyuda.add(new JSeparator());
        	
        	ayudaAcercaDe.setText( "Acerca De..." ); //TODO: A archivo de lenguajes
        	ayudaAcercaDe.setName("ayudaAcercaDe");
        	menuAyuda.add(ayudaAcercaDe);
        }
	}
	
	protected void showInstalledPackages() {
		fillPackagesList(packageManager.getInstalledPackages());
	}

	protected void doClean() {
		fillPackagesList(packageManager.getRemovedPackages());
	}

	protected void doNew() {
		fillPackagesList(packageManager.getNewPackages());
	}

	protected void doUpgrade() {
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
		PackagesList packagesList = (PackagesList) splitPane.getTopComponent();
		packagesList.addPackages( packages );
	}
	
	private void setDefaults() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
        		setExtendedState(JFrame.MAXIMIZED_BOTH);
            	splitPane.setOneTouchExpandable(true);
//            	splitPane.getBottomComponent().setMinimumSize(new Dimension());
            	splitPane.setDividerLocation(1.0d);
            }
        });
    }

	private static final long serialVersionUID = 5009947455770939715L;
	private final JTabbedPane tabbedPane = new JTabbedPane();
}
