package com.example.android.margaretssecretprojectv2;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements ServerInterator.ServerResponse {

    private static final String TAG = "MainActivity";

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;
    private List<String> data;
    private Snackbar loadingMessage;
    private boolean parentMode;

    private int parentModeCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        parentModeCounter = 0;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!parentMode) {
                    parentModeCounter += 1;
                    if (parentModeCounter >= 4) {
                        parentMode = true;
                        getData();
                        parentModeCounter = 0;
                        Toast.makeText(MainActivity.this, R.string.parent_mode_disabled, Toast.LENGTH_SHORT).show();

                    }
                } else {
                    parentModeCounter += 1;
                    if (parentModeCounter >= 4) {
                        parentMode = false;
                        getData();
                        parentModeCounter = 0;
                        Toast.makeText(MainActivity.this, R.string.parent_mode_enabled, Toast.LENGTH_SHORT).show();


                    }
                }

            }
        });

        loadingMessage = Snackbar.make(findViewById(android.R.id.content), R.string.loading, Snackbar.LENGTH_INDEFINITE);

        this.parentMode = false;

        getDataFromServer();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            getDataFromServer();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getDataFromServer() {
        loadingMessage.show();
        new ServerInterator(this).execute();
    }

    public void getData() {
        this.data = JSONHelper.importFromJSON(this, this);


        if (!this.parentMode) {
            disableParentMode();
        } else {
            enableParentMode();
        }

        if (this.data != null) {
            // Create the adapter that will return a fragment for each of the three
            // primary sections of the activity.
            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), data);

            // Set up the ViewPager with the sections adapter.
            mViewPager = (ViewPager) findViewById(R.id.container);
            mViewPager.setAdapter(mSectionsPagerAdapter);
        }
    }

    @Override
    public void processFinish() {
        getData();
        loadingMessage.dismiss();
    }

    private void disableParentMode() {
        String[] dataArray = new String[data.size()];
        for (int i = 0; i < dataArray.length; i++) {
            dataArray[i] = data.get(i);
        }

        for (int i = 0; i < dataArray.length; i++) {
            if (dataArray[i].substring(0, 1).equals("/")) {
                dataArray[i] = dataArray[i].substring(1);
            }
        }

        data = new ArrayList<>(Arrays.asList(dataArray));
    }

    private void enableParentMode() {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).substring(0, 1).equals("/")) {
                data.remove(i);
                enableParentMode();
            }
        }
    }






    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_TITLE = "section_title";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(String title) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putString(ARG_SECTION_TITLE, title);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getArguments().getString(ARG_SECTION_TITLE));
            return rootView;
        }
    }

    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        private static final int LOOPS_COUNT = 1000;
        List data;

        public SectionsPagerAdapter(FragmentManager fm, List data) {
            super(fm);
            this.data = randomizeList(data);

        }

        private List randomizeList(List data) {
            Random rand = new Random();
            List ans = new ArrayList();
            for (int i = 0; i < data.size(); i++) {
                Object temp = data.get(rand.nextInt(data.size()));
                ans.add(temp);
                data.remove(temp);
            }
            return ans;
        }

        @Override
        public Fragment getItem(int position) {
            if (data.size() > 0) {
                return PlaceholderFragment.newInstance((String) data.get(position));
            } else {
                return PlaceholderFragment.newInstance("");
            }
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

    }
}

class ServerInterator extends AsyncTask<Void, Void, Void> {

    private static final String TAG = "MainActivity";
    private static final String FILE_NAME = "MargaretsSecretProjectv2/data.json";
    private List<String> datas = null;
    private ServerResponse mListener;

    public ServerInterator(ServerResponse listener) {
        this.mListener = listener;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            fileFromURL("https://storage.googleapis.com/margaretmarcusv2/reasons.json");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mListener.processFinish();
    }

    public void fileFromURL(String dowloadUrl) throws IOException {
        URL url = null;//Create Download URl
        url = new URL(dowloadUrl);
        HttpURLConnection c = (HttpURLConnection) url.openConnection();//Open Url Connection
        c.setRequestMethod("GET");//Set Request Method to "GET" since we are grtting data
        c.connect();//connect the URL Connection

        if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
            Log.e(TAG, "Server returned HTTP " + c.getResponseCode()
                    + " " + c.getResponseMessage());
        }

        File ans = new File(Environment.getExternalStorageDirectory() + "/"
                + JSONHelper.FILE_NAME);


        FileOutputStream fos = new FileOutputStream(ans);//Get OutputStream for NewFile Location

        InputStream is = c.getInputStream();//Get InputStream for connection

        byte[] buffer = new byte[1024];//Set buffer type
        int len1 = 0;//init length
        while ((len1 = is.read(buffer)) != -1) {
            fos.write(buffer, 0, len1);//Write new file
        }

        //Close all connection after doing task
        fos.close();
        is.close();
    }

    public interface ServerResponse {
        void processFinish();
    }
}
