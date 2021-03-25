package ch.epfl.sdp.drone3d.storage.dao

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ch.epfl.sdp.drone3d.storage.data.MappingMission
import ch.epfl.sdp.drone3d.storage.data.State
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import javax.inject.Inject


class FirebaseMappingMissionDao : MappingMissionDao {

    private lateinit var database: FirebaseDatabase

    /**
     * Default constructor, injected through hilt
     */
    @Inject
    constructor()

    /**
     * Constructor to specify the firebase database to use.
     *
     * This is mostly for testing purpose
     */
    constructor(database: FirebaseDatabase) : this() {
        this.database = database
    }

    private fun getDatabase(): FirebaseDatabase {
        // Query the database if it is not initialized yet
        if (!this::database.isInitialized)
            database =
                Firebase.database("https://drone3d-6819a-default-rtdb.europe-west1.firebasedatabase.app/")//.setPersistenceEnabled(true)
        return database
    }

    private val privateMappingMissions: MutableLiveData<List<MappingMission>> = MutableLiveData()
    private var privateMappingMissionsIsInit: Boolean = false
    private val sharedMappingMissions: MutableLiveData<List<MappingMission>> = MutableLiveData()
    private var sharedMappingMissionsIsInit: Boolean = false

    companion object {
        private const val TAG = "FirebaseMappingMissionDao"
        private const val PRIVATE_ID_PATH = "privateId"
        private const val SHARED_ID_PATH = "sharedId"
        private const val STATE_PATH = "state"
        private const val MAPPING_MISSIONS_PATH = "mappingMissions"
    }

    /**
     * Return the firebase database reference to the repo of private mapping missions of the user specified by [UID]
     */
    private fun privateMappingMissionRef(UID: String): DatabaseReference {
        return getDatabase().getReference("users/$UID/$MAPPING_MISSIONS_PATH")
    }

    /**
     * Return the firebase database reference to the repo of shared mapping missions
     */
    private fun sharedMappingMissionRef(): DatabaseReference {
        return getDatabase().getReference(MAPPING_MISSIONS_PATH)
    }

