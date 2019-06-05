


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Suleiman Hesham
 */
public class start_Huffman extends javax.swing.JFrame {

    static Vector< PrimaryType> plist = new Vector<PrimaryType>();        
    static Vector< PrimaryType> slist = new Vector<PrimaryType>();
    static int originalsize=0;
    static Vector<PrimaryType> decVector=new Vector<PrimaryType>();
    public static int findbykey (Vector<PrimaryType> myvec,String schar)
    {
        for (int i=0;i<myvec.size();i++)
        {
            if(myvec.elementAt(i).key.equals(schar))
            {
                return i;
            }
        }
        return -1;
    }
    public static int findbycode (String scode)
    {
        for (int i=0;i<decVector.size();i++)
        {
            if(decVector.elementAt(i).code.equals(scode))
            {
                return i;
            }
        }
        return -1;
    }
    public static void sortlist(Vector<PrimaryType> myvec)
    {
        int n = myvec.size();
        for(int i=0;i<n-1;i++)
        {
            for(int j=0;j<n-i-1;j++)
            {
                if(myvec.elementAt(j).prob < myvec.elementAt(j+1).prob)
                {
                    PrimaryType obj=myvec.elementAt(j);
                    myvec.set(j, myvec.elementAt(j+1));
                    myvec.set(j+1, obj);
                }
            }
        }
    }
    
    public static void readNodes() throws IOException{
		PrimaryType newNode;
		String line;
		BufferedReader br = new BufferedReader(new FileReader("list.txt"));
		// Read line by line and it will be false when there is no new lines
		while ((line = br.readLine()) != null) {
			// Split my string based on spaces
			String [] parts = line.split("\\s+");
			String symbol=parts[0], code=parts[1];
			newNode = new PrimaryType(symbol,0,0,0,code,0);
			decVector.add(newNode);
		}
	}
	
