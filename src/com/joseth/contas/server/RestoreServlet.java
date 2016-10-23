package com.joseth.contas.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.joseth.contas.beans.Classificacao;
import com.joseth.contas.beans.Conta;
import com.joseth.contas.beans.Movimento;
import com.joseth.contas.beans.Usuario;
import com.joseth.contas.daos.ClassificacaoDAO;
import com.joseth.contas.daos.ContaDAO;
import com.joseth.contas.daos.MovimentoDAO;
import com.joseth.contas.daos.UsuarioDAO;

@WebServlet(urlPatterns="/restrito/restore")
public class RestoreServlet extends HttpServlet
{
//	@EJB(beanName="mvd")
	MovimentoDAO mvd = new MovimentoDAO();
	
//	@EJB(beanName="ud")
	UsuarioDAO ud = new UsuarioDAO();
	
//	@EJB(beanName="cd")
	ContaDAO cd = new ContaDAO();
	
//	@EJB(beanName="cld")
	ClassificacaoDAO cld = new ClassificacaoDAO();
	   
    protected void doPost( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException
    {
//		Logger rl = Logger.getLogger(this.getClass());
//		rl.setLevel(Level.INFO);
//		
//		Logger jpaLogger = Logger.getLogger("org.jboss.as.jpa");
//		jpaLogger.setLevel(Level.WARN);
//		
//		Logger sqlLogger = Logger.getLogger("org.hibernate.SQL");
//		sqlLogger.setLevel(Level.WARN);

//    	rl.info("Inicio do restore");
        getServletContext().log("Inicio do restore"); 
    	
    	ZipInputStream zis = null;
    	
        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(100*1024*1024);
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setSizeMax( 100*1024*1024 ); 
        
        try
        {
        	List<FileItem> fileItems = upload.parseRequest(req);
        	zis = new ZipInputStream(fileItems.get(0).getInputStream());
        }
        catch( FileUploadException fue )
        {
        	throw new ServletException(fue);
        }
		
		List<String> classificacoesStr = new ArrayList<String>();
		List<String> usuariosStr = new ArrayList<String>();
		List<String> contasStr = new ArrayList<String>();
		Map<Integer,String> movimentosStr = new TreeMap<Integer,String>();
		
		try 
		{
			ZipEntry ze;
			byte b[] = new byte[65535];
			while( (ze = zis.getNextEntry()) != null )
			{
				int cnt;
				StringBuffer sb = new StringBuffer();
				while( (cnt = zis.read(b)) > 0 )
				{
					sb.append( new String(b,0,cnt) );
				}
				String nomeArquivo = ze.getName();
				if( nomeArquivo.startsWith("classificacao") )
					classificacoesStr.add( sb.toString() );
				else if( nomeArquivo.startsWith("conta") )
					contasStr.add( sb.toString() );
				else if( nomeArquivo.startsWith("usuario") )
					usuariosStr.add( sb.toString() );
				else if( nomeArquivo.startsWith("movimento") )
				{
					String id = nomeArquivo.substring(9,nomeArquivo.length()-4);
					movimentosStr.put(Integer.parseInt(id), sb.toString() );
				}
			}
		} 
		catch (IOException e) 
		{
			throw new ServletException(e);
		}
		
//		sqlLogger.setLevel(Level.DEBUG);
//		sqlLogger.setLevel(Level.WARN);
		
//		rl.info("Apagando Movimentos");
		getServletContext().log("Apagando Movimentos");
		int i=0;
		//rl.info("Movimentos Apagados: " + mvd.apagarTodos());
		getServletContext().log("Movimentos Apagados: " + mvd.apagarTodos());
		
//		rl.info("Apagando Contas");
		getServletContext().log("Apagando Contas");
		cd.apagar(cd.pesquisarTodos());
		
//		rl.info("Apagando Usuarios");
		getServletContext().log("Apagando Usuarios");
		ud.apagar(ud.pesquisarTodos());
		
//		rl.info("Apagando Classes");
		getServletContext().log("Apagando Classes");
		cld.apagar(cld.pesquisarTodos());
		
//		rl.info("Restaurando Classes: " + classificacoesStr.size());
		getServletContext().log("Restaurando Classes: " + classificacoesStr.size());
		i=0;
		for( String s: classificacoesStr )
		{
			Classificacao c = (Classificacao)cld.fromXml(s);
			if( c.getVersion() == null )
				c.setVersion(0);
			cld.replicar(c);
			i++;
		}
		
//		rl.info("Restaurando Usuarios: " + usuariosStr.size());
		getServletContext().log("Restaurando Usuarios: " + usuariosStr.size());
		i=0;
		for( String s: usuariosStr )
		{
			Usuario u = (Usuario)ud.fromXml(s);
			if( u.getVersion() == null )
				u.setVersion(0);
			ud.replicar(u);
			i++;
		}
		
//		rl.info("Restaurando Contas: " + contasStr.size());
		getServletContext().log("Restaurando Contas: " + contasStr.size());
		i=0;
		for( String s: contasStr )
		{
			Conta c = (Conta)cd.fromXml(s);
			if( c.getVersion() == null )
				c.setVersion(0);
			cd.replicar(c);
			i++;
		}

//		rl.info("Restaurando Movimentos: " + movimentosStr.size());
		getServletContext().log("Restaurando Movimentos: " + movimentosStr.size());
		List<String> retardatarios = new ArrayList<String>();		
		i=0;
		for( String s: movimentosStr.values() )
		{
 			Movimento m = (Movimento)mvd.fromXml(s);
 			if( m.getConta() == null && m.getMovimento() == null && s.substring(1).contains("<movimento>") )
 			{
 				retardatarios.add(s);
 				continue;
 			}
 			
			if( m.getVersion() == null )
				m.setVersion(0);

			mvd.replicar(m);
			i++;
			if( (i % 500) == 0 )
			{
//				rl.info( i + " de " + movimentosStr.size() + " restaurados ");
			    getServletContext().log( i + " de " + movimentosStr.size() + " restaurados ");
			}
		}

//		rl.info("Restaurando Movimentos Retardatarios: " + retardatarios.size());
		getServletContext().log("Restaurando Movimentos Retardatarios: " + retardatarios.size());
		i=0;
		for( String s: retardatarios )
		{
 			Movimento m = (Movimento)mvd.fromXml(s);
			if( m.getVersion() == null )
				m.setVersion(0);
			mvd.replicar(m);
			i++;
			if( (i % 500) == 0 )
			{
//				rl.info( i + " de " + retardatarios.size() + " restaurados ");
			    getServletContext().log(i + " de " + retardatarios.size() + " restaurados ");
			}
		}

//		jpaLogger.setLevel(Level.DEBUG);
//		sqlLogger.setLevel(Level.DEBUG);
		
//    	rl.info("Fim do restore");
    	getServletContext().log("Fim do restore");
      }
} 