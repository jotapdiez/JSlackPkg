package org.sgg.tests;

import javax.swing.table.TableModel;

import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.builders.StyleBuilder;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;

public class ReportJRXMLGeneratorDynamicJasper
{
	String NAMESPACE = "http://jasperreports.sourceforge.net/jasperreports";
	
	private TableModel _model = null;
	
	DynamicReportBuilder drb = new DynamicReportBuilder();
	
	public ReportJRXMLGeneratorDynamicJasper(TableModel model)
	{
		_model = model;

		drb.setTitle("November 2006 sales report")		//defines the title of the report
        .setSubtitle("The items in this report correspond "
                        +"to the main products: DVDs, Books, Foods and Magazines")
        .setDetailHeight(15)		//defines the height for each record of the report
        .setMargins(30, 20, 30, 15)		//define the margin space for each side (top, bottom, left and right)
//        .setDefaultStyles(titleStyle, subtitleStyle, headerStyle, detailStyle)
        .setColumnsPerPage(1)		//defines columns per page (like in the telephone guide)
		.setUseFullPageWidth(true);  //we tell the report to use the full width of the page. this rezises
        //the columns width proportionally to meat the page width.
		
		preparseModel();
	}
	
	private void preparseModel()
	{
//		AbstractColumn lastColumn = null;
		
		Font font = new Font(12, "DejaVu Sans", "/resources/fonts/dejavu/DejaVuSans.ttf",
				Font.PDF_ENCODING_Identity_H_Unicode_with_horizontal_writing, true);
		
		Style style = new StyleBuilder(false).setFont(font).build();
		
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

			ColumnBuilder column = ColumnBuilder.getNew()		//creates a new instance of a ColumnBuilder
			        .setColumnProperty(name, className)		//defines the field of the data source that this column will show, also its type
			        .setTitle(name)		//the title for the column
			        .setStyle(style)
			        .setWidth(columnWidth);		//the width of the column
			
//			if (lastColumn != null)
//				column.setPercentageColumn((PropertyColumn) lastColumn);
			
    		//builds and return a new AbstractColumn
			AbstractColumn cc = column.build(); 
			drb.addColumn(cc);
//			lastColumn = cc;
		}
	}

	public JasperPrint getPrint()
	{
		DynamicReport dr = drb.build(); //Finally build the report!

		JRDataSource ds = new JRTableModelDataSource(_model);    //Create a JRDataSource, the Collection used
		                                                                                                //here contains dummy hardcoded objects...

		JasperPrint jp = null;
		try
		{
			//Creates the JasperPrint object, we pass as a Parameter
			jp = DynamicJasperHelper.generateJasperPrint(dr, new ClassicLayoutManager(), ds);
		} catch (JRException e)
		{
			e.printStackTrace();
		}
		return jp;
	}
}
