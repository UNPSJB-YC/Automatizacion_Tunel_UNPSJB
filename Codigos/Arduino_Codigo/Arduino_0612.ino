//Librerías
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
#include <Custom_PID_Tunel.h> //Librería modificada
//SoftwareSerial BT(2,3);
#define Fallaext 5
//inicialización de variables
//variables utilizadas en PHT
float t1=0,t2=0;
float h1=0,h2=0;
float p2=0;
//////DHT21//////////////////////////
Adafruit_Si7021 sensor = Adafruit_Si7021();
//////BME280//////////////////////////
Adafruit_BME280 bme; // I2C
////////ADS115////////////////////////
Adafruit_ADS1115 ads;


/////Variables Dif - Presion//////////
float presionSF2=0,presionSF21;
float ADCFilterM=0,ADCFilterM1;
float presionCF21=0;
float adc0=0;

//Variables utilizadas en los filtros
MeanFilter<float> meanFilter(20);
MedianFilter<float> medianFilter(40);
ExponentialFilter<float> ADCFilter1(35,0);
float Y=0.0;
float alpha1=0.2;
float S21=Y;


//////Variables velocidad/////////////
float velocidadA=0,velocidadB=0,velocidadC=0,velocidadD=0;
int contador=0;
float offsetp2=0,offsetdf=0,offsetdf1=2.592;
//////////////////////////////////////
///Constantes Densidad/////
float a0=0.00000158123,a1=-0.000000029331,a2=0.00000000011043;
float b0=0.000005707,b1=-0.00000002051,c0=0.00019898,c1=-0.000002376;
float d=0.0000000000183,e=-0.00000000765,A=0.000012378847,B=-0.019121316;
float C=33.93711047,D=-6343.1645,alfa=1.00062,beta=0.0000000314,gamma=0.00000056;

float T1; ////////////////////////////////////////////////////////OJO QUE ESTÁ DEFINIDO EN THP IGUAL
float fpt,psv,xv,Z,den;

boolean paro, BOT=0,BOT2=0,step1=0,Estado=0,Errorvar=0,pulsador,Estado1=0,Errorvar1=0,Estadoant=0,Errorvarant=0;
long tiemporead=0;
int entrada[7], Inref,len;
bool ControlAutomatico=0;
float entrada1[60];// {30000,5000,20000,5000,15000,5000,28020,5000};
float entrada2[60];
bool cambio=0,cambio1=0,terminoautoma=0;
int inc=0,inc1=1;
float Inref1;
long vtiempoant,tiempoautomatico;
boolean EnableAi1=0,RUNSTOP=0,Control=0,FallaExterna=0,Resetfalla=0,Encendido;
float In=0;
long time1, time2,time3,tiemporetardo;
int contserie=0;
//////////////////////////
/////Variables Entrada Serial/////
String data; //Guardo los datos del buffer para utilizar en funcion princ.
////////PID////////
double output=0;
float VelRef=0,VelRef1=0;
// variables externas del controlador


PID pid(0.6846, 0.4183, 0);
float Error1=0;

long tiempo=0;
int pw=0,pwi=0;
float Cte=56.88;

void setup() {
  Serial.begin(115200);
  /////SHT21/////////////////
  if (!sensor.begin()) {
    Serial.println("Problema sensor - Si7021");
    while (true);
  }
  /////BME280////////////////////
  if (!bme.begin(0x76)) {
    Serial.println("Problema sensor - BME280");
    while (1);
  }
  /////ADS1115///////////////////////
  ads.setGain(GAIN_ONE);        // 1x gain   +/- 4.096V  1 bit = 2mV      0.125mV
  ads.begin();
  /////Inicializo Entradas/Salidas  
  pinMode(2,INPUT_PULLUP);
  pinMode(3,OUTPUT);
  pinMode(4,OUTPUT);
  pinMode(Fallaext,OUTPUT);
  pinMode(6,OUTPUT);
  pinMode(7,INPUT_PULLUP);
  pinMode(13,OUTPUT);
  ///Inicializo pwm//
  Timer1.initialize(90);
}

void loop(){
  
  THP(); 
  difPresion();
  vel_tiempo();
  if (terminoautoma==1 & (millis()-tiempoautomatico)>=3500){
  
  cambio_automatico();}
  PIDS();
  entradas();
  salidas();
  pulsador = digitalRead(8);
  //if (contserie>=4){ 
  imprimir_datos(); 
  
  //contserie=0;}
  //contserie++;
}



void serialEvent() {

  if(Serial.available() > 0){ // Lectura del puerto mientras sigue abierto
     data = Serial.readString();//('\n');                   // Lectura del dato hasta el line feed 
     //Serial.print("Dato original: "); Serial.println(data); // Muestra del dato original
     int largovector=sizeof(entrada)/sizeof(int);
     int n,n1=0,n2=0; // Variables para algoritmo de lectura
//Serial.println(data.length());
     for (int i = 0; i < data.length(); i++){ // Lectura total del tamano del dato
       if (data.substring(i, i+1) == ","){     // Lectura del dato hasta encontrar el caracter ","
         if (n1==0){
          entrada[n1] = data.substring(0, i).toInt();
          n1=n1+1;
          n = i + 1; 
         }
         else{
          if (n1>=largovector){
            break;}
          else{
         entrada[n1] = data.substring(n, i).toInt();
         n1=n1+1;
         n = i + 1;                            // Posicion de la letra final leida + 1
       }}}
     }
     
     
//     for (int i = 0; i < largovector; i++){
//     Serial.println(entrada[i]);}
     n=0;
/////////////////STRING A FLOAT////////////
     for (int i = 15; i <= data.length(); i++){ // Lectura total del tamano del dato
       if (data.substring(i, i+1) == ","){     // Lectura del dato hasta encontrar el caracter ","
         if (n2==0){
          if (entrada[6]>=10){
          entrada2[n2] = data.substring(15, i).toFloat();}
          else{entrada2[n2] = data.substring(14, i).toFloat();}
          //Serial.println(entrada2[n2]);
          n2=n2+1;
          n = i + 1; 
          //Serial.println(entrada2[0]);
         }
         else{
         entrada2[n2] = data.substring(n, i).toFloat();
         //Serial.println(entrada2[n2]);
         n2=n2+1;
         n = i + 1;                            // Posicion de la letra final leida + 1
       }}
     }
/////////////////////////////////////////
     if (entrada[6]!=0){
      int j = 0;
      for (int i = 0; i <= data.length();i++){
        if (i>=entrada[6]){
            break;}
        else{
        entrada1[j] = entrada2[i];
        //Serial.println(entrada1[j]);
        j++;
      }}
//      Serial.print(entrada1[0]);Serial.print(" ");
//      Serial.print(entrada1[1]);Serial.print(" ");
//      Serial.print(entrada1[2]);Serial.print(" ");
//      Serial.print(entrada1[3]);Serial.println(" ");

      tiempoautomatico=millis();
      cambio=1;
      terminoautoma=1;
      //len = data.length()-7;
      len = entrada[6];
      //Serial.println(len);
      ControlAutomatico = 1;
     }else {
      terminoautoma = 0;
      cambio = 0;
      ControlAutomatico = 0;
      }
  
}}

//Bibliografía
//FiltroA https://www.megunolink.com/documentation/arduino-libraries/exponential-filter/
//FiltroB   https://www.youtube.com/watch?v=QGDG5v_UnIk
//#include <> "" https://es.stackoverflow.com/questions/1959/da-igual-usar-include-iostream-o-include-iostream/1960
