package org.sgg.tests;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.Serializer;

import org.sgg.core.db.Adapter;
import org.sgg.core.hibernate.Cliente;
import org.sgg.ui.custom.extendedTable.SGGExtendedTable;
import org.sgg.utils.Defines;
import org.sgg.utils.XOMWrapper;

import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.FastReportBuilder;
import ar.com.fdvs.dj.domain.constants.Border;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;

public class JasperReportTest extends JPanel
{
	public void setData(SGGExtendedTable table)
	{
		{
			Cliente cli = new Cliente();
			cli.setNombre("j");
			LinkedList<Cliente> list = new LinkedList<Cliente>();
			Adapter.getInstance().findElement(cli, list);
			table.setData(list);
		}

		// {
		// Empleado cli = new Empleado();
		// cli.setNombre("");
		// LinkedList<Empleado> list = new LinkedList<Empleado>();
		// Adapter.getInstance().findElement(cli, list);
		// table.setData(list);
		// }

		// {
		// Asistencia checkin = new Asistencia();
		// checkin.setCliente(new Cliente(2l));
		// LinkedList<Asistencia> list = new LinkedList<Asistencia>();
		// Adapter.getInstance().findElement(checkin, list);
		// table.setData(list);
		// }

		// {
		// Movimiento mov = new Movimiento();
		// mov.setPersonaTipo("Cliente");
		// LinkedList<Movimiento> list = new LinkedList<Movimiento>();
		// Adapter.getInstance().findElement(mov, list);
		// table.setData(list);
		// }
	}
	
