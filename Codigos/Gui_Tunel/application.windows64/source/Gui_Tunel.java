import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import controlP5.*; 
import grafica.*; 
import processing.serial.*; 
import java.util.Random; 
import java.util.*; 
import java.awt.Frame; 
import java.awt.BorderLayout; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Gui_Tunel extends PApplet {

/*   =================================================================================       
GUI realizada para proyecto final "Automatización del túnel de viento UNPSJB"
de la carrera de ingeniería electrónica de la 
Universidad Nacional de la Patagonia San Juan Bosco (UNPSJB.)

Autores:
Caamiña Daniela
Yapura Cristian
Repositorio: https://github.com/UNPSJB-YC/Automatizacion_Tunel_UNPSJB
=================================================================================*/   

// BIBLIOTECAS





//gui
//gui

CColor ccS = new CColor((0xffAA0000),(0xffAA0000),(0xffAAAAAA),(0xffAAAAAA),(0xff000000));
CColor cc = new CColor((0xff5DD2EA),color(248,240,248),(0xff002D5A),color(248,240,248),color(0,0,255));
CColor ccONOFF = new CColor((0xff5DD2EA),(0xff009900),(0xff000000),(0xff0000ff),(0xff000000));

ControlP5 cp5;

// Settings for the plotter are saved in this file
JSONObject plotterConfigJSON;

//grafica en tiempo real
Graph LineGraph = new Graph(70,  115, 500, 400, color(20, 20, 200));
float[][] lineGraphValues = new float[6][100];
float[] lineGraphSampleNumbers = new float[100];
int[] graphColors = new int[6]; //grafica en tiempo real
String topSketchPath = "";

//serial
Serial Arduino;
String puerta;
List l; 
int baudrate = 115200; 

//variables
String textV, textF;// usado para ingresar número de vel o frec
String text2;// nombre del texto del archivo .csv
Float textVV, textFF;// usado para ingresar número de vel o frec
int textVVV, textFFF;// usado para ingresar número de vel o frec
int unicavez;// contadores
boolean unicavez2 = false,unicavez3 = false; //contadores
int variablecontrol1;
long tiempo1,tiempo2; //contadores
float tiempo3;//contadores
float[] algo;
Table table;
String val,val1;
byte[] inBuffer = new byte[500]; // holds serial message
String[] nums;
int tomomuestra = 0;
int colfun; //color para autofun
String myString = "";
float tiempo = 0,t = 0,h = 0,p = 0,d = 0,dp = 0,VelRef = 0,v1 = 0,pwm = 0,controlSINO = 0, error = 0, Ev = 0, ErrorVariador = 0, ControlAutomatico = 0;
boolean ONOFF = false;
boolean Corre = false;
boolean Parada = true;
boolean ERROR = true;
boolean ControlONOFFnueva;
String fitem;//autofun
Table table1;
int colorONOFF;
boolean Run = false;
boolean Stop = true;
boolean DD4 = false;
boolean DD5 = false;
boolean DD6 = false;
boolean reinicioreset = false;
boolean HabilitacionV;
int contgraf = 0;
int a = 0;

//inicializacion de elementos de la biblioteca controlP5.
Textlabel TL1,TL2,TL3,TL4,TL5,TL6,TL7,TL77,TL8,TL9,TL99,TL10,TL11,TL12,TL13,TL14,TL15,TL16,TL17, Tx3, Tx4,TL144,TL155,TL166,TL177, TL18, TL19, TL20, TL21, TL22, TL23;
Textfield TF1,TF11, TF2,TF3,TF4,TF5,TF6,TF7,TF8;
Button TB2, TB22, TB3,TB4,TB5,TB10;
controlP5.Button TB11;
Toggle TB6,TB7,TB8,TB9;
ScrollableList ddl1; //autofun
Button ICO;//autofun
ScrollableList SL, autoLista;   
Icon ser, serono, TODOonoff, TB1, TB12, TB13;
//envío de datos a Arduino
String Dato0 = "0",Dato1 = "0", Dato2 = "0", Dato3 = "0", Dato4 = "0", Dato5 = "0", Dato6 = "0" ,Dato7 = "",DatosWrite, DatosWrite2;


public void setup() {
    
    surface.setTitle("Automatización Túnel UNPSJB");
               // Configura resolucion interfaz
    ControlONOFFnueva = false;
    unicavez = 0;
    variablecontrol1 = 0;
    println(unicavez);
    cp5 = new ControlP5(this);
    PFont font = createFont("arial",20);
    PFont font2 = createFont("arial",14);
    PFont font3 = createFont("arial",14);  
    
    //Serial
    l = Arrays.asList(Serial.list());
    SL = cp5.addScrollableList("dropdown").setPosition(690,40).setSize(150, 120).setBarHeight(20).setFont(font3).setItemHeight(20).addItems(l).setOpen(false).setLabel("Elija puerto...");
    cp5.get(ScrollableList.class, "dropdown").removeItem("COM1");
    ser = cp5.addIcon("actualizar",10).setPosition(850,30).setSize(50,50).setRoundedCorners(20).setFont(createFont("fontawesome-webfont.ttf", 40)).setFontIcon(0xff00f021).setColorBackground(color(255,100)).hideBackground();
    serono = cp5.addIcon("seronoser",10).setPosition(900,30).setSize(70,50).setRoundedCorners(20).setFont(createFont("fontawesome-webfont.ttf", 40)).setFontIcons(0xff00f205,0xff00f204).setSwitch(true).setColorBackground(color(255,100)).hideBackground();
    //fin serial
    
    //Elementos texto, botones, etc
    cp5.addButton("buttonA").setPosition(10,10).setImage(loadImage("unpsjb.png"));
    cp5.addTextlabel("label1").setText("Automatización túnel de viento").setPosition(70,30).setColorValue(0xff03045e).setFont(createFont("Arial",25));
    TL1 = cp5.addTextlabel("label12").setText("Puerto:").setPosition(620,35).setColorValue(0xff002D5A).setFont(createFont("Arial",20));
    TL2 = cp5.addTextlabel("label2").setText("Parametros actuales").setPosition(690,155).setColorValue(0xff002D5A).setFont(createFont("Arial",20));
    TL3 = cp5.addTextlabel("label3").setText("T:           °C").setPosition(602,205).setColorValue(0xff002D5A).setFont(createFont("Arial",20));
    TL4 = cp5.addTextlabel("label4").setText("H:          %").setPosition(743,205).setColorValue(0xff002D5A).setFont(createFont("Arial",20));
    TL5 = cp5.addTextlabel("label5").setText("P:           hPa").setPosition(862,205).setColorValue(0xff002D5A).setFont(createFont("Arial",20));
    TL6 = cp5.addTextlabel("label6").setText("v:           m/s").setPosition(642,245).setColorValue(0xff002D5A).setFont(createFont("Arial",20));
    TL7 = cp5.addTextlabel("label7").setText("v(ref):           m/s").setPosition(790,245).setColorValue(0xff002D5A).setFont(createFont("Arial",20));
    TL77 = cp5.addTextlabel("label77").setText("f(aprox):           Hz").setPosition(770,245).setColorValue(0xff002D5A).setFont(createFont("Arial",20));
    TL8 = cp5.addTextlabel("label8").setText("Control").setPosition(610,460).setColorValue(0xff002D5A).setFont(createFont("Arial",20));
    TB1 = cp5.addIcon("ControlONOFF",10).setPosition(705,465).setSize(25, 15).setRoundedCorners(20).setFont(createFont("fontawesome-webfont.ttf", 40)).setFontIcons(0xff00f205,0xff00f204).setSwitch(true).setColorBackground(color(255,100)).hideBackground();
    TL9 = cp5.addTextlabel("label10").setText("Velocidad: ").setPosition(640,495).setColorValue(0xff002D5A).setFont(createFont("Arial",20));
    TF1 = cp5.addTextfield("Veloc").setPosition(744,495).setSize(100, 30).setAutoClear(false).setColor(cc).setFont(font).setLabel("");
    TB2 = cp5.addButton("EnviarVel").setPosition(744 + 100,495).setSize(85, 30).setLabel("Enviar").setFont(font2); //.toInt ver
    TL99 = cp5.addTextlabel("label110").setText("Frecuencia: ").setPosition(635,495).setColorValue(0xff002D5A).setFont(createFont("Arial",20));
    TF11 = cp5.addTextfield("Frec").setPosition(744,495).setSize(100, 30).setAutoClear(false).setColor(cc).setFont(font).setLabel("");
    TB22 = cp5.addButton("EnviarFrec").setPosition(744 + 100,495).setSize(85, 30).setLabel("Enviar").setFont(font2); //.toInt ver
    TL10 = cp5.addTextlabel("label9").setText("Guardado").setPosition(730,560).setColorValue(0xff002D5A).setFont(createFont("Arial",20));
    TL11 = cp5.addTextlabel("label11").setText("Archivo: ").setPosition(640,590).setColorValue(0xff002D5A).setFont(createFont("Arial",20));
    TF2 = cp5.addTextfield("Nombre archivo").setPosition(720,590).setSize(160, 30).setAutoClear(false).setColor(cc).setFont(font).setLabel("");
    TB3 = cp5.addButton("Guardar").setPosition(720 + 160,590).setSize(85, 30).setFont(font2); 
    TB4 = cp5.addButton("Run").setPosition(620,310).setSize(85, 85).setLabel("RUN").setFont(font2).setColorActive(0xff009900);
    TB5 = cp5.addButton("Stop").setPosition(733,310).setSize(85, 85).setLabel("STOP").setFont(font2).setColorActive(0xff990000);
    TL20 = cp5.addTextlabel("estado de motor").setText("").setPosition(640,410).setColorValue(0xff002D5A).setFont(createFont("Arial",20));
    TB10 = cp5.addButton("reset").setPosition(910,75).setSize(60, 25).setLabel("RESET").setFont(createFont("Arial",14)).setColorActive(0xff990000);
    TB11 = cp5.addButton("fallaext").setPosition(910,105).setSize(60, 35).setLabelVisible(false).setFont(createFont("Arial",14)).setColor(ccS);
    TL21 = cp5.addTextlabel("emerg").setText("   STOP \n EMERG").setPosition(905,105).setColorValue(0xffAAAAAA).setFont(createFont("Arial",14));
    TL18 = cp5.addTextlabel("valiTL18").setText("ingrese un numero valido").setPosition(725,525).setColorValue(0xff770000).setFont(createFont("Arial",15));
    TB12 = cp5.addIcon("Habilitacion",10).setPosition(920,410).setSize(25, 15).setRoundedCorners(20).setFont(createFont("fontawesome-webfont.ttf", 40)).setFontIcons(0xff00f205,0xff00f204).setSwitch(true).setColorBackground(color(255,100)).hideBackground();
    TL19 = cp5.addTextlabel("Habili").setText("Ai1").setPosition(920,380).setColorValue(0xff002D5A).setFont(createFont("Arial",20));
    TB13 = cp5.addIcon("autoFun",10).setPosition(920,350).setSize(25, 15).setRoundedCorners(20).setFont(createFont("fontawesome-webfont.ttf", 40)).setFontIcons(0xff00f205,0xff00f204).setSwitch(true).setColorBackground(color(255,100)).hideBackground();
    TL22 = cp5.addTextlabel("autoFuncion").setText("AutoFuncion").setPosition(870,320).setColorValue(0xff002D5A).setFont(createFont("Arial",20));
    
    List funciones = Arrays.asList("FUN_A.csv","FUN_B.csv","FUN_C.csv");
    ddl1 = cp5.addScrollableList("Settings").setPosition(750,460).setSize(150, 300).setItemHeight(30).setBarHeight(30).setOpen(false).setFont(font3).setLabel("Elija archivo").addItems(funciones);
    ICO = cp5.addButton("ICONO").setPosition(902,460).setSize(85, 30).setLabel("Abrir").setFont(font2); //.toInt ver
    TL23 = cp5.addTextlabel("autoFunciontex").setText("Elija un archivo donde \n   estén los datos de\n       los escalones").setPosition(595,475).setColorValue(0xff002D5A).setFont(createFont("Arial",14));
    
    table = new Table(); //realizo una tabla que luego se guardará en csv.
    table.addColumn("Muestra");
    table.addColumn("Tiempo");
    table.addColumn("Temp");
    table.addColumn("Hum");
    table.addColumn("Pres");
    table.addColumn("Den");
    table.addColumn("DP");
    table.addColumn("Ref");
    table.addColumn("VelFrec");
    table.addColumn("PWM");
    table.addColumn("Control");
    table.addColumn("Error");
    table.addColumn("ESTADOvariador"); //motor funcionando
    table.addColumn("ERRORvariador");
    table.addColumn("TiempoRel");
    table.addColumn("ControlAutomatico");
    
    grafica();
}


public void draw() {
    background(248,240,248);  //Configura color fondo
    graf_draw();
    fill(255);//cuadros con fondo
    stroke(0xff5DD2EA);
    rect(590,35,400,110);//serial
    if (serono.isOn() == true) {
        fill(colorONOFF);//cuadros con fondo
        noStroke();
        rect(620,400,200,50);//Prendido o apagado motor
        fill(255);//cuadros con fondo
        stroke(0xff5DD2EA);
        rect(590,155,400,145);//Parametros actuales
        rect(626,206,53,30);//T
        rect(767,206,53,30);//H
        rect(885,206,53,30);//P
        rect(668,246,53,30);//v
        rect(850,246,53,30);//vref
        rect(590,455,400,90);//control
        rect(590,560,400,80);//guardado
        TL2.setVisible(true);
        TL3.setVisible(true);        TL4.setVisible(true);
        TL5.setVisible(true);        TL6.setVisible(true);
        TL10.setVisible(true);        TL11.setVisible(true);
        TF2.setVisible(true);        TB3.setVisible(true);
        TL12.setVisible(true);        TL13.setVisible(true);
        TL14.setVisible(true);        TL15.setVisible(true);
        TL16.setVisible(true);        TL17.setVisible(true);
        TL144.setVisible(true);        TL155.setVisible(true);
        TL166.setVisible(true);        TL177.setVisible(true);
        TF3.setVisible(true);        TF4.setVisible(true);
        TF5.setVisible(true);        TF6.setVisible(true);
        TF7.setVisible(true);        TF8.setVisible(true);
        TB6.setVisible(true);        TB7.setVisible(true);
        TB8.setVisible(true);        TB9.setVisible(true);
        Tx3.setVisible(true);        Tx4.setVisible(true);
        TB12.setVisible(true);        TL19.setVisible(true);
        TB11.setVisible(true);      TL21.setVisible(true);
        textSize(15);
        fill(0xff002D5A);
        text(nf(t,0,2), 669,226);
        text(nf(h,0,2), 813,226);
        text(nf(p,0, -1), 935,226);
        text(nf(v1,0,1), 715,266);
        text(nf(VelRef,0,2), 900,266);
        
        if (TB12.isOn() ==  true) { //ai1
            TB4.setVisible(true);           TB5.setVisible(true);
            if (TB1.isOn() ==  true) { //control onoff
                TL9.setVisible(true);        TF1.setVisible(true);
                TB2.setVisible(true);        TL7.setVisible(true);
                TL99.setVisible(false);        TF11.setVisible(false);
                TB22.setVisible(false);        TL77.setVisible(false);  
            }
            else{
                TL9.setVisible(false);        TF1.setVisible(false);
                TB2.setVisible(false);        TL7.setVisible(false);
                TL99.setVisible(true);        TF11.setVisible(true);
                TB22.setVisible(true);        TL77.setVisible(true);
            }
            TB13.setVisible(false);         TL22.setVisible(false); //boton de autofuncion 
        }   
        else{
            if (TB1.isOn() ==  true) {
                TL9.setVisible(false);        TF1.setVisible(false);
                TB2.setVisible(false);        TL7.setVisible(true);
                TL99.setVisible(false);        TF11.setVisible(false);
                TB22.setVisible(false);        TL77.setVisible(false); 
            }
            else{
                TL9.setVisible(false);        TF1.setVisible(false);
                TB2.setVisible(false);        TL7.setVisible(false);
                TL99.setVisible(false);        TF11.setVisible(false);
                TB22.setVisible(false);        TL77.setVisible(true);
            }
            TB13.setVisible(true);         TL22.setVisible(true);
        }
        if (TB13.isOn() ==  true) {   //boton de autofuncion 
            TB12.setVisible(false); //apago ai1
            TL19.setVisible(false);//apago ai1
            ddl1.setVisible(true);//autofuncion
            ICO.setVisible(true);//autofuncion
            TL23.setVisible(true);//autofuncion
            TL77.setVisible(false); //no muestro frecuencia   ///esto lo tengo q hacer segun la variable que mande arduino cuando comienza y termina de hacer la autofuncion
            TL7.setVisible(false); //muestro vref
            TB5.setVisible(true);
        }
        else{
            TB12.setVisible(true); //ai1
            TL19.setVisible(true); //ai1
            ddl1.setVisible(false);//autofuncion
            ICO.setVisible(false);//autofuncion
            TL23.setVisible(false);//autofuncion
        }
        if (TB13.isOn() ==  false && TB12.isOn() ==  false) {   
            TB4.setVisible(false);           TB5.setVisible(false);
        }
        if (unicavez2 ==  false) {
            unicavez2 = true;
        }
        if (PApplet.parseInt(Ev) ==  1) {
            colorONOFF = 0xffAA0000;
            TL20.setVisible(true);
            TL20.setText("Motor en Marcha").setColorValue(0xffFFFFFF);
        }
        else{
            colorONOFF = 0xff00AA00;
            TL20.setVisible(true);
            TL20.setText("  Motor Apagado");
        } 
        if (PApplet.parseInt(ErrorVariador) ==  1) {   //agregar la variable de reset del boton para que desaparezca el triangulo y boton
            TB10.setVisible(true);
            fill(255,0,0);
            noStroke();
            triangle(850, 90, 900, 90, 875, 135);
            if (unicavez3 ==  false) {
                TB1.setOff();
                TB12.setOff();
                TB13.setOff();
                unicavez3 = true;
            }
        }
        else{
            TB10.setVisible(false);
            fill(255);
            noStroke();
            triangle(850, 90, 900, 90, 875, 135);
            unicavez3 = false;
        }
        if (PApplet.parseInt(ControlAutomatico) ==  1  && TB1.isOn() ==  false) {//señal visual de que está corriendo la autofuncion
            fill(colfun);//de que envie el archico
            noStroke();
            rect(930,500,30,30);
            colfun = 0xff008800; 
            TL77.setVisible(false); //no muestro frecuencia   ///esto lo tengo q hacer segun la variable que mande arduino cuando comienza y termina de hacer la autofuncion
            TL7.setVisible(true); //muestro vref
        }
        if (PApplet.parseInt(ControlAutomatico) ==  0  && TB1.isOn() ==  false) {
            colfun = 0xff228888;
            TL77.setVisible(true); //no muestro frecuencia   ///esto lo tengo q hacer segun la variable que mande arduino cuando comienza y termina de hacer la autofuncion
            TL7.setVisible(false); //muestro vref
        }
        if (DD4 ==  true) { //reset
            if ((millis() - tiempo2)>3000) {
                
                DD4 = false;
                Dato4 = "0";
                DatosW();
                reinicioreset = false;
            }
        }
    }
    else{ //si esta apagado serial arduino
        TL2.setVisible(false);
        TL3.setVisible(false);        TL4.setVisible(false);
        TL5.setVisible(false);        TL6.setVisible(false);
        TL7.setVisible(false);       TL9.setVisible(false);
        TF1.setVisible(false);        TB2.setVisible(false);
        TL77.setVisible(false);       TL99.setVisible(false);
        TF11.setVisible(false);        TB22.setVisible(false);
        TL8.setVisible(false);        TL10.setVisible(false);
        TL11.setVisible(false);        TF2.setVisible(false);
        TB3.setVisible(false);        TB4.setVisible(false);
        TB5.setVisible(false);        TB1.setVisible(false);
        TL12.setVisible(false);      TL20.setVisible(false);
        TL13.setVisible(false);        TL14.setVisible(false);
        TL15.setVisible(false);        TL16.setVisible(false);
        TL17.setVisible(false);        TL144.setVisible(false);
        TL155.setVisible(false);        TL166.setVisible(false);
        TL177.setVisible(false);        TF3.setVisible(false);
        TF4.setVisible(false);        TF5.setVisible(false);
        TF6.setVisible(false);        TF7.setVisible(false);
        TF8.setVisible(false);        TB6.setVisible(false);
        TB7.setVisible(false);        TB8.setVisible(false);
        TB9.setVisible(false);        Tx3.setVisible(false);
        Tx4.setVisible(false);        TB10.setVisible(false);
        TL18.setVisible(false);        TB12.setVisible(false);
        TL19.setVisible(false);        TB11.setVisible(false);
        TL21.setVisible(false);       TB13.setVisible(false);
        TL22.setVisible(false);       TL23.setVisible(false);
        ddl1.setVisible(false);       ICO.setVisible(false);
    }
}

public void DatosW() {
    DatosWrite = (Dato0 + "," + Dato1 + "," + Dato2 + "," + Dato3 + "," + Dato4 + "," + Dato5 + "," + Dato6 + "," + '\n');
    Arduino.write(DatosWrite);
}

public void verificacion() {
    if (unicavez == 0) {
        variablecontrol1 = PApplet.parseInt(controlSINO);
        tiempo3 = tiempo;
        if (variablecontrol1 == 1) {
            cp5.get(Textfield.class, "Veloc").setText(str(VelRef));
            EnviarVel();
            TB12.setOn();
            TB1.setOn();
        } else {
            TB12.setOff();
            TB1.setOff();
        }
        unicavez =  1;
    } 
}


public void serialEvent(Serial Arduino) {
    if ((millis() - tiempo1)>1000) {
        try {
            val = Arduino.readStringUntil('\n'); //separador de nueva linea 
            if (val!= null) { //verifica q no esta vacio
                if (tomomuestra ==  1) {
                    val1 = val;
                    val = trim(val); //
                    float algo[] = PApplet.parseFloat(split(val, ';')); //separo los datos que recibo por ; y los paso a flotante.
                    tiempo = algo[4];
                    t = algo[5];
                    h = algo[6];
                    p = algo[7] / 100;
                    d = algo[8];
                    dp = algo[2];
                    VelRef = algo[1];
                    v1 = algo[0];
                    pwm = algo[3];
                    controlSINO = algo[9];
                    error = algo[10];
                    Ev = algo[11];//estado del motor
                    ErrorVariador = algo[12]; //indicacion de error en variador
                    ControlAutomatico = algo[13];
                    
                    TableRow newRow = table.addRow(); //crea nueva columna 
                    newRow.setInt("Muestra", table.lastRowIndex());
                    newRow.setFloat("Tiempo", tiempo); //0
                    newRow.setFloat("Temp", t); //1
                    newRow.setFloat("Hum", h);//2
                    newRow.setFloat("Pres", p);//3
                    newRow.setFloat("Den", d);//4
                    newRow.setFloat("DP", dp);//5
                    newRow.setFloat("Ref", VelRef);//6
                    newRow.setFloat("VelFrec", v1);//7 
                    newRow.setFloat("PWM", pwm);//8
                    newRow.setFloat("Control", controlSINO);//9
                    newRow.setFloat("Error", error);//10
                    newRow.setFloat("ESTADOvariador", Ev);//11 //motor funcionando
                    newRow.setFloat("ERRORvariador", ErrorVariador);//12
                    newRow.setFloat("TiempoRel",tiempo - tiempo3);
                    newRow.setFloat("ControlAutomatico",ControlAutomatico);
                    tomomuestra = 0;
                }
                tomomuestra = tomomuestra + 1;
            }
        }
        catch(RuntimeException e) {//catches errors
        } 
    }
}
 
/*   =================================================================================       
    Realiza el gráfico
     =================================================================================*/   

class Graph   {
  boolean Dot=true;            // Draw dots at each data point if true
  boolean RightAxis;            // Draw the next graph using the right axis if true
  boolean ErrorFlag=false;      // If the time array isn't in ascending order, make true  
  boolean ShowMouseLines=true;  // Draw lines and give values of the mouse position
  int     xDiv=5,yDiv=5;            // Number of sub divisions
  int     xPos,yPos;            // location of the top left corner of the graph  
  int     Width,Height;         // Width and height of the graph
  int   GraphColor;
  int   BackgroundColor=color(255);  
  int   StrokeColor=color(180);     
  
  String  Title="Title";          // Títulos
  String  xLabel="x - Label";
  String  yLabel="y - Label";

  float   yMax=1024, yMin=0;      // Default axis dimensions
  float   xMax=10, xMin=0;
  float   yMaxRight=1024,yMinRight=0;
  PFont   Font;                   // Selected font used for text 
     
  Graph(int x, int y, int w, int h,int k) {  // The main declaration function
    xPos = x;
    yPos = y;
    Width = w;
    Height = h;
    GraphColor = k;
  }

     
  public void DrawAxis(){

    fill(BackgroundColor); color(0);stroke(StrokeColor);strokeWeight(1);
    int t=40;
    rect(xPos-t*1.6f,yPos-t*0.8f,Width+t*2,Height+t*1.7f);            // outline
    textAlign(CENTER);textSize(18);
    float c=textWidth(Title);
    fill(BackgroundColor); color(0);stroke(0);strokeWeight(1);
    rect(xPos+Width/2-c/2,yPos-35,c,0);                         // Heading Rectangle  
    
    fill(0);
    textSize(10); noFill(); stroke(0); smooth();strokeWeight(1);
      //Edges
      line(xPos-3,yPos+Height,xPos-3,yPos);                        // y-axis line 
      line(xPos-3,yPos+Height,xPos+Width+5,yPos+Height);           // x-axis line 
      
        stroke(200);
      if(yMin<0){
                line(xPos-7,                                       // zero line 
                      yPos+Height-(abs(yMin)/(yMax-yMin))*Height,   // 
                      xPos+Width,
                      yPos+Height-(abs(yMin)/(yMax-yMin))*Height
                      );
      }
      
      if(RightAxis){                                       // Right-axis line   
          stroke(0);
          line(xPos+Width+3,yPos+Height,xPos+Width+3,yPos);
        }
      
        stroke(0);
            for(int x=0; x<=xDiv; x++){
/*  =========================================================================================
      x-axis
    ==========================================================================================  */
             
            line(PApplet.parseFloat(x)/xDiv*Width+xPos-3,yPos+Height,       //  x-axis Sub devisions    
                 PApplet.parseFloat(x)/xDiv*Width+xPos-3,yPos+Height+5);     

            textSize(15);                                      // x-axis Labels
            String xAxis=str(xMin+PApplet.parseFloat(x)/xDiv*(xMax-xMin));  // the only way to get a specific number of decimals 
            String[] xAxisMS=split(xAxis,'.');                 // is to split the float into strings 
            //text(xAxisMS[0]+"."+xAxisMS[1].charAt(0),          // ...
                // float(x)/xDiv*Width+xPos-3,yPos+Height+15);   // x-axis Labels
          }
/*  =========================================================================================
      left y-axis
    ==========================================================================================  */
          
    for(int y=0; y<=yDiv; y++){
            line(xPos-3,PApplet.parseFloat(y)/yDiv*Height+yPos,                // ...
                  xPos-7,PApplet.parseFloat(y)/yDiv*Height+yPos);              // y-axis lines 
            textAlign(RIGHT);fill(20);
            String yAxis=str(yMin+PApplet.parseFloat(y)/yDiv*(yMax-yMin));     // Make y Label a string
            String[] yAxisMS=split(yAxis,'.');                    // Split string
           
            text(yAxisMS[0]+"."+yAxisMS[1].charAt(0),             // ... 
                 xPos-15,PApplet.parseFloat(yDiv-y)/yDiv*Height+yPos+3);       // y-axis Labels 
             stroke(0);
    }
  }
      
  
/*  =========================================================================================
Streight line graph 
==========================================================================================  */
       
  public void LineGraph(float[] x ,float[] y) {
         for (int i=0; i<(x.length-1); i++){
                    strokeWeight(2);stroke(GraphColor);noFill();smooth();
           line(xPos+(x[i]-x[0])/(x[x.length-1]-x[0])*Width,
                                            yPos+Height-(y[i]/(yMax-yMin)*Height)+(yMin)/(yMax-yMin)*Height,
                                            xPos+(x[i+1]-x[0])/(x[x.length-1]-x[0])*Width,
                                            yPos+Height-(y[i+1]/(yMax-yMin)*Height)+(yMin)/(yMax-yMin)*Height);
         }
  }
}
  
/*   =================================================================================       
   Adquiere los valores que serán representados en tiempo real
     =================================================================================*/   

int i = 0; // loop variable

public void graf_draw(){
  
/* Read serial and update values */
      if (serono.isOn() ==true) {
    String myString = "";
      myString=str(v1)+";" +str(VelRef)+";"+str(dp)+";"+str(pwm)+";";
    String[] nums = split(myString, ';');
  


    for (i=0; i<nums.length; i++) { //nums.length
      // update line graph
      try {
        if (i<lineGraphValues.length) {
          for (int k=0; k<lineGraphValues[i].length-1; k++) {
            lineGraphValues[i][k] = lineGraphValues[i][k+1];

          }

          lineGraphValues[i][lineGraphValues[i].length-1] = PApplet.parseFloat(nums[i])*PApplet.parseFloat(getPlotterConfigString("lgMultiplier"+(i+1)));
        }
      }
      catch (Exception e) {
      }
    }
  

  // draw the bar chart
  background(255); 
 
  // draw the line graphs
  LineGraph.DrawAxis();
  for (int i=0;i<lineGraphValues.length; i++) {
    LineGraph.GraphColor = graphColors[i];
    if (PApplet.parseInt(getPlotterConfigString("lgVisible"+(i+1))) == 1)
      LineGraph.LineGraph(lineGraphSampleNumbers, lineGraphValues[i]);
  }
  
}
}  
/*   =================================================================================       
   Eventos de la gráfica
     =================================================================================*/   
public void setChartSettings() {

  LineGraph.xLabel=" Samples ";
  LineGraph.yLabel="Value";
  LineGraph.Title="";  
  LineGraph.xDiv=10;  
  LineGraph.xMax=0; 
  LineGraph.xMin=-100;  
  LineGraph.yMax=PApplet.parseInt(getPlotterConfigString("lgMaxY")); 
  LineGraph.yMin=PApplet.parseInt(getPlotterConfigString("lgMinY"));
}

// handle gui actions
public void controlEvent(ControlEvent theEvent) {
  if (theEvent.isAssignableFrom(Textfield.class) || theEvent.isAssignableFrom(Toggle.class) || theEvent.isAssignableFrom(Button.class)) {
    String parameter = theEvent.getName();
    String value = "";
    if (theEvent.isAssignableFrom(Textfield.class))
      value = theEvent.getStringValue();
    else if (theEvent.isAssignableFrom(Toggle.class) || theEvent.isAssignableFrom(Button.class))
      value = theEvent.getValue()+"";

    plotterConfigJSON.setString(parameter, value);
    saveJSONObject(plotterConfigJSON, topSketchPath+"/plotter_config.json");
  }
  setChartSettings();
}

// get gui settings from settings file
public String getPlotterConfigString(String id) {
  String r = "";
  try {
    r = plotterConfigJSON.getString(id);
  } 
  catch (Exception e) {
    r = "";
  }
  return r;
}
/*   =================================================================================       
   Elementos de la parte gráfica de tiempo real
     =================================================================================*/   

public void grafica() {
    
    // set line graph colors
    graphColors[0] = color(131, 255, 20);
    graphColors[1] = color(232, 158, 12);
    graphColors[2] = color(255, 0, 0);
    graphColors[3] = color(62, 12, 232);
    
    //settings save file
    topSketchPath = sketchPath();
    plotterConfigJSON = loadJSONObject(topSketchPath + "/plotter_config.json");
    
    
    //init charts
    setChartSettings();
    for (int i = 0; i < lineGraphValues.length; i++) {
        for (int k = 0; k < lineGraphValues[0].length; k++) {
            lineGraphValues[i][k] = 0;
           if (i ==  0)
                lineGraphSampleNumbers[k] = k;
        }
}
    

    
    //build the gui
    int x = 24;
    int y = 105;
    TF3=cp5.addTextfield("lgMaxY").setPosition(475, 575).setLabel("").setText(getPlotterConfigString("lgMaxY")).setWidth(40).setAutoClear(false).setFont(createFont("Arial",15));
    TF4=cp5.addTextfield("lgMinY").setPosition(475, 610).setLabel("").setText(getPlotterConfigString("lgMinY")).setWidth(40).setAutoClear(false).setFont(createFont("Arial",15));
    Tx3=cp5.addTextlabel("yMax3").setText("yMax:").setPosition(415, 570).setColor(0xff002D5A).setFont(createFont("Arial",20));
    Tx4=cp5.addTextlabel("yMin4").setText("yMin:").setPosition(417, 605).setColor(0xff002D5A).setFont(createFont("Arial",20));

    TL14=cp5.addTextlabel("variable1").setText("vel").setPosition(x = 65, y = 554).setColor(0xff002D5A).setFont(createFont("Arial",20));
    TL15=cp5.addTextlabel("variable2").setText("v/f(ref)").setPosition(x = x+80, y).setColor(0xff002D5A).setFont(createFont("Arial",20));
    TL16=cp5.addTextlabel("variable3").setText("dDP").setPosition(x = x+80, y).setColor(0xff002D5A).setFont(createFont("Arial",20));
    TL17=cp5.addTextlabel("variable4").setText("Acc.C").setPosition(x = x+80, y).setColor(0xff002D5A).setFont(createFont("Arial",20)); //accion de control
    TL144=cp5.addTextlabel("variable11").setText("[ m/s ]").setPosition(x = 76, y = 576).setColor(0xff002D5A).setFont(createFont("Arial",14));
    TL155=cp5.addTextlabel("variable22").setText("[m/s - Hz]").setPosition(x = x+80, y).setColor(0xff002D5A).setFont(createFont("Arial",14));
    TL166=cp5.addTextlabel("variable33").setText("[ Pa ]").setPosition(x = x+80, y).setColor(0xff002D5A).setFont(createFont("Arial",14));
    TL177=cp5.addTextlabel("variable44").setText("[ - ]").setPosition(x = x+80, y).setColor(0xff002D5A).setFont(createFont("Arial",14));


    TL12=cp5.addTextlabel("label").setText("I / O").setPosition(x = 12, y = 600).setColor(0xff002D5A).setFont(createFont("Arial",20));
    TL13=cp5.addTextlabel("multipliers").setText("Escala").setPosition(x = 7, y = 620).setColor(0xff002D5A).setFont(createFont("Arial",20));
    TF5=cp5.addTextfield("lgMultiplier1").setPosition(x = 75, y = 625).setLabel("").setText(getPlotterConfigString("lgMultiplier1")).setWidth(40).setAutoClear(false).setFont(createFont("Arial",20));
    TF6=cp5.addTextfield("lgMultiplier2").setPosition(x = x + 80, y).setLabel("").setText(getPlotterConfigString("lgMultiplier2")).setWidth(40).setAutoClear(false).setFont(createFont("Arial",20));
    TF7=cp5.addTextfield("lgMultiplier3").setPosition(x = x + 80, y).setLabel("").setText(getPlotterConfigString("lgMultiplier3")).setWidth(40).setAutoClear(false).setFont(createFont("Arial",20));
    TF8=cp5.addTextfield("lgMultiplier4").setPosition(x = x + 80, y).setLabel("").setText(getPlotterConfigString("lgMultiplier4")).setWidth(40).setAutoClear(false).setFont(createFont("Arial",20));
    TB6=cp5.addToggle("lgVisible1").setPosition(x = 75, y = 602).setLabel("").setValue(PApplet.parseInt(getPlotterConfigString("lgVisible1"))).setColorActive(graphColors[0]);//.setMode(ControlP5.SWITCH)
    TB7=cp5.addToggle("lgVisible2").setPosition(x = x + 80, y).setLabel("").setValue(PApplet.parseInt(getPlotterConfigString("lgVisible2"))).setColorActive(graphColors[1]);
    TB8=cp5.addToggle("lgVisible3").setPosition(x = x + 80, y).setLabel("").setValue(PApplet.parseInt(getPlotterConfigString("lgVisible3"))).setColorActive(graphColors[2]);
    TB9=cp5.addToggle("lgVisible4").setPosition(x = x + 80, y).setLabel("").setValue(PApplet.parseInt(getPlotterConfigString("lgVisible4"))).setColorActive(graphColors[3]);

    }
   
/*   =================================================================================       
   Sección dedicada a las funciones de los distintos laboratorios
     =================================================================================*/   

public void dropdown(int n) { //serial
    puerta = cp5.get(ScrollableList.class, "dropdown").getItem(n).get("name").toString();
}

public void actualizar() {//serial
    cp5.get(ScrollableList.class, "dropdown").clear();
    l = Arrays.asList(Serial.list());
    cp5.get(ScrollableList.class, "dropdown").setItems(l);
    cp5.get(ScrollableList.class, "dropdown").removeItem("COM1");
}

public void seronoser(boolean estado) { //serial
    if (estado ==  true) {
        println("abro puerto" + puerta);
        Arduino = new Serial(this,puerta,baudrate);
        unicavez = 0;
        tiempo1 = millis();  
        Dato0 = "0";
        Dato1 = "0";
        Dato2 = "0";
        Dato3 = "0";
        Dato4 = "0";
        Dato5 = "0";
        Dato6 = "0";
        DatosW();
    }
    
    else{
        println("cierro puerto " + puerta);
        actualizar();
        Corre = false;
        Parada = true;
        unicavez = 0;
        variablecontrol1=0;
        TB12.setOff();
        TB1.setOff();
        TB13.setOff();//autofuuncion
        Dato0 = "0";
        Dato1 = "0";
        Dato2 = "0";
        Dato3 = "0";
        Dato4 = "0";
        Dato5 = "0";
        Dato6 = "0";
        DatosW();
        Arduino.clear();
        Arduino.stop();
    }
}

public void Run() { //encendido del motor
    if (serono.isOn() ==  true) {
        Corre = true;
        Parada = false;
        Dato0 = "1";
        DatosW();
    }
}

public void Stop() {//apagado del motor
    if (serono.isOn() ==  true) {  
        Corre = false;
        Parada = true;
        Dato0 = "0";
        DatosW();
    }
}

public void EnviarVel() { //envio el valor de la velocidad
    textV = cp5.get(Textfield.class, "Veloc").getText();
    textVV = PApplet.parseFloat(textV);
    if (4.7f < textVV && textVV < 17.05f) {
        textVV = map(textVV,4.7f,17,0,32767);
        textVVV = PApplet.parseInt(textVV);
        Dato1 = str(textVVV);
        DatosW();
        TL18.setVisible(false);
    }
    else{
        TL18.setVisible(true);//numero no válido
    } 
    cp5.get(Textfield.class,"Veloc").clear();
}

public void EnviarFrec() {//envio el valor de la frecuencia
    textF = cp5.get(Textfield.class, "Frec").getText();
    textFF = PApplet.parseFloat(textF);
    if (13.8f < textFF && textFF < 50.5f) {
        textFF = map(textFF,14,50,0,32767);
        textFFF = PApplet.parseInt(textFF);
        Dato1 = str(textFFF);
        TL18.setVisible(false);
        DatosW();
    }
    else{
        TL18.setVisible(true); //numero no válido
    } 
    cp5.get(Textfield.class, "Frec").clear();
}


public void Guardar() {  //guardo el archivo csv
    text2 = cp5.get(Textfield.class, "Nombre archivo").getText();
    print(text2);
    saveTable(table, "data1/" + text2 + ".csv","csv");
    cp5.get(Textfield.class, "Nombre archivo").clear();
    table.clearRows();
    tiempo3 = tiempo;  
}

public void ControlONOFF(boolean D2) { //control prendido adentro de Ai
    if (D2 ==  true) {
        Dato2 = "1";
        DatosW();
     //   Stop();
    }
    else {
        Dato2 = "0";
        DatosW();
    }
}


public void reset() { //boton de reset sobre fallas
        DD4=true;
        Dato4 = "1";
        Dato5 = "0";
        DatosW();
        reinicioreset = true;
        tiempo2 = millis();
}

public void fallaext(boolean D5) { //boton de falla externa
    if (D5 ==  true) {
        Dato0 = "0";
        Dato2 = "0";
        Dato3 = "0";
        Dato5 = "1";
        DatosW();
    }
    else{
        Dato5 = "0";
        DatosW();
    }
}

public void Habilitacion(boolean D6) {//habilitacion de ai1
    if (D6 ==  true) {
        Dato3 = "1";
        DatosW();
        TB1.setVisible(true);  
        TL8.setVisible(true);
    }
    else{
        Dato3 = "0";
        Dato0 = "0";
        DatosW();
        TB1.setVisible(false);  
        TL8.setVisible(false);
        TL99.setVisible(false);        TF11.setVisible(false);
        TB22.setVisible(false);        TL77.setVisible(true);    
        TL9.setVisible(false);        TF1.setVisible(false);
        TB2.setVisible(false);        TL7.setVisible(false);
        TB1.setOff();
    }
    
}

public void Settings(int n) {   //autofuncion -lista donde elijo el archivo a leer
    fitem = cp5.get(ScrollableList.class, "Settings").getItem(n).get("name").toString();
}

public void ICONO(){ //autofuncion- boton de enviar el archivo
table1 = loadTable("autofun/" +fitem, "header"); //leo el archivo donde estan los escalones

float [] yfun = new float [table1.getRowCount()];
float [] dTiem = new float [table1.getRowCount()];
for(int f= 0 ; f<table1.getRowCount(); f++){
    yfun [f] = table1.getFloat(f, 0); 
    dTiem [f] =  table1.getFloat(f, 1);
}   ///SI AL FINAL MARCA NULL O NaN ES PORQ EL ARCHIVO CSV TIENE LINEAS EN BLANCO
   for (int i = 0; i < yfun.length; i++) {
    Dato7 = Dato7 + "," + str(yfun[i]) + "," + str(dTiem[i]);
    }
  
  Dato6= str(yfun.length+dTiem.length);
  ddl1.setLabel("Elija archivo");
 
 DatosWrite2 = (Dato0 + "," + Dato1 + "," + Dato2 + "," + Dato3 + "," + Dato4 + "," + Dato5 + "," + Dato6  + Dato7+ ","+'\n');
 println(DatosWrite2);
 Arduino.write(DatosWrite2);
Dato6= "0";
Dato7="";

}
  public void settings() {  size(1000,650); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Gui_Tunel" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
