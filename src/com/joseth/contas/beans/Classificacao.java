package com.joseth.contas.beans; 

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;

import org.hibernate.annotations.Formula;

@Entity
@Cacheable
public class Classificacao implements Serializable, Comparable<Classificacao> {
	
	private Integer id;
	private Integer version;
	private String nome;
	
	private int movimentosCount;
//	private List<Movimento> movimentos = new ArrayList<Movimento>();
	
	public Classificacao(){}
	public Classificacao(Integer id, String nome)
	{
	    this.id = id;
	    this.nome = nome;
	}
		
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
	
//	@Length(max=20)
	public String getNome() { 
	     return nome;
	}

	public void setNome(String nome) {
	     this.nome = nome;
	}
	
//	@ManyToMany(fetch=FetchType.LAZY,mappedBy="classificacoes")
////    @Fetch(FetchMode.JOIN)
//    @LazyCollection(LazyCollectionOption.EXTRA)
//    public List<Movimento> getMovimentos() 
//    {      
////        return movimentos;
//	    return null;
//    }
//    
//    public void setMovimentos(List<Movimento> movimentos) {
//        this.movimentos = movimentos;
//    }

	@Formula(value="(select count(distinct mc.Movimento_id) from Movimento_Classificacao mc where mc.classificacoes_id = id)")
	public int getMovimentosCount()
	{
	    return movimentosCount;
	}
	
    public void setMovimentosCount( int movimentosCount)
    {
        this.movimentosCount = movimentosCount; 
    }
	
	public int hashCode(){return id==null?super.hashCode():id.intValue();}
	public boolean equals( Object o ){return o instanceof Classificacao && ((Classificacao)o).getId().equals(id);}

	public String toString(){return "Classificacao("+id+","+nome+")";}
	
	public int compareTo(Classificacao c) {
		return nome.compareTo(((Classificacao)c).getNome());
	}
}
