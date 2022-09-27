package co.edu.unal.tictactoe

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class MultiplayerActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var games: DatabaseReference
    private var gamesList : List<GameModel>? = arrayListOf()
    private val gamesListLive = MutableLiveData<List<GameModel>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multiplayer)

        database = Firebase.database.reference
        games = Firebase.database.getReference("games")
        val createdGames = findViewById<RecyclerView>(R.id.createdGames)
        val noGames = findViewById<TextView>(R.id.noGames)
        createdGames.layoutManager = LinearLayoutManager(this)

        val options: FirebaseRecyclerOptions<GameModel>  = FirebaseRecyclerOptions.Builder<GameModel>().setQuery(games, GameModel::class.java).setLifecycleOwner(this).build()
        if(options == null){
            noGames.text = "No hay juegos disponibles"
        }
        val adapter = object : FirebaseRecyclerAdapter<GameModel, ViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                return ViewHolder(
                    LayoutInflater.from(parent.context)
                    .inflate(R.layout.card_multiplayer, parent, false))
            }
            override fun onBindViewHolder(holder: ViewHolder, position: Int, model: GameModel) {
                holder.gameRoom.text = model.gameId.toString()
                holder.joinButton.setOnClickListener {
                    joinGame(model.gameId)
                }
                if(model.gameFull){
                    holder.joinButton.invalidate()
                }
                if(model.gameFull){
                    holder.players.text = "Full"
                }else{
                    holder.players.text = "1/2"
                }

            }
        }
        createdGames.adapter = adapter
    }

    private fun joinGame(gameId: Int){
        games.child(gameId.toString()).child("gameFull").setValue(true)
        val joinGameIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("gameId", gameId)
            putExtra("numberPlayer", 2)
        }
        startActivity(joinGameIntent)
    }
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val gameRoom: TextView = view.findViewById(R.id.gameRoom)
    val players: TextView = view.findViewById(R.id.players)
    val joinButton: AppCompatButton = view.findViewById(R.id.joinGame)

}