	// Write nodes in the 		PrimaryType newNode;file
	public static void saveNodes() throws IOException{

		String line;
		//Create File to write in
	    FileWriter fileWriter = new FileWriter("list.txt");
	    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);	 
	    for(int i=0;i<originalsize;i++) {    
			bufferedWriter.write(plist.elementAt(i).key + "   " + plist.elementAt(i).code);
			bufferedWriter.newLine();
		}
	    bufferedWriter.close();
	}
    public static void compress()throws IOException
    {
        String comp = "";
        // Open code File to read 
        BufferedReader br = new BufferedReader(new FileReader("statement.txt"));
        // Read message in string
        comp = br.readLine();
        br.close();
         

        for (int i = 0; i < comp.length(); i++) {
            int place=findbykey(plist,comp.charAt(i)+"");
            // If this is first occurrence of Character
            // Insert the Character
            if (place==-1)
            {
                //putting the character in object
                PrimaryType obj=new PrimaryType (comp.charAt(i)+"",1,-1,-1,"",0);
                plist.addElement(obj);
            }
            // If elements already exists in hash map 
            // Increment the count of element by 1 
            else if (place!=-1)
            {
                plist.elementAt(place).prob++;
            }
        }
        
        
        //calculating the probability for chars
        for(int i=0;i<plist.size();i++)
        {
            plist.elementAt(i).prob/=comp.length();
        }
        
        originalsize=plist.size();
        while(true)
        {
            
            slist=new Vector<PrimaryType>();
            for(int i=0;i<plist.size();i++)
            {
                if(plist.elementAt(i).added==0)
                {
                    slist.add(plist.elementAt(i));
                }
            }
            int listsize=slist.size();
            sortlist(slist);
            if(listsize>2)
            {
                String newkey=slist.elementAt(listsize-2).key+slist.elementAt(listsize-1).key;
                double newprob=slist.elementAt(listsize-2).prob+slist.elementAt(listsize-1).prob;
                String fkey=slist.elementAt(listsize-2).key;
                String skey=slist.elementAt(listsize-1).key;
                int fplace=findbykey(plist,fkey);
                int splace=findbykey(plist,skey);
                plist.elementAt(fplace).added=1;
                plist.elementAt(splace).added=1;
                PrimaryType obj=new PrimaryType(newkey,newprob,fplace,splace,"",0);
                plist.addElement(obj);
            }
            else if(listsize==1)
            {
                String fkey=slist.elementAt(0).key;
                int fplace=findbykey(plist,fkey);
                plist.elementAt(fplace).code="0";
                break;
            }
            else
            {
                String fkey=slist.elementAt(0).key;
                String skey=slist.elementAt(1).key;
                int fplace=findbykey(plist,fkey);
                int splace=findbykey(plist,skey);
                plist.elementAt(fplace).code="0";
                plist.elementAt(splace).code="1";
                break;
            }
        }
        int counter=0;
        while(counter < originalsize)
        {
            for(int i=0;i<plist.size();i++)
            {
                if(plist.elementAt(i).added==0&&plist.elementAt(i).findex!=-1&&plist.elementAt(i).sindex!=-1)
                {
                    plist.elementAt(plist.elementAt(i).findex).code=plist.elementAt(i).code+"0";
                    plist.elementAt(plist.elementAt(i).sindex).code=plist.elementAt(i).code+"1";
                    plist.elementAt(plist.elementAt(i).findex).added=0;
                    plist.elementAt(plist.elementAt(i).sindex).added=0;
                }
            }
            counter=0;
            for(int i=0;i<originalsize;i++)
            {
                if(plist.elementAt(i).added==0&&plist.elementAt(i).findex==-1&&plist.elementAt(i).sindex==-1)
                {
                    counter++;
                }
            }
        }
        String compressCode="";
        //Create File to write in
        FileWriter fileWriter = new FileWriter("code.txt");
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);	 

        // Write code of every symbol in the file
        for(int i=0;i<comp.length();i++)
        {
            int charIndex=findbykey(plist,comp.charAt(i)+"");
            bufferedWriter.write(plist.elementAt(charIndex).code);//appends the string to the file

        }
        bufferedWriter.close();

        //Save Nodes in File
        saveNodes();
        
    }
    public static void decompress() throws IOException{
        readNodes();
		
        String codedStatement = "",originalWord="";
        // Open code File to read 
        BufferedReader br = new BufferedReader(new FileReader("code.txt"));
        // Read code in string
        codedStatement = br.readLine();
        br.close();
        
        for (int i = 0 ; i<codedStatement.length();i++){
            String holdIt = "" + codedStatement.charAt(i);
            while(true)
            {
                int index=findbycode(holdIt);
                if(index!=-1)
                {
                    originalWord+=decVector.elementAt(index).key;
                    break;
                }
                else
                {
                    holdIt += codedStatement.charAt(++i);
                }
            }
        }
        
                
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("code.txt", true)));
        out.println();
        out.println(originalWord);
	out.close();
        
        System.out.println(originalWord);
    }
    
    /**
     * Creates new form start_Huffman
     */
    public start_Huffman() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton1.setText("Compress");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Decompress");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(57, 57, 57)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 101, Short.MAX_VALUE)
                .addComponent(jButton2)
                .addGap(72, 72, 72))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(43, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addGap(50, 50, 50))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
            compress();
        } catch (IOException ex) {
            Logger.getLogger(start_Huffman.class.getName()).log(Level.SEVERE, null, ex);
        }
        Compress obj=new Compress();
        obj.show();
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        try {
            // TODO add your handling code here:
            decompress();
        } catch (IOException ex) {
            Logger.getLogger(start_Huffman.class.getName()).log(Level.SEVERE, null, ex);
        }
        Decompress obj=new Decompress();
        obj.show();
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(start_Huffman.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(start_Huffman.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(start_Huffman.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(start_Huffman.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new start_Huffman().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    // End of variables declaration//GEN-END:variables
}
