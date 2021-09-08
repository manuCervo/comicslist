package mcervini.comicslist

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main_menu.*

/**
 * activity for the main menu
 */
class MainMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        viewComicsButton.setOnClickListener {
            startActivity(Intent(this, ListActivity::class.java))
        }

        searchButton.setOnClickListener {
            startActivity(Intent(this, ListActivity::class.java).apply {
                putExtra("search", true)
            })
        }

        missingListButton.setOnClickListener {
            startActivity(Intent(this, ListActivity::class.java).apply {
                putExtra("missingOnly", true)
            })
        }
    }
}