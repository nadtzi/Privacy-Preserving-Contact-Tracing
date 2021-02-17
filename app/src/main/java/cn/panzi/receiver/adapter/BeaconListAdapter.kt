package cn.panzi.receiver.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import cn.panzi.receiver.R
import cn.panzi.receiver.widget.CommonCard
import org.altbeacon.beacon.Beacon

//add for notification
import cn.panzi.receiver.MainActivity
import cn.panzi.receiver.byteArrtoLongArr
import cn.panzi.receiver.getTEK
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.common_card.view.*

class BeaconListAdapter(
    private var beaconList: List<Beacon>, private var ctxt: MainActivity
) : androidx.recyclerview.widget.RecyclerView.Adapter<BeaconListAdapter.MyHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_beacon, parent, false)
        return MyHolder(view)
    }


    override fun getItemCount(): Int {
        return beaconList.size
    }

    override fun onBindViewHolder(holder: MyHolder, p1: Int) {
        var name = beaconList[p1].id1.toString()
        //var name = beaconList[p1].dataFields[0].toString()

        val distance = beaconList[p1].distance.toString()
        holder.commonCard.setCardTitleText(name)

        holder.commonCard.tekText.text="${beaconList[p1].dataFields}"
        //else holder.commonCard.tekText.text="no data"

        //holder.commonCard.tekText.text = beaconList[p1].parserIdentifier.toString()
       // if (holder.test!=null)
       // holder.commonCard.tekText.text=(beaconList[p1].dataFields.add(0,holder.test.text.toString().toLong()).toString() )
        holder.commonCard.setCardSubscribeText(distance)
        holder.commonCard.setCardImageRes(R.mipmap.ic_launcher)
    }

    class MyHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val commonCard: CommonCard = itemView.findViewById(R.id.common_card)
        val test = itemView.findViewById<TextView>(R.id.testDataInput)
    }

}
