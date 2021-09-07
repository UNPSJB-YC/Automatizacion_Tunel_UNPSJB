void imprimir_datos() {

  Serial.print(velocidadB, 3); Serial.print(";");//Velocidad del Aire con Filtro Mediana
  if (Control == 1){
  Serial.print(VelRef); Serial.print(";");           //Velocidad de referencia o frecuencia aproximada.(dependiendo el control)
  }
  else{Serial.print(VelRef1); Serial.print(";");}
  
  //Serial.print(Estado, 3); Serial.print(";");//Velocidad del Aire con Filtro Mediana
  //Serial.print(Errorvar); Serial.print(";");           //Velocidad de referencia o frecuencia aproximada.(dependiendo el control)


  Serial.print(ADCFilterM, 3); Serial.print(";"); //Diferencia de Presion - Filtro Mediana

  Serial.print(pw); Serial.print(";");       //PWM -- Variable de control
  
  Serial.print(tiempo); Serial.print(";"); 
  
  Serial.print(t1, 3); Serial.print(";");
  Serial.print(h1, 3); Serial.print(";");
  Serial.print(p2, 3); Serial.print(";");
  
  Serial.print(den, 4); Serial.print(";");    //Densidad

  Serial.print(Control); Serial.print(";");     //Variable indicacion de control activado
  
  Serial.print(Error1); Serial.print(";");  //Error - Diferencia entre velocidad del aire y velocidad de referencia

  Serial.print(Estado); Serial.print(";"); //Estado del variador (encendido o apagado)
  Serial.print(Errorvar); Serial.print(";");  //Indicacion de error en variador
  Serial.print(ControlAutomatico); Serial.println(";");  //Indicacion velocidad automatica activado
  
  digitalWrite(13,Control);


}
