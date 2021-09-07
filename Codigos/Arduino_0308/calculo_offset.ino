void calculo_offset() {
  if (contador < 35) { 
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
