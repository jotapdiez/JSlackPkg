package org.sgg.tests;

import java.util.LinkedList;
import java.util.List;

import javax.swing.table.TableModel;

import org.sgg.utils.XOMWrapper;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.NodeFactory;

public class ReportJRXMLGenerator
{
	String NAMESPACE = "http://jasperreports.sourceforge.net/jasperreports";
	
	private TableModel _model = null;
	
	private List<Node> _fields = new LinkedList<Node>();
	private List<Node> _columns = new LinkedList<Node>();
	private List<Node> _details = new LinkedList<Node>();
	
	public ReportJRXMLGenerator(TableModel model)
	{
		_model = model;
		preparseModel();
	}
	
	private void preparseModel()
	{
		int x = 0;
		
		for (int columnIndex=0 ; columnIndex<_model.getColumnCount() ; ++columnIndex)
		{
			String name = _model.getColumnName(columnIndex);
//			String classNameSimple = _model.getColumnClass(columnIndex).getSimpleName();
			String className = _model.getColumnClass(columnIndex).getName();
			
			int columnWidth = 0;
			
			for (int rowIndex = 0 ; rowIndex<_model.getRowCount() ; ++rowIndex)
			{
				String value = _model.getValueAt(rowIndex, columnIndex).toString();
				if (value.length()>columnWidth)
					columnWidth = value.length(); 
			}
//			columnWidth = columnWidth;
			
			Element field = new Element("field", NAMESPACE);
			field.addAttribute(new Attribute("name", name));
			field.addAttribute(new Attribute("class", className));
			_fields.add(field);

			{
				Element staticText = new Element("staticText", NAMESPACE);
				Element reportElement = new Element("reportElement", NAMESPACE);
				reportElement.addAttribute(new Attribute("style", "Sans_Italic"));
				reportElement.addAttribute(new Attribute("mode", "Opaque"));
				reportElement.addAttribute(new Attribute("x", String.valueOf(x)));
				reportElement.addAttribute(new Attribute("y", "0"));
				reportElement.addAttribute(new Attribute("width", String.valueOf(columnWidth)));
				reportElement.addAttribute(new Attribute("height", "15"));
				reportElement.addAttribute(new Attribute("forecolor", "#FFFFFF"));
				reportElement.addAttribute(new Attribute("backcolor", "#333333"));
				staticText.appendChild(reportElement);
				
				Element textElement = new Element("textElement", NAMESPACE);
				staticText.appendChild(textElement);
				
				Element text = new Element("text", NAMESPACE);
//				text.appendChild("<![CDATA["+name+"]]>");
				text.appendChild(name);
				staticText.appendChild(text);
				
				_columns.add(staticText);
			}
			
			{
				Element textField = new Element("textField", NAMESPACE);
				textField.addAttribute(new Attribute("isStretchWithOverflow", "true"));
				textField.addAttribute(new Attribute("isBlankWhenNull", "true"));
				
				Element reportElement = new Element("reportElement", NAMESPACE);
				reportElement.addAttribute(new Attribute("style", "Sans_Normal"));
				reportElement.addAttribute(new Attribute("positionType", "FixRelativeToTop"));
				reportElement.addAttribute(new Attribute("x", String.valueOf(x)));
				reportElement.addAttribute(new Attribute("y", "0"));
				reportElement.addAttribute(new Attribute("width", String.valueOf(columnWidth)));
				reportElement.addAttribute(new Attribute("height", "15"));
				reportElement.addAttribute(new Attribute("stretchType", "RelativeToTallestObject"));
				reportElement.addAttribute(new Attribute("isPrintWhenDetailOverflows", "true"));
				textField.appendChild(reportElement);
				
				Element textElement = new Element("textElement", NAMESPACE);
				textField.appendChild(textElement);
				
				Element textFieldExpression = new Element("textFieldExpression", NAMESPACE);
				textFieldExpression.addAttribute(new Attribute("class", className));
//				textFieldExpression.appendChild("<![CDATA[$F{"+name+"}]]>");
				textFieldExpression.appendChild("$F{"+name+"}");
				textField.appendChild(textFieldExpression);
				
				_details.add(textField);
			}

			x += (columnWidth+2);
		}
		
//		System.out.println("=========== FIELDS =================\n"+
//							fields + 
//						   "====================================");
//		System.out.println("=========== COLUMNHEADER =================\n"+
//				columns + 
//			   "====================================");
//		System.out.println("=========== DETAILS =================\n"+
//				details + 
//			   "====================================");
	}

	public List<Attribute> getRootAttributes()
	{
		List<Attribute> result = new LinkedList<Attribute>();
		
		//Name
		result.add(new Attribute("name", "faltante"));
		
		//Size
		result.add(new Attribute("pageWidth", "595"));
		result.add(new Attribute("pageHeight", "842"));
		result.add(new Attribute("columnWidth", "515"));

		//Margins
		result.add(new Attribute("leftMargin", "40"));
		result.add(new Attribute("rightMargin", "40"));
		result.add(new Attribute("topMargin", "50"));
		result.add(new Attribute("bottomMargin", "50"));
		
		return result;
	}
	
	public Element generate()
	{
		
		return null;
	}

	public List<Node> generateFields()
	{
//		return XOMWrapper.fromString(fields.toString(), "fields").getRootElement();
		return _fields;
	}
	
	public List<Node> generateDetails()
	{
		return _details;
	}
	
	public List<Node> generateColumns()
	{
		return _columns;
	}
}
