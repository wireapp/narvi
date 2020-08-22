package com.wire.bots.narvi.db

import com.wire.bots.narvi.db.model.IssueTracker
import com.wire.bots.narvi.db.model.Usernames
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

// class just because of the possibility to mock it
class UsernamesService {

    fun getTrackerUsername(wireUsername: String, tracker: IssueTracker) =
        transaction {
            Usernames
                .slice(Usernames.trackerUsername)
                .select {
                    (Usernames.wireUsername eq wireUsername) and
                            (Usernames.issueTracker eq tracker)
                }.firstOrNull()
                ?.let { it[Usernames.trackerUsername] }
        }

    fun getWireUsername(trackerUsername: String, tracker: IssueTracker) =
        transaction {
            Usernames
                .slice(Usernames.wireUsername)
                .select {
                    (Usernames.trackerUsername eq trackerUsername) and
                            (Usernames.issueTracker eq tracker)
                }.firstOrNull()
                ?.let { it[Usernames.wireUsername] }
        }


    fun insertUsernamesPair(wireUsername: String, trackerUsername: String, tracker: IssueTracker) {
        transaction {
            Usernames.insert {
                it[this.wireUsername] = wireUsername
                it[this.issueTracker] = tracker
                it[this.trackerUsername] = trackerUsername
            }
        }
    }
}
