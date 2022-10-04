package co.unal.edu.reto6

import android.app.AlertDialog
import android.content.Intent
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import co.unal.edu.reto6.databinding.ActivityDisplayCompanyBinding

class DisplayCompany : AppCompatActivity() {

    private lateinit var binding: ActivityDisplayCompanyBinding

    private var mydb: DBHelper? = null

    private var name: TextView? = null
    private var URL: TextView? = null
    private var phone: TextView? = null
    private var email: TextView? = null
    private var products: TextView? = null
    private var classification: Spinner? = null
    private var idToUpdate = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDisplayCompanyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        name = binding.editTextName
        URL = binding.editTextUrl
        phone = binding.editTextPhone
        email = binding.editTextEmail
        products = binding.editTextProducts
        classification = binding.editSpinnerClassification

        val dataAdapter = ArrayAdapter.createFromResource(this, R.array.classification_array,
            android.R.layout.simple_spinner_item)

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        classification!!.adapter = dataAdapter

        mydb = DBHelper(this)
        val extras = intent.extras
        if (extras != null) {
            val value = extras.getInt("id")
            if (value > 0) {
                //means this is the view part not the add contact part.
                val rs: Cursor? = mydb!!.getData(value)
                idToUpdate = value
                rs?.moveToFirst()
                val nam: String? = rs?.getString(rs.getColumnIndexOrThrow(DBHelper.COMPANIES_COLUMN_NAME))
                val url: String? = rs?.getString(rs.getColumnIndexOrThrow(DBHelper.COMPANIES_COLUMN_URL))
                val phon: String? =
                    rs?.getString(rs.getColumnIndexOrThrow(DBHelper.COMPANIES_COLUMN_PHONE))
                val emai: String? =
                    rs?.getString(rs.getColumnIndexOrThrow(DBHelper.COMPANIES_COLUMN_EMAIL))
                val prod: String? =
                    rs?.getString(rs.getColumnIndexOrThrow(DBHelper.COMPANIES_COLUMN_PRODUCTS_SERVICES))

                val clas: String? =
                    rs?.getString(rs.getColumnIndexOrThrow(DBHelper.COMPANIES_COLUMN_CLASSIFICATION))

                if (rs != null) {
                    if (!rs.isClosed) {
                        rs.close()
                    }
                }

                val b: Button = findViewById<View>(R.id.button1) as Button
                b.visibility = View.INVISIBLE
                name!!.text = nam
                name!!.isFocusable = false
                name!!.isClickable = false
                URL!!.text = url
                URL!!.isFocusable = false
                URL!!.isClickable = false
                phone!!.text = phon
                phone!!.isFocusable = false
                phone!!.isClickable = false
                email!!.text = emai
                email!!.isFocusable = false
                email!!.isClickable = false
                products!!.text = prod
                products!!.isFocusable = false
                products!!.isClickable = false
                val item = dataAdapter.getPosition(clas)
                classification!!.setSelection(item)
                classification!!.isEnabled = false
                classification!!.isClickable = false

            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        //Bundle extras = getIntent().getExtras()
        val extras = intent.extras

        if(extras !=null) {
            val value = extras.getInt("id")
            if(value>0){
                menuInflater.inflate(R.menu.display_company, menu)
            } else{
                menuInflater.inflate(R.menu.main_menu, menu)
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        return when (item.itemId) {
            R.id.Edit_Company -> {
                val b = findViewById<View>(R.id.button1) as Button
                b.visibility = View.VISIBLE
                name!!.isEnabled = true
                name!!.isFocusableInTouchMode = true
                name!!.isClickable = true
                URL!!.isEnabled = true
                URL!!.isFocusableInTouchMode = true
                URL!!.isClickable = true
                phone!!.isEnabled = true
                phone!!.isFocusableInTouchMode = true
                phone!!.isClickable = true
                email!!.isEnabled = true
                email!!.isFocusableInTouchMode = true
                email!!.isClickable = true
                products!!.isEnabled = true
                products!!.isFocusableInTouchMode = true
                products!!.isClickable = true
                classification!!.isEnabled = true
                classification!!.isFocusableInTouchMode = true
                classification!!.isClickable = true
                classification!!.isEnabled = true
                true
            }
            R.id.Delete_Company -> {
                val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                builder.setMessage(R.string.deleteContact)
                    .setPositiveButton(R.string.yes) { _, _ ->
                        mydb?.deleteCompany(idToUpdate)
                        Toast.makeText(
                            applicationContext, "Deleted Successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)
                    }
                    .setNegativeButton(R.string.no) { _, _ ->
                        // User cancelled the dialog
                    }
                val d: AlertDialog = builder.create()
                d.setTitle("Are you sure")
                d.show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun run(view: View?) {
        val extras = intent.extras
        if (extras != null) {
            val value = extras.getInt("id")
            if (value > 0) {
                if (mydb?.updateCompany(
                        idToUpdate, name!!.text.toString(), URL!!.text.toString(),
                        phone!!.text.toString(), email!!.text.toString(),
                        products!!.text.toString(), classification!!.selectedItem.toString()
                    ) == true
                ) {
                    Toast.makeText(applicationContext, "Updated", Toast.LENGTH_SHORT).show()
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(applicationContext, "not Updated", Toast.LENGTH_SHORT).show()
                }
            } else {
                if (mydb?.insertCompany(
                        name!!.text.toString(), URL!!.text.toString(), phone!!.text.toString(),
                        email!!.text.toString(), products!!.text.toString(),
                        classification!!.selectedItem.toString()
                    ) == true
                ) {
                    Toast.makeText(
                        applicationContext, "done",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        applicationContext, "not done",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

}