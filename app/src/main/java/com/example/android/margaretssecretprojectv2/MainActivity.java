package com.example.android.margaretssecretprojectv2;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.box.sdk.BoxAPIConnection;
import com.box.sdk.BoxFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements ServerInterator.ServerResponse {

    private static final String TAG = "MainActivity";
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private List<String> data;
    private Snackbar loadingMessage;
    private boolean parentMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loadingMessage = Snackbar.make(findViewById(android.R.id.content), "Loading", Snackbar.LENGTH_INDEFINITE);

        getData();


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
        if (id == R.id.action_refresh) {
            loadingMessage.show();
            new ServerInterator(this).execute();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getData() {
        this.data = JSONHelper.importFromJSON(this, this);

        if (!this.parentMode) {
            for (String s : data) {
                if (s.substring(0, 1).equals("/")) {
                    s = s.substring(1);
                }
            }
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

    /**
     * A placeholder fragment containing a simple view.
     */
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

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        private static final int LOOPS_COUNT = 1000;
        List data;

        public SectionsPagerAdapter(FragmentManager fm, List data) {
            super(fm);
            this.data = data;
        }

        @Override
        public Fragment getItem(int position) {
            Random rand = new Random();

            if (data.size() > 0) {
                return PlaceholderFragment.newInstance((String) data.get(rand.nextInt(data.size())));
            } else {
                return PlaceholderFragment.newInstance("");
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return Integer.MAX_VALUE;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
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
//        try {
//            URL url = new URL("https://api.box.com/2.0/files/243729054955/content");
//            Gson gson = new Gson();
//            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
//            String ans = "";
//            String str;
//
//            while ((str = in.readLine()) != null){
//                ans += str;
//            }
//
//            in.close();
//
//            Log.d(TAG, str);
//            Log.d(TAG, "HI");
//
//            //datas = gson.fromJson(str, ArrayList.class);
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        BoxAPIConnection api = new BoxAPIConnection("HVKg08klrcMP3FQqyxtqYhafVbYNDzpP");

        BoxFile file = new BoxFile(api, "243729054955");
        BoxFile.Info info = file.getInfo();

        try {
            file.download(new FileOutputStream(new File(Environment.getExternalStorageDirectory(), FILE_NAME)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mListener.processFinish();
    }

    public interface ServerResponse {
        void processFinish();
    }
}
