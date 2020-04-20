package com.example.appservidor

//App conexion con el servidor, si hay internet los datos se envian al servidor y si no, los datos se
//guardan en SQLite y se pueden mostrar los datos almacenados en SQLite, y se verifica si el servidor responde

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteOpenHelper
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.lang.Exception
import java.net.URI.create


class MainActivity : AppCompatActivity() {
    lateinit var nom : EditText
    lateinit var ape : EditText
    lateinit var eda : EditText
    lateinit var but : RadioButton
    lateinit var gua : Button
    lateinit var mos : Button

    //instancia para acceder a la clase de SQLite
    internal var dbHelper = DatabaseHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        nom = findViewById(R.id.nombre)
        ape = findViewById(R.id.apellido)
        eda = findViewById(R.id.edad)
        but = findViewById(R.id.radio)

        gua = findViewById(R.id.guardar)
        mos = findViewById(R.id.mostrar)

        gua.setOnClickListener {
            var nombre=nom.text.toString()
            var apellido=ape.text.toString()
            var edad=eda.text.toString()
            var ver=but.isChecked.toString()

            //Obtener acceso a servicio de conectividad de internet de android
            val con=getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            //Obteniendola informacion de la red
            val netInfo = con.activeNetworkInfo
            //Evaluando si hay conexion
            if (netInfo != null && netInfo.isConnected){
                subproceso(this).execute(nombre,apellido,edad,ver)
                //Toast.makeText(applicationContext,"Los datos se guardaron en el servidor", Toast.LENGTH_SHORT).show()
                clearEditText()
            }else{
                AccionesSQLite(nombre,apellido,edad,ver)
                //Toast.makeText(applicationContext, "Los datos se guardaron en SQLite", Toast.LENGTH_SHORT).show()
            }
    }
        //llamada a mostrar datos sqlite
        handleViewing()
    }

    //Apartado para enviar datos al servidor
//--------------------------------------------------------------------------------------------------
    //funcion en donde se realiza la peticion a servidor
    fun run (no:String, ap:String, ed:String, bu:String){
        var nam=no
        var pel=ap
        var da=ed.toInt()
        var bt=bu.toBoolean()
        //println(nam+pel+da+bt)

        val client = OkHttpClient()

        //Creamos un objeto - los que esta en " " se utiliza en el php
        val json=JSONObject()
        json.put("nombre",nam)
        json.put("apellido",pel)
        json.put("edad",da)
        json.put("radio",bt)
        //println(json)

        //Dirección del servidor
        val url="http://10.5.49.14/android_servidor/conexion_servidor.php"
        //Para indicar el tipo de texto
        val mediaType="application/json; charset=utf-8".toMediaType()

        //Para formatear y convertir el tipo de datos request aceptado para el body
        val body=json.toString().toRequestBody(mediaType)
        //println(body)

        //Para contruir la petición
        val request=Request.Builder()
            .url(url)
            .post(body)
            .build()
            //println(request.toString())

        //Ejecutar la petición
        client.newCall(request).execute().use { response ->
            var res=response.isSuccessful
            if (!res){
                println("Los datos se han enviado $response.body!!.string()")
            }else{
                println("No se han guardado los datos "+response.body!!.string())
            }
        }
    }

    //Subproceso
//--------------------------------------------------------------------------------------------------
    //Se crea una clase interna
    internal class subproceso(param:MainActivity) : AsyncTask<String, Void, String>(){//este es el subproceso con asyncTask
        //Se realiza un metodo constructor e inicializa con init{}
        var para:MainActivity
        init {
            this.para = param
        }

        //params recibe el array que se le envio de en onclick al invocar subproceso().execute(//parametros a enviar)
        override fun doInBackground(vararg params: String?): String {
            //Realiza una llamada a la funcion en donde se hace la peticion a servidor Objeto.Metodo(//parametros a enviar segun el tipo que se reciva)
            var ma= (params[0]).toString()
            var ma1=(params[1]).toString()
            var ma2=(params[2]).toString()
            var ma3=(params[3]).toString()
            //println(ma+ma1+ma2+ma3)
            try {
                para.run(ma,ma1,ma2,ma3)
                //Thread para que se pedan ejecutar varios subprocesos y no se sature
                //Se muestra el toast si hay internet
                println("Entra "+para.toString())
                Thread(Runnable {
                    para.runOnUiThread(Runnable {
                        para.showToast("Datos guardados en el servidor")
                    })
                }).start()
                //Una excepcion si el servidor no responde y se muestra el toast
            }catch (e: Exception){
                e.printStackTrace()
                println("No Hay Error Baboso "+e.toString())
                //Thread par aque se pedan ejecutar varios subprocesos y no se sature
                Thread(Runnable {
                    para.runOnUiThread(Runnable {
                        para.AccionesSQLite(ma, ma1, ma2, ma3)
                        para.showToast("Datos guardados en SQLite")
                    })
                }).start()
            }
        return ""
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
        }
    }

        ///funcion para limpiar
        fun clearEditText(){
            nom.setText("")
            ape.setText("")
            eda.setText("")
            but.isChecked=false
        }

    //funcion para ejecutar toast cuando sea requerido
    fun showToast(text:String){
        Toast.makeText(this@MainActivity, text, Toast.LENGTH_SHORT).show()
    }

    //funcion para invocar un cuadro de dialogo alert
    fun showDialog(title : String, Message : String){
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(true)
        builder.setTitle(title)
        builder.setMessage(Message)
        builder.show()
    }

    //funcion para ejecutar insert en SQLite
    fun AccionesSQLite(no:String,ap:String,ed: String,bo:String){
        var nom = no
        var ape = ap
        var edad =ed.toInt()
        var bol=bo.toBoolean()
        try {
            val x=dbHelper.insertData(nom,ape,edad,bol)
            clearEditText()
            if (x==kotlin.Unit){
                //Se muestra el toast si no hay internet
                showToast("Datos guardados en SQLite")
            }

        }catch (e: java.lang.Exception){
            e.printStackTrace()
            showToast(e.message.toString())
        }
    }

    //funcion para ver los datos guardados
    fun handleViewing(){
        mos.setOnClickListener(
            View.OnClickListener {
                val res = dbHelper.allDate
                if (res.count == 0) {
                    showToast("No Hay Datos Guardados en SQLite")
                    return@OnClickListener
                }

                val buffer = StringBuffer()
                while (res.moveToNext()) {
                    buffer.append("ID : " + res.getString(0) + "\n")
                    buffer.append("NOMBRE : " + res.getString(1) + "\n")
                    buffer.append("APELLIDO : " + res.getString(2) + "\n")
                    buffer.append("EDAD : " + res.getString(3) + "\n")
                    buffer.append("RADIO : " + res.getString(4) + "\n" + "\n")
                }
                //showToast("lista de datos: "+buffer.toString())
                showDialog("Lista de Datos:",buffer.toString())
            }
        )
    }

}