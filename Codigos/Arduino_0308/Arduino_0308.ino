//Bibliotecas
#include <Arduino.h>
#include <Wire.h>
#include <SoftwareSerial.h>
#include "Filter.h"  //Filtro
#include "MegunoLink.h"
#include "MedianFilterLib.h"  //Filtro de Mediana
#include "MeanFilterLib.h"  //Filtro de Media
#include <TimerOne.h> //Libreria PWM
#include "Adafruit_Si7021.h"
#include <Adafruit_Sensor.h>
#include <Adafruit_BME280.h> //sensor THP
#include <Adafruit_ADS1015.h> //Convertidor analógico digital
#include <Custom_PID_Tunel.h> //Librería modificada por Cristian Yapura

Adafruit_Si7021 sensor = Adafruit_Si7021();//DHT21
Adafruit_BME280 bme; // //BME280
Adafruit_ADS1115 ads;//ADS115

//Variables utilizadas en los filtros
MeanFilter<float> meanFilter(20);
MedianFilter<float> medianFilter(40);

//inicialización de variables
#define Fallaext 5
float t1 = 0 , h1 = 0, p2 = 0; //variables utilizadas en THP
float presionSF2 = 0, presionSF21, ADCFilterM = 0, ADCFilterM1; /////Variables Dif - Presion//////////
float velocidadA = 0, velocidadB = 0; //////Variables velocidad/////////////
float adc0 = 0, offsetp2 = 0, offsetdf = 0, offsetdf1 = 2.592;
float entrada1[30], entrada2[30];
float Inref1, In = 0;
float VelRef = 0, VelRef1 = 0; //PID
int entrada[7], Inref, len, inc = 0, inc1 = 1, contserie = 0, contador = 0;
long tiemporead = 0, vtiempoant, tiempoautomatico, time1, time2, time3, tiemporetardo;
double output = 0; //PID
boolean paro, BOT = 0, BOT2 = 0, step1 = 0, Estado = 0, Errorvar = 0, pulsador, Estado1 = 0, Errorvar1 = 0, Estadoant = 0, Errorvarant = 0;
boolean EnableAi1 = 0, RUNSTOP = 0, Control = 0, FallaExterna = 0, Resetfalla = 0, Encendido;
bool ControlAutomatico = 0, cambio = 0, cambio1 = 0, terminoautoma = 0;
String data; //Guardo los datos del buffer para utilizar en funcion princ.

///Constantes para cálculo de densidad//
float a0 = 0.00000158123, a1 = -0.000000029331, a2 = 0.00000000011043;
float b0 = 0.000005707, b1 = -0.00000002051, c0 = 0.00019898, c1 = -0.000002376;
float d = 0.0000000000183, e = -0.00000000765, A = 0.000012378847, B = -0.019121316;
float C = 33.93711047, D = -6343.1645, alfa = 1.00062, beta = 0.0000000314, gamma = 0.00000056;
float T1; //temperatura en absoluto
float fpt, psv, xv, Z, den;

//PID
PID pid(0.6846, 0.4183, 0);
float Error1 = 0;
long tiempo = 0;
int pw = 0;
float Cte = 56.88;

void setup() {
  Serial.begin(115200);
  if (!sensor.begin()) {//SHT21
    Serial.println("Problema sensor - Si7021");
    while (true);
  }
  if (!bme.begin(0x76)) {//BME280
    Serial.println("Problema sensor - BME280");
    while (1);
  }
  ads.setGain(GAIN_ONE);// 1x gain   +/- 4.096V  1 bit = 2mV      0.125mV
  ads.begin(); //ADS1115
  /////Inicializo Entradas/Salidas
  pinMode(2, INPUT_PULLUP);
  pinMode(3, OUTPUT);
  pinMode(4, OUTPUT);
  pinMode(Fallaext, OUTPUT);
  pinMode(6, OUTPUT);
  pinMode(7, INPUT_PULLUP);
  pinMode(13, OUTPUT);
  ///Inicializo pwm//
  Timer1.initialize(90);
}

void loop() {
  THP();
  difPresion();
  vel_tiempo();
  if (terminoautoma == 1 & (millis() - tiempoautomatico) >= 3500) {
    cambio_automatico();
  }
  PIDS();
  entradas();
  salidas();
  pulsador = digitalRead(8);
  imprimir_datos();
}


void serialEvent() {
  if (Serial.available() > 0) { // Lectura del puerto mientras sigue abierto
    data = Serial.readStringUntil('\n');                   // Lectura del dato hasta el line feed
    int n, n1 = 0, n2 = 0; // Variables para algoritmo de lectura

    for (int i = 0; i <= data.length(); i++) { // Lectura total del tamano del dato
      if (data.substring(i, i + 1) == ",") {  // Lectura del dato hasta encontrar el caracter ","
        if (n1 == 0) {
          entrada[n1] = data.substring(0, i).toInt();
          n1 = n1 + 1;
          n = i + 1;
        }
        else {
          entrada[n1] = data.substring(n, i).toInt();
          n1 = n1 + 1;
          n = i + 1;    // Posicion de la letra final leida + 1
        }
      }
    }
    n = 0;
    ///////////////////STRING A FLOAT////////////
    for (int i = 0; i <= data.length(); i++) { // Lectura total del tamano del dato
      if (data.substring(i, i + 1) == ",") {  // Lectura del dato hasta encontrar el caracter ","
        if (n2 == 0) {
          entrada2[n2] = data.substring(0, i).toFloat();
          n2 = n2 + 1;
          n = i + 1;
        }
        else {
          entrada2[n2] = data.substring(n, i).toFloat();
          n2 = n2 + 1;
          n = i + 1;   // Posicion de la letra final leida + 1
        }
      }
    }
    if (entrada[6] != 0) {
      int j = 0;
      for (int i = 7; i <= data.length(); i++) {
        entrada1[j] = entrada2[i];
        j++;
      }
      tiempoautomatico = millis();
      cambio = 1;
      terminoautoma = 1;
      len = entrada[6];
      ControlAutomatico = 1;
    } else {
      terminoautoma = 0;
      cambio = 0;
      ControlAutomatico = 0;
    }

  }
}
