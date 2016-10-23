package com.joseth.contas.server;

import java.io.StringReader;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.joseth.contas.beans.Conta;
import com.joseth.contas.beans.Movimento;
import com.joseth.contas.daos.MovimentoDAO;

public class Text2Movs 
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
    
    private MovimentoDAO mvd;
    private Movimento pai;
    private Conta conta;
    
    public Text2Movs(MovimentoDAO mvd, Movimento pai, Conta conta)
    {
        this.mvd = mvd;
        this.pai = pai;
        this.conta = conta;
    }

    List<Movimento> movimentos = new ArrayList<Movimento>();
    List<String> descartados = new ArrayList<String>();
    
    
    public List[] preProcessar(String texto, Boolean inverter) //throws Exception
    {    
    	movimentos.clear(); 
    	descartados.clear();
        try
        {
            if( texto.matches("(?s).*<OFX>.*</OFX>.*") )    
                processarOFX(texto, inverter);
            else
                processarTSV(texto, inverter);
        }
        catch( Exception e )
        {
            throw new RuntimeException(e);
        }

        return new List[]{movimentos,descartados};
        
    }
    
    String cabecalhos[]=
    {
        "Data \t  \tHistórico \tDocto. \t  \tValor \tSaldo",
        "Data \t  \t  \tLançamento \tAg/Origem \tValor (R$) \t  \tSaldo (R$)",
        "Data \tHistórico das Despesas \tR$ \tUS$",
        "Data\tDescrição\tNº Documento\tMovimento (R$)" 
    };
    
    String padroes[]=
      {
          "(\\d{2}\\/\\d{2}\\/\\d{4})\\ *\\t[\\w\\ ]*\\t([\\S\\ ]*)\\t([\\S\\ ]*)\\t\\t([\\d\\.\\,\\-]+).*",
          "(\\d{2}\\/\\d{2})\\s*([\\S\\ ]*)\\t([\\S\\ ]*)\\t([\\d\\.\\,\\-\\t\\ ]*).*",
          "(\\d{2}\\/\\d{2}\\/\\d{4})\\ \\t([\\S\\ ]*)(\\t)([\\d\\.\\,\\-]+).*",
          //"(\\d{2}\\/\\d{2}\\/\\d{4})\\t([\\S\\ ]*)\\t([\\S\\ ]*)\\t([\\d\\.\\,\\-]+).*"
          "(\\S*)\\t([\\S\\ ]*)\\t([\\S\\ ]*)\\t([\\S]+).*"
      };
  
  public void processarTSV( String t, boolean inverter )
  {
      String lns[] = t.split("\n");

      int maxmatchid=-1; 
      for (int i=0; i< cabecalhos.length; i++) 
      {
          String c = cabecalhos[i];
          if( t.contains(c) )
              maxmatchid = i;
      }
      Pattern p = Pattern.compile(padroes[maxmatchid]);
      
      for (int i=0; i< lns.length; i++) 
      {
          Matcher m = p.matcher(lns[i]);
          if( m.matches() && !m.group(2).trim().toUpperCase().startsWith("SALDO") )
          {
              Movimento mov = new Movimento();
              try
              {
                  String dataStr = m.group(1).trim();
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
              mov.setDescricao( m.group(2).trim() );
              mov.setDocumento( m.group(3).trim() );
              String val = m.group(4).replaceAll("\\ ", "").replaceAll("\\t","");
              if( val.endsWith("-") )
                  val = "-"+val.substring(0,val.length()-1);
              try
              {
                  mov.setValor( (inverter?-1:1) * nf.parse( val.trim() ).doubleValue() );
              }
              catch( ParseException pe)
              {
                  // Valor invalida
                  descartados.add(lns[i]);
                  continue;
              }
              mov.setMovimento(pai);
              mov.setConta(conta);
              
              if( !mvd.pesquisarIdentico(mov) )
            	  movimentos.add(mov);
              else
                  descartados.add(lns[i]);
          }
          else
        	  descartados.add(lns[i]);
      }
      
  }
  
  public int maxidx( int pmatch[])
  {
      int max = -1;
      int maxid = -1;
      for( int i=0; i< pmatch.length; i++)
      {
          if( pmatch[i] > max && pmatch[i] > 0)
          {
              max = pmatch[i];
              maxid = i;
          }
      }
      return maxid;
  }
  
  public void processarOFX( String xml, boolean inverter ) throws Exception
  {
      xml = xml.substring(xml.indexOf("<OFX>"));
      InputSource is = new InputSource(new StringReader(xml));

      XPathFactory xpf = XPathFactory.newInstance();
      XPath xp = xpf.newXPath();
      
      NodeList nl = null;

      try
      {
          nl = (NodeList)xp.evaluate("//STMTTRN",is,XPathConstants.NODESET);
      }
      catch(XPathExpressionException xee)
      {
          xml = xml.replaceAll("<CODE>(.*)\r","<CODE>$1</CODE>\r");
          xml = xml.replaceAll("<SEVERITY>(.*)\r","<SEVERITY>$1</SEVERITY>\r");
          xml = xml.replaceAll("<DTSERVER>(.*)\r","<DTSERVER>$1</DTSERVER>\r");
          xml = xml.replaceAll("<LANGUAGE>(.*)\r","<LANGUAGE>$1</LANGUAGE>\r");
          xml = xml.replaceAll("<TRNUID>(.*)\r","<TRNUID>$1</TRNUID>\r");
          xml = xml.replaceAll("<CURDEF>(.*)\r","<CURDEF>$1</CURDEF>\r");
          xml = xml.replaceAll("<BANKID>(.*)\r","<BANKID>$1</BANKID>\r");
          xml = xml.replaceAll("<ACCTID>(.*)\r","<ACCTID>$1</ACCTID>\r");
          xml = xml.replaceAll("<ACCTTYPE>(.*)\r","<ACCTTYPE>$1</ACCTTYPE>\r");
          xml = xml.replaceAll("<DTSTART>(.*)\r","<DTSTART>$1</DTSTART>\r");
          xml = xml.replaceAll("<DTEND>(.*)\r","<DTEND>$1</DTEND>\r");
          
          
          xml = xml.replaceAll("<DTASOF>(.*)\r","<DTASOF>$1</DTASOF>\r");
          xml = xml.replaceAll("<BALAMT>(.*)\r","<BALAMT>$1</BALAMT>\r");
          xml = xml.replaceAll("<MEMO>(.*)\r","<MEMO>$1</MEMO>\r");
          xml = xml.replaceAll("<CHECKNUM>(.*)\r","<CHECKNUM>$1</CHECKNUM>\r");
          xml = xml.replaceAll("<FITID>(.*)\r","<FITID>$1</FITID>\r");
          xml = xml.replaceAll("<TRNAMT>(.*)\r","<TRNAMT>$1</TRNAMT>\r");
          xml = xml.replaceAll("<DTPOSTED>(.*)\r","<DTPOSTED>$1</DTPOSTED>\r");
          xml = xml.replaceAll("<TRNTYPE>(.*)\r","<TRNTYPE>$1</TRNTYPE>\r");
          System.out.println(xml);
          is = new InputSource(new StringReader(xml));
          nl = (NodeList)xp.evaluate("//STMTTRN",is,XPathConstants.NODESET);
      }
 
    for (int i=0; i< nl.getLength(); i++) 
    {
        Movimento mov = new Movimento();
          
        String data = ((Element)nl.item(i)).getElementsByTagName("DTPOSTED").item(0).getTextContent();
        String desc = ((Element)nl.item(i)).getElementsByTagName("MEMO").item(0).getTextContent();
           
        NodeList nlDoc = ((Element)nl.item(i)).getElementsByTagName("REFNUM");  
          
        String doc  = null;
        if( nlDoc != null && nlDoc.getLength() > 0 )
             doc = nlDoc.item(0).getTextContent();
          
        String val  = ((Element)nl.item(i)).getElementsByTagName("TRNAMT").item(0).getTextContent();
          
//      log.info(data.substring(0,8));
          
        mov.setData(ofxDf.parse( data.substring(0,8) ));
        mov.setDescricao( desc );
        mov.setDocumento( doc );
        mov.setValor( ( inverter ? -1 : 1 ) * nf.parse( val.replace('.', ',')).doubleValue());                
        if( pai != null )
        {
            mov.setMovimento(pai);
            mov.getMovimento().getMovimentos().add(mov);
        }
        else
            mov.setConta(conta);
    
        if( !mvd.pesquisarIdentico(mov) )
            movimentos.add(mov);
      }
  }
}


