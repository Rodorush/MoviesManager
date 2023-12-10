package br.com.rodorush.moviesmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import br.com.rodorush.moviesmanager.databinding.ActivityMainBinding

class MainActivity : ComponentActivity() {
    private val amb: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(amb.root)

    }
}