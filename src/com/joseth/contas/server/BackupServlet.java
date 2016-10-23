package com.joseth.contas.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.joseth.contas.beans.Classificacao;
import com.joseth.contas.beans.Conta;
import com.joseth.contas.beans.Movimento;
import com.joseth.contas.beans.Usuario;
import com.joseth.contas.daos.ClassificacaoDAO;
import com.joseth.contas.daos.ContaDAO;
import com.joseth.contas.daos.MovimentoDAO;
import com.joseth.contas.daos.UsuarioDAO;

@WebServlet(urlPatterns="/restrito/backup")
public class BackupServlet extends HttpServlet
{
//	@EJB(beanName="dg")
//	DAOGenerico dg;
	
//	@EJB(beanName="mvd")
	MovimentoDAO mvd = new MovimentoDAO();
	
//	@EJB(beanName="ud")
	UsuarioDAO ud = new UsuarioDAO();
	
//	@EJB(beanName="cd")
	ContaDAO cd = new ContaDAO();
	
//	@EJB(beanName="cld")
	ClassificacaoDAO cld = new ClassificacaoDAO();
	
    protected void doGet( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException
    {
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	ZipOutputStream zos = new ZipOutputStream(baos);
    	
    	List<Classificacao> lc = cld.pesquisarTodos();
    	List<Conta> lco = cd.pesquisarTodos();
    	List<Usuario> lu = ud.pesquisarTodos();
    	List<Movimento> lm = mvd.pesquisarTodos();
    	
    	try
    	{
	    	for( Classificacao c: lc)
	    	{
	    		String bf = cld.toXml(c);
	    		ZipEntry ze = new ZipEntry("classificacao"+c.getId()+".xml");
	    		zos.putNextEntry(ze);
	    		zos.write(bf.getBytes());
	    	}
	    	for( Usuario u: lu)
	    	{
	    		String bf = ud.toXml(u);
	    		ZipEntry ze = new ZipEntry("usuario"+u.getId()+".xml");
	    		zos.putNextEntry(ze);
	    		zos.write(bf.getBytes());
	    	}
	    	for( Conta c: lco)
	    	{
	    		String bf = cd.toXml(c);
	    		ZipEntry ze = new ZipEntry("conta"+c.getId()+".xml");
	    		zos.putNextEntry(ze);
	    		zos.write(bf.getBytes());
	    	}	    	
	    	for( Movimento m: lm)
	    	{
	    		//JAXB.marshal(m, System.out);
	    		String bf = mvd.toXml(m);
	    		ZipEntry ze = new ZipEntry("movimento"+m.getId()+".xml");
	    		zos.putNextEntry(ze);
	    		zos.write(bf.getBytes());
	    	}
	    	zos.close();
    	}
    	catch( IOException e )
    	{
    		throw new RuntimeException(e);
    	}
		resp.setContentType("application/zip");
        resp.addHeader("Content-disposition", "attachment; filename=backup.zip");
		try 
		{
			ServletOutputStream os = resp.getOutputStream();
			os.write(baos.toByteArray());
			os.flush();
			os.close();
//			facesContext.responseComplete();
		} 
		catch(Exception e) 
		{
//			log.error("\nFailure : " + e.toString() + "\n");
		}
      }
} 