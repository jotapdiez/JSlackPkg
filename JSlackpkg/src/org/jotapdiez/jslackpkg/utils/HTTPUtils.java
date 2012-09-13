package org.jotapdiez.jslackpkg.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;
import org.jotapdiez.jslackpkg.core.settings.SettingsManager;
import org.jotapdiez.jslackpkg.ui.components.custom.StatusBar;

public class HTTPUtils
{
	private static Logger logger = Logger.getLogger(HTTPUtils.class.getCanonicalName());
	
	/**
	 * Descarga el contenido de una URL (archivo?)
	 * @param file {@link String} con el nombre del archivo remoto
	 * @return {@link String} con el contenido del archivo remoto
	 */
	public static String getFileContent(String url, String file) {
		try {
			StatusBar.getInstance().setFocusComponentText(ResourceMap.getInstance().getString("statusbar.info.downloading.text").replaceFirst("%FILE%", file));

			URL mirrorContext = new URL(url);
			URL mirrorFile = new URL(mirrorContext, file);
	        URLConnection mirror = mirrorFile.openConnection();
	        
	        StatusBar.getInstance().setTotal(mirror.getContentLength());
	        
	        BufferedReader in = new BufferedReader(new InputStreamReader(mirror.getInputStream()));
	        
	        StringBuffer result = new StringBuffer();
	        String inputLine;
	        int total = 0;
	        while ((inputLine = in.readLine()) != null) 
	        {
	        	result.append(inputLine+"\n");
	        	total += inputLine.length();
	        	StatusBar.getInstance().increaseProgress(total);
	        }
	        in.close();
	        StatusBar.getInstance().resetProgress();
	        StatusBar.getInstance().resetText();
	        return result.toString();
		} catch (MalformedURLException ex) {
			logger.error("downloadFile", ex);
		} catch (IOException ex) {
			logger.error("downloadFile", ex);
		}
		return "";
	}
	
	/**
	 * Descarga un archivo de la URL en la configuracion
	 * @param sourceUrl URL de donde descargar el archivo
	 * @param fileName Nombre completo del archivo a descargar
	 * @return Path absoluto donde se descargo el paquete
	 */
	public static String downloadFile(String sourceUrl, String fileName)
	{
		try {
			StatusBar.getInstance().setFocusComponentText(ResourceMap.getInstance().getString("statusbar.info.downloading.text").replaceFirst("%FILE%", fileName));
//			String state = "Descargando: ";
//			infoPanel.setState(state);
//			infoPanel.updateProgress(0);
//			logger.debug("downloadFile:"+remoteFile+" to "+localFile);

			URL url = new URL(sourceUrl); //
			URLConnection conexion = url.openConnection();
			conexion.connect();

			StatusBar.getInstance().setTotal(conexion.getContentLength());
			
//			int lenghtOfFile = conexion.getContentLength();
//			infoPanel.setTotalPB(lenghtOfFile);
			InputStream input = new BufferedInputStream(url.openStream());
			
			File local = new File(SettingsManager.getInstance().getWorkingDir(), fileName);
			if (!local.exists())
				local.createNewFile();
			
			OutputStream output = new FileOutputStream(local);

			byte data[] = new byte[8192];

			long total = 0;
			
			int count;
			while ((count = input.read(data)) != -1) {
				total += count;
				StatusBar.getInstance().increaseProgress((int) total);
//				infoPanel.updateProgress((int) total);
//				logger.debug(total+"/"+lenghtOfFile);
				output.write(data, 0, count);
			}

			StatusBar.getInstance().resetProgress();
			StatusBar.getInstance().resetText();
			output.flush();
			output.close();
			input.close();
			
			return local.getAbsolutePath();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			logger.error("MalformedURLException", e);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("IOException", e);
		}
		return null;
	}
}

