/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package metodosimplex;

/**
 *
 * @author diego
 */
public class Main {
    double[][] matrizA={{2,2,0},{1,2,4},{1,3,2}};;
    double vectorZ[]={-1000,-6000,-3000};
    String vectorDesigualdades[]={">",">",">"};
    double[] LD={0,20,80,70}; //primer valor para Z, otros para matrizA
    int n= vectorDesigualdades.length;
    int m= vectorZ.length;
    int artFase1=0;
    
    public Main(double[][] MatrizA,double[] vectorZ,double LD[], String[] vectorDesigualdades){
        this.matrizA = MatrizA;
        this.vectorZ = vectorZ;
        this.LD = LD;
        this.vectorDesigualdades = vectorDesigualdades;
        n= vectorDesigualdades.length;
        m= vectorZ.length;
        this.ampliarMatrices();
    }
    
    public Main(){
        this.ampliarMatrices();
    }
    
    public static void main(String a[]){
        //Main uno = new Main();
        //uno.ampliarMatrices();
        //uno.ejecutaMetodo();
        
        primerFrame ventana = new primerFrame();
        ventana.setVisible(true);
       
    }
    
    
    
    private void ampliarMatrices(){
        
        double[][] nuevaA= new double[n][m+n];
        double[] nuevoZ= new double[m+n];
        
        for(int i=0; i<n;i++){
            for(int j=0;j<(m+n);j++){
                if(j<m){
                    nuevaA[i][j]=matrizA[i][j];
                    nuevoZ[j]=vectorZ[j];
                }else{
                    nuevaA[i][j]=0;
                    nuevoZ[j]=0;
                }
            }
        }
        
        for(int i=0 ;i<n; i++){
            if(vectorDesigualdades[i].equalsIgnoreCase(">")){
                nuevaA[i][m+i]=-1;
                artFase1++;
            }else{
                nuevaA[i][m+i]=1;
            }
        }
        
        matrizA=nuevaA;
        vectorZ=nuevoZ;
    }
    
    public void imprimirDatos(){
        for(int i=0;i<m+n;i++){
            System.out.print("["+vectorZ[i]+"]");
        }
        System.out.println("=["+LD[0]+"]/n");
        
        for(int i=0;i<n;i++){
            for(int j=0;j<m+n;j++){
                System.out.print("["+matrizA[i][j]+"]");
            }
            System.out.println("=["+LD[i+1]+"]/n");
        }
    }
    
    private void iniciaMetodo(){
         MetodoSimplex dos = new MetodoSimplex(matrizA,vectorZ,LD,vectorDesigualdades,artFase1);
         dos.aplicarMetodo();
    }
    
    public String ejecutaMetodo(){
        MetodoSimplex dos = new MetodoSimplex(matrizA,vectorZ,LD,vectorDesigualdades,artFase1);
        String salida=dos.aplicarMetodo();
        
        //System.out.print(salida);
        return salida;
    }
}
