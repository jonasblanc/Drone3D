package ch.epfl.sdp.drone3d.ui.map.offline

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ch.epfl.sdp.drone3d.R
import ch.epfl.sdp.drone3d.map.offline.OfflineMapSaver
import ch.epfl.sdp.drone3d.map.offline.OfflineMapSaverImpl.Companion.getMetadata
import ch.epfl.sdp.drone3d.ui.ToastHandler
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mapbox.mapboxsdk.offline.OfflineRegion
import timber.log.Timber

/**
 * ViewAdapter for the RecyclerView holding OfflineRegions. Use an [offlineMapSaver] to delete
 * the missions when we click on the delete button.
 */
class OfflineRegionViewAdapter(private val offlineMapSaver: OfflineMapSaver) :
    ListAdapter<OfflineRegion, OfflineRegionViewAdapter.OfflineRegionViewHolder>(RegionDiff) {

    /**
     * ViewHolder for the RecyclerView holding OfflineRegions. Contains a textView with the name of
     * the region and a button to delete it as [view].
     */
    class OfflineRegionViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val deleteButton: FloatingActionButton = view.findViewById(R.id.deleteRegionButton)
        private val textView: TextView = view.findViewById(R.id.offline_region_name)
        private var offlineRegion: OfflineRegion? = null

        /**
         * Bind holder with the [OfflineRegion] by setting the name in the textView and the
         * onClickListener of the button to delete it using the [offlineMapSaver].
         */
        fun bind(offRegion: OfflineRegion, offlineMapSaver: OfflineMapSaver) {
            offlineRegion = offRegion
            textView.text = getMetadata(offRegion).name

            deleteButton.setOnClickListener {
                offlineMapSaver.deleteRegion(
                    offRegion.id,
                    object : OfflineRegion.OfflineRegionDeleteCallback {
                        override fun onDelete() {
                            ToastHandler.showToast(it.context, R.string.delete_successful)
                        }

                        override fun onError(error: String?) {
                            Timber.e(error)
                            ToastHandler.showToast(it.context, R.string.delete_fail)
                        }
                    })
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfflineRegionViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.offline_region_item, parent, false)

        return OfflineRegionViewHolder(view)
    }

    override fun onBindViewHolder(holder: OfflineRegionViewHolder, position: Int) {
        holder.bind(getItem(position), offlineMapSaver)
    }

    /**
     * Used by the OfflineRegionViewAdapter which implements ListAdapter.
     */
    object RegionDiff : DiffUtil.ItemCallback<OfflineRegion>() {

        override fun areItemsTheSame(oldItem: OfflineRegion, newItem: OfflineRegion): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: OfflineRegion, newItem: OfflineRegion): Boolean {
            return oldItem.id == newItem.id
        }
    }
}



