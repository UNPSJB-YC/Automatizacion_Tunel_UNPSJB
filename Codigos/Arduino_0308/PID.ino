void PIDS() {
  if (terminoautoma==0){
    In=Inref;
  }else In=Inref1;
  if (Control==1){
    if (terminoautoma==1){
      VelRef=In;
    }else{
   // In=Inref;      //En (m/s)
    In=map(In,0,32767,9059,32767);

    VelRef=In/1927.47;}
      if (VelRef>17.5){
      VelRef=17;
      }
      if (VelRef<4.7){
      VelRef=4.7;
      }}
  
  else if (Control==0){
    //In=Inref;      //En (hz) aproximados
    VelRef1=map(In,0,32767,9175,32767);///9175
    VelRef1=VelRef1/655.3;
    In=map(In,0,32767,7300,32767);///9175
    VelRef=In/655.34;
    if (VelRef>=50){
      VelRef=50;
    }
    if (VelRef<=11.14){
      VelRef=11.14;
    }
    
    step1=0;
  }else VelRef=0;
  
  Error1 = Cte*(VelRef - velocidadB);

  if (Control == 1) {
    if (step1 == 0) {
      pid.Initialize(output);
      step1 = 1;   
  }
    
    output = pid.Update(Error1, 55);            //Calcula PID con el Error (el 55 esta deshabilitado por libreria) - Salida PID
  }
  else {
    output = VelRef*19.8;          //19.3                  // La salida esta determinada por la frecuencia aproximada
    step1 = 0;
  }
  pw = output;
  Timer1.pwm(9, pw);
}
