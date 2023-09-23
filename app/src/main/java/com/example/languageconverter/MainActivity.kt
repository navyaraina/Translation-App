package com.example.languageconverter

import ModelLanguage
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Button
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.TextView
import com.example.LanguageConverter.R
import androidx.activity.ComponentActivity
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.nl.translate.TranslatorOptions.Builder
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.languageconverter.MainActivity
import com.example.languageconverter.ui.theme.LanguageConverterTheme
import com.google.android.material.button.MaterialButton
import com.google.mlkit.nl.translate.Translator
import java.util.Locale

var conditions = DownloadConditions.Builder()
    .requireWifi()
    .build()

class MainActivity : AppCompatActivity() {

//  UI Views
    private lateinit var SourceLang: EditText
    private lateinit var TranslatedLang: TextView
    private lateinit var sourcelangchoice: MaterialButton
    private lateinit var translangbtn: MaterialButton
    private lateinit var translatebtn: MaterialButton

    companion object{
//      to print logs
        private const val TAG = "MAIN_TAG"
    }

//  list with languages and codes
    private var LangArrayList: ArrayList<ModelLanguage>?=null

    private var sourceLangCode="en"
    private var sourceLangTitle="English"
    private var TranslatedLangCode="de"
    private var TranslatedLangTitle="German"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//      initialises UI views
        SourceLang=findViewById(R.id.SourceLang)
        TranslatedLang=findViewById(R.id.TranslatedLang)
        sourcelangchoice=findViewById(R.id.sourcelangchoice)
        translangbtn=findViewById(R.id.translangbtn)
        translatebtn=findViewById(R.id.translatebtn)

        loadAvailableLanguages()

        sourcelangchoice.setOnClickListener {

        }

        translangbtn.setOnClickListener {

        }

        translatebtn.setOnClickListener {

        }

        }

    private fun loadAvailableLanguages(){
        LangArrayList=ArrayList()
        val languageCodeList = TranslateLanguage.getAllLanguages()

        for(languageCode in languageCodeList){
            val languageTitle=Locale(languageCode).displayLanguage
            Log.d(TAG, "loadAvailableLanguages: languageCode: $languageCode")
            Log.d(TAG, "loadAvailableLanguages: languageTitle: $languageTitle")

            val modelLanguage= ModelLanguage(languageCode, languageTitle)

            LangArrayList!!.add(modelLanguage)
        }
    }

    private fun sourceLanguageChoose(){
        val popupMenu= PopupMenu(this,sourcelangchoice)

        for(i in LangArrayList!!.indices){
            popupMenu.menu.add(Menu.NONE, i, i, LangArrayList!![i].languageTitle)
        }

        popupMenu.show()
    }
}