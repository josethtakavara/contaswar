package com.joseth.contas.server;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

@WebServlet(urlPatterns="/restrito/carregarArquivo")
public class CarregarArquivoServlet extends HttpServlet
{  
    protected void doPost( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException
    {
        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(100*1024*1024);
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setSizeMax( 100*1024*1024 ); 
        
        FileItem fu;
        String hd;
        
        try
        {
        	Map<String,List<FileItem>> fim = upload.parseParameterMap(req);
        	hd = fim.get("hd").get(0).getString();
        	fu = fim.get("arquivoFileUpload").get(0);
        }
        catch( FileUploadException fue )
        {
        	throw new ServletException(fue);
        }
        
    	req.getSession(true).setAttribute(hd,fu.getString()); 

    }

} 