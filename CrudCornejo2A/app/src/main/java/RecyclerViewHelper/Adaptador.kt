package RecyclerViewHelper

import Bryan.Cornejo.crudcornejo2_a.R
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import modelo.ClaseConexion
import modelo.dataClassProductos


class Adaptador(private var Datos: List<dataClassProductos>) : RecyclerView.Adapter<ViewHolder>() {


    fun actualizarLista(nuevaLista: List<dataClassProductos>){
        Datos = nuevaLista
        notifyDataSetChanged()
    }

    fun eliminarRegistro(nombreProducto: String, posicion: Int){

        //Quitar el elemento de la lista
        val listaDatos= Datos.toMutableList()
        listaDatos.removeAt(posicion)

        //Quitar de la base de datos
        GlobalScope.launch(Dispatchers.IO){
            //1-Crear un objeto de clase conexion
            val objConexion = ClaseConexion().cadenaConexion()
            val deleteProducto= objConexion?.prepareStatement("Delete tbProductos where nombreProducto = ?")!!
            deleteProducto.setString(1,nombreProducto)
            deleteProducto.executeUpdate()


            val commit = objConexion.prepareStatement("commit")!!
            commit.executeUpdate()
        }
        Datos = listaDatos.toList()
        notifyItemRemoved(posicion)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val vista =            LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
    return ViewHolder(vista)    }


    override fun getItemCount() = Datos.size
    @SuppressLint("SuspiciousIndentation")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto = Datos[position]
        holder.textView.text = producto.nombreProductos
    val item = Datos[position]
        holder.imgBorrar.setOnClickListener {

            //Creamos un alerta
            //Creamos el contexto
            val context = holder.itemView.context

            //Creo la alerta
            val builder = AlertDialog.Builder(context)

            //A mi alerta le pongo un titulo
            builder.setTitle("¿Estas seguro?")

            //Ponerle un mensaje
            builder.setMessage("¿Desea eliminar el registro?")

            //Paso final, agregamos los botones
            builder.setPositiveButton("SI") { dialog, wich ->
                eliminarRegistro(item.nombreProductos, position)
            }

            builder.setNegativeButton("No"){dialog, wich ->
            }
            //Creamos la alerta
            val alertDialog = builder.create()
            //Mostramos la alerta
            alertDialog.show()
        }



    }
}