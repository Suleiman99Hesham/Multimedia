
package vectorquantizer;

public class Codebook {
    int [][] array;
    int index;
    public Codebook(int [][] a,int x,int vr, int vc)
    {
        array=new int [vr][vc];
        for(int i=0;i<vr;i++)
        {
            for(int j=0;j<vc;j++)
                array[i][j]=a[i][j];
        }
        index=x;
    }
}