    /**
     * Return a the mapping mission specified by its [id]
     * If the [ownerUid] is not null the mission is fetch from private repo of the owner
     * Otherwise in the shared repo
     */
    private fun getMappingMission(ownerUid: String?, id: String): LiveData<MappingMission> {
        val mission: MutableLiveData<MappingMission> = MutableLiveData()

        val missionListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val missionSnapshot = dataSnapshot.getValue<MappingMission>()
                mission.value = missionSnapshot
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "getMappingMission:onCancelled", databaseError.toException())
            }
        }

        val rootRef =
            if (ownerUid != null) privateMappingMissionRef(ownerUid) else sharedMappingMissionRef()
        rootRef.child(id).addListenerForSingleValueEvent(missionListener)
        return mission
    }

    override fun getPrivateMappingMission(
        ownerUid: String,
        privateId: String
    ): LiveData<MappingMission> {
        return getMappingMission(ownerUid, privateId)
    }

    override fun getSharedMappingMission(sharedId: String): LiveData<MappingMission> {
        return getMappingMission(null, sharedId);
    }

    /**
     * Return a all mapping mission of a repo
     * If the [ownerUid] is not null the missions are fetch from private repo of the owner
     * Otherwise in the shared repo
     */
    private fun getMappingMissions(ownerUid: String?): LiveData<List<MappingMission>> {
        var mappingMissionIsInit =
            if (ownerUid != null) privateMappingMissionsIsInit else sharedMappingMissionsIsInit
        val rootRef =
            if (ownerUid != null) privateMappingMissionRef(ownerUid) else sharedMappingMissionRef()
        val mappingMissions =
            if (ownerUid != null) privateMappingMissions else sharedMappingMissions

        if (!mappingMissionIsInit) {
            val missionsListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val missionsSnapshot = dataSnapshot.children.map { c ->
                        c.getValue(MappingMission::class.java)!!
                    }
                    mappingMissions.value = missionsSnapshot
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w(TAG, "getMappingMissions:onCancelled", databaseError.toException())
                }
            }
            rootRef.addValueEventListener(missionsListener)
            if (ownerUid != null) {
                privateMappingMissionsIsInit = true
            } else {
                sharedMappingMissionsIsInit = true
            }
        }
        return mappingMissions
    }

    override fun getPrivateMappingMissions(ownerUid: String): LiveData<List<MappingMission>> {
        return getMappingMissions(ownerUid)
    }

    override fun getSharedMappingMissions(): LiveData<List<MappingMission>> {
        return getMappingMissions(null)
    }

    override fun storeMappingMission(ownerUid: String, mappingMission: MappingMission) {
        val privateId = privateMappingMissionRef(ownerUid).push().key
        mappingMission.privateId = privateId;
        mappingMission.ownerUid = ownerUid;
        when (mappingMission.state) {
            // Not already stored => only in private repo
            State.NOT_STORED -> mappingMission.state = State.PRIVATE
            // Already stored in shared repo => private and shared
            State.SHARED -> {
                mappingMission.state = State.PRIVATE_AND_SHARED

                // Update state and privateId in shared repo
                val refSharedMission = sharedMappingMissionRef().child(mappingMission.sharedId!!);
                refSharedMission.child(PRIVATE_ID_PATH).setValue(privateId)
                refSharedMission.child(STATE_PATH).setValue(State.PRIVATE_AND_SHARED)
            }
            else -> {
            }
        }
        privateMappingMissionRef(ownerUid).child(privateId!!).setValue(mappingMission)
    }


    override fun shareMappingMission(ownerUid: String, mappingMission: MappingMission) {
        val sharedId = sharedMappingMissionRef().push().key
        mappingMission.sharedId = sharedId
        mappingMission.ownerUid = ownerUid;
        when (mappingMission.state) {
            // Not already stored => only in shared repo
            State.NOT_STORED -> mappingMission.state = State.SHARED
            // Already stored in private repo => private and shared
            State.PRIVATE -> {
                mappingMission.state = State.PRIVATE_AND_SHARED

                // Update state and sharedId in private repo
                val refPrivateMission =
                    privateMappingMissionRef(ownerUid).child(mappingMission.privateId!!)
                refPrivateMission.child(SHARED_ID_PATH).setValue(sharedId)
                refPrivateMission.child(STATE_PATH).setValue(State.PRIVATE_AND_SHARED)
            }
            else -> {
            }
        }
        sharedMappingMissionRef().child(sharedId!!).setValue(mappingMission)
    }

    override fun removePrivateMappingMission(ownerUid: String, privateId: String) {
        val missionListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val mission = dataSnapshot.getValue<MappingMission>()
                if (mission != null) {

                    // We need to update the shared copy in case there is one
                    if (mission.state == State.PRIVATE_AND_SHARED) {

                        // Remove privateId and change state
                        val refSharedMission = sharedMappingMissionRef().child(mission.sharedId!!);
                        refSharedMission.child(PRIVATE_ID_PATH).removeValue()
                        refSharedMission.child(STATE_PATH).setValue(State.SHARED)
                    }
                    // Remove mission from the private repo
                    privateMappingMissionRef(ownerUid).child(privateId).removeValue()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "removePrivateMappingMission:onCancelled", databaseError.toException())
            }
        }

        privateMappingMissionRef(ownerUid).child(privateId)
            .addListenerForSingleValueEvent(missionListener)
    }

    override fun removeSharedMappingMission(ownerUid: String, sharedId: String) {
        val missionListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val mission = dataSnapshot.getValue<MappingMission>()
                if (mission != null) {

                    // We need to update the private copy in case there is one
                    if (mission.state == State.PRIVATE_AND_SHARED) {

                        // Remove sharedId and change state in private copy
                        val refPrivateMission =
                            privateMappingMissionRef(ownerUid).child(mission.privateId!!)
                        refPrivateMission.child(SHARED_ID_PATH).removeValue()
                        refPrivateMission.child(STATE_PATH).setValue(State.PRIVATE)
                    }
                    // Remove mission from the shared repo
                    sharedMappingMissionRef().child(sharedId).removeValue()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "removePrivateMappingMission:onCancelled", databaseError.toException())
            }
        }

        // Get the mission data before removing it
        sharedMappingMissionRef().child(sharedId).addListenerForSingleValueEvent(missionListener)
    }

    override fun removeMappingMission(ownerUid: String, privateId: String?, sharedId: String?) {
        if (privateId != null) {
            privateMappingMissionRef(ownerUid).child(privateId).removeValue()
        }
        if (sharedId != null) {
            sharedMappingMissionRef().child(sharedId).removeValue()
        }
    }
}