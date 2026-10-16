package com.moniliu.freetogame.activities

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.moniliu.freetogame.R
import com.moniliu.freetogame.adapters.GameAdapter
import com.moniliu.freetogame.data.FreeToGameApi
import com.moniliu.freetogame.data.Game
import com.moniliu.freetogame.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    lateinit var adapter: GameAdapter
    var gameList: List<Game> = emptyList()

    var filteredGameList: List<Game> = emptyList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // configuramos el view binding

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val id = intent.getIntExtra("EXTRA_GAME_ID", -1)


        // configuramos el recycle view

        adapter = GameAdapter(gameList) { position ->
            val game = gameList[position]
            Toast.makeText(this, game.title, Toast.LENGTH_SHORT).show()


        }

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        //obtener los juegos del Api
        val api = FreeToGameApi.getInstance()

        CoroutineScope(Dispatchers.IO).launch {
            gameList = api.getGamesList()
            filteredGameList = gameList

            CoroutineScope(Dispatchers.Main).launch {
                showData()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_main_menu, menu)

        val searchMenuItem = menu.findItem(R.id.menu_search)
        val searchView = searchMenuItem.actionView as SearchView

        searchView.setOnQueryTextListener(object  : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                filteredGameList = gameList.filter {
                    it.title.contains(newText,true)
            }
                showData()
                return true
            }

        })
        return true



    }
    fun showData(){
        adapter.updateData(filteredGameList)
        supportActionBar?.subtitle = "Found ${filteredGameList.size} games"



    }

}