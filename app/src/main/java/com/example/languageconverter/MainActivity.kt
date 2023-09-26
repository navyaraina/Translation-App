package com.example.languageconverter

import ModelLanguage
import android.app.ProgressDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import android.util.Log
import android.view.Menu
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import com.example.LanguageConverter.R
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.nl.translate.Translator
import java.util.Locale

class MainActivity : AppCompatActivity() {

//  UI Views
    private lateinit var SourceLang: EditText
    private lateinit var TranslatedLang: TextView
    private lateinit var sourcelangchoice: MaterialButton
    private lateinit var translangchoice: MaterialButton
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

    private lateinit var translatorOptions: TranslatorOptions
    private lateinit var progressDialog: ProgressDialog
    private lateinit var translator: Translator


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//      initialises UI views
        SourceLang=findViewById(R.id.SourceLang)
        TranslatedLang=findViewById(R.id.TranslatedLang)
        sourcelangchoice=findViewById(R.id.sourcelangchoice)
        translangchoice=findViewById(R.id.translangchoice)
        translatebtn=findViewById(R.id.translatebtn)

//      displays dialogue, this provides context to the constructor
        progressDialog= ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)

        loadAvailableLanguages()

        sourcelangchoice.setOnClickListener {
            sourceLanguageChoose()
        }

        translangchoice.setOnClickListener {
            transLanguageChoose()
        }

        translatebtn.setOnClickListener {
            validateData()
        }
    }

    private var sourceLanguageText=""
    private fun validateData() {
        sourceLanguageText= SourceLang.text.toString().trim()
        Log.d(TAG, "validateData: sourceLanguageText: $sourceLanguageText")

        if(sourceLanguageText.isEmpty()){
            showToast("Enter Text to Translate")
        }
        else
            startTranslation()
    }

    private fun startTranslation() {
        progressDialog.setMessage("Processing Language Model")
        progressDialog.show()

//      creating a translator object
        translatorOptions= TranslatorOptions.Builder()
            .setSourceLanguage(sourceLangCode)
            .setTargetLanguage(TranslatedLangCode)
            .build()
        translator=Translation.getClient(translatorOptions)

        val downloadConditions= DownloadConditions.Builder()
            .requireWifi()
            .build()

        translator.downloadModelIfNeeded(downloadConditions)
            .addOnSuccessListener {
                Log.d(TAG, "startTranslation: model ready, start translation")

                progressDialog.setMessage("Translating")

                translator.translate(sourceLanguageText)
                    .addOnSuccessListener { translatedText->
                        Log.d(TAG, "startTranslation: translatedText: $translatedText")
                        progressDialog.dismiss()

                        TranslatedLang.text= translatedText
                    }
                    .addOnFailureListener { e->
                        progressDialog.dismiss()
                        Log.e(TAG, "startTranslation:",e)
                        showToast("failure due to ${e.message}")
                    }
            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Log.e(TAG, "startTranslation:",e)
                showToast("failure due to ${e.message}")
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
            popupMenu.menu.add(Menu.NONE, i, i, LangArrayList!![i].LanguageTitle)
        }

        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { menuItem->
            val position = menuItem.itemId
            sourceLangCode=LangArrayList!![position].LanguageCode
            sourceLangTitle=LangArrayList!![position].LanguageTitle

            sourcelangchoice.text=sourceLangTitle
            SourceLang.hint="Enter $sourceLangTitle"

            Log.d(TAG,"sourceLanguageChoose: sourceLangCode: $sourceLangCode")
            Log.d(TAG,"sourceLanguageChoose: sourceLangTitle: $sourceLangTitle")
            false
        }
    }

    private fun transLanguageChoose(){
        val popupMenu= PopupMenu(this,translangchoice)

        for(i in LangArrayList!!.indices){
            popupMenu.menu.add(Menu.NONE, i, i, LangArrayList!![i].LanguageTitle)
        }

        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { menuItem->
            val position = menuItem.itemId
            TranslatedLangCode=LangArrayList!![position].LanguageCode
            TranslatedLangTitle=LangArrayList!![position].LanguageTitle

            translangchoice.text=TranslatedLangTitle

            Log.d(TAG,"transLanguageChoose: TranslatedLangCode: $TranslatedLangCode")
            Log.d(TAG,"transLanguageChoose: TranslatedLangTitle: $TranslatedLangTitle")
            false
        }
    }
    private fun showToast(message: String){
        Toast.makeText(this,message,Toast.LENGTH_LONG).show()
    }
}