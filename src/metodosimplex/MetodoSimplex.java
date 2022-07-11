/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package metodosimplex;

import java.text.DecimalFormat;

/**
 *
 * @author diego
 */
public class MetodoSimplex {
    double[][] matrizA;// tamaño nxm
    double vectorZ[]; // tamaño m
    String vectorDesigualdades[]; //tamaño n
    double[] LD;//primer valor para Z, otros para matrizA tamaño n+1
    int n;
    int m;
    int artFase1;
    String varDentro[]=null;
    DecimalFormat formato = new DecimalFormat("#,###.###");
    String salida="";
    
    public MetodoSimplex(double[][] MatrizA,double[] vectorZ,double LD[], String[] vectorDesigualdades,int artFase1){
        this.matrizA=MatrizA;
        this.vectorZ=vectorZ;
        this.LD=LD;
        this.vectorDesigualdades=vectorDesigualdades;
        n= vectorDesigualdades.length;
        m= vectorZ.length;
        this.artFase1=artFase1;
    
    }
    
    public String aplicarMetodo(){
        if(artFase1>0){           
            fase1(); //si existen desigualdades >
        }else{
            fase2();// si solo hay desigualdades < o = 
        }
        return salida;
    }
    
    private void fase1(){
        //contar el numero de -1 para agregar 1 artificial
        double max=0,min=0;
        int posicionJ=-1;
        int posicionI=-1;
        int i,j,k;
        int largo = m+artFase1;
        double[] LDdividido= new double [n];
        double divisor;
        double pivote[];
        double nuevaA[][];
        double nuevoLD[];
        double nuevoZ[];
        String cadena="";
        boolean fin=false;
        varDentro=new String[n];
        String varFuera[]=new String [largo];
        String temporal="";
        int a=0;
        
        double fase1A[][]=new double[n][largo];
        double fase1Z[]=new double[largo];
        double vectorX0[]=new double[largo];
        double fase1LD[]=new double[n+2];//primr espacio para X0, segundo para Z, otros para igualdades de matriz A
        
        double nuevoX0[];
        
        //añadir variables artificiales a nueva fase1A (ampliar matriz)
        //iniciar el LD
        fase1LD[0]=0;
        k=0;
        int comodin=-1;
        boolean artificial=false;
        
        for(i=0;i<n;i++){
            for(j=0;j<m;j++){
                fase1A[i][j]=matrizA[i][j];
                if(matrizA[i][j]==-1){
                    //añadir verificacion si es del tipo Si 
                    /*
                    comodin=i;
                    for(int l=0;l<n;l++){
                        if(l!=i){
                            
                        }
                    }
                    */
                    //fin verificacion
                    fase1A[i][m+k]=1;
                    k++;
                }
            }
            fase1LD[i+2]=LD[i+1];
        }
        
        //si Z no es negativa, la hara negativa
        for(i=0;i<m;i++){
            if(vectorZ[i]>0){
                vectorZ[i]=vectorZ[i]*-1;
            }
        }
        //ampliar Z y establecer X0 inicial
        for(i=0;i<largo;i++){
            if(i<m){
                vectorX0[i]=0;
                fase1Z[i]=vectorZ[i];
            }else{
                vectorX0[i]=-1;
                fase1Z[i]=0;
            }
        }
        
        //probar imprimiendo matrices ampliadas
        salida+="Inicia así: \n";
        salida+=imprimirFase1(fase1A,fase1Z,vectorX0,fase1LD);
        
        //sumando renglones a X0
        double sumador;
        for(i=0;i<largo;i++){
            sumador=0;
            for(j=0;j<n;j++){
                sumador+=fase1A[j][i];
            }
            vectorX0[i]+=sumador;
        }
        
        sumador=0;
        for(j=0;j<n;j++){
                sumador+=fase1LD[j+2];
            }
        fase1LD[0]=sumador;
        
        salida+="Ahora con la suma: \n";
        salida+=imprimirFase1(fase1A,fase1Z,vectorX0,fase1LD);
        
        
        for(i=0;i<largo;i++){
            if(i<n){
                varFuera[i]="X"+(i+1);
            }else if(i<m){
                varFuera[i]="S"+(i-n+1);                
            }else{
                varFuera[i]="A"+(i-n+1);
            }
        }
        
        for(i=0;i<n;i++){
            varDentro[i]="A"+(i+1);
        }
        
        //inicia pivoteo fase1---------------------------------------------------------------------
        do{
           posicionJ=-1;
            posicionI=-1;
            max=0;min=0;
        for(i=0;i<largo;i++){
            if(vectorX0[i]>max){
            max=vectorX0[i];
            posicionJ=i;
            }
        }
        
        if(posicionJ==-1){ //cambiar
            a=1;
            salida+=terminado("Punto óptimo, pasando a fase 2")+"\n";
            this.transicion(fase1A, fase1Z, fase1LD);
            //fin=true;
            break;
        }
        
        //comodin = 0;
        for(i=0; i<n;i++){
            if(fase1A[i][posicionJ]!=0){
                LDdividido[i]=fase1LD[i+2]/fase1A[i][posicionJ];
                if(LDdividido[i]>0){
                    if(min==0){
                        min=LDdividido[i];
                        posicionI=i;    
                    }else if( LDdividido[i]<min){
                        min=LDdividido[i];
                        posicionI=i;
                    }
                }
            }
        }
        
        if(posicionI==-1){
            salida+=terminado("Infactible");
            //fin=true;
            break;
        }
        
         divisor=fase1A[posicionI][posicionJ];
         pivote = new double[largo+1]; //ultima posicion para LD
         nuevaA =new double [n][largo];
         nuevoLD= new double[n+2];; //primera posicion para X0, segunda para Z
         nuevoZ= new double [largo];
         nuevoX0= new double [largo];
         cadena="";
        
        for(i=0;i<largo;i++){
            pivote[i]=nuevaA[posicionI][i]=fase1A[posicionI][i]/divisor;
        }
        pivote[largo]=nuevoLD[posicionI+2]=fase1LD[posicionI+2]/divisor;
        
        
        
        for(i=0;i<n;i++){
            if(i!=posicionI){
                for(j=0;j<largo;j++){
                nuevaA[i][j]=fase1A[i][j]-fase1A[i][posicionJ]*pivote[j];
                }
                nuevoLD[i+2]=fase1LD[i+2]-fase1A[i][posicionJ]*pivote[largo];
            }
            
        }
        
        //pivoteandoX0
        for(i=0;i<largo;i++){
            nuevoX0[i]=vectorX0[i]-vectorX0[posicionJ]*pivote[i];
        }
        nuevoLD[0]=fase1LD[0]-vectorX0[posicionJ]*pivote[largo];
        
        //pivoteandofase1Z
        for(i=0;i<largo;i++){
            nuevoZ[i]=fase1Z[i]-fase1Z[posicionJ]*pivote[i];
        }
        nuevoLD[1]=fase1LD[1]-fase1Z[posicionJ]*pivote[largo];
        
        cadena="Entra "+varFuera[posicionJ]+" sale "+varDentro[posicionI]+"\n" ;
        temporal=varFuera[posicionJ];
        varFuera[posicionJ]=varDentro[posicionI];
        varDentro[posicionI]=temporal;
        
        cadena+= "X0: ";
        for(i=0;i<largo;i++){
            cadena+="["+formato.format(nuevoX0[i])+"]";
        }
        cadena+="="+"["+formato.format(nuevoLD[0])+"]"+"\n";
        
        
        cadena+= "Z : ";
        for(i=0;i<largo;i++){
            cadena+="["+formato.format(nuevoZ[i])+"]";
        }
        cadena+="="+"["+formato.format(nuevoLD[1])+"]"+"\n";
        
        for(i=0;i<n;i++){
            cadena+=varDentro[i]+": ";
            for(j=0;j<largo;j++){
                cadena+="["+formato.format(nuevaA[i][j])+"]";
            }
            cadena+="="+"["+formato.format(nuevoLD[i+2])+"]"+"\n";
        }
        System.out.println(cadena);
        salida+="\n"+cadena;
        fase1A=nuevaA;
        fase1Z=nuevoZ;
        fase1LD=nuevoLD;
        vectorX0=nuevoX0;
            
        }while(fase1LD[0]>0);
        if(a==0){
            salida+=terminado("Punto óptimo, pasando a fase 2")+"\n";
            this.transicion(fase1A, fase1Z, fase1LD);
        }
    }
    