//public String toXml(byte[] conteudo) throws Exception
//{
//  StringBuffer ret = new StringBuffer();
//  Workbook wb = null;
//    wb = Workbook.getWorkbook(new ByteArrayInputStream(conteudo));
//  
//  ret.append("<?xml version=\"1.0\" ?>");
//  ret.append("\n");
//  ret.append("<workbook>");
//  ret.append("\n");
//  for (int sheet = 0; sheet < wb.getNumberOfSheets(); sheet++)
//  {
//    Sheet s = wb.getSheet(sheet);
//
//    ret.append("  <sheet>");
//    ret.append("\n");
//    ret.append("    <name><![CDATA["+s.getName()+"]]></name>");
//    ret.append("\n");
//  
//    Cell[] row = null;
//  
//    for (int i = 0 ; i < s.getRows() ; i++)
//    {
//      ret.append("    <row number=\"" + i + "\">");
//      ret.append("\n");
//      row = s.getRow(i);
//
//      for (int j = 0 ; j < row.length; j++)
//      {
//        if (row[j].getType() != CellType.EMPTY)
//        {
//          ret.append("      <col number=\"" + j + "\">");
//          ret.append("<![CDATA[");
//          if( row[j] instanceof NumberCell )
//              ret.append(nf.format(((NumberCell)row[j]).getValue()));
//          else if( row[j] instanceof DateCell )
//              ret.append(dfGmt.format(((DateCell)row[j]).getDate()));
//          else
//              ret.append(row[j].getContents());
//          ret.append("]]>");
//          ret.append("</col>");
//          ret.append("\n");
//        }
//      }
//      ret.append("    </row>");
//      ret.append("\n");
//    }
//    ret.append("  </sheet>");
//    ret.append("\n");
//  }
//  
//  ret.append("</workbook>");
//  ret.append("\n");
//  
//  return ret.toString();
//}
//
//public int processarXLS( byte[] conteudo) throws Exception
//{    
//  String xml = toXml(conteudo);
//  
//  InputSource is = new InputSource(new StringReader(xml));
//  
//  XPathFactory xpf = XPathFactory.newInstance();
//  XPath xp = xpf.newXPath();
//  
//  NodeList nl = (NodeList)xp.evaluate("//row[translate(col/text(),'123456789 ','000000000')='00/00/0000']",is,XPathConstants.NODESET);
//        
//  if( nl.getLength() > 0 )
//  {
//      NodeList cols = ((Element)nl.item(0)).getElementsByTagName("col");
//      String[] colsArray = new String[cols.getLength()];
//      for( int j=0; j<cols.getLength(); j++ )
//          colsArray[j] = ((Node)cols.item(j)).getTextContent();
//
//      Integer dataCol = pesquisarDataCell(colsArray);
//        Integer descCol = pesquisarDescCell(colsArray);
//        Integer docCol  = pesquisarDocCell(colsArray);
//        Integer valorCol = pesquisarValorCell(colsArray);
//        
//        for (int i=0; i< nl.getLength(); i++) 
//        {
//            cols = ((Element)nl.item(i)).getElementsByTagName("col");
//            colsArray = new String[cols.getLength()];
//            Movimento mov = new Movimento();
//            
//            for( int j=0; j<cols.getLength(); j++ )
//                colsArray[j] = cols.item(j).getTextContent();
//            
//            if( dataCol == null || descCol == null || valorCol == null )
//                return -1;
//            
//            if( valorCol == docCol )
//                return -1;
//        
//            mov.setData(df.parse(colsArray[dataCol]));
//            mov.setDescricao(colsArray[descCol]);
//            mov.setValor((inverter?-1:1)*nf.parse(colsArray[valorCol]).doubleValue());
//            if( docCol != null )
//                mov.setDocumento(colsArray[docCol]);
//                    
//            if (getMovimentoPai() != null)
//            {
//                mov.setMovimento(getMovimentoPai());
//                mov.getMovimento().getMovimentos().add(mov);
//            }
//            else
//                mov.setConta(conta);
//            if( !movimentoDAO.pesquisarIdentico(mov) )
//                movimentos.add(mov);
//        }
//  }
//  return 0;
//}

