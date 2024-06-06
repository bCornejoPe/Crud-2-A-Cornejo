package RecyclerViewHelper

import Bryan.Cornejo.crudcornejo2_a.R
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.dataClassProductos


class Adaptador(private var Datos: List<dataClassProductos>) : RecyclerView.Adapter<ViewHolder>() {


    fun actualizarLista(nuevaLista: List<dataClassProductos>){
        Datos = nuevaLista
        notifyDataSetChanged()
    }

    fun ActualizarListaDespuesDeActualizarDatos(uuid: String, nuevoNombre: String){
        val index = Datos.indexOfFirst { it.uuid == uuid }
        Datos[index].nombreProductos = nuevoNombre
        notifyItemChanged(index)
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

    fun actualizarproductos(nombreProducto: String, uuid: String){
        //1-Creo un acorrutina
        GlobalScope.launch(Dispatchers.IO){

            val objConexion = ClaseConexion().cadenaConexion()

            val updateproducto= objConexion?.prepareStatement("update tbproductos set nombreProducto = ? where uuid= ?")!!
            updateproducto.setString(1,nombreProducto)
            updateproducto.setString(2,uuid)
            updateproducto.executeUpdate()


            val commit = objConexion?.prepareStatement("commit")!!
            commit.executeUpdate()

            withContext(Dispatchers.Main){
                ActualizarListaDespuesDeActualizarDatos(uuid, nombreProducto)
            }
        }
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
        
         holder.imgEditar.setOnClickListener {
             val context= holder.itemView.context

             //Creo la alerta
             val builder = AlertDialog.Builder(context)
             builder.setTitle("Editar Nombre")

             //Agregamos un cuadro de texto
             //pueda escribir el nuevo nombre
             val cuadritoNuevoNombre= EditText(context)
             cuadritoNuevoNombre.setHint(item.nombreProductos)
             builder.setView(cuadritoNuevoNombre)

             builder.setPositiveButton("Actualizar"){ dialog, wich->
                 actualizarproductos(cuadritoNuevoNombre.text.toString(), item.uuid)
             }

             builder.setNegativeButton("Cancelar"){dialog, wich ->
                 dialog.dismiss()
             }

             val dialog= builder.create()
             dialog.show()
         }
       //Darle click a la cart
        holder.itemView.setOnClickListener {  }

    }
}