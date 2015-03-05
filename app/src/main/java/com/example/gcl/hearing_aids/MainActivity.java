package com.example.gcl.hearing_aids;
import java.util.Locale;
import java.util.Queue;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.w3c.dom.Text;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity implements RecognitionListener, TextToSpeech.OnInitListener{
//    private static final int REQUEST_CODE = 1234;
//    Button Start;
//    TextView Speech;
//    Dialog match_text_dialog;
//    ListView textlist;
//    ArrayList<String> matches_text;
    final private Float SPEECH_SLOW = 0.5f;
    final private Float SPEECH_NORMAL = 1.0f;
    final private Float SPEECH_FAST = 1.5f;
    final private Float PITCH_LOW = 0.5f;
    final private Float PITCH_NORMAL = 1.0f;
    final private Float PITCH_HIGH = 1.5f;
    private TextToSpeech    tts;

    private TextView returnedText;
    private Button refreshButton;
    private ProgressBar progressBar;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private String LOG_TAG = "VoiceRecognitionActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialize for main view.
        returnedText = (TextView)findViewById(R.id.textView1);
        progressBar = (ProgressBar)findViewById(R.id.progressBar1);
        progressBar.setVisibility(View.INVISIBLE);
        refreshButton = (Button)findViewById(R.id.refresh_button);
        // Initialize for Speech Recognition
        speech = SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener(this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        speech.startListening(recognizerIntent);
        // set toggleButton the event listener for switching speech
//        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked){
//                    progressBar.setVisibility(View.VISIBLE);
//                    progressBar.setIndeterminate(true);
//                    speech.startListening(recognizerIntent);
//                }else{
//                    progressBar.setIndeterminate(false);
//                    progressBar.setVisibility(View.INVISIBLE);
//                    speech.stopListening();
//                }
//            }
//        });
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speech.stopListening();
                speech.startListening(recognizerIntent);
            }
        });

        tts = new TextToSpeech(this, this);

//        Start = (Button)findViewById(R.id.toggleButton1);
//        Speech = (TextView)findViewById(R.id.textView1);
//        Start.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//               if(isConnected()){
//                   Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//                   intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
//                           RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//                   startActivityForResult(intent, REQUEST_CODE);
//               }else{
//                   Toast.makeText(getApplicationContext(), "Please Connect to Internet", Toast.LENGTH_LONG).show();
//               }
//            }
//        });
    }
//
//    public boolean isConnected(){
//        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo net = cm.getActiveNetworkInfo();
//        if (net != null && net.isAvailable() && net.isConnected()){
//            return true;
//        }else{
//            return false;
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data){
//        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){
//            match_text_dialog = new Dialog(MainActivity.this);
//            match_text_dialog.setContentView(R.layout.fragment_dialog_matches);
//            match_text_dialog.setTitle("Select Matching Text");
//            textlist = (ListView)match_text_dialog.findViewById(R.id.list);
//            matches_text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                    android.R.layout.simple_list_item_1, matches_text);
//            textlist.setAdapter(adapter);
//            textlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    Speech.setText("You have said " + matches_text.get(position));
//                    match_text_dialog.hide();
//                }
//            });
//            match_text_dialog.show();
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(speech != null){
            speech.destroy();
            Log.i(LOG_TAG, "destroy");
        }
        if(tts != null){
            tts.shutdown();
        }
    }

    @Override
    public void onBeginningOfSpeech(){
        Log.i(LOG_TAG, "onBeginningOfSpeech");
        progressBar.setIndeterminate(false);
        progressBar.setMax(10);
    }

    @Override
    public void onBufferReceived(byte[] buffer){
        Log.i(LOG_TAG, "onBufferReceived: " + buffer);
    }

    @Override
    public void onEndOfSpeech(){
        Log.i(LOG_TAG, "onEndOfSpeech");
        progressBar.setIndeterminate(true);
    }

    @Override
    public void onError(int errorCode){
        String errorMessage = getErrorText(errorCode);
        Log.d(LOG_TAG, "FAILED " + errorMessage);
//        returnedText.setText(errorMessage);
//        toggleButton.setChecked(false);
    }

    @Override
    public void onEvent(int arg0, Bundle arg1){
        Log.i(LOG_TAG, "onEvent");
    }

    @Override
    public void onPartialResults(Bundle arg0){
        Log.i(LOG_TAG, "onPartialResults");
    }

    @Override
    public void onReadyForSpeech(Bundle arg0){
        Log.i(LOG_TAG, "onReadyForSpeech");
    }

    @Override
    public void onResults(Bundle results){
        Log.i(LOG_TAG, "onResults");
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";
        for(String result:matches) text += result + "\n";
        Log.i("[speech message]",matches.get(0));
        if (tts.isSpeaking()) {
            // 読み上げ中なら止める
            tts.stop();
        }
        tts.speak(matches.get(0), TextToSpeech.QUEUE_FLUSH, null);
        matches.clear();
        while(tts.isSpeaking()){}
        speech.stopListening();
        speech.startListening(recognizerIntent);
        returnedText.setText(text);

    }

    @Override
    public void onRmsChanged(float rmsdB){
        Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);
        progressBar.setProgress((int) rmsdB);
    }

    public static String getErrorText(int errorCode){
        String message;
        switch (errorCode){
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }

    @Override
    public void onInit(int status) {
        if (TextToSpeech.SUCCESS == status) {
            Locale locale = Locale.ENGLISH;
            if (tts.isLanguageAvailable(locale) >= TextToSpeech.LANG_AVAILABLE) {
                tts.setLanguage(locale);
            } else {
                Log.d("", "Error SetLocale");
            }
        } else {
            Log.d("", "Error Init");
        }
    }






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
