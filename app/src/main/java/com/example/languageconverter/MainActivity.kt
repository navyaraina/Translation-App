package com.example.languageconverter

import ModelLanguage
import android.view.View
import java.util.HashMap
import android.app.ProgressDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import android.util.Log
import android.widget.*
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import com.example.LanguageConverter.R
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.TranslateRemoteModel
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.nl.translate.Translator
import java.util.Locale

class MainActivity : AppCompatActivity() {

    //  UI Views
    private lateinit var SourceLang: EditText
    private lateinit var TranslatedLang: TextView
    private lateinit var sourcelangchoice: Spinner
    private lateinit var translangchoice: Spinner
    private lateinit var translatebtn: MaterialButton

    companion object {
        //      to print logs
        private const val TAG = "MAIN_TAG"
    }

    //  list with languages and codes
    private var LangArrayList: ArrayList<ModelLanguage>? = null

    private lateinit var sourceLangTitle: String
    private var TranslatedLangTitle= ""
    private var sourceLangCode=  ""
    private var TranslatedLangCode= ""


    private lateinit var translatorOptions: TranslatorOptions
    private lateinit var progressDialog: ProgressDialog
    private lateinit var translator: Translator


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//      initialises UI views
        SourceLang = findViewById(R.id.SourceLang)
        TranslatedLang = findViewById(R.id.TranslatedLang)
        sourcelangchoice = findViewById(R.id.sourcelangchoice)
        translangchoice = findViewById(R.id.translangchoice)
        translatebtn = findViewById(R.id.translatebtn)

//      displays dialogue, this provides context to the constructor
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)

        loadAvailableLanguages()
        chooseSourceLang()
        chooseTransLang()

        translatebtn.setOnClickListener {
            validateData()
//            startTranslation()
        }
    }


    private var sourceLanguageText= ""
    private fun validateData() {
        sourceLanguageText = SourceLang.text.toString().trim()
        Log.d(TAG, "validateData: sourceLanguageText: $sourceLanguageText")

        if (sourceLanguageText.isEmpty()) {
            showToast("Enter Text to Translate")
        } else
            startTranslation()
    }

    val map = HashMap<String, String>()

    private fun loadAvailableLanguages() {
        LangArrayList = ArrayList()
        val languageCodeList = TranslateLanguage.getAllLanguages()

        for (languageCode in languageCodeList) {
            val languageTitle = Locale(languageCode).displayLanguage
            Log.d(TAG, "loadAvailableLanguages: languageCode: $languageCode")
            Log.d(TAG, "loadAvailableLanguages: languageTitle: $languageTitle")

            val modelLanguage = ModelLanguage(languageCode, languageTitle)
            LangArrayList!!.add(modelLanguage)
            map[languageCode] = languageTitle
        }
    }

    private fun chooseSourceLang() {
        ArrayAdapter.createFromResource(
            this@MainActivity,
            R.array.all_languages,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            sourcelangchoice.adapter = adapter
        }
        sourcelangchoice.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                sourceLangTitle = sourcelangchoice.selectedItem.toString()
                SourceLang.hint = "Enter $sourceLangTitle"
                sourceLangCode= map.entries.find { it.value == sourceLangTitle }?.key.toString()
            }


                override fun onNothingSelected(parent: AdapterView<*>?) {
                    showToast("Choose a language!")
                }
            }
        }

    private fun chooseTransLang(){
       ArrayAdapter.createFromResource(
            this@MainActivity,
            R.array.all_languages,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            translangchoice.adapter = adapter
        }

        translangchoice.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Handle item selection for the target language Spinner
                TranslatedLangTitle = translangchoice.selectedItem.toString()
                TranslatedLangCode= map.entries.find { it.value == TranslatedLangTitle }?.key.toString()

//                translatebtn.setOnClickListener {
//                    validateData()
//                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                showToast("Choose a language!")
            }
        }
    }

    private fun startTranslation()  {
        progressDialog.setMessage("Processing Language Model")
        progressDialog.show()

//      creating a translator object
        translatorOptions = TranslatorOptions.Builder()
            .setSourceLanguage(sourceLangCode)
            .setTargetLanguage(TranslatedLangCode)
            .build()
        translator = Translation.getClient(translatorOptions)
        translator = Translation.getClient(translatorOptions)
        lifecycle.addObserver(translator)
        val downloadConditions = DownloadConditions.Builder()
            .requireWifi()
            .build()


        translator.downloadModelIfNeeded(downloadConditions)
            .addOnSuccessListener {
                Log.d(TAG, "startTranslation: model ready, start translation")

                progressDialog.setMessage("Translating")

                translator.translate(sourceLanguageText)
                    .addOnSuccessListener { translatedText ->
                        Log.d(TAG, "startTranslation: translatedText: $translatedText")
                        progressDialog.dismiss()


                        TranslatedLang.text = translatedText
//                        translator.close()
                    }
                    .addOnFailureListener { e ->
                        progressDialog.dismiss()
                        Log.e(TAG, "startTranslation:", e)
                        showToast("failure due to ${e.message}")
                    }
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Log.e(TAG, "startTranslation:", e)
                showToast("failure in downloading- ${e.message}")
            }
    }

    private fun showToast(message: String){
        Toast.makeText(this,message,Toast.LENGTH_LONG).show()
    }
}