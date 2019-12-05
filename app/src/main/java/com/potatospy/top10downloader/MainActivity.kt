package com.potatospy.top10downloader

import android.content.Context
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import kotlinx.android.synthetic.main.activity_main.*
import java.net.URL
import kotlin.properties.Delegates


private const val CURRENT_FEED_LIMIT = "currentFeedLimit"
private const val CURRENT_FEED_URL = "currentFeedUrl"



class FeedEntry{
    var name: String =""
    var artist: String=""
    var releasedDate:String=""
    var summary:String =""
    var imageUrl:String=""

//    override fun toString(): String {
//        return "FeedEntry(name='$name', artist='$artist', releasedDate='$releasedDate', summary='$summary', imageUrl='$imageUrl')".trimIndent()
//    }
}




class MainActivity : AppCompatActivity() {


    // ==Fields==

    private val LOG_TAG = "MainActivity"
//    private val downloadData by lazy { DownloadData(this, xmlListView) }
    private var downloadData: DownloadData? = null

    private var feedUrl: String="http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml" // .format finds %d because we're passing an int when we use it below
    private var feedLimit = 10

    private var feedCachedUrl = "INVALIDATED"
    private val STATE_URL = "feedUrl"
    private val STATE_LIMIT = "feedLimit"


    private val freeUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml"
    private val paidUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml"
    private val songUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml"



    // == onCreate(), download Url, Menu config, onDestroy() ==

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(LOG_TAG, "onCreate called")

        if(savedInstanceState!=null){   // If savedInstanceState is null then it has never been stopped before
            feedUrl = savedInstanceState.getString(STATE_URL)
            feedLimit = savedInstanceState.getInt(STATE_LIMIT)
        }

        downloadUrl(feedUrl.format(feedLimit))

    }


    private fun downloadUrl(feedUrl:String){

        if(feedCachedUrl!=feedUrl) {
            Log.d(LOG_TAG, "downloadUrl() - Starting Async task")
            downloadData = DownloadData(this, xmlListView)
            downloadData?.execute(feedUrl)  // Safe ? call because may be null
            feedCachedUrl = feedUrl
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.feeds_menu, menu)

        if(feedLimit==10){
            menu?.findItem(R.id.mnu10)?.isChecked=true
        }else{
            menu?.findItem(R.id.mnu25)?.isChecked=true
        }
        return true // Return true to tell android a menu was inflated
    }

    // Called when user selects and item from the menu so item will never be null SO it's safe to use nullable item?.itemId
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when(item?.itemId){
            R.id.mnuFree ->
                    feedUrl = freeUrl

            R.id.mnuPaid ->
                    feedUrl = paidUrl

            R.id.mnuSongs ->
                    feedUrl = songUrl

            R.id.mnu10, R.id.mnu25 ->
                if(!item.isChecked){
                    item.isChecked = true
                    feedLimit = 35 - feedLimit
                    downloadUrl(feedUrl.format(feedLimit))
                }
            R.id.mnuRefresh ->
                feedCachedUrl = "INVALIDATED"
            else ->
                return super.onOptionsItemSelected(item)    // Its possible to create a submenu, and android would trigger
                                                            // a call to this menu so you could get a surprise if you dont catch else
        }

        downloadUrl(feedUrl.format(feedLimit))
        return true
    }


    // Android has to know it's ok to interrupt the activity
    override fun onDestroy() {
        super.onDestroy()
        downloadData?.cancel(true)
    }


    // == Companion Object ==

    // Non-static inner class holds a reference to the parent activity
    // so use a companion object
    companion object {

        private class DownloadData(context:Context, listView: ListView) : AsyncTask<String, Void, String>() {

            private val LOGTAG = "DownloadData"

            var propContext : Context by Delegates.notNull()    // var propContext : Context - context is considered a leak
            var propListView : ListView by Delegates.notNull()  // Same here

            init {
                propContext = context
                propListView = listView
            }

            override fun onPostExecute(result: String) {
                super.onPostExecute(result)
                //Log.d(LOG_TAG, "onPostExecute got $result")

                val parseApplications = ParseApplication()
                parseApplications.parse(result)

                // ArrayAdapter needs Context, Layout, Object
//                val arrayAdapter = ArrayAdapter<FeedEntry>(propContext, R.layout.list_item, parseApplications.applications)    // Can't be instantiated without a Context
//                propListView.adapter = arrayAdapter // Give data to list view

                val feedAdapter = FeedAdapter(propContext, R.layout.list_record, parseApplications.applications)
                propListView.adapter = feedAdapter
            }

            override fun doInBackground(vararg arg: String?): String {

                Log.d(LOGTAG, "doInBackground got ${arg[0]}")

                val rssFeed = downloadXML(arg[0])

                if(rssFeed.isEmpty()){

                    Log.e(LOGTAG, "\n\n! ERROR !\ndoInBackground: rssFeed is empty") // Error
                }

                return rssFeed
            }

            private fun downloadXML(urlPath: String?): String {

                return URL(urlPath).readText()
            }
        }
    }


    // === Save/Restore ===

    override fun onSaveInstanceState(outState: Bundle) {   // Only called if there's a Bundle to save


        Log.d(LOG_TAG, "onSaveInstanceState called")
        super.onSaveInstanceState(outState)

        // DownloadData wont ever be null so don't need to check
        outState.putInt(STATE_LIMIT, feedLimit!!)   // !! Not null

        outState.putString(STATE_URL, feedUrl!!)

        // How to save downloaded data? Shouldnt download every time we change orientation

    }

//    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
//
//        Log.d(LOG_TAG, "onRestoreInstanceState called")
//        super.onRestoreInstanceState(savedInstanceState)
//
//        feedLimit = savedInstanceState.getInt(STATE_LIMIT)
//
//        feedUrl = savedInstanceState.getString(STATE_URL)
//    }
}






















