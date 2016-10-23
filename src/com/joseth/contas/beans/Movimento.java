package com.joseth.contas.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.joseth.contas.xmlbkp.ClassificacaoRefXmlAdapter;
import com.joseth.contas.xmlbkp.ContaRefXmlAdapter;
import com.joseth.contas.xmlbkp.MovimentoRefXmlAdapter;
//import org.jboss.seam.contexts.Contexts;
//
//import com.joseth.common.jpa.DAOGenerico;

@Entity 
public class Movimento implements Serializable 
{
//    @EJB(beanName="cld")
//    ClassificacaoDAO cld;
	
	//seam-gen attributes (you should probably edit these)
	private Integer id;
	private Integer version;
	private Conta conta;
	private String descricao;
    private String comentario;
    private String documento;
	private Date data;
	private Date dataRaiz;
	private Double valor=0d;
	private Movimento movimento;
	private List<Classificacao> classificacoes = new ArrayList<Classificacao>();
	@XmlTransient private List<Movimento> movimentos = new ArrayList<Movimento>();

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Integer getId() {
	     return id;
	}

	public void setId(Integer id) {
	     this.id = id;
	}
	
	@Version
	public Integer getVersion() {
	     return version;
	}

	public void setVersion(Integer version) {
	     this.version = version;
	}

	@XmlJavaTypeAdapter(ContaRefXmlAdapter.class)
	@ManyToOne(fetch=FetchType.EAGER)
	public Conta getConta() {
		return conta;
	}

	public void setConta(Conta conta) {
		this.conta = conta;
		if( movimento != null )
			conta = getMovimentoRaiz().getConta();
	}

	
	public Double getValor() {
		return valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	@XmlJavaTypeAdapter(MovimentoRefXmlAdapter.class)
	@ManyToOne(fetch=FetchType.EAGER)
	public Movimento getMovimento() {
		return movimento;
	}

	public void setMovimento(Movimento movimento) {
		this.movimento = movimento;
		if( dataRaiz != null && getMovimentoRaiz().getData() != null )
			setDataRaiz(getMovimentoRaiz().getData());
		if( conta != null && getMovimentoRaiz().getConta() != null )
			setConta(getMovimentoRaiz().getConta());
	}

	@XmlJavaTypeAdapter(ClassificacaoRefXmlAdapter.class)
	@ManyToMany(fetch=FetchType.EAGER,cascade={CascadeType.PERSIST})
	@JoinTable(
        name="Movimento_Classificacao",
        joinColumns=
            @JoinColumn(name="Movimento_id", referencedColumnName="id"),
        inverseJoinColumns=
            @JoinColumn(name="classificacoes_id", referencedColumnName="id")
    )
	public List<Classificacao> getClassificacoes() {
		return classificacoes;
	}

	public void setClassificacoes(List<Classificacao> classificacoes) {
		this.classificacoes = classificacoes;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
		if( movimento != null )
			setDataRaiz( movimento.getData() );
		else
			setDataRaiz( data );
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public int hashCode(){return id==null?super.hashCode():id.intValue();}
	public boolean equals( Object o ){return o instanceof Movimento && ((Movimento)o).getId().equals(id);}

	@XmlTransient
	@OneToMany(mappedBy="movimento"/*,fetch=FetchType.EAGER*/)
//    @-Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	public List<Movimento> getMovimentos() {
		return movimentos;
	}

	public void setMovimentos(List<Movimento> movimentos) {
		this.movimentos = movimentos;
	}
	
	@Transient
	public Usuario getUsuarioRaiz()
	{
		if( movimento == null )
		{
			if( conta != null )
				return conta.getUsuario();
			else
				return null;
		}
		else 
			return movimento.getUsuarioRaiz();
	}
	
	@Transient
    public Movimento getMovimentoRaiz()
    {
        if( movimento == null ) return this;
        else return movimento.getMovimentoRaiz();
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }
    

    
    @Transient
    public Double getValorSubs()
    {
    	Double ret=0d;
    	for( Movimento m: getMovimentos() )
    		ret += m.getValor();
    	ret = (double)Math.round(ret*100)/100;
    	return ret;
    }
    
    @Transient
    public List<Classificacao> getClassificacoesExtra()
    {
    	List<Classificacao> ret = new ArrayList<Classificacao>();
    	ret.addAll(getClassificacoes());

//    	initCache();
    	
    	Set stringSet = new HashSet();
    	for(Classificacao c:ret)
    	    stringSet.add(c.getNome());
    	
    	if( !stringSet.contains(ad) && !stringSet.contains(pe) && !stringSet.contains(sq) && 
    		!stringSet.contains(ac) && !stringSet.contains(iv) && !stringSet.contains(em) &&
    		ret.size()>0 && valor < 0 && getMovimentos().size()==0 )
    		ret.add(peC);
    	
    	if( ret.size() == 0 )
    		ret.add(scC);
    	
    	return ret;
    }
    public static String ad = "A Dividir";
    public static String pe = "Pessoal";
    public static String sc = "Sem classificação";
    public static String sq = "Saque";
    public static String ac = "Acertos";
    public static String iv = "Investimento";
    public static String em = "Empréstimos";
    public static Classificacao peC = new Classificacao(-1,"Pessoal");
    public static Classificacao scC = new Classificacao(-2,"Sem classificação");

    
//    private static Set cache = new HashSet();
//    private static void initCache()
//    {
//        
//    }
    
    
    //select * from Movimento_bkp1 where concat(id,',',descricao) not in (select concat(id,',',descricao) from Movimento);
//    public static Classificacao ad;
//    public static Classificacao pe;
//    public static Classificacao sc;
//    public static Classificacao sq;
//    public static Classificacao ac;
//    public static Classificacao iv;
//    public static Classificacao em;
//    public void initCache()
//    {
//    	if( ad == null )
//    		ad = (Classificacao)cld.pesquisar("nome","A Dividir").iterator().next();
//    	if( pe == null )
//    		pe = (Classificacao)cld.pesquisar("nome","Pessoal").iterator().next();
//    	if( sc == null )
//    		sc = (Classificacao)cld.pesquisar("nome","Sem classificação").iterator().next();
//    	if( sq == null )
//    		sq = (Classificacao)cld.pesquisar("nome","Saque").iterator().next();
//    	if( ac == null )
//    		ac = (Classificacao)cld.pesquisar("nome","Acertos").iterator().next();
//    	if( iv == null )
//    		iv = (Classificacao)cld.pesquisar("nome","Investimento").iterator().next();
//    	if( em == null )
//    		em = (Classificacao)cld.pesquisar("nome","Empréstimos").iterator().next();
//    }

	public Date getDataRaiz() {
		return dataRaiz;
	}

	public void setDataRaiz(Date dataRaiz) {
		this.dataRaiz = dataRaiz;
	}
}

