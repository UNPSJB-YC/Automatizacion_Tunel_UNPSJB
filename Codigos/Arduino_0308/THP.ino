void THP() {
  t1 = sensor.readTemperature(); //SHT21
  T1 = t1 + 273.15; //T abs
  h1 = sensor.readHumidity(); //SHT21
  p2 = bme.readPressure(); //BME280
}

void difPresion() {
  ///////////ADS1115///////////////////////////
  adc0 = ads.readADC_SingleEnded(0) * 4.096 / 32768;
  presionSF2 = (adc0 - offsetdf1 - offsetp2) * 1000; ////Presion en PA   res MPX70002dp= 1kPa/V
  ADCFilterM = medianFilter.AddValue(presionSF2);
}

void calculo_offset() {
  if (contador < 35) { //contador para descartar primeros valores
    offsetdf = meanFilter.AddValue(adc0 - offsetdf1);
    contador = contador + 1;
  }
  else {
    if (contador == 35) {
      offsetp2 = offsetdf;
      contador = contador + 1;
    }
  }
}

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
  velocidadA = sqrt((2 * (abs(presionSF21))) / den);  //sin filtro
  velocidadB = sqrt((2 * (abs(ADCFilterM1))) / den);//con filtro
  tiempo = millis();
}
