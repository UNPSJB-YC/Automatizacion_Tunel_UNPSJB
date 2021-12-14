void cambio_automatico(){

  if (cambio1==0){
    //Control=1;
    if (entrada[3]==0){
    entrada[3] = 1; //Encendido = 1;   //Habilito Ai1
    //Serial.println("Habilito Ai1");
    }
    if ((millis()-tiempoautomatico)>=5000){
    if (entrada[0]==0){  
    entrada[0] = 1;
    //Serial.println("Doy Marcha");
    }} //RUNSTOP = 1;     //Doy Marcha
    
    if ((millis()-tiempoautomatico)>=10000){
    
    entrada[2]=1;
    //Serial.println("Activo Control");
    cambio1 = 1;
    inc=0;
    inc1=1;}
  }
  else{

 
  if  (inc1<=len-1){
    
  
 if (cambio==1){
  
    Inref1=entrada1[inc];
    //Serial.println(Inref1);
    vtiempoant=millis();  
    cambio=0;
   
 }


 

 if ((millis()-vtiempoant)>=entrada1[inc1]*1000){
  inc = inc+2;
  inc1 = inc1+2;
  cambio=1;
  tiemporetardo=millis();
 }
 }else {
        if (entrada[0]==1){
        entrada[0] = 0; //RUNSTOP = 0;     //Apago Motor
        //Serial.println("Apago Motor");
        }
        if (entrada[2]==1){
        entrada[2]=0;
        //Serial.println("Desactivo Control");
        }
        Control=0;
        if ((millis()-tiemporetardo)>3000){
        if (entrada[3]==1){
        entrada[3] = 0; //Encendido = 0;   //Deshabilito Ai1
        //Serial.println("Deshabilito Ai1");}
        }
        terminoautoma=0;
        cambio1=0;    
        cambio = 0;
        ControlAutomatico = 0; 
       }}

// Serial.print(Inref1);Serial.print(" ");
// Serial.print(vtiempoant);Serial.println(" ");


  }
}
