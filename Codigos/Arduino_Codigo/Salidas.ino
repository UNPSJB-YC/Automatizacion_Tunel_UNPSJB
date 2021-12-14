void salidas() {
  digitalWrite(3,RUNSTOP);
  digitalWrite(4,Encendido);   //Habilito control por Ai1
  //Serial.print(Encendido);Serial.print(" ");Serial.println(pw);
  digitalWrite(Fallaext,FallaExterna);
  digitalWrite(6,Resetfalla);
}