    private String imprimirFase1(double fase1A[][],double fase1Z[],double vectorX0[],double fase1LD[]){
        int largo = m+artFase1; 
        String cadena="X0:";
        int i,j;
        //imprimir vectorX0 y fase1LD[0]
        for(i=0;i<largo;i++){
            cadena+="["+vectorX0[i]+"]";
        }
        cadena+="=["+fase1LD[0]+"]\n";
        
        cadena+="Z :";
        //imprimir fase1Z y fase1LD[1]
        for(i=0;i<largo;i++){
            cadena+="["+fase1Z[i]+"]";
        }
        cadena+="=["+fase1LD[1]+"]\n";
        //imprimir fase1A y fase1LD[i+2]
        for(i=0;i<n;i++){
            cadena+="E"+(i+1)+":";
            for(j=0;j<largo;j++){
                cadena+="["+fase1A[i][j]+"]";
            }
            cadena+="=["+fase1LD[i+2]+"]\n";
        }
        
        cadena+="\n\n";
        System.out.print(cadena);
        return cadena;
    }
    
    
    private void fase2(){
        salida+="\n"+imprimirDatos();
        
        double max=0,min=0;
        int posicionJ=-1;
        int posicionI=-1;
        int i,j;
        double[] LDdividido= new double [n];
        double divisor;
        double pivote[];
        double nuevaA[][];
        double nuevoLD[];
        double nuevoZ[];
        String cadena="";
        boolean fin=false;
        String varFuera[]=new String [m];
        String temporal="";
        int multiplicar = 0;
        
        for(i=0;i<m;i++){
            if(i<n){
                varFuera[i]="X"+(i+1);
            }else{
                varFuera[i]="S"+(i-n+1);
            }
        }
        
        if(varDentro==null){
        multiplicar = -1;    
        varDentro=new String[n];
        for(i=0;i<n;i++){
            varDentro[i]="S"+(i+1);
        }
        }
        
        //int comodin;
        do{
            posicionJ=-1;
            posicionI=-1;
            max=0;min=0;
        for(i=0;i<m;i++){
            if(vectorZ[i]>max){
            max=vectorZ[i];
            posicionJ=i;
            }
        }
        
        if(posicionJ==-1){
            salida+="\n"+terminado("Punto óptimo");
            fin=true;
            break;
        }
        
        //comodin = 0;
        for(i=0; i<n;i++){
            if(matrizA[i][posicionJ]!=0){
                LDdividido[i]=LD[i+1]/matrizA[i][posicionJ];
                if(LDdividido[i]>0){
                    if(min==0){
                        min=LDdividido[i];
                        posicionI=i;    
                    }else if( LDdividido[i]<min){
                        min=LDdividido[i];
                        posicionI=i;
                    }
                }
            }
        }
        
        if(posicionI==-1){
            salida+="\n"+terminado("Infactible");
            fin=true;
            break;
        }
        
         divisor=matrizA[posicionI][posicionJ];
         pivote = new double[m+1]; //ultima posicion para LD
         nuevaA =new double [n][m];
         nuevoLD= new double[n+1];; //primera posicion para Z
         nuevoZ= new double [m];
         cadena="";
        
        for(i=0;i<m;i++){
            pivote[i]=nuevaA[posicionI][i]=matrizA[posicionI][i]/divisor;
        }
        pivote[m]=nuevoLD[posicionI+1]=LD[posicionI+1]/divisor;
        
        
        
        for(i=0;i<n;i++){
            if(i!=posicionI){
                for(j=0;j<m;j++){
                nuevaA[i][j]=matrizA[i][j]-matrizA[i][posicionJ]*pivote[j];
                }
                nuevoLD[i+1]=LD[i+1]-matrizA[i][posicionJ]*pivote[m];
            }
            
        }
        
        for(i=0;i<m;i++){
            nuevoZ[i]=vectorZ[i]-vectorZ[posicionJ]*pivote[i];
        }
        nuevoLD[0]=LD[0]-vectorZ[posicionJ]*pivote[m];
        
        cadena="Entra "+varFuera[posicionJ]+" sale "+varDentro[posicionI]+"\n" ;
        temporal=varFuera[posicionJ];
        varFuera[posicionJ]=varDentro[posicionI];
        varDentro[posicionI]=temporal;
        cadena+= "Z : ";
        for(i=0;i<m;i++){
            cadena+="["+formato.format(nuevoZ[i])+"]";
        }
        if (multiplicar==-1){
            cadena+="="+"["+formato.format(nuevoLD[0]*-1)+"]"+"\n";
        }else{
            cadena+="="+"["+formato.format(nuevoLD[0])+"]"+"\n";
        }
        
        for(i=0;i<n;i++){
            cadena+=varDentro[i]+": ";
            for(j=0;j<m;j++){
                cadena+="["+formato.format(nuevaA[i][j])+"]";
            }
            cadena+="="+"["+formato.format(nuevoLD[i+1])+"]"+"\n";
        }
        System.out.println(cadena);
        salida+="\n"+cadena;
        matrizA=nuevaA;
        vectorZ=nuevoZ;
        LD=nuevoLD;
        
        }while(fin!=true);
       
    }
    
    
    private String terminado(String mensajeSalida){
        String cadena;
        cadena="CODIGO: "+mensajeSalida;
        System.out.println(cadena);
        return cadena;
    }
    
    public String getSalida(){
        return salida;
    }

    private void transicion(double fase1A[][],double fase1Z[],double fase1LD[]) {
        int i,j;
        for(i=0;i<m;i++){
            vectorZ[i]=fase1Z[i];
        }
        
        for(i=0;i<n+1;i++){
            LD[i]=fase1LD[i+1];
        }
        
        for(i=0;i<n;i++){
            for(j=0;j<m;j++){
                matrizA[i][j]=fase1A[i][j];
            }
        }
        
        fase2();
    }
    private String imprimirDatos(){
        System.out.print("Entran los siguientes datos: \n");
        String cadena="Z : ";
        for(int i=0;i<m;i++){
            cadena+="["+formato.format(vectorZ[i])+"]";
        }
        cadena+="=["+formato.format(LD[0])+"]\n";
        
        for(int i=0;i<n;i++){
            cadena+="E"+(i+1)+": ";
            for(int j=0;j<m;j++){
                cadena+="["+formato.format(matrizA[i][j])+"]";
            }
            cadena+="=["+formato.format(LD[i+1])+"]\n";
        }
        System.out.println(cadena);
        return cadena;
    }
    
}
