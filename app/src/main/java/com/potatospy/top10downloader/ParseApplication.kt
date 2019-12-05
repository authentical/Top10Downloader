package com.potatospy.top10downloader

import android.util.Log
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader
import java.lang.Exception

class ParseApplication{

    private val LOG_TAG = "ParseApplication"
    val applications = ArrayList<FeedEntry>()


    fun parse(xmlData: String): Boolean {

        Log.d(LOG_TAG, "parse() with $xmlData")

        var status = true
        var inEntry = false
        var textValue = ""

        // Initialze and configure XmlPullParser
        try{

            val factory = XmlPullParserFactory.newInstance();   // Get instance
            factory.isNamespaceAware=true;                      // will provide * support for XML namespaces.
                                                                // Helps sort out conflicts like HTML <table> vs a wooden <table>
            val xpp = factory.newPullParser()                   // Initialize parser
            var inputStream = StringReader(xmlData)

            xpp.setInput(inputStream)                      // Set reader() to incoming xmlData
            var eventType = xpp.eventType                       // Returns the type of the current event (START_TAG, END_TAG, TEXT, etc.)
            var currentRecord = FeedEntry()

            while(eventType!= XmlPullParser.END_DOCUMENT){      // If it's not the end of the readable Xml

                val tagName = xpp.name?.toLowerCase()    // Get tag <something>
                when(eventType){

                    XmlPullParser.START_TAG -> {
                        Log.d(LOG_TAG, "parse: Starting tag for " + tagName)
                        if(tagName=="entry"){
                            inEntry = true
                        }
                        if(tagName=="title"){
                            Log.d(LOG_TAG, "parse: Found title: " + tagName)
                        }
                    }

                    XmlPullParser.TEXT -> textValue = xpp.text  // Get tag's text field

                    XmlPullParser.END_TAG -> {
                        Log.d(LOG_TAG, "parse: Ending tag for " + tagName)
                        if(inEntry){
                            when(tagName){
                                "entry" -> {
                                    applications.add(currentRecord)
                                    inEntry = false
                                    currentRecord = FeedEntry() // Create a new FeedEntry
                                }

                                "name" -> currentRecord.name = textValue
                                "artist" -> currentRecord.artist = textValue
                                "releaseDate" -> currentRecord.releasedDate = textValue
                                "summary" -> currentRecord.summary = textValue
                                "imageUrl" -> currentRecord.imageUrl = textValue
                            }
                        }
                    }
                }
                eventType = xpp.next()  // Nothing left to do
            }


            for(app in applications){
                Log.d(LOG_TAG, "**************")
                Log.d(LOG_TAG, app.toString())
            }


        }catch(e: Exception){ e.printStackTrace()
            status=false
        }

        return status
    }
}