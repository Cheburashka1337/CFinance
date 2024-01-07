package com.example.chebafinance;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;


public class MainActivity extends AppCompatActivity {

    public static class global {
        public static ArrayList <String> valuts = new ArrayList();
        public static ArrayList <Double> valuts_price = new ArrayList();
        public static Double resultat = 0.0;
    }
    //private static final int REQUEST_IMAGE_CAPTURE = 1;
    //private static final int REQUEST_IMAGE_PICK = 2;
    //private OCRAsyncTask ocrAsyncTask;
    //private EditText editPrice;
    //private static final int REQUEST_IMAGE_CAPTURE = 1;
    // private static final int REQUEST_PERMISSION_CODE = 2;
    // private ImageView imageView;
    // private EditText editPrice;
    //private static final int REQUEST_IMAGE_CAPTURE = 1;
    //private EditText editPrice;
    private static final int SPEECH_REQUEST_CODE = 123;
    private Set<Currency> mAvailableCurrenciesSet;
    private List<Currency> mAvailableCurrenciesList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button addButton = (Button) findViewById(R.id.add_button);
        Button delButton = (Button) findViewById(R.id.del_button);
        EditText editName = (EditText) findViewById(R.id.edit_name);
        EditText editPrice = (EditText) findViewById(R.id.edit_price);
        EditText editCount = (EditText) findViewById(R.id.edit_count);
        EditText  result = (EditText) findViewById(R.id.result);
        Spinner sValuta = (Spinner) findViewById(R.id.svaluta);
        Spinner sValuta2 = (Spinner) findViewById(R.id.svaluta2);
        ListView listView = findViewById(R.id.listView);

        //ArrayList <String> names = new ArrayList();
        //ArrayList <Double> price = new ArrayList();
        //ArrayList <Double> count = new ArrayList();
        //ArrayList <String> valuta = new ArrayList();
        ArrayList<String> names = new ArrayList<>();
        ArrayList<Double> price = new ArrayList<>();
        ArrayList<Double> count = new ArrayList<>();
        ArrayList<String> valuta = new ArrayList<>();


        // приоритетные валюты
        global.valuts.add("RUB");
        global.valuts_price.add(1.0);
        global.valuts.add("EUR");
        global.valuts.add("KZT");
        global.valuts.add("USD");
        global.valuts.add("BYN");
        global.valuts.add("CNY");

        //Toast.makeText(MainActivity.this,"опа", Toast.LENGTH_SHORT).show();
        result.setInputType(InputType.TYPE_NULL);

        String vurl = "https://www.cbr-xml-daily.ru/daily_json.js";
        try{
            new GetURLData(MainActivity.this).execute(vurl);
        } catch (Exception e) {
        }

        mAvailableCurrenciesSet = Currency.getAvailableCurrencies();
        mAvailableCurrenciesList = new ArrayList<Currency>(mAvailableCurrenciesSet);


        // Массив имен атрибутов, из которых будут читаться данные
        String[] from = {"Name", "Price"};
        ArrayList<HashMap<String, Object>> data = new ArrayList();
        SimpleAdapter adapter = new SimpleAdapter(this, data, android.R.layout.simple_list_item_2,
                from,
                new int[]{android.R.id.text1, android.R.id.text2});
        listView.setAdapter(adapter);

