package lzw;
//ABAABABBAABAABAABAAAABABBBBBBBB
import java.util.*;
public class LZW {
    public static void main(String[] args) {
        Vector<String>dictionary=new Vector<String>();
        Vector<Integer>tags=new Vector<Integer>();
        char ascci=(char)0;
        for(int i=0;i<=127;i++,ascci++){
            dictionary.addElement(ascci+"");
        }
       String word,buffer="";
       Scanner input=new Scanner(System.in);
       word=input.next();
       int index=0;
       for(int i=0;i<word.length();i++){
       buffer+=word.charAt(i);
       if(dictionary.contains(buffer)){
       index=dictionary.indexOf(buffer);
       }
       else if(!dictionary.contains(buffer)){
       dictionary.addElement(buffer);
       tags.addElement(index);
       buffer="";
       i--;
       }
       }
       
       if(buffer!=""){
       tags.addElement(index);
       buffer="";
       }
       for(int i=0;i<tags.size();i++){
           System.out.println(tags.get(i));
       }
       //-------------------------------------------------------    
       dictionary=new Vector();
       ascci=(char)0;
       for(int i=0;i<=127;i++,ascci++){
            dictionary.addElement(ascci+"");
       }
       String dWord="";
       for(int i=0;i<tags.size();i++)
       {
           if(i==0)
           {
               dWord+=dictionary.get(tags.get(i));
               
           }
           else if(tags.get(i)<dictionary.size())
           {
               dWord+=dictionary.get(tags.get(i));
               dictionary.addElement(dictionary.get(tags.get(i-1))+dictionary.get(tags.get(i)).charAt(0));
               
           }
           else if(tags.get(i)>=dictionary.size())
           {
               dictionary.addElement(dictionary.get(tags.get(i-1))+(dictionary.get(tags.get(i-1))).charAt(0));
               dWord+=dictionary.get(tags.get(i));
               
           }
       }
       System.out.println("Decompressed Word:"+dWord);
    }
   

}
