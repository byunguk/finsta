package com.homework.finsta.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.homework.finsta.R;
import com.homework.finsta.model.Data;
import com.homework.finsta.util.Const;
import com.homework.finsta.util.NetworkAgent;
import com.homework.finsta.util.RestAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kbw815 on 7/26/15.
 */
public class FeedActivity extends AppCompatActivity {
    private final String DEBUG_TAG = "[FeedActivity]";
    private final int DEFAULT_FEED_COUNT = 5;

    private String mAccessToken;
    private String mNextMaxId;
    private SwipeRefreshLayout mSwipeRefeshLayout;
    private List<Data> mDataList;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private boolean mLoading;
    private int mVisibleItemCount;
    private int mTotalItemCount;
    private int mFirstVisibleItem;

    private SwipeRefreshLayout.OnRefreshListener mRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            mDataList.clear();
            FeedAsyncTask task = new FeedAsyncTask();
            task.execute();
        }
    };

    private RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
        private boolean userScrolled = false;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            mVisibleItemCount = mRecyclerView.getChildCount();
            mTotalItemCount = mLayoutManager.getItemCount();
            mFirstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

            Log.v(DEBUG_TAG, "VisibleItemCount: " + mVisibleItemCount +  ", FirstVisibleItem: " + mFirstVisibleItem + ", TotalItemCount: " + mTotalItemCount + ", Size: " + mDataList.size());
            if (!mLoading && (mTotalItemCount - mVisibleItemCount) <= (mFirstVisibleItem) && !TextUtils.isEmpty(mNextMaxId) && userScrolled)
            {
                FeedAsyncTask task = new FeedAsyncTask();
                task.execute("", mNextMaxId);
            }
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if(newState == RecyclerView.SCROLL_STATE_DRAGGING)
            {
                userScrolled = true;
            }
            else if (newState == RecyclerView.SCROLL_STATE_IDLE)
            {
                userScrolled = false;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        mAccessToken = getIntent().getStringExtra(Const.FIELD_ACCESS_TOKEN);
        mDataList = new ArrayList<>();
        bindUIElements();
        setUpRecyclerView();
        setUpListeners();
        if (!TextUtils.isEmpty(mAccessToken))
        {
            FeedAsyncTask task = new FeedAsyncTask();
            task.execute();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sign_out:
                //Toast.makeText(this, "Sign out", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, WebViewActivity.class);
                intent.putExtra(Const.FIELD_URL, Const.URL_LOG_OUT);
                startActivity(intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void bindUIElements()
    {
        mSwipeRefeshLayout = (SwipeRefreshLayout)findViewById(R.id.feed_swipe_refresh_layout);
        mRecyclerView = (RecyclerView)findViewById(R.id.feed_recycler_view);
    }

    private void setUpRecyclerView()
    {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new FeedAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }

    private void setUpListeners()
    {
        mSwipeRefeshLayout.setOnRefreshListener(mRefreshListener);
        mRecyclerView.addOnScrollListener(mScrollListener);
    }

    private class FeedAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoading = true;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                List<RestAgent.Parameter> parameters = new ArrayList<>();
                parameters.add(new RestAgent.Parameter(Const.FIELD_ACCESS_TOKEN, mAccessToken));
                parameters.add(new RestAgent.Parameter(Const.FIELD_COUNT, "" + DEFAULT_FEED_COUNT));
                String min = "";
                String max = "";
                if (params != null)
                {
                    if (params.length > 0)
                        min = params[0];
                    if (params.length > 1)
                        max= params[1];
                }
                parameters.add(new RestAgent.Parameter(Const.FIELD_MIN_ID, min));
                parameters.add(new RestAgent.Parameter(Const.FIELD_MAX_ID, max));
                RestAgent agent = new RestAgent(Const.FEED_URL, RestAgent.GET, parameters);
                String result = agent.send();
                return result;
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (TextUtils.isEmpty(result))
            {
                Toast.makeText(FeedActivity.this, "null", Toast.LENGTH_SHORT).show();
            }
            else
            {
                try
                {
                    JSONObject resultJSONObject = new JSONObject(result);
                    JSONObject paginationJSONObject = resultJSONObject.optJSONObject(Const.FIELD_PAGINATION);
                    mNextMaxId = paginationJSONObject.optString(Const.FIELD_NEXT_MAX_ID);
                    JSONArray dataJSONArray = resultJSONObject.optJSONArray(Const.FIELD_DATA);

                    for (int i = 0; i < dataJSONArray.length(); i++)
                    {
                        JSONObject obj = dataJSONArray.getJSONObject(i);
                        Data data = Data.fromJSONObject(obj);
                        mDataList.add(data);
                    }
                    mAdapter.notifyDataSetChanged();
                    mSwipeRefeshLayout.setRefreshing(false);
                }
                catch (JSONException ex)
                {
                    ex.printStackTrace();
                }
                mLoading = false;
            }
        }
    }

    private class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {
        public class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public ImageView playImageView;
            public ViewHolder(View v) {
                super(v);
                imageView = (ImageView)v.findViewById(R.id.item_image_view);
                playImageView = (ImageView)v.findViewById(R.id.item_play_image_view);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_feed_item, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            try {
                final int pos = position;
                final Data data = mDataList.get(position);

                final ImageRequest request = new ImageRequest(data.getImageUrl(), new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        Bitmap b = Bitmap.createScaledBitmap(bitmap, holder.imageView.getMeasuredWidth(), holder.imageView.getMeasuredWidth(), false);
                        holder.imageView.setImageBitmap(b);
                        if (Const.FIELD_VIDEO.equals(data.getType()))
                        {
                            holder.imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(FeedActivity.this, PlayActivity.class);
                                    intent.putExtra(Const.FIELD_URL, data.getVideoUrl());
                                    startActivity(intent);
                                }
                            });
                            holder.playImageView.setVisibility(View.VISIBLE);
                        }
                        else {
                            holder.imageView.setOnClickListener(null);
                            holder.playImageView.setVisibility(View.INVISIBLE);
                        }
                    }
                }, 0, 0, ImageView.ScaleType.CENTER_INSIDE, null, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v(DEBUG_TAG, "error: " + error.getMessage());
                        holder.imageView.setImageResource(R.mipmap.ic_launcher);
                    }
                });

                NetworkAgent.getInstance(FeedActivity.this).addToRequestQueue(request);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return mDataList.size();
        }
    }

}
