package RecyclerViewHelper

import Bryan.Cornejo.crudcornejo2_a.R
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
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto = Datos[position]
        holder.textView.text = producto.nombreProductos
    val item = Datos[position]
        holder.imgBorrar.setOnClickListener {
            eliminarRegistro(item.nombreProductos, position)
        }

    }
}