package co.edu.unal.tictactoe

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

class HomeActivity : AppCompatActivity() {

    private lateinit var buttonCreate: AppCompatButton
    private lateinit var buttonJoin: AppCompatButton
    private var userUID: String? = ""
    private var mPrefs: SharedPreferences? = null

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        database = Firebase.database.reference

        mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE)
        userUID = mPrefs?.getString("user", "")

        if(userUID == null){
            userUID = UUID.randomUUID().toString()
        }

        buttonCreate = findViewById<View>(R.id.buttonCreate) as AppCompatButton
        buttonJoin = findViewById<View>(R.id.buttonJoin) as AppCompatButton

        buttonCreate.setOnClickListener{
            createGame()
        }

        buttonJoin.setOnClickListener{
            joinGame()
        }

    }


    private fun createGame(){
        val gameId = createGameDB()
        val createGameIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("user", userUID)
            putExtra("gameId", gameId)
            putExtra("numberPlayer", 1)
        }
        startActivity(createGameIntent)
    }

    private fun joinGame(){
        val joinGameIntent = Intent(this, MultiplayerActivity::class.java).apply {
        }
        startActivity(joinGameIntent)
    }

    override fun onStop() {
        super.onStop()
        // Save the current scores
        val ed : SharedPreferences.Editor = mPrefs!!.edit()
        ed.putString("user", userUID)
        ed.commit()
    }

    private fun createGameDB(): Int{
        var foundGame = true

        val cal = Calendar.getInstance()
        val seed: Long = 0L + cal.get(Calendar.HOUR_OF_DAY)+cal.get(Calendar.DAY_OF_MONTH)+cal.get(Calendar.SECOND)+cal.get(Calendar.MILLISECOND)

        val rand = Random(seed)
        var gameId = rand.nextInt(9999- 1000) + 1000

        /*database.child("games").child(gameId.toString()).get().addOnSuccessListener {
            Log.i("firebase", "Got value ${it.value}")
            if(it.value == null){
                foundGame = false
            }
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }*/

        var newGame = GameModel(gameId)
        database.child("games").child(gameId.toString()).setValue(newGame)
        return gameId
    }


}
