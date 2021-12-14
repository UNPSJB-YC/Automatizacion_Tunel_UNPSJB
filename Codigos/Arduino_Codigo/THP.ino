void THP(){

  ////////SHT21/////////////////////////
  t1 = sensor.readTemperature();
  T1 = t1 + 273.15; //OJO QUE ESTÁ DEFINIDO COMO T1=290.85; EN EL PRINCIPAL
  h1 = sensor.readHumidity();

  /////////BME280///////////////////////
  t2 = bme.readTemperature();
  h2 = bme.readHumidity();
  p2 = bme.readPressure();

}
