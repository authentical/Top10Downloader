package com.potatospy.top10downloader

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView



// view holder pattern holds the views since last use
class ViewHolder(v: View){

    val tvName:TextView = v.findViewById(R.id.tvName)
    val tvArtist:TextView = v.findViewById(R.id.tvArtist)
    val tvSummary:TextView = v.findViewById(R.id.tvSummary)

}



                    /* This is a constructor*/                                                 /* this is extends */
class FeedAdapter(context: Context, val resource:Int, val applications: List<FeedEntry>) : ArrayAdapter<FeedEntry>(context, resource){


    private val LOG_TAG = "FeedAdapter"
    private val inflater = LayoutInflater.from(context) // LayoutInflater creates objects described in XML




                // Determine how many items to display
                override fun getCount(): Int {

                    Log.d(LOG_TAG, "\n\ngetCount()")

                    return applications.size
                }


                // Get Layout
                override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

                    Log.d(LOG_TAG, "\n\ngetView()")

                    //val view  = inflater.inflate(resource, parent, false)    // Inflates a new view every time it's called
                    // Its better to reuse the view
                    val view:View
                    val viewHolder : ViewHolder
                    if(convertView==null){
                        Log.d(LOG_TAG, "\n\ngetView called with null convertView")
                        view = inflater.inflate(resource, parent, false)    // Inflates a new view every time it's called
                        viewHolder = ViewHolder(view)   // Instantiate an instance of ViewHolder
                        view.tag = viewHolder   // Link "Tags can also be used to store data within a view" to viewHolder

                    }else{
                        Log.d(LOG_TAG, "\n\nconvertView was not null")
                        view = convertView
                        viewHolder = view.tag as ViewHolder
                    }


//                    val tvName:TextView = view.findViewById(R.id.tvName)        // Each time findViewById is called it has to scan the layout from the start (inefficient)
//                    val tvArtist:TextView = view.findViewById(R.id.tvArtist)
//                    val tvSummary:TextView = view.findViewById(R.id.tvSummary)




                    val currentApp = applications[position]

                    viewHolder.tvName.text = (position+1).toString() + ": " +  currentApp.name       // Give textView with type the applications[element]
                    viewHolder.tvArtist.text = currentApp.artist
                    viewHolder.tvSummary.text = currentApp.summary

                    return view
                }

}












