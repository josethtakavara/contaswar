package com.joseth.contas.server;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.joseth.contas.beans.Conta;
import com.joseth.contas.beans.Movimento;
import com.joseth.contas.daos.MovimentoDAO;

public class Text2MovsTemplate 
{
    private static SimpleDateFormat df = (SimpleDateFormat)DateFormat.getDateInstance();
    private static SimpleDateFormat dfSa = (SimpleDateFormat)DateFormat.getDateInstance();
    private static SimpleDateFormat df2y = (SimpleDateFormat)DateFormat.getDateInstance();
    private static SimpleDateFormat ofxDf = (SimpleDateFormat)DateFormat.getDateInstance();
    private static SimpleDateFormat dfGmt = (SimpleDateFormat)DateFormat.getDateInstance();
    private static DecimalFormat nf = (DecimalFormat)NumberFormat.getInstance();
    static
    {
        dfGmt.applyPattern("dd/MM/yyyy");
        dfGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        df.applyPattern("dd/MM/yyyy");
        df2y.applyPattern("dd/MM/yy");
        dfSa.applyPattern("dd/MM");
        ofxDf.applyPattern("yyyyMMdd");
        nf.applyPattern("#,###.##");
    }
    
    private String template;
    private MovimentoDAO mvd;
    private Movimento pai;
    private Conta conta;
    private int idxData      = -1;
    private int idxDescricao = -1;
    private int idxDocumento = -1;
    private int idxValor     = -1;
    
    private Set<String> bs; 
    private Set<String> bd;
    
//    "(\\d{2}\\/\\d{2}\\/\\d{4})\\ *\\t[\\w\\ ]*\\t([\\S\\ ]*)\\t([\\S\\ ]*)\\t\\t([\\d\\.\\,\\-]+).*",
//    "(\\d{2}\\/\\d{2})\\s*([\\S\\ ]*)\\t([\\S\\ ]*)\\t([\\d\\.\\,\\-\\t\\ ]*).*",
//    "(\\d{2}\\/\\d{2}\\/\\d{4})\\ \\t([\\S\\ ]*)(\\t)([\\d\\.\\,\\-]+).*",
//    //"(\\d{2}\\/\\d{2}\\/\\d{4})\\t([\\S\\ ]*)\\t([\\S\\ ]*)\\t([\\d\\.\\,\\-]+).*"
//    "(\\S*)\\t([\\S\\ ]*)\\t([\\S\\ ]*)\\t([\\S]+).*"
    
    private String dataPattern      = "([\\\\d\\\\/]*)";
    private String descricaoPattern = "([\\\\S\\\\ ]*)";
    private String documentoPattern = "([\\\\S\\\\ ]*)";
    private String valorPattern     = "([\\\\d\\\\.\\\\,\\\\-\\\\t\\\\ ]*)";
    
    public Text2MovsTemplate(String template, MovimentoDAO mvd, Set<String> bs, Set<String> bd, Movimento pai, Conta conta)
    {
        this.mvd = mvd;
        this.bs = bs;
        this.bd = bd;
        this.pai = pai;
        this.conta = conta;
        //\\{(\\w)\\}
        Pattern p = Pattern.compile("\\$\\{(\\w*)\\}");
        Matcher m = p.matcher(template);
        int i=1;
        while( m.find() )
        {
            String campo = m.group(1);
            if( "data".equals(campo) )
                idxData = i;
            else if( "descricao".equals(campo) )
                idxDescricao = i;
            else if( "documento".equals(campo) )
                idxDocumento = i;
            else if( "valor".equals(campo) )
                idxValor = i;
            i++;
        }
        
        template=template.replaceAll("\\$\\{data\\}",      dataPattern);
        template=template.replaceAll("\\$\\{descricao\\}", descricaoPattern);
        template=template.replaceAll("\\$\\{documento\\}", documentoPattern);
        template=template.replaceAll("\\$\\{valor\\}",     valorPattern);
        this.template = template;
    }

    List<Movimento> movimentos = new ArrayList<Movimento>();
    List<String> descartados = new ArrayList<String>();
    
    
    public List[] preProcessar(String texto, Boolean inverter) //throws Exception
    {    
    	movimentos.clear();
    	descartados.clear();
        processarTSV(texto, inverter);
        return new List[]{movimentos,descartados};
    }
     
    public void processarTSV( String t, boolean inverter )
    {
      String lns[] = t.split("\n");

      Pattern p = Pattern.compile(template);
      
      for (int i=0; i< lns.length; i++) 
      {
          Matcher m = p.matcher(lns[i]);
          if( m.matches() )
          {
              Movimento mov = new Movimento();
              if( idxData > 0 )
              {
                  try
                  {
                      String dataStr = m.group(idxData).trim();
                      if( dataStr.length() == 5)
                      {
                          Date data = dfSa.parse(dataStr);
                          Date hj = new Date();
                          if( data.getMonth() > hj.getMonth() )
                              data.setYear(hj.getYear()-1);
                          else
                              data.setYear(hj.getYear());
                          mov.setData(data);
                          
                      }
                      else if( dataStr.length() == 8 )
                          mov.setData(df2y.parse(dataStr));              
                      else if( dataStr.length() == 10 )
                          mov.setData(df.parse(dataStr));
                  }
                  catch( ParseException pe)
                  {
                      // Data invalida
                      descartados.add(lns[i]);
                      continue;
                  }
              }
              if( idxDescricao > 0 )
              {
                  String desc = m.group(idxDescricao);
                  for( String b: bs )
                      desc = desc.replaceAll(b,"");
                  mov.setDescricao( desc.trim() );
              }
              if( idxDocumento > 0 )
                  mov.setDocumento( m.group(idxDocumento).trim() );
              
              if( idxValor > 0 )
              {
                  String val = m.group(idxValor).replaceAll("\\ ", "").replaceAll("\\t","");
                  if( val.endsWith("-") )
                      val = "-"+val.substring(0,val.length()-1);
                  try
                  {
                      mov.setValor( (inverter?-1:1) * nf.parse( val.trim() ).doubleValue() );
                  }
                  catch( ParseException pe)
                  {
                      // Valor invalido
                      descartados.add(lns[i]);
                      continue;
                  }
                  mov.setValor( Math.round( mov.getValor() * 100.0 ) / 100.0 );
              }
              
              mov.setMovimento(pai);
              mov.setConta(conta);
              
              if( !mvd.pesquisarIdentico(mov) )
              {
                  boolean ok=true;
                  for( String b: bd )
                  {
                      if( mov.getDescricao()!= null && mov.getDescricao().contains(b) )
                      {
                          ok = false;
                          break;
                      }
                  }
                  if( ok )
                      movimentos.add(mov);
                  else
                      descartados.add(lns[i]);
              }
              else
                  descartados.add(lns[i]);
          }
          else
        	  descartados.add(lns[i]);
      }
  }  
}