package mcervini.comicslist

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main_menu.*

class MainMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        viewComicsButton.setOnClickListener {
            val intent: Intent = Intent(this, ListActivity::class.java);
            startActivity(intent)
        }
    }
}