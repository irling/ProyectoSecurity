import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.ImageView
import java.io.ByteArrayInputStream
import javax.crypto.Cipher

//INVESTIGAR LA OBTENCION DE FOTOS ===================================================================================
class ImageAdapter(private val context: Context, private val images: List<ByteArray>) : BaseAdapter() {

    override fun getCount(): Int = images.size

    override fun getItem(position: Int): Any = images[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false)
        val imageView: ImageView = view.findViewById(android.R.id.text1)

        val base64String = Base64.encodeToString(images[position], Base64.DEFAULT)

        return view
    }

}
