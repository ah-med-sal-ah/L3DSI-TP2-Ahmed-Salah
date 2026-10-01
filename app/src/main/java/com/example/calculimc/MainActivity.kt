package com.example.calculimc

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var editTextPoids: EditText
    private lateinit var editTextTaille: EditText
    private lateinit var buttonCalculer: Button
    private lateinit var buttonEffacer: Button
    private lateinit var textViewImc: TextView
    private lateinit var textViewCategorie: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        editTextPoids = findViewById(R.id.editTextPoids)
        editTextTaille = findViewById(R.id.editTextTaille)
        buttonCalculer = findViewById(R.id.buttonCalculer)
        buttonEffacer = findViewById(R.id.buttonEffacer)
        textViewImc = findViewById(R.id.textViewImc)
        textViewCategorie = findViewById(R.id.textViewCategorie)

        buttonCalculer.setOnClickListener { calculerImc() }

        // Temporary: the real Effacer comes in step 6
        buttonEffacer.setOnClickListener {
            Toast.makeText(this, "Effacer cliqué", Toast.LENGTH_SHORT).show()
        }
    }

    private fun calculerImc() {
        // Remove the old result before a new calculation
        textViewImc.text = ""
        textViewCategorie.text = ""

        // 1. Get the entered values (accept both "1,75" and "1.75")
        val textePoids = editTextPoids.text.toString().trim().replace(',', '.')
        val texteTaille = editTextTaille.text.toString().trim().replace(',', '.')

        // 2. Check that both fields are filled in
        var champsRemplis = true
        if (textePoids.isEmpty()) {
            editTextPoids.error = getString(R.string.erreur_champ_vide)
            champsRemplis = false
        }
        if (texteTaille.isEmpty()) {
            editTextTaille.error = getString(R.string.erreur_champ_vide)
            champsRemplis = false
        }
        if (!champsRemplis) {
            Toast.makeText(this, R.string.erreur_completer, Toast.LENGTH_SHORT).show()
            return
        }

        // 3. Convert to decimal numbers (null if the text isn't a valid number)
        val poids = textePoids.toDoubleOrNull()
        val taille = texteTaille.toDoubleOrNull()
        if (poids == null) {
            editTextPoids.error = getString(R.string.erreur_valeur_invalide)
            return
        }
        if (taille == null) {
            editTextTaille.error = getString(R.string.erreur_valeur_invalide)
            return
        }

        // 4. Check that both values are strictly positive
        if (poids <= 0) {
            editTextPoids.error = getString(R.string.erreur_valeur_positive)
            return
        }
        if (taille <= 0) {
            editTextTaille.error = getString(R.string.erreur_valeur_positive)
            return
        }

        // 5. Calculate the IMC
        val imc = poids / (taille * taille)

        // 6. Round to two decimals, with a comma (French format: 22,86)
        val imcTexte = String.format(Locale.FRANCE, "%.2f", imc)

        // 7. Find the category with if / else if / else
        val categorie: Int
        val couleur: Int
        if (imc < 18.5) {
            categorie = R.string.cat_insuffisance
            couleur = R.color.imc_orange
        } else if (imc < 25) {
            categorie = R.string.cat_normale
            couleur = R.color.imc_vert
        } else if (imc < 30) {
            categorie = R.string.cat_surpoids
            couleur = R.color.imc_orange
        } else if (imc < 35) {
            categorie = R.string.cat_obesite_moderee
            couleur = R.color.imc_rouge
        } else if (imc < 40) {
            categorie = R.string.cat_obesite_severe
            couleur = R.color.imc_rouge
        } else {
            categorie = R.string.cat_obesite_morbide
            couleur = R.color.imc_rouge_fonce
        }

        // 8. Display the value, the category and the color
        textViewImc.text = getString(R.string.resultat_imc, imcTexte)
        textViewCategorie.setText(categorie)
        textViewCategorie.setTextColor(ContextCompat.getColor(this, couleur))
    }
}