        // кнопка плюс
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editName.getText().toString().trim().equals("") && !editPrice.getText().toString().trim().equals(""))
                {
                    if (!names.contains(editName.getText().toString())) {
                        names.add(editName.getText().toString());
                        price.add(Double.valueOf(editPrice.getText().toString()));
                        if (!editCount.getText().toString().trim().equals("")){
                            count.add(Double.valueOf(editCount.getText().toString()));
                        } else { count.add(1.0); }
                        //valuta.add("RUB"); // !
                        valuta.add(global.valuts.get(sValuta.getSelectedItemPosition()));
                        //Toast.makeText(MainActivity.this," Данные успешно добавлены ", Toast.LENGTH_SHORT).show();
                    } else {
                        int nameIndex = names.indexOf(editName.getText().toString());
                        names.set(nameIndex,editName.getText().toString());
                        price.set(nameIndex,Double.valueOf(editPrice.getText().toString()));
                        if (!editCount.getText().toString().trim().equals("")){
                            count.set(nameIndex,Double.valueOf(editCount.getText().toString()));
                        } else { //count.set(nameIndex,1.0);
                        }
                        valuta.set(nameIndex,global.valuts.get(sValuta.getSelectedItemPosition()));
                        //valuta.set(nameIndex,"RUB");
                        Toast.makeText(MainActivity.this," Данные по совпадающему элементу успешно обновлены ", Toast.LENGTH_SHORT).show();
                    }

                    //global.resultat=global.resultat+Double.valueOf(editPrice.getText().toString())*Double.valueOf(editCount.getText().toString())*global.valuts_price.get(sValuta.getSelectedItemPosition());
                    double sum = 0;
                    for (int i = 0; i < names.size(); i++) {
                        sum=sum+price.get(i)*count.get(i)*global.valuts_price.get(global.valuts.indexOf(valuta.get(i)));
                    }
                    global.resultat=sum;
                    if (sValuta2.getSelectedItem().toString()!="RUB"){
                        global.resultat=global.resultat/global.valuts_price.get(global.valuts.indexOf(sValuta2.getSelectedItem().toString()));
                    }
                    Formatter f =  new Formatter();
                    result.setText("Сумма "+f.format("%.2f%n", global.resultat));
                    data.clear();
                    HashMap<String, Object> map;
                    for (int i = 0; i < names.size(); i++) {
                        map = new HashMap<>();
                        map.put("Name", names.get(i));
                        map.put("Price", price.get(i) +" x "+ count.get(i) +" = "+ price.get(i) * count.get(i)+"  "+ valuta.get(i));
                        data.add(map);
                    }
                    adapter.notifyDataSetChanged();
                    // подготавливаем поля для дальнейшего ввода
                    editName.setText("");
                    editPrice.setText("");
                    editCount.setText("");
                    // RUB - но думаю тут пока не надо


                    // сохранение для вылета
                    SharedPreferences cashe = getSharedPreferences("cashe", Context.MODE_PRIVATE);
                    SharedPreferences.Editor cashe_editor = cashe.edit();
                    cashe_editor.clear(); // Очищаем все предыдущие значения
                    for (int i = 0; i < names.size(); i++) {
                        cashe_editor.putString(names.get(i), names.get(i));
                        cashe_editor.putString(names.get(i)+"_P", Double.toString(price.get(i)));
                        cashe_editor.putString(names.get(i)+"_C", Double.toString(count.get(i)));
                        cashe_editor.putString(names.get(i)+"_V", valuta.get(i));
                    }
                    cashe_editor.apply(); // А это сохранение без него работать ничего не будет
                    //Toast.makeText(MainActivity.this,"Данные в кеш сохранены", Toast.LENGTH_SHORT).show();
                }
                else { Toast.makeText(MainActivity.this," Заполните обязательные поля ", Toast.LENGTH_SHORT).show(); }
            }

        });

        // удержание на элемент списка
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                editName.setText(names.get(i), TextView.BufferType.EDITABLE);
                editPrice.setText(String.valueOf(price.get(i)), TextView.BufferType.EDITABLE);
                editCount.setText(String.valueOf(count.get(i)), TextView.BufferType.EDITABLE);
                // RUB
                Toast.makeText(MainActivity.this," Данные доступны для редактирования ", Toast.LENGTH_SHORT).show();
                return false;
            }
        });


        // кнопка минус
        delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    if (!editName.getText().toString().trim().equals("")){
                        int deathIndex = names.indexOf(editName.getText().toString());
                        names.remove(deathIndex);
                        price.remove(deathIndex);
                        count.remove(deathIndex);
                        valuta.remove(deathIndex);
                        Toast.makeText(MainActivity.this," Данные успешно удалены ", Toast.LENGTH_SHORT).show();
                        data.clear();
                        HashMap<String, Object> map;
                        for (int i = 0; i < names.size(); i++) {
                            map = new HashMap<>();
                            map.put("Name", names.get(i));
                            map.put("Price", price.get(i) +" x "+ count.get(i) +" = "+ price.get(i) * count.get(i) +"  "+ valuta.get(i));
                            data.add(map);
                        }
                        adapter.notifyDataSetChanged();

                    }
                    double sum = 0;
                    for (int i = 0; i < names.size(); i++) {
                        sum=sum+price.get(i)*count.get(i)*global.valuts_price.get(global.valuts.indexOf(valuta.get(i)));
                    }
                    global.resultat=sum;
                    if (sValuta2.getSelectedItem().toString()!="RUB"){
                        global.resultat=global.resultat/global.valuts_price.get(global.valuts.indexOf(sValuta2.getSelectedItem().toString()));
                    }
                    Formatter f =  new Formatter();
                    result.setText("Сумма "+f.format("%.2f%n", global.resultat));

                    editName.setText("");
                    editPrice.setText("");
                    editCount.setText("");
                    // RUB - но думаю тут пока не надо
                    //Toast.makeText(MainActivity.this,"запрос данных "+global.valuts_price, Toast.LENGTH_SHORT).show();

                    // сохранение для вылета
                    SharedPreferences cashe = getSharedPreferences("cashe", Context.MODE_PRIVATE);
                    SharedPreferences.Editor cashe_editor = cashe.edit();
                    cashe_editor.clear(); // Очищаем все предыдущие значения
                    for (int i = 0; i < names.size(); i++) {
                        cashe_editor.putString(names.get(i), names.get(i));
                        cashe_editor.putString(names.get(i)+"_P", Double.toString(price.get(i)));
                        cashe_editor.putString(names.get(i)+"_C", Double.toString(count.get(i)));
                        cashe_editor.putString(names.get(i)+"_V", valuta.get(i));
                    }
                    cashe_editor.apply(); // А это сохранение без него работать ничего не будет
                    //Toast.makeText(MainActivity.this,"Данные в кеш сохранены", Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    editName.setText("");
                    editPrice.setText("");
                    editCount.setText("");
                }
            }
        });
        //долгое удержание на удалении для стирания всего
        delButton.setOnLongClickListener(view -> {
            editName.setText("");
            editPrice.setText("");
            editCount.setText("");
            SharedPreferences cashe = getSharedPreferences("cashe", Context.MODE_PRIVATE);
            SharedPreferences.Editor cashe_editor = cashe.edit();
            cashe_editor.clear();
            cashe_editor.apply();
            data.clear();
            adapter.notifyDataSetChanged();
            return true;
        });

        // выбор итоговой валюты
        sValuta2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                double sum = 0;
                for (int j = 0; j < names.size(); j++) {
                    sum=sum+price.get(j)*count.get(j)*global.valuts_price.get(global.valuts.indexOf(valuta.get(j)));
                }
                global.resultat=sum;
                if (sValuta2.getSelectedItem().toString()!="RUB"){
                    global.resultat=global.resultat/global.valuts_price.get(global.valuts.indexOf(sValuta2.getSelectedItem().toString()));
                }
                Formatter f =  new Formatter();
                result.setText("Сумма "+f.format("%.2f%n", global.resultat));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Toast.makeText(MainActivity.this," "+data, Toast.LENGTH_LONG).show();

        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            }
        });

       // synchronized (this) {
       // try { Thread.sleep(300); } catch (InterruptedException e) { throw new RuntimeException(e);}
        // загрузка кеша последнего вылета
        SharedPreferences cashes = getSharedPreferences("cashe", Context.MODE_PRIVATE);
        Map<String, ?> allEntries = cashes.getAll();
        if (allEntries.isEmpty()) {
            Log.d("SharedPreferences", "No entries found");
        } else
        {
            Set<String> keys = allEntries.keySet();
            for (String key : keys) {
                if (!key.contains("_")){
                    names.add(key);
                }
            }
            for (int i = 0; i <= names.size()-1; i++) {
                price.add(Double.parseDouble(cashes.getString(names.get(i)+"_P","1")));
                count.add(Double.parseDouble(cashes.getString(names.get(i)+"_C","1")));
                valuta.add(cashes.getString(names.get(i)+"_V","RUB"));
            }
            double sum = 0;
            for (int i = 0; i < names.size(); i++) {
                try {
                    sum=sum+price.get(i)*count.get(i)*global.valuts_price.get(global.valuts.indexOf(valuta.get(i)));
                } catch (Exception e){ sum=sum+price.get(i)*count.get(i)*1; }
                //sum=sum+price.get(i)*count.get(i)*global.valuts_price.get(global.valuts.indexOf(valuta.get(i)));
            }
            global.resultat=sum;
            if (sValuta2.getSelectedItem().toString()!="RUB"){
                global.resultat=global.resultat/global.valuts_price.get(global.valuts.indexOf(sValuta2.getSelectedItem().toString()));
            }
            Formatter f =  new Formatter();
            result.setText("Сумма "+f.format("%.2f%n", global.resultat));
            data.clear();
            HashMap<String, Object> map;
            for (int i = 0; i < names.size(); i++) {
                map = new HashMap<>();
                map.put("Name", names.get(i));
                map.put("Price", price.get(i) +" x "+ count.get(i) +" = "+ price.get(i) * count.get(i)+"  "+ valuta.get(i));
                data.add(map);
            }
            adapter.notifyDataSetChanged();
        } /*_*/



        editName.setOnLongClickListener(view -> {
            startVoiceInput();
            return true; // Возвращаем true, чтобы событие было обработано полностью
        });


    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE);
        } catch (Exception e) {
            Toast.makeText(this, "Голосовой ввод не поддерживается на вашем устройстве", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String spokenText = result.get(0);

            // Делайте что-то с полученным текстом (например, установите его в EditText)
            //EditText editName = findViewById(R.id.editName);
            EditText editName = (EditText) findViewById(R.id.edit_name);
            editName.setText(spokenText.toString());

            Toast.makeText(this, "Вы сказали: " + spokenText, Toast.LENGTH_SHORT).show();
        }
    }

    private class GetURLData extends AsyncTask<String, String, String> {
        private Context context; // Добавьте поле для хранения контекста
        public GetURLData(Context context) {
            this.context = context;
        }
        protected void onPreExecute(){
            super.onPreExecute();
            //Toast.makeText(MainActivity.this,"запрос данных", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpsURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(strings[0]);
                connection = (HttpsURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while((line=reader.readLine()) != null)
                    buffer.append(line).append("\n");
                return buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
               // e.printStackTrace();
            } finally {
                try{
                if(connection != null)
                    connection.disconnect();}
                catch (Exception e){}
                try {
                    if (reader != null)
                        reader.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result){
            try {
            super.onPostExecute(result);
            } catch(Exception e){}

            try{

                if (result != null && isNetworkAvailable()==true) {

                JSONObject jsonObject2 = new JSONObject(result);
                JSONObject valuteObject = jsonObject2.getJSONObject("Valute");
                Iterator<String> keys = valuteObject.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    global.valuts.add(key);
                }

                // Получаем ссылку на Spinner из макета
                Spinner spinner = ((Activity) context).findViewById(R.id.svaluta);
                Spinner spinner2 = ((Activity) context).findViewById(R.id.svaluta2);

                // Создаем ArrayAdapter и устанавливаем его в Spinner
                ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, global.valuts);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                spinner2.setAdapter(adapter);

                }

            } catch (JSONException e) {
                e.printStackTrace();
                //Toast.makeText(MainActivity.this,"Ошибка загрузки данных 2", Toast.LENGTH_SHORT).show();
            } catch(Exception e){}


            try {

                if (result != null && isNetworkAvailable()==true) {

                JSONObject jsonObject = new JSONObject(result); //String test = ""+jsonObject.getJSONObject("Valute"); //.getString("EUR") .getJSONObject("AZN").getDouble("Value")
                JSONObject subObj = jsonObject.getJSONObject("Valute");
                JSONObject subObj2; // сохранение данных по нашим валютам
                for (int i = 1; i <= global.valuts.size()-1; i++){
                    subObj2 = subObj.getJSONObject(global.valuts.get(i));
                    global.valuts_price.add(subObj2.getDouble("Value")/subObj2.getDouble("Nominal"));
                }
                //Toast.makeText(MainActivity.this,"Данные по валютному курсу были загружены", Toast.LENGTH_SHORT).show();


                SharedPreferences valu = getSharedPreferences("valu", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor_valu = valu.edit();
                editor_valu.clear(); // Очищаем все предыдущие значения
                for (int i = 1; i <= global.valuts.size()-1; i++) {
                    editor_valu.putString(global.valuts.get(i), Double.toString(global.valuts_price.get(i)));
                }
                editor_valu.apply(); // А это сохранение без него работать ничего не будет
                //editor_valu.commit();
                //Toast.makeText(MainActivity.this,"Данные по валютному курсу сохранены", Toast.LENGTH_SHORT).show();


                } else if (result == null && isNetworkAvailable()==false) {
                    try{
                        SharedPreferences valu = getSharedPreferences("valu", Context.MODE_PRIVATE);
                        Map<String, ?> allEntries = valu.getAll();
                        if (allEntries.isEmpty()) {
                            Log.d("SharedPreferences", "No entries found");
                        }
                        Set<String> keys = allEntries.keySet();
                        for (String key : keys) {
                            global.valuts.add(key); // Toast.makeText(MainActivity.this,"Загружаем ключ "+key, Toast.LENGTH_SHORT).show();
                        }
                        for (int i = 1; i <= global.valuts.size()-1; i++) {
                            global.valuts_price.add(Double.parseDouble(valu.getString(global.valuts.get(i),"1")));
                        }

                        // Получаем ссылку на Spinner из макета
                        Spinner spinner = ((Activity) context).findViewById(R.id.svaluta); // Замените на ваш реальный идентификатор
                        Spinner spinner2 = ((Activity) context).findViewById(R.id.svaluta2);
                        // Создаем ArrayAdapter и устанавливаем его в Spinner
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, global.valuts);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(adapter);
                        spinner2.setAdapter(adapter);

                    }catch (Exception e) {

                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
                //Toast.makeText(MainActivity.this,"Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
            }

        }
        private boolean isNetworkAvailable() {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
    }

}