//public Integer pesquisarDataCell( String[] cols )
//{
//  int cnt=0;
//  int col=-1;
//  for( int n=0; n<cols.length; n++)
//  {
//      if( cols[n].matches("\\d\\d/\\d\\d/\\d\\d\\d\\d") )
//      {
//          col = n;
//          cnt++;
//      }
//  }
//  return cnt!=1? -1:col;
//}
//
//public Integer pesquisarDescCell( String[] cols )
//{
//  int cnt=0;
//  int col=-1;
//  for( int n=0; n<cols.length; n++)
//  {
//      if( cols[n].matches("[\\w\\s]*") && !cols[n].matches("[\\d\\,\\.]*") )
//      {
//          col = n;
//          cnt++;
//      }
//  }
//  return cnt!=1? -1:col;
//}
//
//public Integer pesquisarDocCell( String[] cols )
//{
//  int cnt=0;
//  int col=-1;
//  for( int n=0; n<cols.length; n++)
//  {
//      if( cols[n].matches("[\\d]*") )
//      {
//          col = n;
//          cnt++;
//      }
//  }
//  return cnt!=1? -1:col;
//}
//
//public Integer pesquisarValorCell( String[] cols )
//{
//  int cnt=0;
//  int col=-1;
//  for( int n=0; n<cols.length; n++)
//  {
//      if( cols[n].matches("[\\d\\,\\.]*") )
//      {
//          col = n;
//          cnt++;
//      }
//  }
//  return cnt!=1? -1:col;
//}