	public JasperDesign generateReport(TableModel model)
	{
		File bigReport = null;
		try
		{
			bigReport = File.createTempFile("__SGG_tmpReport_", ".jrxml");
			bigReport.deleteOnExit();
		} catch (IOException e1)
		{
			e1.printStackTrace();
		}
		
		nu.xom.Document jasperReportXML = XOMWrapper.createDocument("jasperReport", "http://jasperreports.sourceforge.net/jasperreports");
		Element styleSansNormal = new Element("style", "http://jasperreports.sourceforge.net/jasperreports");
		styleSansNormal.addAttribute(new Attribute("name", "Sans_Normal"));
		styleSansNormal.addAttribute(new Attribute("isDefault", "true"));
		styleSansNormal.addAttribute(new Attribute("fontName", "DejaVu Sans"));
		styleSansNormal.addAttribute(new Attribute("fontSize", "12"));
		styleSansNormal.addAttribute(new Attribute("isBold", "false"));
		styleSansNormal.addAttribute(new Attribute("isItalic", "false"));
		styleSansNormal.addAttribute(new Attribute("isUnderline", "false"));
		styleSansNormal.addAttribute(new Attribute("isStrikeThrough", "false"));
		jasperReportXML.getRootElement().insertChild(styleSansNormal, 0);
		
		Element styleSansBold = new Element("style", "http://jasperreports.sourceforge.net/jasperreports");
		styleSansBold.addAttribute(new Attribute("name", "Sans_Bold"));
		styleSansBold.addAttribute(new Attribute("isDefault", "false"));
		styleSansBold.addAttribute(new Attribute("fontName", "DejaVu Sans"));
		styleSansBold.addAttribute(new Attribute("fontSize", "12"));
		styleSansBold.addAttribute(new Attribute("isBold", "true"));
		styleSansBold.addAttribute(new Attribute("isItalic", "false"));
		styleSansBold.addAttribute(new Attribute("isUnderline", "false"));
		styleSansBold.addAttribute(new Attribute("isStrikeThrough", "false"));
		jasperReportXML.getRootElement().insertChild(styleSansBold, 1);
		
		Element styleSansItalic = new Element("style", "http://jasperreports.sourceforge.net/jasperreports");
		styleSansItalic.addAttribute(new Attribute("name", "Sans_Italic"));
		styleSansItalic.addAttribute(new Attribute("isDefault", "false"));
		styleSansItalic.addAttribute(new Attribute("fontName", "DejaVu Sans"));
		styleSansItalic.addAttribute(new Attribute("fontSize", "12"));
		styleSansItalic.addAttribute(new Attribute("isBold", "false"));
		styleSansItalic.addAttribute(new Attribute("isItalic", "true"));
		styleSansItalic.addAttribute(new Attribute("isUnderline", "false"));
		styleSansItalic.addAttribute(new Attribute("isStrikeThrough", "false"));
		jasperReportXML.getRootElement().insertChild(styleSansItalic, 2);
		
//		<style name="Sans_Normal" isDefault="true" fontName="DejaVu Sans" ="12" isBold="false" isItalic="false" isUnderline="false" isStrikeThrough="false"/>
//		<style name="Sans_Bold" isDefault="false" fontName="DejaVu Sans" fontSize="12" isBold="true" isItalic="false" isUnderline="false" isStrikeThrough="false"/>
//		<style name="Sans_Italic" isDefault="false" fontName="DejaVu Sans" fontSize="12" isBold="false" isItalic="true" isUnderline="false" isStrikeThrough="false"/>
		try
		{
			Element headerXML = XOMWrapper.fromFile(getClass().getResource("/resources/reports/header.xml").getPath()).getRootElement();
			Element footerXML = XOMWrapper.fromFile(getClass().getResource("/resources/reports/footer.xml").getPath()).getRootElement();

			Element jasperReportXMLRoot = jasperReportXML.getRootElement();
			
			for (int i=0 ; i < headerXML.getChildElements().size() ; ++i)
			{
				Node item = headerXML.getChildElements().get(i);
				jasperReportXMLRoot.appendChild(item.copy());
			}

			for (int i=0 ; i < footerXML.getChildElements().size() ; ++i)
			{
				Node item = footerXML.getChildElements().get(i);
				jasperReportXMLRoot.appendChild(item.copy());
			}
			
			ReportJRXMLGenerator generator = new ReportJRXMLGenerator(model);

			{
				List<Node> fields = generator.generateFields();
				List<Element> SGG_fields = XOMWrapper.getChildElementsWithTagName(jasperReportXMLRoot, "SGG_fields");
				Element SGG_fieldElement = SGG_fields.get(0);
				
				Iterator<Node> it = fields.iterator();
				while (it.hasNext())
					XOMWrapper.insertBefore(SGG_fieldElement, it.next().copy());
				
				jasperReportXMLRoot.removeChild(SGG_fieldElement);
			}
			
//			{
//			Element sgg_parameters = generator.generateFields();
//			List<Element> SGG_fields = XOMWrapper.getChildElementsWithTagName(jasperReportXMLRoot, "SGG_fields");
//			Element SGG_fieldElement = SGG_fields.get(0);
//			for (int i=0 ; i < sgg_parameters.getChildElements().size() ; ++i)
//			{
//				Node item = sgg_parameters.getChildElements().get(i);
//				XOMWrapper.insertBefore(SGG_fieldElement, item.copy());
//			}
//			}
			
			{
				List<Element> SGG_details = XOMWrapper.getChildElementsWithTagName(jasperReportXMLRoot, "SGG_details");
				Element SGG_detailElement = SGG_details.get(0);

				Element detail = new Element("detail", "http://jasperreports.sourceforge.net/jasperreports");
				Element detailBand = new Element("band", "http://jasperreports.sourceforge.net/jasperreports");
				detailBand.addAttribute(new Attribute("height", "20"));
				
				List<Node> details = generator.generateDetails();
				Iterator<Node> it = details.iterator();
				while (it.hasNext())
					detailBand.appendChild(it.next().copy());
				
				detail.appendChild(detailBand);
				
				jasperReportXMLRoot.replaceChild(SGG_detailElement, detail);
			}
			{
				List<Element> SGG_columnHeader = XOMWrapper.getChildElementsWithTagName(jasperReportXMLRoot, "SGG_columns");
				Element SGG_columnHeaderElement = SGG_columnHeader.get(0);

				Element columnHeader = new Element("columnHeader", "http://jasperreports.sourceforge.net/jasperreports");
				Element columnsBand = new Element("band", "http://jasperreports.sourceforge.net/jasperreports");
				columnsBand.addAttribute(new Attribute("height", "20"));
				
				List<Node> details = generator.generateColumns();
				Iterator<Node> it = details.iterator();
				while (it.hasNext())
				{
					Node col = it.next();
					columnsBand.appendChild(col.copy());
				}
				columnHeader.appendChild(columnsBand);
				jasperReportXMLRoot.replaceChild(SGG_columnHeaderElement, columnHeader);
			}

			Iterator<Attribute> it = generator.getRootAttributes().iterator();
			while (it.hasNext())
				jasperReportXMLRoot.addAttribute(it.next());
			
			jasperReportXMLRoot.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
//			jasperReportXMLRoot.setNamespacePrefix("xsi");
//			jasperReportXMLRoot.addNamespaceDeclaration("schemaLocation","http://jasperreports.sourceforge.net/jasperreports"/*+"http://jasperreports.sourceforge.net/xsd/jasperreport.xsd"*/);
			
			try {
		        Serializer serializer = new Serializer(System.out, "UTF-8");
		        serializer.setIndent(4);
		        serializer.setMaxLength(150);
		        serializer.write(jasperReportXML);  
		      }
		      catch (IOException ex) {
		         System.err.println(ex); 
		      }
//			System.out.println("Col: " + jasperReportXML.toXML());
			XOMWrapper.toFile(jasperReportXML, bigReport);
			System.out.println(bigReport.getAbsolutePath());
			JasperDesign jasperDesign = JRXmlLoader.load(bigReport);
			
			return jasperDesign;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public JasperReportTest()
	{
		setLayout(new GridLayout());
		SGGExtendedTable table = new SGGExtendedTable();
		
		setData(table);

		add(table);
		
		long start = System.currentTimeMillis();
		String dateString = new SimpleDateFormat(Defines.DATE_FORMAT).format(new Date());
		
		// Preparing parameters
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("ReportTitle", dateString + " - Reporte de Empleado");
		parameters.put("DataFile", "Cliente");

		String p1Name = "net.sf.jasperreports.extension.registry.factory.simple.font.families";
		String p1Value = "net.sf.jasperreports.engine.fonts.SimpleFontExtensionsRegistryFactory";
		String p2Name = "net.sf.jasperreports.extension.simple.font.families.dejavu";
		String p2Value = getClass().getResource("/resources/fonts/fonts.xml").getPath();
		
		TableModel model = table.getModel();
		JRTableModelDataSource dataSource = new JRTableModelDataSource(model);
		
		JasperPrint full = null;
		JasperDesign jasperDesign = null;
		try
		{ // FULL
			jasperDesign = generateReport(model);
//			jasperDesign = JRXmlLoader.load(getClass().getResource("/resources/reports/__SGG_tmpReport.jrxml").getPath());
//			jasperDesign = JRXmlLoader.load(getClass().getResource("/resources/reports/_tmpReport.xml").getPath());
//			jasperDesign = JRXmlLoader.load(getClass().getResource("/resources/OriginalReportes.xml").getPath());
			
			JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
			jasperReport.setProperty(p1Name, p1Value);
			jasperReport.setProperty(p2Name, p2Value);
			
			full = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
//		JasperExportManager.exportReportToPdfFile(jasperPrint, "sales-report.pdf");
//		JasperViewer.viewReport(new ReportJRXMLGeneratorDynamicJasper(model).getPrint(), true);
		JasperViewer.viewReport(full, true);
		System.err.println("Filling time : " + (System.currentTimeMillis() - start));
	}

	private static void createAndShowGUI()
	{
		JFrame frame = new JFrame("JasperReportTest");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(1000, 200));
		frame.add(new JasperReportTest());

		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args)
	{
		javax.swing.SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				createAndShowGUI();
			}
		});
	}

	private static final long	serialVersionUID	= 4002690193799269777L;
}
