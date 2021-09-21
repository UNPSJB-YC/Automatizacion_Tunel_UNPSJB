void cambio_automatico() {

  if (cambio1 == 0) {
    entrada[3] = 1; //Encendido = 1;   Habilito Ai1
    if ((millis() - tiempoautomatico) >= 4000) {
      entrada[0] = 1;
    } //RUNSTOP = 1;    Doy Marcha
    if ((millis() - tiempoautomatico) >= 9000) {
      entrada[2] = 1;
      cambio1 = 1;
      inc = 0;
      inc1 = 1;
    }
  }
  else {

    if  (inc1 <= len - 1) {
      if (cambio == 1) {
        Inref1 = entrada1[inc];
        vtiempoant = millis();
        cambio = 0;
      }
      if ((millis() - vtiempoant) >= entrada1[inc1] * 1000) {
        inc = inc + 2;
        inc1 = inc1 + 2;
        cambio = 1;
        tiemporetardo = millis();
      }
    } else {
      entrada[0] = 0; //RUNSTOP = 0;   Apago Motor
      entrada[2] = 0;
      Control = 0;
      if ((millis() - tiemporetardo) > 3000) {
        entrada[3] = 0; //Encendido = 0;  Deshabilito Ai1
        terminoautoma = 0;
        cambio1 = 0;
        cambio = 0;
        ControlAutomatico = 0;
      }
    }
  }
}
