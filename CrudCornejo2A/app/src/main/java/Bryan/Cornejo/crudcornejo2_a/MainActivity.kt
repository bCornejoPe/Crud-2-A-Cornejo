package Bryan.Cornejo.crudcornejo2_a

import RecyclerViewHelper.Adaptador
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.dataClassProductos

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //1-Mandar a lllamar a todos los elementos de la Pantalla

        val txtNombre= findViewById<EditText>(R.id.txtNombre)
        val txtPrecio= findViewById<EditText>(R.id.txtPrecio)
        val  txtCantidad= findViewById<EditText>(R.id.txtCantidad)

        val btnAgregar = findViewById<Button>(R.id.btnAgregar)

            fun Limpiar(){
            txtNombre.setText("")
                txtCantidad.setText("")
                txtPrecio.setText("")
        }
        ///////////////////////////////////////////////////////////////////////Mostara////////////////////////////////////////////////////////////////////////
        val rcvProductos = findViewById<RecyclerView>(R.id.rcvProductos)

        //Asignar un layout al ReciclerView
        rcvProductos.layoutManager= LinearLayoutManager(this)

        //Funcion para obtener datos
        fun obtenerDatos(): List<dataClassProductos>{
            val objConexion = ClaseConexion().cadenaConexion()

            val statement = objConexion?.createStatement()
            val resulset = statement?.executeQuery("select * from tbProductos")!!
            val productos = mutableListOf<dataClassProductos>()
            while (resulset.next()){
                val nombre= resulset.getString("nombreProducto")

                val producto= dataClassProductos(nombre)
                productos.add(producto)
            }
            return productos
        }

        //asignar un adaptador
        CoroutineScope(Dispatchers.IO).launch {
            val productosDB = obtenerDatos()
            withContext(Dispatchers.Main){
                val miAdapter = Adaptador(productosDB)
                rcvProductos.adapter = miAdapter
            }
        }

        //////////////////////////////TODO: GUARDAR DATOS///////////////////////////////////////////

        //2-Programar el boton

        btnAgregar.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO){
                //Guardar datos
                //1-Crear un objeto de la clase conexion
                val claseConexion= ClaseConexion().cadenaConexion()

                //2- Creo un variable que contenga un PreparedStatement
                val addProducto = claseConexion?.prepareStatement("insert into tbProductos(nombreProducto, precio, cantidad) values (?, ?, ?)")!!

                addProducto.setString(1,txtNombre.text.toString())
                addProducto.setInt(2,txtPrecio.text.toString().toInt())
                addProducto.setInt(3,txtCantidad.text.toString().toInt())
                   addProducto.executeUpdate()

                val nuevosProductos = obtenerDatos()
                withContext(Dispatchers.Main){
                    (rcvProductos.adapter as? Adaptador)?.actualizarLista(nuevosProductos)
                }
            }
            //Limpiar()
        }


    }
}