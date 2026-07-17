package com.example.restaurante

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.restaurante.databinding.ActivityMainBinding

// Aquí empieza la magia de la navegación, chiavo
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNav.setOnItemSelectedListener { item ->
            val fragmento: Fragment = when (item.itemId) {
                R.id.nav_menu -> MenuFragment()
                R.id.nav_carrito -> CartFragment()
                R.id.nav_pedidos -> HistoryFragment()
                else -> ProfileFragment()
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.contenedor, fragmento)
                .commit()
            true
        }

        if (savedInstanceState == null) {
            binding.bottomNav.selectedItemId = R.id.nav_menu
        }
    }
}
