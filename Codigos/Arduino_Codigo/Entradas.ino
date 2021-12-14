void entradas(){

  ////FISICAS///
  if ((millis()-tiemporead)>300){
  Estado1 = !digitalRead(2);
  Errorvar1= !digitalRead(7);
  tiemporead = millis();

  if (Estado1 == 1 && Estadoant == 1){
    Estado = 1;
  }
  if (Estado1 == 0 && Estadoant == 0){
        Estado = 0;
        }

  if (Errorvar1 == 1 && Errorvarant == 1){
      Errorvar = 1;    
  }
  if (Errorvar1 == 0 && Errorvarant == 0){
      Errorvar = 0;
      }
      Estadoant = Estado1;
      Errorvarant=Errorvar1;
    
  }

  if (Errorvar == 1){

      entrada[0] = 0; //RUNSTOP = 0;     //Apago Motor
      entrada[3] = 0; //Encendido = 0;   //Deshabilito Ai1
      entrada[2] = 0;
      terminoautoma = 0;
      cambio = 0;
      ControlAutomatico = 0;
  }

  /////Serial/////
  RUNSTOP=entrada[0];       //Variable Marcha- Parada
  Inref=entrada[1];         //Variable referencia velocidad o frecuencia aprox.
  Control=entrada[2];       //Control activado/desacivado
  Encendido=entrada[3];     //ON - OFF . Habilitacion Ai1
  Resetfalla=entrada[4];    //Reset falla
  FallaExterna=entrada[Fallaext];  //Falla externa
}