//public void processarTSV2( String t/*, */ ) //throws Exception
//{
//    int datacol=0;
//    int desccol=1;
//    int doccol=2;
//    int valcol=3;
//    String lns[] = t.split("\n");
//    for (int i=0; i< lns.length; i++) 
//    {
//        String cls[] = lns[i].split("\t");
//          List<String> ls = new ArrayList( Arrays.asList(cls) );
//      
//          // Remove as colunas vazias
//          for( int n=0; n<ls.size(); n++ )
//          {
//              if( ls.get(n) == null || "".equals(ls.get(n).trim()))
//            {
//                  ls.remove(n);
//                  n--;
//                  continue;
//              }
//              ls.set(n, ls.get(n).trim());
//          }
//
//          // Linha inválida (pode ser lixo, numero do cartao, etc)
//          if( ls.size() < 4 )
//              continue;
//          
//          // Testa se a primeira linha é o header e procura tenta identificar as colunas
//          if( i==0 && testHeader(ls) )
//          {
//              int ret;
//              ret = pesquisaData(ls);
//              datacol = ret >= 0 ? ret:datacol;
//              ret = pesquisaDesc(ls);
//              desccol = ret >= 0 ? ret:desccol;
//              ret = pesquisaDoc(ls);
//              doccol = ret;// >= 0 ? ret:doccol;
//              ret = pesquisaVal(ls);
//              valcol = (ret >= 0 ? ret:valcol);
//              continue;
//          }
//      
//          String data = ls.get(datacol).trim();
//        String desc = ls.get(desccol).trim();
//        String doc  = "";
//        if( doccol > -1 ) 
//            doc = ls.get(doccol).trim();
//          String val  = ls.get(valcol).trim();
//
//          Movimento mov = new Movimento();
//
////          mov.setData(df.parse(data));
//        mov.setDescricao( desc );
//        mov.setDocumento( doc );
////        mov.setValor((inverter.getValue()?-1:1)*nf.parse( val ));//.doubleValue());
//        
////        if (getMovimentoPai() != null)
////        {
////            mov.setMovimento(getMovimentoPai());
////            mov.getMovimento().getMovimentos().add(mov);
////            mov.setData(mov.getData());
////            mov.setConta( getMovimentoPai().getConta() );
////        }
////        else
////            mov.setConta(conta);
//
////        if( !movimentoDAO.pesquisarIdentico(mov) )
//            movimentos.add(mov);
//      }            
//}
//
//private boolean testHeader( List<String> ls )
//{
//    if( ls.contains("Data") )
//        return true;
//    return false;
//}
//
//private int pesquisaData( List<String> ls )
//{
//    int ret = ls.indexOf("Data");
//    if( ret >= 0 )
//        return ret;
//    ret = ls.indexOf("data");
//       return ret;
//}
//
//private int pesquisaDesc( List<String> ls )
//{
//    int ret = ls.indexOf("Histórico das Despesas");
//    if( ret >= 0 )
//        return ret;
//    ret = ls.indexOf("Histórico");
//       return ret;
//}
//
//private int pesquisaDoc( List<String> ls )
//{
//    int ret = ls.indexOf("    ");
//       return ret;        
//}
//
//private int pesquisaVal(  List<String> ls )
//{
//    int ret = ls.indexOf("Valor");
//    if( ret >= 0 )
//        return ret;
//    ret = ls.indexOf("R$");
//       return ret;
//}


//int pmatch[] = new int[padroes.length];
//for( int xp=0; xp<pmatch.length; xp++ )
//{
//Pattern p = Pattern.compile(padroes[xp]);
//  for (int i=0; i< lns.length; i++) 
//  {
//   Matcher m = p.matcher(lns[i]);
////     if( lns[i].matches(padroes[xp]) )
//   if( m.matches() )
//     {
//       if( m.group(2).trim().length() > 0 &&
////             m.group(3).trim().length() > 0 &&
//           m.group(4).trim().length() > 0
//         )
//         pmatch[xp]++;
//     }
//  }
//}
//
//int maxmatchid = maxidx(pmatch);
//if( maxmatchid < 0 )
//  return;

//"(\\d{2}\\/\\d{2})\\s*([\\S\\ ]*)\\t(\\t)([\\d\\.\\,\\-\\t\\ ]*).*"