package cz.freego.spacexexplorer.watchapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import cz.freego.spacexexplorer.R
import cz.freego.spacexexplorer.Utils
import cz.freego.spacexexplorer.remote.data.response.Crew
import kotlinx.android.synthetic.main.activity_crew_detail.*

class CrewDetailActivity : AppCompatActivity()  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crew_detail)

        val crewJson = intent.getStringExtra("crew")
        val moshi = Utils.getMoshi()
        val crew = moshi.adapter(Crew::class.java).fromJson(crewJson)

        if (crew != null) {
            Glide.with(this).load(crew.image).circleCrop().into(image_bg)
            full_name.text = crew.name
            agency.text = crew.agency
            status.text = crew.status
        }

    }


}