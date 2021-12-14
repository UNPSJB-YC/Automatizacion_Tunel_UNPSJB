void vel_tiempo() {
  /////////Calculo densidad////////////////////
  fpt = alfa + beta * p2 + gamma * pow(17, 2);
  psv = 1 * exp(A * (pow(T1, 2)) + B * T1 + C + D / T1);
  xv = (h1 / 100) * fpt * psv / p2;
  Z = 1 - (p2 / T1) * (a0 + a1 * t1 + a2 * pow(t1, 2) + (b0 + b1 * t1) * xv + (c0 + c1 * t1) * pow(xv, 2)) + (d + e * pow(xv, 2)) * pow((p2 / T1), 2);
  den = 0.00348374 * p2 * (1 - 0.378 * xv) / (Z * T1);
  //////////Calculo velocidad////////////////
  if (ADCFilterM < 0) {
    ADCFilterM1 = 0;
  } else ADCFilterM1 = ADCFilterM;
  if (presionSF2 < 0) {
    presionSF21 = 0;
  } else presionSF21 = presionSF2;
  
  velocidadA = sqrt((2 * (abs(presionSF21))) / den);
  velocidadB = sqrt((2 * (abs(ADCFilterM1))) / den);
  //velocidadB = analogRead(A0);
  //velocidadB = velocidadB/60.17;

  /////////////////////////////////////////
  tiempo = millis();

}
