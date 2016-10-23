package com.joseth.contas.client.resumo;

import java.io.Serializable;
 
public class ResumoDTO implements Serializable
{
    public int usuariosCnt;
    public String mesDe;
    public String mesAte;
    public int movCnt;
    public int movClassCnt;
    public double movPercentClass;
    public double valor;
}
 