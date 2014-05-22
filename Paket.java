/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chatserver;

/**
 *
 * @author martin ulrich
 */
public class Paket {
   private DruhOperace druhOperace;
   private String data,odesilatel,prijemce;
   public DruhOperace VratDruhOperace ()
   {
       return druhOperace;
   }
   public String VratData ()
   {
       return data;
   }
   public String VratOdesilatele ()
   {
       return odesilatel;
   }
   public String VratPrijemce ()
   {
       return prijemce;
   }
   public Paket (DruhOperace druhOperace,String data,String odesilatel,String prijemce)
   {
       this.druhOperace=druhOperace;
       this.data=data;
       this.odesilatel=odesilatel;
       this.prijemce=prijemce;
   }
   public Paket (String data)
   {
   String [] p=data.split("\\|");
   DruhOperace druh=null;
   int pocetRozdeleni=0;
   for (int i=0;i<data.length();i++)
   {
       if (data.charAt(i)=='|')
           pocetRozdeleni++;
   }
   if (pocetRozdeleni==4)
   {
   switch (p[0])
   {
       case "PRIHLASENI":
           druh=DruhOperace.PRIHLASENI;
       break;
       case "REGISTRACE":
           druh=DruhOperace.REGISTRACE;
       break;
       case "NASTAVIT_STATUS":
           druh=DruhOperace.NASTAVIT_STATUS;
       break;
       case "PRIJMOUT_ZPRAVU":
           druh=DruhOperace.PRIJMOUT_ZPRAVU;
       break;
   }
   this.druhOperace=druh;
   this.data=p[1];
   this.odesilatel=p[2];
   this.prijemce=p[3];
   }
   }
   
   public Paket PrevedNaPaket (String data)
   {
   String [] p=new String [4];
   p=data.split("\\|");
   DruhOperace druh=null;
   switch (p[0])
   {
       case "PRIHLASENI":
           druh=DruhOperace.PRIHLASENI;
       break;
       case "REGISTRACE":
           druh=DruhOperace.REGISTRACE;
       break;
       case "NASTAVIT_STATUS":
           druh=DruhOperace.NASTAVIT_STATUS;
       break;
       case "PRIJMOUT_ZPRAVU":
           druh=DruhOperace.PRIJMOUT_ZPRAVU;
       break;
   }
   return new Paket (druh,p[1],p[2],p[3]);
   }
   public String PrevedNaText ()
   {
     String vysledek="";
     StringBuilder sb=new StringBuilder (vysledek);
     sb.append (druhOperace.toString());
     sb.append ("|");
     sb.append(data);
     sb.append ("|");
     sb.append (odesilatel);
     sb.append ("|");
     sb.append (prijemce);
     sb.append ("\n");
     vysledek=sb.toString();
     return vysledek;
   }
}
