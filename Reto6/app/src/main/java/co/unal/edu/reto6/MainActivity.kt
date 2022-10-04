package co.unal.edu.reto6

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView.*
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import co.unal.edu.reto6.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var obj: ListView? = null
    private var mydb: DBHelper? = null

    private var filter : ArrayList<String> = ArrayList()
    private var checkedFilter : BooleanArray = booleanArrayOf(false, false, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mydb = DBHelper(this)

        if(filter.isEmpty()) {
            createListView()
        }

        val searchText = binding.filterTextName
        searchText.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                val arrayListName = mydb!!.getByName(s)
                val arrayListNameIds = mydb!!.getByNameIds(s)

                val arrayAdapterName: ArrayAdapter<*> =
                    ArrayAdapter<Any?>(this@MainActivity,android.R.layout.simple_list_item_1, arrayListName as List<Any?>)

                obj!!.adapter = arrayAdapterName
                obj!!.onItemClickListener = OnItemClickListener { _, _, arg2, _ ->
                    val idToSearch = arrayListNameIds[arg2]
                    val dataBundle = Bundle()
                    dataBundle.putInt("id", idToSearch.toInt())
                    val intent = Intent(applicationContext, DisplayCompany::class.java)
                    intent.putExtras(dataBundle)
                    startActivity(intent)
                }
            }
        })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        return when (item.itemId) {
            R.id.item1 -> {
                val dataBundle = Bundle()
                dataBundle.putInt("id", 0)
                val intent = Intent(applicationContext, DisplayCompany::class.java)
                intent.putExtras(dataBundle)
                startActivity(intent)
                true
            }
            R.id.itemFilterClas -> {
                classificationAlert()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun classificationAlert() {
        filter.clear()
        val builder = AlertDialog.Builder(this)
        val classification = arrayOf("Consultancy", "Software development", "Machine Learning")
        val checkedClass = checkedFilter
        with(builder)
        {
            setTitle("Buscar")
            setMultiChoiceItems(classification, checkedClass){ dialog, which, isChecked ->
                checkedClass[which] = isChecked
            }
            setPositiveButton("Buscar") { dialog: DialogInterface, which: Int ->
                for (i in checkedClass.indices) {
                    val checked = checkedClass[i]
                    checkedFilter[i] = checked
                    if (checked) {
                        filter.add(classification[i])
                    }
                }
                if(filter.isNotEmpty()){
                    filterListView()
                }else{
                    createListView()
                }
            }
            show()
        }
    }

    private fun filterListView() {
        val arrayList: ArrayList<String> = ArrayList()
        val ids: ArrayList<String> = ArrayList()
        for(element in filter){
            var arrayListAux = mydb!!.getCompaniesClas(element)
            var idsAux = mydb!!.getCompaniesClasIds(element)
            arrayList.addAll(arrayListAux)
            ids.addAll(idsAux)
        }
        val arrayAdapter: ArrayAdapter<*> =
            ArrayAdapter<Any?>(
                this,
                android.R.layout.simple_list_item_1,
                arrayList as List<Any?>
            )
        arrayAdapter.notifyDataSetChanged()
        obj = binding.listView1
        obj!!.adapter = arrayAdapter
        obj!!.onItemClickListener = OnItemClickListener { _, _, arg2, _ ->
            val idToSearch = ids[arg2]
            val dataBundle = Bundle()
            dataBundle.putInt("id", idToSearch.toInt())
            val intent = Intent(applicationContext, DisplayCompany::class.java)
            intent.putExtras(dataBundle)
            startActivity(intent)
        }
    }

    private fun createListView() {
        val arrayList = mydb!!.getAllCompanies()
        val ids = mydb!!.getAllCompaniesIds()
        val arrayAdapter: ArrayAdapter<*> =
            ArrayAdapter<Any?>(
                this,
                android.R.layout.simple_list_item_1,
                arrayList as List<Any?>
            )
        obj = binding.listView1
        obj!!.adapter = arrayAdapter
        obj!!.onItemClickListener = OnItemClickListener { _, _, arg2, _ ->
            val idToSearch = ids[arg2]
            val dataBundle = Bundle()
            dataBundle.putInt("id", idToSearch.toInt())
            val intent = Intent(applicationContext, DisplayCompany::class.java)
            intent.putExtras(dataBundle)
            startActivity(intent)
        }
    }

    override fun onKeyDown(keycode: Int, event: KeyEvent?): Boolean {
        if (keycode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true)
        }
        return super.onKeyDown(keycode, event)
    }
}