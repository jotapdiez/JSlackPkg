package org.jotapdiez.jslackpkg.utils;

import java.text.DecimalFormat;

public class Conversions {

	public static String parseSize(double sizeToParse)
	{
		char sizeType = 'K';
		double size = sizeToParse;
		if (size > 1024)
		{
			size = size / 1024;
			sizeType = 'M';
			if (size > 1024)
			{
				size = size / 1024;
				sizeType = 'G';
			}
		}
		DecimalFormat twoDForm = new DecimalFormat("#.##");
		return String.valueOf(twoDForm.format(size)) + String.valueOf(sizeType);
	}
	
}
