package com.example.knowyourgovernment;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Downloader extends AsyncTask<String, Integer, String> {

    @SuppressLint("StaticFieldLeak")
    private MainActivity mainActivity;

    private static final String DATA_URL1 =
            "https://www.googleapis.com/civicinfo/v2/representatives?key=";
    private String apiKey = "AIzaSyB10w9DTMixMfEWTc-ceAyQGzgLgWrNQ_U";
    private static final String DATA_URL2 =
            "&address=";

    private static final String TAG = "Downloader";

    private List<Official> officialList = new ArrayList<>();
    private String location;
    private static HashMap<Integer, String> indexMap = new HashMap<>();

    Downloader(MainActivity ma) {
        this.mainActivity = ma;
    }


    @Override
    protected void onPostExecute(String s) {
        parseJSON(s);
//        Official official = parseJSON(s);
//        if (official != null)
//            Toast.makeText(mainActivity, "Loaded stock financial data for " + stock.getCompanyName(), Toast.LENGTH_SHORT).show();

        mainActivity.updateData(officialList, location);
    }


    @Override
    protected String doInBackground(String... params) {

        String location = params[0];
        Log.d(TAG, "doInBackground: " + location);
        Uri dataUri = Uri.parse(DATA_URL1 + apiKey + DATA_URL2 + location);
        String urlToUse = dataUri.toString();
        Log.d(TAG, "doInBackground: " + urlToUse);

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            Log.d(TAG, "doInBackground: ResponseCode: " + conn.getResponseCode());

            conn.setRequestMethod("GET");

            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

            Log.d(TAG, "doInBackground: " + sb.toString());

        } catch (Exception e) {
            Log.e(TAG, "doInBackground: ", e);
            return null;
        }

        return sb.toString();
    }


    private void parseJSON(String s) {
        try {

            JSONObject jObjMain = new JSONObject(s);

            JSONObject jLocation = (JSONObject) jObjMain.get("normalizedInput");
            String city = jLocation.getString("city");
            String state = jLocation.getString("state");
            String zip = jLocation.getString("zip");
            location = city + ", " + state + " " + zip;

            JSONArray jOffices = (JSONArray) jObjMain.get("offices");
            for (int i = 0; i < jOffices.length(); i++) {
                JSONObject jOffice = (JSONObject) jOffices.get(i);
                String officeName = jOffice.getString("name");
                JSONArray jOfficialIndices = (JSONArray) jOffice.get("officialIndices");
                for (int j = 0; j < jOfficialIndices.length(); j++) {
                    int index = (Integer) jOfficialIndices.get(j);
                    indexMap.put(index, officeName);
                }
            }

            JSONArray jOfficials = (JSONArray) jObjMain.get("officials");
            for (int i = 0; i < jOfficials.length(); i++) {
                JSONObject jOfficial = (JSONObject) jOfficials.get(i);
                String name = jOfficial.getString("name");
                String party = jOfficial.getString("party");
                Official official = new Official(indexMap.get(i), name, party);
                officialList.add(official);

                //address
                if (jOfficial.has("address")) {
                    JSONArray jAddress = (JSONArray) jOfficial.get("address");
                    String address = "";

                    if (jAddress.length() == 1) {
                        JSONObject jAddressObj = (JSONObject) jAddress.get(0);
                        address = jAddressObj.getString("line1") + "\n"
                                + jAddressObj.getString("line2") + "\n"
                                + jAddressObj.getString("line3")
                                + jAddressObj.getString("city") + ", "
                                + jAddressObj.getString("state") + " "
                                + jAddressObj.getString("zip");
                    } else if (jAddress.length() == 2) {
                        JSONObject jAddressObj = (JSONObject) jAddress.get(0);
                        String address1 = jAddressObj.getString("line1") + "\n"
                                + jAddressObj.getString("line2") + "\n"
                                + jAddressObj.getString("line3");

                        JSONObject jAddressObj2 = (JSONObject) jAddress.get(1);
                        String address2 = jAddressObj2.getString("line1") + "\n"
                                + jAddressObj2.getString("line2") + "\n"
                                + jAddressObj2.getString("line3")
                                + jAddressObj2.getString("city") + ", "
                                + jAddressObj2.getString("state") + " "
                                + jAddressObj2.getString("zip");
                        address = address1 + address2;
                    }
                        official.setAddress(address);
                    }

                    //phone
                    if (jOfficial.has("phones")) {
                        JSONArray jPhones = (JSONArray) jOfficial.get("phones");
                        String phone = (String) jPhones.get(0);
                        official.setPhone(phone);
                    }

                    //urls
                    if (jOfficial.has("urls")) {
                        JSONArray jUrls = (JSONArray) jOfficial.get("urls");
                        String url = (String) jUrls.get(0);
                        official.setUrl(url);
                    }

                    //emails
                    if (jOfficial.has("emails")) {
                        JSONArray jEmails = (JSONArray) jOfficial.get("emails");
                        String email = (String) jEmails.get(0);
                        official.setEmail(email);
                    }

                    //photoUrl
                    if (jOfficial.has("photoUrl")) {
                        String photoUrl = jOfficial.getString("photoUrl");
                        official.setPhotoURL(photoUrl);
                    }

                    //channels
                    if (jOfficial.has("channels")) {
                        JSONArray jChannels = (JSONArray) jOfficial.get("channels");
                        for (int j = 0; j < jChannels.length(); j++) {
                            JSONObject jChannel = (JSONObject) jChannels.get(j);
                            String type = jChannel.getString("type");
                            String id = jChannel.getString("id");
                            switch (type) {
                                case "GooglePlus":
                                    official.setGooglePlus(id);
                                    break;
                                case "Facebook":
                                    official.setFacebook(id);
                                    break;
                                case "Twitter":
                                    official.setTwitter(id);
                                    break;
                                case "YouTube":
                                    official.setYoutube(id);
                                    break;
                                default:
                            }
                        }
                    }
                }

            } catch(Exception e){
                Log.d(TAG, "parseJSON: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }